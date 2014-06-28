package it.uniroma3.giw.stopwordsremover;

import it.uniroma3.giw.search.DocumentIO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StopWordsRemover {
    private int threshold;
    private String pathFile;
    private List<String> stopWordsList;
    private DocumentIO dio = new DocumentIO();

    public StopWordsRemover() {

	this.pathFile = this.dio.getStopWordsPath();
	this.threshold = 3;
	this.stopWordsList = new ArrayList<String>();

	this.loadStopWords();
    }

    private void loadStopWords() {

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

    public String getPathFile() {
	return pathFile;
    }

    public void setPathFile(String pathFile) {
	this.pathFile = pathFile;
    }
    
}
