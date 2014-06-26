package it.uniroma3.giw.search;

public class NGram {
	
	private String name;
	private int occurrency;
	private float score;

	public NGram(String name, float score){
		this.setName(name);
		this.setScore(score);
		this.setOccurrency(1);
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

}
