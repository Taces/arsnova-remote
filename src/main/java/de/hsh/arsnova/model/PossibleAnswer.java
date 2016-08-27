package de.hsh.arsnova.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PossibleAnswer {

	private String id;
	private String text;
	private boolean correct;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isCorrect() {
		return correct;
	}
	public void setCorrect(boolean correct) {
		this.correct = correct;
	}
	
	@Override
	public String toString(){
		return "[id: "+id+"; text: "+text+"; correct: "+correct+"]";
	}
	
}
