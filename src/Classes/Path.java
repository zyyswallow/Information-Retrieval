package Classes;

public class Path {
	public static String StopwordDir="data//stopword.txt";//address of stopwords.txt
	public static String ResultHM1="data//result.";//
	
	
	public static String DataWebDir="data//docset.trecweb";//address of docset.trectext
	public static String DataTextDir="data//docset.trectext";//address of docset.trectext
	
	/* My indexing path */
	public static String MyIndexTextDir="data//index_result//indextext.";//address of generated Text index file
	public static String MyIndexWebDir="data//index_result//indexweb.";//address of generated Web index file
	public static String MyTermDir="data//index_result//term.";// term list file
	
	public static String MyDocnoDir="data//index_temp//docno.";// docno-docid relationship file
	public static String MyBlockDir="data//index_temp//block.";// each block's posting list 	
	
	/* Lucene indexing path */
	public static String IndexTextDir="data//indextext//";//address of generated Text index file
	public static String IndexWebDir="data//indexweb//";//address of generated Web index file
	
	public static String TopicDir="data//topics.txt";//address of topics.txt

}
