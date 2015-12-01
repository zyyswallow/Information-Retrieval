package Indexing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Classes.Path;


public class MyIndexReader {
	//you are suggested to write very efficient code here, otherwise, your memory cannot hold our corpus...

	private String type;   // type of the file, trectext or trecweb
	private String termRes;  // store the posting list for a term
	private HashMap<String, Integer> termMap;  // retrieve docid by docno
	
	// to scan the indexing result file
	private FileInputStream fis;
	private BufferedReader reader;
	
	public MyIndexReader( String type ) throws IOException {
		//read the index files you generated in task 1
		//remember to close them when you finish using them
		//use appropriate structure to store your index
		
		this.type = type;
		this.termMap = new HashMap<String, Integer>();
		
		//read all terms from the disk, build the relationship of docid and docno
		FileInputStream fileInputStream = new FileInputStream(Path.MyTermDir+type);
		BufferedReader termReader = new BufferedReader(new InputStreamReader(fileInputStream));
		int i = 1;
		String line = termReader.readLine();
		while(line != null){
			this.termMap.put(line, i);
			line = termReader.readLine();
			i++;
		}
		termReader.close();
		fileInputStream.close();
		
		// open the indexing result file
		String path = type.equals("trectext") ? Path.MyIndexTextDir : Path.MyIndexWebDir;
		this.fis = new FileInputStream(path + type);
		this.reader = new BufferedReader(new InputStreamReader(this.fis));
	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1 !!!!!!!
	public int getDocid(String docno) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(Path.MyDocnoDir+type);
		BufferedReader docnoReader = new BufferedReader(new InputStreamReader(fileInputStream));
		int i = 1;
		String line = docnoReader.readLine();

		// search for docno by the line number
		while (line != null) {

			if (line.equals(docno)) {
				docnoReader.close();
				fileInputStream.close();
				return i;
			}
			line = docnoReader.readLine();
			i++;
		}
		docnoReader.close();
		fileInputStream.close();

		return -1;
	}

	// Retrieve the docno for the integer docid
	public String getDocno(int docid) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(Path.MyDocnoDir + type);
		BufferedReader docnoReader = new BufferedReader(new InputStreamReader(fileInputStream));
		int i = 1;
		String line = docnoReader.readLine();

		// search for docno by the line number
		while (line != null) {

			if (i == docid) {
				docnoReader.close();
				fileInputStream.close();
				return line;
			}
			line = docnoReader.readLine();
			i++;
		}
		docnoReader.close();
		fileInputStream.close();

		return null;
	}

	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] getPostingList( String token ) throws IOException {
		String[] results = this.termRes.split(";");

		// create a two-dimension array to store the posting list
		int[][] postingList = new int[results.length][2];
		for (int i = 0; i < results.length; i++) {
			String[] result = results[i].split(":");
			postingList[i][0] = Integer.parseInt(result[0]);
			postingList[i][1] = Integer.parseInt(result[1]);
		}

		return postingList;
	}

	
	// Return the number of documents that contains the token.
	public int DocFreq( String token ) throws IOException {
		// retrieve token from the term list hashmap
		int docid = termMap.get(token);

		// search for token's posting list
		int i = 1;
		String line = reader.readLine();
		while (line != null) {
			if(i == docid){
				this.termRes = line;
				break;
			}else{
				line = reader.readLine();
				i++;
			}
		}
		String[] results = this.termRes.split(";");
		
		return results.length;
	}
	
	// Return the total number of times the token appears in the collection.
	public long CollectionFreq( String token ) throws IOException {
		String[] results = this.termRes.split(";");
		int collectionFreq = 0;
		for (int i = 0; i < results.length; i++) {
			String[] result = results[i].split(":");
			collectionFreq += Integer.parseInt(result[1]);
		}
		return collectionFreq;
	}
	
	public void close() throws IOException {
		// close the resources
		reader.close();
		fis.close();
	}
	
}