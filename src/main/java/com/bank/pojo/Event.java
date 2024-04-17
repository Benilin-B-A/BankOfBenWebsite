package com.bank.pojo;

public class Event {

	private String event;
	private long userId;
	private long targetUserId;
	private long time;
	private String description;
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getTargetUserId() {
		return targetUserId;
	}
	public void setTargetUserId(long targetUserId) {
		this.targetUserId = targetUserId;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
