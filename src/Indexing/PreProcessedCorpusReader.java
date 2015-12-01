package Indexing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

public class PreProcessedCorpusReader {
	// This is a simple corpus reader
	
	private FileInputStream fis;
	private BufferedReader reader;
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor should open the pre-processed corpus file, Path.ResultHM1 + type
		// which was generated in assignment 1
		// remember to close the file that you opened, when you do not use it any more
		
		this.fis = new FileInputStream(Path.ResultHM1 + type);
		this.reader = new BufferedReader(new InputStreamReader(fis));
	}
	

	public Map<String, String> nextDocument() throws IOException {
		// read a line for docNo
		// read another line for the word list 
		// put them in map, and return it
		
		Map<String, String> doc = new HashMap<String, String>();
		String docNo = reader.readLine();  // lists-118-9744836
		if (docNo == null) {  // reach the end of the file
			// close the resources
			reader.close();
			fis.close();
			return null;
		} else {
			String wordList = reader.readLine();
			doc.put(docNo, wordList);
			return doc;
		}

	}

}
