package Indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import Classes.Path;

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory
	// cannot hold our corpus...

	private String type;
	private String path;
	private int count;  // to count the document number
	private FileWriter wr;   // to write the dictionary index file
	private FileWriter docWriter;  // to write docno in the disk
	private LinkedHashMap<String, LinkedHashMap> termMap;
	
	private int BLOCKSIZE = 50000;

	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index initiate the BufferedWriter to output result

		this.type = type;
		this.path = type.equals("trectext") ? Path.MyIndexTextDir : Path.MyIndexWebDir;
		this.wr = new FileWriter(path + type);
		this.docWriter = new FileWriter(Path.MyDocnoDir + type);
		this.count = 0;
		this.termMap = new LinkedHashMap<String, LinkedHashMap>();

	}

	public void index(String docno, String content) throws IOException {
		// you are strongly suggested to build the index by installments
		// in your implementation of the index, you should transform your string docnos into non-negative integer docids !!!
		// In MyIndexReader, you will need to request the integer docid for docnos.

		count++;
		String docid = count + "";
		docWriter.append(docno + "\n");
		
		// put term and its relevant doc information in the hashmap
		String[] words = content.split(" ");
		for (int i = 0; i < words.length; i++) {
			String term = words[i];
			if (termMap.containsKey(term)) { // the term is exist in the
												// dictionary
				LinkedHashMap map = termMap.get(term);

				if (map.containsKey(docid)) { // this document has contains the term more than once
					int freq = (int) map.get(docid);
					map.put(docid, freq + 1);
				} else { // a new document that contains this term
					map.put(docid, 1);
				}

			} else { // dictionary doesn't contain the term

				// create a map to store docno and frequency
				LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
				map.put(docid, 1);
				termMap.put(term, map);
			}
		}
		
		// save dictionary after scanning a block
		if(count % BLOCKSIZE == 0){
			saveBlock();
		}
	}
	
	// move the posting lists part from memory into disk
	public void saveBlock() throws IOException {
		int num = (count % BLOCKSIZE == 0) ? count / BLOCKSIZE : (count
				/ BLOCKSIZE + 1);
		FileWriter fileWriter = new FileWriter(Path.MyBlockDir + num + "." + type);
		
		// traverse HashMap to store the dictionary term file
		Iterator iter = termMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String term = (String) entry.getKey();
			LinkedHashMap map = (LinkedHashMap) entry.getValue();

			Iterator iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry2 = (Map.Entry) iterator.next();
				String docid = (String) entry2.getKey();
				int freq = (int) entry2.getValue();
				fileWriter.append(docid + ":" + freq + ";");
			}
			fileWriter.append("\n");
			
			termMap.put(term, new LinkedHashMap());  // release memory
		}
		
		//System.out.println("one block has been moved from memory to disk.");
	}

	public void close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// and if you write your index into several files, you need to fuse them here.

		// 1. close docno-docid file resources
		docWriter.close(); 

		// 2. move last part of posting list into disk
		saveBlock(); 
		
		// 3. save all terms and their order in the disk
		saveTermList(); 

		// 4. merge all the temporary posting lists
		int num = (count % BLOCKSIZE == 0) ? count / BLOCKSIZE : (count	/ BLOCKSIZE + 1);
		mergeAllBlocks(num);
		
		// 5. close the indexing result file
		wr.close();
		
	}
	
	public void saveTermList() throws IOException{
		
		FileWriter fileWriter = new FileWriter(Path.MyTermDir + type);
		
		// traverse HashMap to save all terms
		Iterator iter = termMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String term = (String) entry.getKey();
			fileWriter.append(term + "\n");
		}
		// clear all the map entries in the term hashmap
		termMap.clear();  
		//System.out.println("All the terms in the " + type + " file have been saved.");
	}
	
	public void mergeAllBlocks(int num) throws IOException{
		FileInputStream[] fileInputStream = new FileInputStream[num];
		BufferedReader[] reader = new BufferedReader[num];
		String[] line = new String[num];

		// read each block line by line at the same time
		for(int i=0; i<num; i++){
			fileInputStream[i] = new FileInputStream(Path.MyBlockDir +(i+1)+"."+ type);
			reader[i] = (BufferedReader) new BufferedReader(new InputStreamReader(fileInputStream[i]));
			 line[i] = reader[i].readLine();
		}	
		
		Boolean isEnd = false;
		while(!isEnd){
			// merge each block's content in the same line
			for(int i=0; i<num; i++){
				if(line[i]!= null){
					wr.append(line[i]);
					line[i] = reader[i].readLine();
				}
			}
			wr.append("\n");
			
			// scan the end of the last block, which means all the blocks have been scan completely
			if(line[num-1] == null){  
				isEnd = true;
			}
		}
		
		// close all the resources
		for(int i=0; i<num; i++){
			reader[i].close();
			fileInputStream[i].close();		
			
			// delete this block file
			File file = new File(Path.MyBlockDir + (i+1) + "." + type);
			if(file.exists()){
				file.delete();
			}
		}
		
	}

}

