package it.uniroma3.giw.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class MeaningExtractor {

	private NetHttpTransport httpTransport;
	private HttpRequestFactory requestFactory;
	private JSONParser parser;
	private Map<String, NGram> ngrams2Result;

	public MeaningExtractor(){
		httpTransport = new NetHttpTransport();
		requestFactory = httpTransport.createRequestFactory();
		parser = new JSONParser();
		
		this.ngrams2Result = new HashMap<String, NGram>();
	}

	public String extract(String field) {

		for (String token : field.split(" ")){
			GenericUrl url = this.prepareUrl(token);		
			List<Result> results = this.getResults(url);

			if (!results.isEmpty()){
				this.populateMap(results);
						
				//Attribbuire il significato
			}
		}
		
		List<NGram> sortedNGrams = this.sortedNgrams();
		System.out.println("Tosort: "+ngrams2Result.values());
		System.out.println("sorted: "+sortedNGrams);

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
		return recursivePowerSet(tokens);
	}
	
	private List<String> recursivePowerSet(List<String> list){
		if(list.size() == 1) {
			return list;
		}

		else{
			String elem = list.remove(0);
			List<String> results = recursivePowerSet(list);
			List<String> resultsWithElem = new ArrayList<String>();
			resultsWithElem.add(elem);
			for(String result : results) 
				resultsWithElem.add(elem + " " + result);
			
			resultsWithElem.addAll(results);
			return resultsWithElem;
		}		
	}

	

}
