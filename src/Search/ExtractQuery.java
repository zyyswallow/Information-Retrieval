package Search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Classes.Path;
import Classes.Query;
import Classes.Stemmer;

public class ExtractQuery {
	
	private HashMap<String,String> stopwordMap;
	
	public ExtractQuery() throws Exception{
		stopwordMap = new HashMap<String,String>();
		
		// load all stopwords into memory
		FileInputStream fis = new FileInputStream(Path.StopwordDir);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		String line = reader.readLine();
		while (line != null) {
			this.stopwordMap.put(line, "");
			line = reader.readLine();
		}

		reader.close();
		fis.close();
	}

	public List<Query> GetQueries() throws Exception {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: third topic title is ----Star Trek "The Next Generation"-----, if your code can recognize the phrase marked by "", 
		//    and further process the phrase in search, you will get extra points.
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		
		ArrayList<Query> list = new ArrayList<Query>();
		
		FileInputStream fis = new FileInputStream(Path.TopicDir);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		int id = 1;
		
		String line = reader.readLine();
		while (line != null) {
			if (line.startsWith("<title>")) { // find a new topic
				String title = line.substring(7);
				// 1) tokenized
				List<String> tokenList = tokenize(title);

				ArrayList<String> newTokenList = new ArrayList<String>();
				for (int i = 0; i < tokenList.size(); i++) {
					// 2) to lowercase
					String token = tokenList.get(i).toLowerCase();
					// 3) remove stop words
					if (!stopwordMap.containsKey(token)) {
						// 4) stemming
						Stemmer s = new Stemmer();
						char[] chars = token.toCharArray();
						s.add(chars, chars.length);
						s.stem();
						token = s.toString();

						newTokenList.add(token);
					}
				}

				// save a query
				Query query = new Query();
				query.SetTopicId(id + "");
				query.SetQueryContent(newTokenList);
				list.add(query);
				id++;
			}
			line = reader.readLine();
		}

		// close the resources
		reader.close();
		fis.close();

		return list;
	}

	// To tokenize the title and ignore all punctuation, such as "". 
	public List<String> tokenize(String title){
		ArrayList<String> tokenList = new ArrayList<String>();
		
		int len = title.length();
		int  i = 0;
		while (i < len) {  
			// find the start of a word
			if (Character.isLetter(title.charAt(i))) { 
				StringBuffer sb = new StringBuffer();
				
				// find the end of a word
				while (i < len && (Character.isLetter(title.charAt(i)) || Character
								.isDigit(title.charAt(i)))) {
					sb.append(title.charAt(i));
					i++;
				}

				tokenList.add(sb.toString());
			}
			i++;
		}
		
		return tokenList;
	}
}
