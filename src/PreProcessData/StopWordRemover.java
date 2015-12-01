package PreProcessData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import Classes.Path;

public class StopWordRemover {
	// you can add essential private methods or variables

	private FileInputStream fis;
	private BufferedReader reader;
	private HashMap map;

	public StopWordRemover() throws IOException {
		// load and store the stop words from the fileinputstream with
		// appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.StopwordDir

		// read trectext file
		this.fis = new FileInputStream(Path.StopwordDir);
		this.reader = new BufferedReader(new InputStreamReader(fis));

		// use HashMap to store all the stopwords
		this.map = new HashMap();
		String line = reader.readLine();
		while (line != null) {
			map.put(line, "");
			line = reader.readLine();
		}

		// close the resources
		reader.close();
		fis.close();

	}

	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword(char[] word) throws IOException {
		// return true if the input word is a stopword, or false if not

		// join the characters of a word together
		int len = word.length;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			sb.append(word[i]);
		}

		// check if the hashmap contains this word
		return map.containsKey(sb.toString());
	}
}
