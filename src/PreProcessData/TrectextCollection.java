package PreProcessData;

import java.io.*;
import java.util.*;

import Classes.Path;

/**
 * This is for INFSCI 2140 in 2015
 *
 */
public class TrectextCollection implements DocumentCollection {
	// you can add essential private methods or variables

	private FileInputStream fis;
	private BufferedReader reader;
	private int index;

	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
		// This constructor should open the file in Path.DataTextDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!

		// read trectext file
		this.fis = new FileInputStream(Path.DataTextDir);
		this.reader = new BufferedReader(new InputStreamReader(fis));
		this.index = 1;

	}

	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this
		// document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NTT: remember to close the file that you opened, when you do not use
		// it any more

		// Reading File line by line
		String line = reader.readLine();
		while (line != null) {
			if (line.equals("<TEXT>")) { // found the start of a content

				StringBuffer sb = new StringBuffer();
				line = reader.readLine();

				// to find the end of a content
				while (line != null && !line.equals("</TEXT>")) {
					sb.append(line);
					line = reader.readLine();
				}

				// initialzie a hashmap to store new content
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(index + "", sb.toString().toCharArray());
				//System.out.println(sb.toString());

				index++;
				return map;
			}

			line = reader.readLine();

		}

		System.out.println("Trectext file has been scanned completely.");

		// close the resources
		reader.close();
		fis.close();

		return null;
	}

}
