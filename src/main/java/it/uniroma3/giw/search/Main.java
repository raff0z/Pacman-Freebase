package it.uniroma3.giw.search;

import java.util.List;

public class Main {

	public static void main(String[] args) {

		//Per test
		int MAX_LINE = 5;
		int count = 0;

		MeaningExtractor meaningExtractor = new MeaningExtractor();

		DocumentIO documentIO = new DocumentIO();
		for (String fileName : documentIO.filesCSVToRead()){

			List<String[]> lines = documentIO.openCSV(fileName);
			lines.remove(0); //Elimino l'indentazione

			for (String[] line : lines){
				if (count >= MAX_LINE) //Solo per test
					return;

				//Salto l'id
				//				for (int i=1; i<line.length; i++){
				//					
				//					String field = line[i];
				//					String meaning = meaningExtractor.extract(field);
				//					
				//					System.out.println(meaning); //Da salvare su csv
				//				}


				String field = line[1];
//				String meaningField = meaningExtractor.extract(field);			
				List<String> meaningField = meaningExtractor.getMeanings(field);

				//System.out.println(meaningField); //Da salvare su csv



				count++; //Solo per test
			}
		}

	}

}
