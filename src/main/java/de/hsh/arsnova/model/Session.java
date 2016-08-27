package de.hsh.arsnova.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Session {

	String shortName;
	int keyword;
	String creator;
	boolean active;
	Long lastOwnerActivity;
	String courseType;
	String courseId;
	boolean courseSession;
	
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public int getKeyword() {
		return keyword;
	}
	public void setKeyword(int keyword) {
		this.keyword = keyword;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Long getLastOwnerActivity() {
		return lastOwnerActivity;
	}
	public void setLastOwnerActivity(Long lastOwnerActivity) {
		this.lastOwnerActivity = lastOwnerActivity;
	}
	public String getCourseType() {
		return courseType;
	}
	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String sourseId) {
		this.courseId = sourseId;
	}
	public boolean isCourseSession() {
		return courseSession;
	}
	public void setCourseSession(boolean courseSession) {
		this.courseSession = courseSession;
	}
	
}
