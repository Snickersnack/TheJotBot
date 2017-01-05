package org.wilson.theJotBot.Models;

public class RemindCreationModel {

	Integer hours;
	String text;
	
	public RemindCreationModel(){
		this(null, null);
	}
	public RemindCreationModel(Integer hours, String text){
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
