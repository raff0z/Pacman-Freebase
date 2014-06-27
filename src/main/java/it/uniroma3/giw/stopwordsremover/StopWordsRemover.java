package it.uniroma3.giw.stopwordsremover;

import it.uniroma3.giw.helper.StringHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopWordsRemover {

	private String choosenLang;
	
	private StringHelper sh;
	
	private int threshold;

	private Map<String, List<String>> lang2StopWords;

	//for testing
	private String[] itStopWordsArray = {"a","ai","al","alla","allo","allora","altre",
			"altri","altro","anche","ancora","avere","aveva","avevano","ben","che",
			"con","cui","da","degli","del","della","dello","deve","devo","di","e","ecco",
			"fare","fino","fra","giu","ha","hai","hanno","ho","il",
			"invece","la","le","lo","ma","molta","molti","molto","nei","nella",
			"o","oltre","pero","piu","poco",
			"qua","quasi","quello","questo","qui",
			"quindi","sembra","sembrava",
			"senza","sia","siamo","siete","sono","soprattutto",
			"stati","stato","su","sul","sulla","tanto",
			"tra","un","una","uno","va","vai"};

	private List<String> itStopWordsList = new ArrayList<String>(Arrays.asList(itStopWordsArray)); 
	
	//for testing
	private String[] enStopWordsArray = {"a","about","above","after","again","against","all","am","an","and",
			"any","are","aren't","as","at","be","because","been","before","being",
			"below","between","both","but","by","can't","cannot","could","couldn't",
			"did","didn't","do","does","doesn't","doing","don't","down","during",
			"each","few","for","from","further","had","hadn't","has","hasn't",
			"have","haven't","having","he","he'd","he'll","he's","her","here",
			"here's","hers","herself","him","himself","his","how","how's","i",
			"i'd","i'll","i'm","i've","if","in","into","is","isn't","it","it's",
			"its","itself","let's","me","more","most","mustn't","my","myself",
			"no","nor","not","of","off","on","once","only","or","other","ought",
			"our","ours","ourselves","out","over","own","same","shan't","she",
			"she'd","she'll","she's","should","shouldn't","so","some","such","than",
			"that","that's","the","their","theirs","them","themselves","then",
			"there","there's","these","they","they'd","they'll","they're","they've",
			"this","those","through","to","too","under","until","up","very",
			"was","wasn't","we","we'd","we'll","we're","we've","were","weren't",
			"what","what's","when","when's","where","where's","which","while",
			"who","who's","whom","why","why's","with","won't","would","wouldn't",
			"you","you'd","you'll","you're","you've","your","yours","yourself",
	"yourselves"};

	private List<String> enStopWordsList = new ArrayList<String>(Arrays.asList(enStopWordsArray));

	public StopWordsRemover(String lang) {
		this.choosenLang = lang;

		this.threshold = 3;

		this.lang2StopWords = new HashMap<String, List<String>>();
		this.lang2StopWords.put("it", itStopWordsList);
		this.lang2StopWords.put("en", enStopWordsList);
		
		this.sh = new StringHelper();
	}


	public String deleteStopWords(String inputText) {

		String outputText = "";

		String[] splittedInputText = sh.cleanString(inputText);

		List<String> splittedInputList = new ArrayList<String>(Arrays.asList(splittedInputText));

		List<String> supportList = new ArrayList<String>();

		int inLength = splittedInputList.size();

		if(inLength < this.threshold)
			return inputText;

		for(String s : splittedInputList) {

			if(!this.lang2StopWords.get(choosenLang).contains(s))
				supportList.add(s);
		}

		if(supportList.size() < this.threshold){
			return inputText;
		}else{
			for(String s : supportList) {

				outputText += s + " ";
			}
		}
		return outputText;
	}


	public String getChoosenLang() {
		return choosenLang;
	}


	public void setChoosenLang(String choosenLang) {
		this.choosenLang = choosenLang;
	}

	public int getThreshold() {
		return threshold;
	}


	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	
	
}
