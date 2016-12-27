package org.wilson.theJotBot.Models;

public class RemindModel {

	Integer hours;
	String text;
	
	public RemindModel(){
		this(null, null);
	}
	public RemindModel(Integer hours, String text){
		this.hours = hours;
		this.text = text;
	}

	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
