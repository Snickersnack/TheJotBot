package org.wilson.theJotBot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.logging.BotLogger;
import org.wilson.theJotBot.Updates.UpdateHandler;
import org.wilson.theJotBot.Util.DateScheduler;


/**
 * Main initialization class
 * 
 */

public class Main {
//    private static Webhook webhook;

    public static void main(String[] args) throws TelegramApiRequestException {
    	ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new UpdateHandler());
        } catch (TelegramApiException e) {
            BotLogger.error("Error: ", e);
        }
        Thread dateThread = new Thread(new DateScheduler());
        dateThread.start();

    }
}
