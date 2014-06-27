package it.uniroma3.giw.helper;

import java.util.ArrayList;
import java.util.List;

public class StringHelper {

    public String[] cleanString(String string) {

	string = string.replaceAll("[,.;!?%)(}{\\[\\]><]", " ");
	string = string.replaceAll("[\\s]+", " ");

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

}
