package org.wilson.theJotBot.Updates.MessageHandler.CommandHelpers;

import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.wilson.theJotBot.Cache;
import org.wilson.theJotBot.Commands;
import org.wilson.theJotBot.Config.BotConfig;
import org.wilson.theJotBot.Models.RemindModel;
import org.wilson.theJotBot.ReminderService.ReminderService;

public class RemindFlow {

	
	public static SendMessage parse(String command, SendMessage sendMessageRequest, Message message, HashMap<Integer, RemindModel> remindMap){
		StringBuilder sb = new StringBuilder();
		Integer userId = message.getFrom().getId();
		RemindModel remindModel = remindMap.get(userId);
		
		if(remindModel != null){
			if(remindModel.getHours()== null){
				String input = message.getText();
				Integer hoursInput = 0;
				try{
					hoursInput = Integer.valueOf(input);
				}catch(Exception e){
					sendMessageRequest.setText("Must be valid number");
					return sendMessageRequest;
				}
				if(hoursInput<1 || hoursInput >24){
					sendMessageRequest.setText("Hour must be between 1 and 24 inclusive");
					return sendMessageRequest;
				}
				remindModel.setHours(hoursInput);
				sb.append("Add text to your reminder or /skip");
			}
		
			else{
				String reminderText = remindModel.getText();
				Integer hours = remindModel.getHours();
				Long seconds = hours * 60 * 60L;

				if(command.startsWith(Commands.SKIPCOMMAND)){
					sb.append("Reminder set for " + hours + " hours");
				}else{
					remindModel.setText(message.getText());
					reminderText = message.getText();
					sb.append("Reminder: <i>" + reminderText + "</i> set for " + hours + " hours");
				}
				ScheduledExecutorService scheduler = Cache.getInstance().getScheduler();
				SendMessage reminderMessage = new SendMessage();
				reminderMessage.setChatId(message.getChatId());
				
				if(reminderText == null){
					reminderMessage.setText("Reminder! You set this reminder " + hours + " hours ago!");
				}else{
					reminderMessage.setText("Reminder: " + reminderText);
				}
				

				scheduler.schedule(new ReminderService(reminderMessage), seconds, TimeUnit.SECONDS);
				remindMap.put(userId, null);
			}
		}
		
		else if(command.startsWith(Commands.REMINDCOMMAND)){
			String[] commandArray = command.split(" ");
			if(!commandArray[0].equals(Commands.REMINDCOMMAND)){
				sendMessageRequest.setText("Use /remind by itself or add hours and title");
				return sendMessageRequest;
			}
			
			if(command.length()>7){
				Integer hoursInput = 0;
				try{
					hoursInput = Integer.valueOf(commandArray[1]);
				}catch(Exception e){
					sendMessageRequest.setText("Must be valid number");
					return sendMessageRequest;
				}
				
				if(hoursInput<1 || hoursInput >24){
					sendMessageRequest.setText("Hour must be between 1 and 24 inclusive");
					return sendMessageRequest;
				}
				String text = "";
				for(int i = 2; i<commandArray.length; i++){
					if(i==2){
						text+= commandArray[i];
					}else{
						text+= " " + commandArray[i];
					}
				}
				
				Long seconds = hoursInput * 60 * 60L;
				SendMessage reminderMessage = new SendMessage();
				reminderMessage.setChatId(message.getChatId());
				reminderMessage.setText(text);
				
				ScheduledExecutorService scheduler = Cache.getInstance().getScheduler();
				scheduler.schedule(new ReminderService(reminderMessage), seconds, TimeUnit.SECONDS);
				
				sb.append("Reminder: <i>" + text + "</i> set for " + hoursInput + " hours");
			}
			else{
				sb.append("In how many hours do you want to be reminded?");
				RemindModel reminder = new RemindModel();
				remindMap.put(userId, reminder);
				//add keyboard?					
			}
			
		}
		sendMessageRequest.setText(sb.toString());
		sendMessageRequest.setParseMode(BotConfig.SENDMESSAGEMARKDOWN);
		return sendMessageRequest;
	}
}
