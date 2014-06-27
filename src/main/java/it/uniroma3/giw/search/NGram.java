package it.uniroma3.giw.search;

public class NGram implements Comparable<NGram>{
	
	private String name;
	private int occurrency;
	private float score;

	public NGram(String name, float score){
		this(name, score, 1);
	}

	public NGram(String name, float score, int occurrency) {
		this.setName(name);
		this.setScore(score);
		this.setOccurrency(occurrency);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		return "name:" + this.name + " score:" + this.score + " occurrency:" + this.occurrency;
	}
	
	public void incrementOccurrency(){
		this.setOccurrency(this.getOccurrency() + 1);
	}
	
	public void incrementScore(float score){
		this.setScore(this.score+score);
	}

	public int getOccurrency() {
		return occurrency;
	}

	public void setOccurrency(int occurrency) {
		this.occurrency = occurrency;
	}

	public float getScore() {
		return this.score;
	}

	public void setScore(float score) {
		this.score = score;
	}
	
	public boolean equals(Object ngram){
		return this.name.equals( ((NGram) ngram).getName() );
	}

	public int compareTo(NGram o) {
		return (int) (o.getScore()*10000 - this.getScore()*10000); // Moltiplichiamo per 100 per aumentare la precisione
	}
	
	/**
	 * Riporta true se l'ngrammo è derivato da un altro, ossia se:
	 * hanno la stessa occorrenza
	 * hanno lo stesso score
	 * il nome è sottostringa dell'altro 
	 * @param ngram
	 * @return
	 */
	public boolean derivativeFrom(NGram ngram){
		//TODO: può essere esteso il controllo della sottostringa con uno split. Caso non funzionante: Shooter Video Game Shooter Game
		return this.occurrency == ngram.getOccurrency() && this.score == ngram.getScore() && ngram.getName().contains(this.name);
	}
	
	
	
	/**
	 * TODO: E' un problema molto grande, che può essere risolto con strumenti come le reti semantiche.
	 * Noi ci limitiamo a controllare se è sottostringa:
	 * Esempio: Shooter Video Game è un tipo di Video Game
	 * @param ngram
	 * @return
	 */
	public boolean isKindOf(NGram ngram){
		return this.name.contains(ngram.getName());
	}

}
