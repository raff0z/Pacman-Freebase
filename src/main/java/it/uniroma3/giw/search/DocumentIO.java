package it.uniroma3.giw.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


public class DocumentIO {

	private String csvToRead;
	private String csvToWrite;
	
	private String stopWordsPath;

	private Map<String, CSVWriter> fileMap;
	private String lastCSVRowFile;
	private String lastCSVFileNameFile;

	public DocumentIO(String lastCSVRow, String lastCSVFileName){
				
		Properties conf = new Properties();
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config/pacman_configuration.properties");
			if(inputStream == null)
				System.out.println("Properties not found");

			conf.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}		

		this.csvToRead = conf.getProperty("csv_to_read");
		this.csvToWrite = conf.getProperty("csv_to_write");
		
		this.stopWordsPath = conf.getProperty("stop_words_path");

		this.fileMap = new HashMap<String, CSVWriter>();
		//this.cleanPath();
		

		this.lastCSVRowFile = this.csvToWrite + lastCSVRow;
		this.lastCSVFileNameFile = this.csvToWrite + lastCSVFileName;
	}


	public List<String[]> openCSV(String fileName){
		try {
			CSVReader reader = new CSVReader(new FileReader(this.csvToRead + fileName));
			return reader.readAll();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveCSV(String fileName, String[] line, int lineNumber) {

		try {
			CSVWriter writer = new CSVWriter(new FileWriter(this.csvToWrite + fileName + ".csv", true));
			writer.writeNext(line);			
			this.saveCheckpoint(fileName);			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveCSV(String fileName, String[] line) {

		try {
			CSVWriter writer = new CSVWriter(new FileWriter(this.csvToWrite + fileName + ".csv", true));
			writer.writeNext(line);				
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//	private void cleanPath() {
//		File dir = new File(this.csvToWrite);
//		if (dir.exists())
//			deleteFolder(dir);
//	}
//
//	private void deleteFolder(File d) {
//		for(File f : d.listFiles()) {
//			if(!f.isFile())
//				deleteFolder(f);
//			f.delete();		
//		}
//	}

	private void saveCheckpoint(String fileName) throws IOException {
		String lastFileName = this.getLastCSVFile();
		save(this.lastCSVFileNameFile, fileName);
		
		int realrow;
		if (fileName.equals(lastFileName))
			realrow = this.getLastCSVRow() + 1;
		else
			realrow = 0;
		
		save(this.lastCSVRowFile, realrow+"");
	}

	public static void save(String fileName, String toSave){
		try {
			FileOutputStream file = new FileOutputStream(fileName);
			PrintStream stream = new PrintStream(file);
			stream.print(toSave);
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String[] filesCSVToRead(){
		File dir = new File(this.csvToRead);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File directory, String fileName) {
				return fileName.endsWith(".csv");
			}
		};
		return dir.list(filter);
	}


	public int getLastCSVRow() throws IOException {
		BufferedReader reader = null;
		int row = -1;
		try {
			reader = new BufferedReader(new FileReader(this.lastCSVRowFile));	
			row = Integer.valueOf(reader.readLine());
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("LastDBRowFile does not exist, return -1");
		}
		return row; 
	}


	public String getLastCSVFile() throws IOException {
		BufferedReader reader = null;
		String name = "";
		try {
			reader = new BufferedReader(new FileReader(this.lastCSVFileNameFile));	
			name = reader.readLine();
			reader.close();			
		} catch (FileNotFoundException e) {
			System.out.println("lastCSVFileName does not exist, return \"\"");
		}
		return name; 
	}
	public String getStopWordsPath() {
	    return stopWordsPath;
	}

}
