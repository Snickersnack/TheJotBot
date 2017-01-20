package org.wilson.theJotBot.Config;


public class BotConfig {
	

    public static final String BOTTOKEN = System.getenv("BOTTOKEN");
    public static final String BOTUSERNAME = System.getenv("BOTUSERNAME");
    public static final String SENDMESSAGEMARKDOWN = "HTML";

    public static final String TIME_ZONE = "America/Los_Angeles";
    public static final Long SCHEDULED_REMOVAL_DAYS = 1L;
}

