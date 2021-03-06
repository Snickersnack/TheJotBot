package org.wilson.theJotBot.Updates.MessageHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.theJotBot.Cache;
import org.wilson.theJotBot.CallbackData;
import org.wilson.theJotBot.Commands;
import org.wilson.theJotBot.HibernateUtil;
import org.wilson.theJotBot.Config.BotConfig;
import org.wilson.theJotBot.Models.JotModel;
import org.wilson.theJotBot.Models.RemindCreationModel;
import org.wilson.theJotBot.Models.User;
import org.wilson.theJotBot.Updates.MessageHandler.CommandHelpers.RemindFlow;
import org.wilson.theJotBot.Util.JotConverter;
import org.wilson.theJotBot.Util.JotFinder;
import org.wilson.theJotBot.Util.JotsConstructor;
import org.wilson.theJotBot.Util.KeyboardBuilder;


public class MessageHandler {

	private String command;
	private Message message;
	SendMessage sendMessageRequest;
	Integer userId;
	StringBuilder sb;

	
	public MessageHandler(Message message) {
		this.message = message;
		sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(message.getChatId());
		command = message.getText().toLowerCase();
		userId = message.getFrom().getId();
		sb = new StringBuilder();
		


	}

	// Push commands to service classes

	public SendMessage parse() throws TelegramApiException {
		
		HashMap<Integer, RemindCreationModel> remindMap = Cache.getInstance().getInProgressRemind();
		
		RemindCreationModel inProgressRemind = remindMap.get(userId);
		if(inProgressRemind != null){
			if(command.startsWith(Commands.STARTCOMMAND) || command.startsWith(Commands.REMINDCOMMAND) || command.equals(Commands.JOTSCOMMAND) 
					|| command.startsWith(Commands.JOTCOMMAND) || command.startsWith(Commands.USERCOUNTCOMMAND)){
				inProgressRemind = null;
				remindMap.remove(userId);
				
			}
		}
		
		if(command.startsWith(Commands.STARTCOMMAND)){
			sb.append("theJotBot is a minimalist bot intended to help jot down quick notes and reminders.");
			sb.append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			sb.append("Begin jotting notes by directly messaging @theJotBot with /jot, or create a reminder with /remind.");
			sb.append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			sb.append("Use /jots to view your list of jots and click a jot to mark it as complete!");

		}
		else if(!message.isUserMessage()){
			sb.append("Can't compute! This bot is not meant for group usage. Message @theJotBot to start.");
		}
		
		else{
			if(!remindMap.containsKey(userId)){
				remindMap.put(userId, null);
				
			}
			HashMap<Integer, HashSet<JotModel>> map = Cache.getInstance().getJotMap();
			if(!map.containsKey(userId)){
				map.put(userId, null);
				User newUser = new User();
				//persist user here
				newUser.setId(userId);
				Session session = null;
				try{
					session =  HibernateUtil.getSessionFactory().openSession();
					session.beginTransaction(); 
					session.save(newUser); 
					session.getTransaction().commit();

				}catch(ConstraintViolationException e){
					System.out.println("did not consume: " + newUser.getId());
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
				
				
				
				System.out.println("empty key");

			}
			
			
			HashSet<JotModel> set = map.get(userId); 
			if(set == null){
				System.out.println("empty set");
				set = new HashSet<JotModel>();
				map.put(userId, set);
			}
			RemindCreationModel remindModel = remindMap.get(userId);
			
			
			if(command.startsWith(Commands.REMINDCOMMAND) || remindModel != null){
				RemindFlow flow = new RemindFlow(command, sendMessageRequest, message, remindMap);
				sendMessageRequest = flow.parse();
				return sendMessageRequest;
			}
			
			else if(command.equals(Commands.JOTSCOMMAND)){
				if(set.size() == 0){
					sb.append("You have no jots!");
				}else{
					JotsConstructor.create(sb, set);
				}	
			}
			
			else if(command.startsWith(Commands.JOTCOMMAND)){

				String[] commandArr = command.split(" ");
				if(!commandArr[0].equals(Commands.JOTCOMMAND)){
					sendMessageRequest.setText("Use this format: /jot [your note]");
					return sendMessageRequest;
				}
				String jot = "";
				
				try{
					jot = command.substring(5);
				}catch(Exception e){
					return null;

				}
				
				Pattern p = Pattern.compile("[^a-zA-Z0-9 ]");
				boolean hasSpecialChar = p.matcher(jot).find();
				
				if(jot.startsWith(Commands.JOTCOMMAND) || jot.toLowerCase().startsWith("jot")){
					sb.append("Jot cannot be a jot");	
				}
				else if(hasSpecialChar){
					sb.append("Can't compute! Jots need to be numbers and letters only please :)");
				}
				else if(jot.trim().length() > 0){

					JotModel jotModel = new JotModel(jot);
					if(set.add(jotModel)){
						sb.append("Jotted down <i>" + jot + "</i>");
						Cache.getInstance().registerJot(jotModel);
						jotModel.setUserId(userId);
						Session session = null;
						try{
							session =  HibernateUtil.getSessionFactory().openSession();
							session.beginTransaction(); 
							session.save(jotModel); 
							session.getTransaction().commit();

						}catch(ConstraintViolationException e){
							System.out.println("did not consume: " + jotModel.getId());
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
					else{
						sb.append("You've already jotted this down!");
					}
				}else{
					sb.append("Jots cannot be empty spaces");
				}

			}
			
			//else we have clicked on a command in jots.
			
			else if (command.startsWith(Commands.USERCOUNTCOMMAND)){
				sb.append("Users who have used a command: " + Cache.getInstance().getJotMap().size());
				sb.append(System.getProperty("line.separator"));
			}
			
		
		
			else if (command.startsWith(Commands.commandInitChar)){
					String jotCommand = command.substring(0);
					String jotText = JotConverter.convertToJot(jotCommand);
					System.out.println("jot text: " + jotText);
					JotModel jot = JotFinder.findJotByUser(jotText, userId);
					if(jot== null){
						return null;
					}else{
						
						sb.append(jotText);
						KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
						sendMessageRequest.setReplyMarkup(keyboardBuilder.buildJotsMarkup(CallbackData.COMPLETED, jotText));
						
					}
				}
			
		}
		sendMessageRequest.setText(sb.toString());
		sendMessageRequest.setParseMode(BotConfig.SENDMESSAGEMARKDOWN);
		return sendMessageRequest;

	}
}
