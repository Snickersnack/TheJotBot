package org.wilson.theJotBot.Models;

import org.wilson.theJotBot.Util.JotConverter;

public class JotModel {

	Long id;
	String jotText;
	boolean completed;
	String jotCommand;
	
	public JotModel(){
		id = 0L;
		jotText = null;
		completed = false;
	}
	
	public JotModel(Long id){
		this(id, null, false);

	}
	public JotModel(String jot){
		id = 0L;
		jotText = jot;
		completed = false;
		
		String jotComm = JotConverter.convertToCommand(jot);
		this.jotCommand = jotComm;
	}
	
	public JotModel(Long id, String jotText, boolean completed){
		this.id = id;
		this.jotText = jotText;
		this.completed = completed;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJotText() {
		return jotText;
	}

	public void setJotText(String jotText) {
		this.jotText = jotText;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getJotCommand() {
		return jotCommand;
	}

	public void setJotCommand(String jotCommand) {
		this.jotCommand = jotCommand;
	}
	
	
}
