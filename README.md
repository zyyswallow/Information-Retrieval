# Information Retrieval

###**Step 1**
The goal is to develop Java classes that can process TREC standard format document collections.

####1. Reading Documents from Collection Files
Implement two classes that can read individual documents from trectext and trecweb format collection files :

• **PreProcessData.DocumentCollection** is a general interface for sequentially reading documents from collection files

• **PreProcessData.TrectextCollection** is the class for trectext format

• **PreProcessData.TrecwebCollection** is the class for trecweb format

####2. Normalize Document Texts
First implement classes to tokenize document texts into individual words, normalize all the words into their lowercase characters, and finally filter stop words.

• **PreProcessData.TextTokenizer** is a class for sequentially reading words from a sequence of characters

• **PreProcessData.TextNormalizer** is the class that transform each word to its lowercase version, and conduct stemming on each word.

• **PreProcessData.StopwordsRemover** is the class that can recognize whether a word is a stop word or not. A stop word list file will be provided, so that the class should take the stop word list file as input.


###**Step 2**
The goal is to develop practical understanding of constructing searchable index for a document collection, which is specified as a set of Java classes.

####1. Build an index.

• **Indexing. PreProcessedCorpusReader**

This class is a simple version of TrectextCollection and TrecwebCollection. Get access to the result.trectext and result.trecweb, and return document one by one through the nextDocument(). 

• **Indexing.MyIndexWriter**

This class has one essential method index (String docno, String content) to create index for a document represented by the docno and the content. To write very efficient code in this class, I construct the index by installments, where each installment works on only a block of the documents to be indexed. When processing the documents in a block, everything about the index can be stored in the memory, then when all the documents in the block is processed, the corresponding dictionary and postings can be stored as separate files on the hard drive so that the memory is cleaned for the next block of documents. Once all the blocks have been processed, there will be a fusion process to merge all the dictionary files, and all the posting files. 
     
