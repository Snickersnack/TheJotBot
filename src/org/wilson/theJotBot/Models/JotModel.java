package org.wilson.theJotBot.Models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.wilson.theJotBot.Util.JotConverter;


@Entity
@Table(name = "jots")

public class JotModel {

	Long id;
	Integer userId;
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

	
	@Column(name = "user_id")
	public Integer getUserId() {
		return userId;
	}

	@Id
	@Column(name = "jot_id")
	public Long getId() {
		return id;
	}

	@Column(name = "jot_text")
	public String getJotText() {
		return jotText;
	}
	
	@Column(name = "completed")
	public boolean isCompleted() {
		return completed;
	}
	
	@Column(name = "jot_command")
	public String getJotCommand() {
		return jotCommand;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public void setId(Long id) {
		this.id = id;
	}


	public void setJotText(String jotText) {
		this.jotText = jotText;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}


	public void setJotCommand(String jotCommand) {
		this.jotCommand = jotCommand;
	}
	
	
	
	@Override 
	public boolean equals(Object o){
		if(!(o instanceof JotModel)){
			return false;
		}
		JotModel obj = (JotModel)o;
		return this.jotText.equals(obj.getJotText());
	}
	
	@Override
	public int hashCode(){
		int result = 17;
		result = 31 * result + this.jotText.hashCode();
		return result;
		
	}
}
