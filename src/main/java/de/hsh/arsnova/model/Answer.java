package de.hsh.arsnova.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Answer {

	private String questionId;
	private String answerText;
	private int piRound;
	private int answerCount = 1;
	private int abstentionCount;
	
	@Override
	public String toString()
	{
		return "; questionId: "+questionId+"; answerText: "+answerText+"; piRound: "+piRound+"; asnwerCount: "+answerCount+"; abstentionCount: "+abstentionCount;
	}
	
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getAnswerText() {
		return answerText;
	}
	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
	public int getPiRound() {
		return piRound;
	}
	public void setPiRound(int piRound) {
		this.piRound = piRound;
	}
	public int getAnswerCount() {
		return answerCount;
	}
	public void setAnswerCount(int answerCount) {
		this.answerCount = answerCount;
	}
	public int getAbstentionCount() {
		return abstentionCount;
	}
	public void setAbstentionCount(int abstentionCount) {
		this.abstentionCount = abstentionCount;
	}
	
}
