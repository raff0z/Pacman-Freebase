package it.uniroma3.giw.search;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {

	static String lastCSVRow = "lastcsvrow.txt";
	static String lastCSVFileName = "lastcsvfilename.txt";

	public static void main(String[] args) throws IOException {

		//Per test
		int MAX_LINE = 15;
		int count = 0;

		MeaningExtractor meaningExtractor = new MeaningExtractor();

		DocumentIO documentIO = new DocumentIO(lastCSVRow, lastCSVFileName);
		int lastRow = documentIO.getLastCSVRow();
		String lastCSVFile = documentIO.getLastCSVFile();

		List<String> fileList = filterFileList(documentIO.filesCSVToRead(), lastCSVFile);


		for (String fileName : fileList){

			List<String[]> lines = documentIO.openCSV(fileName);
			lines.remove(0); //Elimino l'indentazione
			restore(lines, lastRow);

			int lineNumber = 0;
			for (String[] line : lines){
				if (count >= MAX_LINE) //Solo per test
					return;
				if (lastRow+lineNumber == -1)
					putHeader(fileName, getFileName(fileName)+"_meaning", documentIO);


				String[] fields = new String[line.length+1];
				System.out.println(line[0]);
				fields[0] = line[0];

				List<List<NGram>> meanings = new LinkedList<List<NGram>>();

				//Salto l'id
				for (int i=1; i<line.length; i++){
					List<NGram> fieldMeanings = meaningExtractor.getMeanings(line[i]);	
					fields[i] = meaningExtractor.extractMeanings(fieldMeanings);
					if (fieldMeanings != null){						
						meanings.add(fieldMeanings);
					}
				}
				fields[fields.length-1] = meaningExtractor.extractDocumentMeaning(meanings);

				documentIO.saveCSV(getFileName(fileName)+"_meaning", fields, lineNumber);

				lineNumber++;
				count++; //Solo per test
			}
		}

	}

	private static void putHeader(String input, String output, DocumentIO documentIO ) {
		String[] firstLine = documentIO.openCSV(input).get(0);
		String[] header = new String[firstLine.length+1];
		for (int i=0; i<firstLine.length; i++)
			header[i] = firstLine[i];
		header[header.length-1] = "document meanings";

		documentIO.saveCSV(output, header);		
	}

	private static void restore(List<String[]> lines, int lastRow) {
		int i = 0;
		while (i<=lastRow){
			lines.remove(0);
			i++;
		}		
	}

	private static List<String> filterFileList(String[] filesCSVToRead, String lastCSVFile) {
		List<String> files = new LinkedList<String>();
		boolean found = false;
		lastCSVFile = lastCSVFile.split("_meaning")[0];

		if (lastCSVFile.equals(""))
			found = true;

		for (String file : filesCSVToRead){
			if (found)
				files.add(file);
			if (file.equals(lastCSVFile+".csv")){
				found = true;
				files.add(file);
			}
		}

		return files;
	}

	/**
	 * Elimina l'estenzione dal file
	 */
	public static String getFileName(String fileName){
		String[] splitted = fileName.split("\\.");
		String out = "";

		for (int i=0; i<splitted.length-1; i++)
			out += splitted[i];

		return out;
	}

}
