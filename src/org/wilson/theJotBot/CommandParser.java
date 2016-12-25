package org.wilson.theJotBot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMemberCount;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;


public class CommandParser {

	private String command;
	private Message message;
	SendMessage sendMessageRequest;
	private static final String TOKEN = BotConfig.TOKENNEWBOT;
	public CommandParser(Message message) {
		this.message = message;
		sendMessageRequest = new SendMessage();

	}

	// Push commands to service classes

	public SendMessage parse() throws TelegramApiException {
		command = message.getText().toLowerCase();
//		String user = message.getFrom().getUserName();
		Integer userId = message.getFrom().getId();
		sendMessageRequest.setChatId(message.getChatId());
		System.out.println("wtf");
		if(command.equals(Commands.TESTCOMMAND)){
			InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText("Clickme");
//			button.setSwitchInlineQuery("test");
			button.setCallbackData("test");
			List<InlineKeyboardButton> buttons = new ArrayList();
			buttons.add(button);
			List<List<InlineKeyboardButton>> grid = new ArrayList();
			grid.add(buttons);
			markup.setKeyboard(grid);
			sendMessageRequest.setReplyMarkup(markup);
			sendMessageRequest.setText("Look down");
			
			
			
		}
		return sendMessageRequest;

	}
}
