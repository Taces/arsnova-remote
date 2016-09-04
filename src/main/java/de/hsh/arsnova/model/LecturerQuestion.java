package de.hsh.arsnova.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LecturerQuestion {

	private String _id;
	private String questionType;
	private String subject;
	private String text;
	PossibleAnswer[] possibleAnswers;
	private Long timestamp;
	private boolean votingDisabled;
	private long piRoundStartTime;
	private long piRoundEndTime;
	private boolean piRoundActive;
	
	
	public boolean isVotingDisabled() {
		return votingDisabled;
	}
	public void setVotingDisabled(boolean votingDisabled) {
		this.votingDisabled = votingDisabled;
	}
	public long getPiRoundStartTime() {
		return piRoundStartTime;
	}
	public void setPiRoundStartTime(long piRoundStartTime) {
		this.piRoundStartTime = piRoundStartTime;
	}
	public long getPiRoundEndTime() {
		return piRoundEndTime;
	}
	public void setPiRoundEndTime(long piRoundEndTime) {
		this.piRoundEndTime = piRoundEndTime;
	}
	public boolean isPiRoundActive() {
		return piRoundActive;
	}
	public void setPiRoundActive(boolean piRoundActive) {
		this.piRoundActive = piRoundActive;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public PossibleAnswer[] getPossibleAnswers() {
		return possibleAnswers;
	}
	public void setPossibleAnswers(PossibleAnswer[] possibleAnswers) {
		this.possibleAnswers = possibleAnswers;
	}
}
