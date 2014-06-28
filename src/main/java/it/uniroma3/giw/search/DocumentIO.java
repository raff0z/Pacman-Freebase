package it.uniroma3.giw.search;

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

	public DocumentIO(){
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
		this.cleanPath();
	}


	public List<String[]> openCSV(String fileName){
		try {
			CSVReader reader = new CSVReader(new FileReader(this.csvToRead + fileName));
			return reader.readAll();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void saveCSV(String fileName, String[] line) {

		CSVWriter writer;
		try {
			if (!this.fileMap.containsKey(fileName)) {
				writer = new CSVWriter(new FileWriter(this.csvToWrite + fileName + ".csv"));
				this.fileMap.put(fileName, writer);
			} else 
				writer = this.fileMap.get(fileName);

			writer.writeNext(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void cleanPath() {
		File dir = new File(this.csvToWrite);
		if (dir.exists())
			deleteFolder(dir);
	}

	private void deleteFolder(File d) {
		for(File f : d.listFiles()) {
			if(!f.isFile())
				deleteFolder(f);
			f.delete();		
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


	public String getStopWordsPath() {
	    return stopWordsPath;
	}

}
