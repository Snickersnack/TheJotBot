package org.wilson.theJotBot.Util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.wilson.theJotBot.Config.BotConfig;


public class DateScheduler implements Runnable {

	public void run(){
		try{
			
		
		LocalDateTime date = DateUtil.getCurrentTime();
		ZoneId currentZone = ZoneId.of(BotConfig.TIME_ZONE);
		ZonedDateTime zonedNow = ZonedDateTime.of(date, currentZone);
        ZonedDateTime zonedNextDay ;
        zonedNextDay = zonedNow.withHour(0).withMinute(0).withSecond(0);
        if(zonedNow.compareTo(zonedNextDay) > 0){
        	zonedNextDay = zonedNextDay.plusDays(1);
            System.out.println("Next event cleanup: " + zonedNextDay);

        }        
        Duration duration = Duration.between(zonedNow, zonedNextDay);
        long initialDelay = duration.getSeconds();
        
	    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);            
	    scheduler.scheduleAtFixedRate(new DateEventCleanup(), initialDelay,  24*60*60, TimeUnit.SECONDS);
		}catch(Exception e){
			System.out.println(e);
		}
	}

}