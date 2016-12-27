package org.wilson.theJotBot.ReminderService;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.theJotBot.Updates.UpdateHandler;

public class ReminderService extends UpdateHandler implements Runnable{
	
	private SendMessage sendMessage;
	
	public ReminderService(SendMessage sendMessage){
		this.sendMessage = sendMessage;
	}
	public void run(){
			try {
				sendApiMethod(sendMessage);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

}
