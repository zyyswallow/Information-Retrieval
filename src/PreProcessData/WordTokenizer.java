package PreProcessData;

/**
 * This is for INFSCI 2140 in 2015
 * 
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	// you can add essential private methods or variables
	private int index;  // use index to traverse each content
	private char[] texts;

	// YOU MUST IMPLEMENT THIS METHOD
	public WordTokenizer(char[] texts) {
		// this constructor will tokenize the input texts (usually it is a char
		// array for a whole document)
		this.index = 0;
		this.texts = texts;
	}

	// YOU MUST IMPLEMENT THIS METHOD
	public char[] nextWord() {
		// read and return the next word of the document
		// or return null if it is the end of the document

		int len = texts.length;
		while (index < len) {  

			if (Character.isLetter(texts[index])) { // find the start of a word
				StringBuffer sb = new StringBuffer();

				// find the end of a word
				while (index < len
						&& (Character.isLetter(texts[index]) || Character
								.isDigit(texts[index]))) {
					sb.append(texts[index]);
					index++;
				}

				// for some abbreviation words, such as wasn't, isn't, I'd, I'm,
				// most of them can be filtered by the process above.
				
				// if word ends with n't (eg. wasn't), remove letter n.
				if (index < len && texts[index] == '\'' && index < len - 1
						&& texts[index + 1] == 't' && index > 0
						&& texts[index - 1] == 'n') {
					sb.deleteCharAt(sb.length() - 1);
				}

				return sb.toString().toCharArray();
			}
			index++;
		}

		// System.out.println("Last word has been found.");
		return null;
	}


}
