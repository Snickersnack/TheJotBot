package org.wilson.theJotBot.Updates;


import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.theJotBot.Config.BotConfig;
import org.wilson.theJotBot.Updates.CallbackHandler.CallbackHandler;
import org.wilson.theJotBot.Updates.MessageHandler.MessageHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateHandler extends TelegramLongPollingBot {

	private static final String TOKEN = BotConfig.TOKENNEWBOT;
	private static final String BOTNAME = BotConfig.USERNAMENEWBOT;

	ObjectMapper mapper = new ObjectMapper();

	private static final boolean USEWEBHOOK = false;
	
	public void onUpdateReceived(Update update) {
		// TODO Auto-generated method stub
		System.out.println("heres' the update: " + update);
		try {
			BotApiMethod<?> msg = handleUpdate(update);
			if (msg != null) {
				executeMessage(msg);
			}

		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public BotApiMethod<?> handleUpdate(Update update)
			throws TelegramApiException {
		
		SendMessage sendMessageRequest = new SendMessage();
		

		if (update.hasMessage()){
			Message message = update.getMessage();

			MessageHandler commandParser = new MessageHandler(message);
			try {
				sendMessageRequest = commandParser.parse();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return sendMessageRequest;		
			}
		
		else if (update.hasCallbackQuery()) {
			//We either have an edit request for the Jots jot OR
			//we have a reminder in progress - which we don't need an edit text for
			CallbackHandler cb = new CallbackHandler(update);
			EditMessageText editRequest = cb.handleCallbackQuery();
			return editRequest;
		}
		return null;

	}
	
	private void executeMessage(BotApiMethod<?> msg)
			throws TelegramApiException {

		if(msg == null){
			return;
		}
		System.out.println("sending: "  + msg.toString());
		if (msg instanceof SendMessage) {
			SendMessage sMessage = (SendMessage) msg;
			if(sMessage.getChatId()!=null){
				Message botMessage = sendMessage(sMessage);

			}


		} else {
			sendApiMethod(msg);

		}

	}
	
	public String getBotUsername() {
		// TODO Auto-generated method stub
		return BOTNAME;
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return TOKEN;
	}

}