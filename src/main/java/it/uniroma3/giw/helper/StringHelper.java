package it.uniroma3.giw.helper;

import it.uniroma3.giw.stopwordsremover.StopWordsRemover;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.tika.language.LanguageIdentifier;

public class StringHelper {

    public String[] cleanString(String string) {
	StopWordsRemover stopWordsRemover = new StopWordsRemover();
	
	string = string.toLowerCase();
	string = string.replaceAll("[-,;!?%)(}{\\[\\]><]", " ");
	string = string.replaceAll("\\.\\s|\\s\\.", " ");
	string = string.replaceAll("[\\s]+", " ");
	string = stopWordsRemover.deleteStopWords(string);
	
	String[] cleanedString = string.split(" ");
	
	return cleanedString;
    }

    public List<String> recursivePowerSet(List<String> list) {
	if (list.size() == 1) {
	    return list;
	}

	else {
	    String elem = list.remove(0);
	    List<String> results = recursivePowerSet(list);
	    List<String> resultsWithElem = new ArrayList<String>();
	    resultsWithElem.add(elem);
	    for (String result : results)
		resultsWithElem.add(elem + " " + result);

	    resultsWithElem.addAll(results);
	    return resultsWithElem;
	}
    }
    
    public boolean isUrl(String url){
	try {
	    new URL(url);
	} catch (MalformedURLException e) {
	    if(e.getMessage().contains("no protocol")){
		url = "http://" + url;
	    }
	}
	
	UrlValidator urlValidator = new UrlValidator();
	
	boolean result = urlValidator.isValid(url);
	
	return result;
	
    }
    
    public String detectLanguage(String string){
	LanguageIdentifier languageIdentifier = new LanguageIdentifier(string);
	return languageIdentifier.getLanguage();
    }
    
    public int wordCount(String string){
	String[] result = string.split(" ");
	return result.length;
    }
}
