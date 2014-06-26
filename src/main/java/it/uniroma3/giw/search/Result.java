package it.uniroma3.giw.search;

public class Result {

	private String name;
	private float score;

	public Result(String name, String originalScore){
		this.setName(name);
		this.setScore( Float.parseFloat(originalScore));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString(){
		return "name:" + this.name + " score:" + this.score;
	}

	public float getScore() {
		return this.score;
	}

	public void setScore(float score) {
		this.score = score;
	}
}
