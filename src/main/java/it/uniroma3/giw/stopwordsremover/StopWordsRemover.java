package it.uniroma3.giw.stopwordsremover;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class StopWordsRemover {
    private int threshold;
    private List<String> stopWordsList;

    public StopWordsRemover() {

	this.threshold = 3;
	this.stopWordsList = new ArrayList<String>();

	this.loadStopWords();
    }

    private void loadStopWords() {
	
	Properties conf = loadConfig();	
	
	String pathFile = conf.getProperty("stop_words_path");
	
	BufferedReader br = null;

	try {

	    String line;

	    br = new BufferedReader(new FileReader(pathFile));

	    while ((line = br.readLine()) != null) {

		this.stopWordsList.add(line);
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (br != null)
		    br.close();
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }
	}
    }

    private Properties loadConfig() {
	Properties conf = new Properties();
	try {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config/pacman_configuration.properties");
		if(inputStream == null)
			System.out.println("Properties not found");

		conf.load(inputStream);
	} catch (IOException e) {
		e.printStackTrace();
	}
	return conf;
    }

    public String deleteStopWords(String inputText) {

	String outputText = "";

	String[] splittedInputText = inputText.split(" ");

	List<String> splittedInputList = new ArrayList<String>(
		Arrays.asList(splittedInputText));

	List<String> supportList = new ArrayList<String>();

	int inLength = splittedInputList.size();

	if (inLength < this.threshold)
	    return inputText;

	for (String s : splittedInputList) {

	    if (!this.stopWordsList.contains(s))
		supportList.add(s);
	}

	if (supportList.size() < this.threshold) {
	    return inputText;
	} else {
	    for (String s : supportList) {

		outputText += s + " ";
	    }
	}
	return outputText;
    }

    public int getThreshold() {
	return threshold;
    }

    public void setThreshold(int threshold) {
	this.threshold = threshold;
    }
}
