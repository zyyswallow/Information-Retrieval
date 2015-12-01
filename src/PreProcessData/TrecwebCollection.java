package PreProcessData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

/**
 * This is for INFSCI 2140 in 2015
 *
 */
public class TrecwebCollection implements DocumentCollection {
	// you can add essential private methods or variables

	private FileInputStream fis;
	private BufferedReader reader;
	private int index;

	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrecwebCollection() throws IOException {
		// This constructor should open the file in Path.DataWebDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!

		// read trectext file
		this.fis = new FileInputStream(Path.DataWebDir);
		this.reader = new BufferedReader(new InputStreamReader(fis));
		this.index = 1;

	}

	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this
		// document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NT: the returned content of the document should be cleaned, all html
		// tags should be removed.
		// NTT: remember to close the file that you opened, when you do not use
		// it any more

		// Reading File line by line
		String line = reader.readLine();
		while (line != null) {
			if (line.equals("</DOCHDR>")) { // found the start of a content

				StringBuffer sb = new StringBuffer();
				line = reader.readLine();

				// to find the end of a content
				while (line != null && !line.equals("</DOC>")) {
					sb.append(line + " ");
					line = reader.readLine();
				}

				// remove all html tags
				String newStr = sb.toString()
						.replaceAll("\\&[a-zA-Z]{1,10};", "")
						.replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "");

				// initialzie a hashmap to store new content
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(index + "", newStr.toCharArray());
				//System.out.println(index);
				//System.out.println(newStr);

				index++;
				return map;
			}

			line = reader.readLine();

		}

		System.out.println("Trecweb file has been scanned completely.");

		// close the resources
		reader.close();
		fis.close();

		return null;
	}

}
