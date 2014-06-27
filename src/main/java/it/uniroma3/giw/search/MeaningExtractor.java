package it.uniroma3.giw.search;

import it.uniroma3.giw.helper.StringHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class MeaningExtractor {

	private NetHttpTransport httpTransport;
	private HttpRequestFactory requestFactory;
	private JSONParser parser;
	private Map<String, NGram> ngrams2Result;
	private StringHelper sh;
	
	public MeaningExtractor(){
		httpTransport = new NetHttpTransport();
		requestFactory = httpTransport.createRequestFactory();
		parser = new JSONParser();
		
		this.ngrams2Result = new HashMap<String, NGram>();
		this.sh = new StringHelper();
	}
	
	public List<String> getMeanings(String field){
	    String[] cleanedField = this.sh.cleanString(field);
	    List<String> cleanedFieldList = new ArrayList<String>(Arrays.asList(cleanedField));
	    
	    List<String> resultList = this.sh.recursivePowerSet(cleanedFieldList);
	    
	    List<String> meanings = new ArrayList<String>();
	    
	    for (String string : resultList) {
		meanings.add(extract(string));
	    }
	    return meanings;
	    
	}

	public String extract(String field) {

	GenericUrl url = this.prepareUrl(field);
	List<Result> results = this.getResults(url);

	if (!results.isEmpty()) {
	    this.populateMap(results);

	    // Attribbuire il significato
	}
		
	List<NGram> sortedNGrams = this.sortedNgrams();
	System.out.println("Tosort: " + ngrams2Result.values());
	System.out.println("sorted: " + sortedNGrams);

	return null;
	}	
	
	private List<NGram> sortedNgrams() {
		List<NGram> sortedNGrams = new ArrayList<NGram>(ngrams2Result.values());
		Collections.sort(sortedNGrams);
		return sortedNGrams;
	}

	private GenericUrl prepareUrl(String query){

		GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");

		url.put("query", query);

		//TODO: da arricchire col sito
		//		if (this.isUrls(query)){
		//			url.put("filter", "(all type:/internet/website)");
		//		}
		url.put("limit", "20");
		url.put("indent", "true");
		//url.put("key", properties.get("API_KEY"));

		return url;
	}



	private List<Result> getResults(GenericUrl url){
		try {
			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
			return this.json2Result((JSONArray) response.get("result"));
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private List<Result> json2Result(JSONArray jsonArray) {

		List<Result> results = new LinkedList<Result>();

		for (Object elem : jsonArray){
			try{
				String name = JsonPath.read(elem,"$.notable.name").toString();
				String score = JsonPath.read(elem,"$.score").toString();
				results.add(new Result(name, score));
			} catch (PathNotFoundException e){

			}
		}

		return results;
	}


		
	

	private float getSumScores(List<Result> results) {
		float sum = 0;
		for (Result result : results)
			sum += result.getScore();		
		return sum;
	}

	private void populateMap(List<Result> results) {
		float sumScores = this.getSumScores(results);
		
		for (Result result : results){
			
			List<String> ngrams = this.createPowerSet(result);
			for (String ngram : ngrams)
				
				if (ngrams2Result.containsKey(ngram)){
					ngrams2Result.get(ngram).incrementOccurrency();
					ngrams2Result.get(ngram).incrementScore(result.getScore()/sumScores);
				}
				else
					ngrams2Result.put(ngram, new NGram(ngram, result.getScore()/sumScores));					
		}
		
	}

	//Crea l'insieme delle parti
	private List<String> createPowerSet(Result result){
		List<String> tokens = new ArrayList<String>(Arrays.asList(result.getName().split(" ")));
		return this.sh.recursivePowerSet(tokens);
	}
	
}
