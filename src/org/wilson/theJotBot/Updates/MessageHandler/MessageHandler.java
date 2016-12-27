package org.wilson.theJotBot.Updates.MessageHandler;

import java.util.HashMap;
import java.util.HashSet;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.theJotBot.Cache;
import org.wilson.theJotBot.CallbackData;
import org.wilson.theJotBot.Commands;
import org.wilson.theJotBot.Config.BotConfig;
import org.wilson.theJotBot.Models.JotModel;
import org.wilson.theJotBot.Models.RemindModel;
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
		
		HashMap<Integer, RemindModel> remindMap = Cache.getInstance().getInProgressRemind();
		
		if(command.startsWith(Commands.HELPCOMMAND)){
			sb.append("Bot used for quick notes and reminders. Users can only use through direct messaging @theJotBot.");
			sb.append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			sb.append("Start with a note by using /jot, or create a reminder with /remind.");
			sb.append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			sb.append("List all of your notes by using /jots and mark them as complete!");

		}
		else if(!message.isUserMessage()){
			sb.append("This bot is not meant for group usage. Message @theJotBot to start.");
		}
		
		else{
			if(!remindMap.containsKey(userId)){
				remindMap.put(userId, null);
			}
			HashMap<Integer, HashSet<JotModel>> map = Cache.getInstance().getJotMap();
			if(!map.containsKey(userId)){
				map.put(userId, null);
			}
			HashSet<JotModel> set = map.get(userId); 
			if(set == null){
				set = new HashSet<JotModel>();
				map.put(userId, set);
			}
			RemindModel remindModel = remindMap.get(userId);
			
			
			if(command.startsWith(Commands.REMINDCOMMAND) || remindModel != null){
				sendMessageRequest = RemindFlow.parse(command, sendMessageRequest, message, remindMap);
				return sendMessageRequest;
			}
			
			else if(command.equals(Commands.JOTSCOMMAND)){
				if(set.size() == 0){
					sb.append("You have no jots!");
				}else{
					JotsConstructor.create(sb, set);
					System.out.println("current output! " + sb.toString());
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
				if(jot.startsWith(Commands.JOTCOMMAND)){
					sb.append("Jot cannot be a jot");	
				}
				
				else if(jot.trim().length() > 0){
					JotModel jotModel = new JotModel(jot);
					jotModel = Cache.getInstance().registerJot(jotModel);
					if(set.add(jotModel)){
						sb.append("Jot <i>" + jot + "</i> added!");
					}
//					else{
//						sb.append("You have the exact same jot somewhere!");
//					}
				}else{
					sb.append("Jots cannot be empty spaces");
				}

			}
			
			//else we have clicked on a command in jots. 
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