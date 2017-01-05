package org.wilson.theJotBot.Models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reminders")

public class InProgressRemind {

	Long id;
	Long chatId;
	private Long targetTime; // date in UTC seconds since Jan 1, 1970 (unix time format)
	Integer userId;
	String reminderText;
	
	
	public InProgressRemind(){
		
	}
	
	public InProgressRemind(Long chatId, Long targetTime, Integer userId, String reminderText){
		this.chatId = chatId;
		this.targetTime = targetTime; 
		this.userId = userId;
		this.reminderText = reminderText;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reminder_id")
	public Long getId() {
		return id;
	}

	@Column(name = "chat_id")
	public Long getChatId() {
		return chatId;
	}

	@Column(name = "target_time")
	public Long getTargetTime() {
		return targetTime;
	}

	@Column(name = "user_id")
	public Integer getUserId() {
		return userId;
	}

	@Column(name = "reminder_text")
	public String getReminderText() {
		return reminderText;
	}

	public void setReminderText(String reminderText) {
		this.reminderText = reminderText;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public void setTargetTime(Long startTime) {
		this.targetTime = startTime;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
}
