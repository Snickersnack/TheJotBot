package org.wilson.theJotBot.Updates.MessageHandler.CommandHelpers;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.wilson.theJotBot.Cache;
import org.wilson.theJotBot.Commands;
import org.wilson.theJotBot.HibernateUtil;
import org.wilson.theJotBot.Config.BotConfig;
import org.wilson.theJotBot.Models.InProgressRemind;
import org.wilson.theJotBot.Models.RemindCreationModel;
import org.wilson.theJotBot.ReminderService.ReminderService;
import org.wilson.theJotBot.Util.DateUtil;

public class RemindFlow {

	private String command;
	private SendMessage sendMessageRequest;
	private Message message;
	private HashMap<Integer, RemindCreationModel> remindMap;
	Integer userId;
	Long chatId;
	
	
	public RemindFlow(String command, SendMessage sendMessageRequest, Message message, HashMap<Integer, RemindCreationModel> remindMap){
		this.command = command;
		this.sendMessageRequest = sendMessageRequest;
		this.message = message;
		this.remindMap = remindMap;
		userId = message.getFrom().getId();
		chatId = message.getChatId();

	}
	
	public  SendMessage parse(){
		StringBuilder sb = new StringBuilder();
		RemindCreationModel remindCreationModel = remindMap.get(userId);
		
		if(remindCreationModel != null){
			if(remindCreationModel.getHours()== null){
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
				remindCreationModel.setHours(hoursInput);
				sb.append("Add text to your reminder or /skip");
			}
		
			else{
				String reminderText = remindCreationModel.getText();
				Integer hours = remindCreationModel.getHours();
				Long seconds = hours * 60 * 60L;

				if(command.startsWith(Commands.SKIPCOMMAND)){
					sb.append("Reminder set for " + hours + " hours");
				}else{
					remindCreationModel.setText(message.getText());
					reminderText = message.getText();
					sb.append("Reminder: <i>" + reminderText + "</i> set for " + hours + " hours");
				}
				SendMessage reminderMessage = new SendMessage();
				reminderMessage.setChatId(message.getChatId());
				
				if(reminderText == null){
					reminderMessage.setText("Reminder! You set this reminder " + hours + " hours ago!");
				}else{
					reminderMessage.setText("Reminder: " + reminderText);
				}
				
				//Long targetTime = seconds + DateUtil.getCurrentTime().getSecond();
				
				scheduleReminder(reminderMessage, seconds);
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
				scheduleReminder(reminderMessage, seconds);
				
				sb.append("Reminder: <i>" + text + "</i> set for " + hoursInput + " hours");
				
				
			}
			
			
			else{
				sb.append("In how many hours do you want to be reminded?");
				RemindCreationModel reminder = new RemindCreationModel();
				remindMap.put(userId, reminder);
			}
			
		}
		sendMessageRequest.setText(sb.toString());
		sendMessageRequest.setParseMode(BotConfig.SENDMESSAGEMARKDOWN);
		return sendMessageRequest;
	}
	
	
	public void scheduleReminder(SendMessage reminderMessage, Long seconds){
		ScheduledExecutorService scheduler = Cache.getInstance().getScheduler();
		scheduler.schedule(new ReminderService(reminderMessage), seconds, TimeUnit.SECONDS);
		
		Long targetDate = DateUtil.getCurrentTime().atZone(ZoneId.of(BotConfig.TIME_ZONE)).toEpochSecond() + seconds;
		InProgressRemind newRemind = new InProgressRemind(chatId, targetDate, userId, reminderMessage.getText());
		Session session = null;
		try{
			session =  HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction(); 
			session.save(newRemind); 
			session.getTransaction().commit();

		}catch(ConstraintViolationException e){
			System.out.println("did not consume: " + newRemind.getId());
			session.getTransaction().rollback();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if (session != null){
			session.close();
			}
		}


	}
	
	
}
