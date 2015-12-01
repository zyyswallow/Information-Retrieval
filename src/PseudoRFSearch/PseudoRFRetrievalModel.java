package PseudoRFSearch;

import java.io.IOException;
import java.util.*;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import Search.QueryRetrievalModel;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	private double miu = 2000;   
	private long collectionLength = 142065539;
	
	public PseudoRFRetrievalModel(MyIndexReader ixreader)
	{
		this.ixreader=ixreader;
	}
	
	/**
	 * Search for the topic with pseudo relevance feedback. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {	
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		//get P(token|feedback documents)
		HashMap<String, Double> TokenRFScore = GetTokenRFScore(aQuery, TopK);
		
		List<String> tokenList = aQuery.GetQueryContent();   // all the tokens in the query
		HashMap<String,HashMap> freqMap = new HashMap<String,HashMap>();   // to store all relevant documents' p(w|d) of each token

		// find all relevant documents that contains at least one token in the query
		HashSet<Integer> docSet = new HashSet<Integer>();
		for (String token : tokenList) {  // for each token in a query
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(); // to store docid & token's frequency

			int[][] posting = ixreader.getPostingList(token);
			if (posting == null || posting.length == 0) { // whole collection doesn't have this token
				tokenList.remove(token);
				continue;
			}
			for (int ix = 0; ix < posting.length; ix++) {
				int docid = posting[ix][0];
				int freq = posting[ix][1];
				map.put(docid, freq); // store docid & token's frequency
				docSet.add(docid); // mark the relevant documents
				freqMap.put(token, map);
			}

		}
		
		List<Document> resultList = new ArrayList<Document>();
		
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')
		for(int docid : docSet) { 
			int docLength = ixreader.docLength(docid);
			double newDocScore = 1;
			
			for (String token : tokenList){
				long cf = ixreader.CollectionFreq(token);
				HashMap tokenMap = freqMap.get(token);   //  <docid, token's frequency>
				int freq = tokenMap.containsKey(docid) ? (Integer)tokenMap.get(docid) : 0;

				// calculate score by multiplying each token's p(w|D')
				double originalScore = (freq + miu * cf / collectionLength) / (docLength + miu);
				double newScore = alpha * originalScore + (1 - alpha) * TokenRFScore.get(token);
				newDocScore *= newScore;
				
			}
			if(newDocScore != 0){
				String docno = ixreader.getDocno(docid);
				Document document = new Document(docid, docno, newDocScore);
				resultList.add(document);
			}
			
		}
		
		// compare the documents
		Comparator<Document> comparator = new Comparator<Document>() {
			@Override
			public int compare(Document d1, Document d2) {
				if (d1.score() <= d2.score()) {
					return 1;
				} else {
					return -1;
				}
			}
		};
		
		// sort all retrieved documents from most relevant to least, and return TopN
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(resultList, comparator);
		return resultList.subList(0, TopN);
		
		
	}
	
	// return <token, feedback score> of given query
	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
	{   // for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)

		QueryRetrievalModel originalModel = new QueryRetrievalModel(ixreader);
		List<Document> feedbackDocList = originalModel.retrieveQuery(aQuery, TopK);
		
		// to save <token, score> in HashMap TokenRFScore
		HashMap<String, Double> TokenRFScore = new HashMap<String, Double>();

		// get all the tokens in the query
		List<String> tokenList = aQuery.GetQueryContent();
		for (String token : tokenList) {
			if (token == null) {
				continue;
			}
			
			// get total docLength in feedback documents
			int docLength = 0;
			HashSet<Integer> docidSet = new HashSet<Integer>();
			for (Document doc : feedbackDocList) {
				docLength += ixreader.docLength(doc.docid()); 
				docidSet.add(doc.docid());
			}

			// get total frequency in feedback documents
			int freq = 0;
			int[][] posting = ixreader.getPostingList(token);
			for (int j = 0; j < posting.length; j++) {
				if (docidSet.contains(posting[j][0])) {
					freq += posting[j][1];
				}
			}

			// use Dirichlet smoothing to generate P(token|feedback documents)
			long cf = ixreader.CollectionFreq(token);
			double score = (freq + miu * cf / collectionLength) / (docLength + miu);
			TokenRFScore.put(token, score);
		}

		return TokenRFScore;
	}
		
	
}