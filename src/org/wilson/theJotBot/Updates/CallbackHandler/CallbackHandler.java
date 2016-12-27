package org.wilson.theJotBot.Updates.CallbackHandler;

import java.util.HashMap;
import java.util.HashSet;

import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.wilson.theJotBot.CallbackData;
import org.wilson.theJotBot.Models.JotModel;
import org.wilson.theJotBot.Updates.UpdateHandler;
import org.wilson.theJotBot.Util.JotConverter;
import org.wilson.theJotBot.Util.JotFinder;
import org.wilson.theJotBot.Util.KeyboardBuilder;

public class CallbackHandler extends UpdateHandler {

	HashMap<Integer, HashSet<JotModel>> map;
	JotModel jotModel;
	Message message;
	CallbackQuery callback;
	long chatId;
	Integer messageId;
	Integer userId;
	EditMessageText editRequest;

	public CallbackHandler(Update update) {
		jotModel = null;
		callback = update.getCallbackQuery();
		chatId = callback.getMessage().getChat().getId();
		messageId = callback.getMessage().getMessageId();
		userId = callback.getFrom().getId();
		editRequest = new EditMessageText();
		editRequest.setChatId(chatId);
		editRequest.setMessageId(messageId);
		

	}


	public EditMessageText handleCallbackQuery() {

		String data = callback.getData();
		String[] dataArr = data.split(" ");
		String action = dataArr[0];
		String jot = "";

		for(int i = 1; i<dataArr.length; i++){
			if(i == 1){
				jot+= dataArr[i];
			}else{
				jot += " " + dataArr[i];

			}
		}
		StringBuilder sb = new StringBuilder();
		System.out.println("action: " + action);
		if(action.equals(CallbackData.COMPLETED) || action.equals(CallbackData.UNDO)){
			System.out.println("jot in the callback: " + jot);
			jotModel = JotFinder.findJotByUser(jot, userId);
			KeyboardBuilder keyboardBuilder = new KeyboardBuilder();
			if(action.equals(CallbackData.COMPLETED)){
				jotModel.setCompleted(true);
				sb.append("✔ ");
				sb.append(jot);
				editRequest.setReplyMarkup(keyboardBuilder.buildJotsMarkup(CallbackData.UNDO, jot));

			}else{
				jotModel.setCompleted(false);
				sb.append(jotModel.getJotText());
				editRequest.setReplyMarkup(keyboardBuilder.buildJotsMarkup(CallbackData.COMPLETED, jot));
				
			}
		}else{
			handleRemind();
			return null;
		}
		
		editRequest.setText(sb.toString());
		editRequest.setParseMode("HTML");
		return editRequest;
	}
	
	private void handleRemind(){
		
	}
}
