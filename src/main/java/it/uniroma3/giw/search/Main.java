package it.uniroma3.giw.search;

public class Main {

	public static void main(String[] args) {

		//Per test
		int MAX_LINE = 1;
		int count = 0;
		
		DocumentIO documentIO = new DocumentIO();
		String[] out = documentIO.filesCSVToRead();
		for (String fileName : documentIO.filesCSVToRead()){
			System.out.println(fileName);
		}
		
	}

}
