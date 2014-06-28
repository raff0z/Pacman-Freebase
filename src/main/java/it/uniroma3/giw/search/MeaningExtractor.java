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
	private StringHelper stringHelper;
	private final float THRESHOLD = (float) 0.8;

	public MeaningExtractor(){
		httpTransport = new NetHttpTransport();
		requestFactory = httpTransport.createRequestFactory();
		parser = new JSONParser();

		this.ngrams2Result = new HashMap<String, NGram>();
		this.stringHelper = new StringHelper();
	}

	public List<String> getMeanings(String field){
		String[] cleanedField = this.stringHelper.cleanString(field);
		List<String> cleanedFieldList = new ArrayList<String>(Arrays.asList(cleanedField));

		List<String> resultList = this.stringHelper.recursivePowerSet(cleanedFieldList);

		List<String> meanings = new ArrayList<String>();

		for (String string : resultList) {
			meanings.add(extract(string));
		}
		return meanings;

	}

	public String extract(String field) {
		System.out.println("entro");
		System.out.println(field);

		GenericUrl url = this.prepareUrl(field);
		List<Result> results = this.getResults(url);
		String meanings = ""; 

		if (!results.isEmpty()) {
			this.populateMap(results);


			System.out.println(ngrams2Result.values());
			List<NGram> sortedNGrams = this.sortedNgrams();
			System.out.println("Tosort: " + ngrams2Result.values());
			System.out.println("sorted: " + sortedNGrams);
			this.compressList(sortedNGrams);
			//System.out.println(sortedNGrams);

			List<NGram> filteredList = this.filterList(sortedNGrams);

			meanings = this.extractMeanings(filteredList);
			//System.out.println(meanings);
		}
		return meanings;
	}	

	private String extractMeanings(List<NGram> filteredList) {
		String meanings = "";
		int i;
		for(i = 0; i<filteredList.size()-1; i++) {
			meanings += "- "+filteredList.get(i).getName()+" "+filteredList.get(i).getScore()+" \n";
		}
		meanings += "- "+filteredList.get(i).getName()+" "+filteredList.get(i).getScore();

		return meanings;
	}

	private List<NGram> filterList(List<NGram> sortedNGrams) {
		List<NGram> filteredList = new LinkedList<NGram>();
		for(NGram ngram : sortedNGrams) {
			if(ngram.getScore() >= this.THRESHOLD) {
				filteredList.add(ngram);
			}
		}
		return filteredList;

	}

	private void compressList(List<NGram> ngrams) {
		this.compressByDerivation(ngrams);
		this.compressByKindOF(ngrams);
	}

	private void compressByDerivation(List<NGram> ngrams) {

		int i=0;
		while (i < ngrams.size()-1){
			NGram first = ngrams.get(i);
			NGram second = ngrams.get(i+1); 

			if (first.derivativeFrom(second)){
				ngrams.remove(i);
				i++;
			} else if (second.derivativeFrom(first)){
				ngrams.remove(i+1);
			}
			else 
				i++;
		}
	}

	private void compressByKindOF(List<NGram> ngrams) {
		NGram ngram1;
		NGram ngram2;
		for (int i=0; i<ngrams.size(); i++){
			ngram1 = ngrams.get(i);
			for (int j=i+1; j<ngrams.size(); j++){
				ngram2 = ngrams.get(j);	

				if (ngram1.isKindOf(ngram2)){
					//Unisco i due ngrammi sommando i valori dentro un unico ngrammo, memorizzando il nome del tipo particolare
					ngram1.setScore(ngram1.getScore()+ngram2.getScore());
					ngram1.setOccurrency(ngram1.getOccurrency()+ngram2.getOccurrency());
					ngrams.remove(j);
				}
				else if (ngram2.isKindOf(ngram1)){
					ngram1.setName(ngram2.getName());
					ngram1.setScore(ngram1.getScore()+ngram2.getScore());
					ngram1.setOccurrency(ngram1.getOccurrency()+ngram2.getOccurrency());
					ngrams.remove(j);
				}

			}
		}
	}

	private List<NGram> sortedNgrams() {
		List<NGram> sortedNGrams = new ArrayList<NGram>(ngrams2Result.values());
		Collections.sort(sortedNGrams);
		return sortedNGrams;
	}

	private GenericUrl prepareUrl(String query){

		GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");

		url.put("query", query);

        	if (this.stringHelper.isUrl(query)) {
        	    url.put("filter", "(all type:/internet/website)");
        	}
		url.put("limit", "20");
		url.put("indent", "true");
		String lang = this.stringHelper.detectLanguage(query);
		if(!lang.equals("unknown"))
		    url.put("lang", lang);
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
		return this.stringHelper.recursivePowerSet(tokens);
	}

}
