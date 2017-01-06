package org.wilson.theJotBot.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.wilson.theJotBot.Cache;
import org.wilson.theJotBot.HibernateUtil;
import org.wilson.theJotBot.Config.BotConfig;
import org.wilson.theJotBot.Models.InProgressRemind;
import org.wilson.theJotBot.Models.JotModel;
import org.wilson.theJotBot.ReminderService.ReminderService;


public class DateJotCleanup implements Runnable{

	private Map<Integer, HashSet<JotModel>> map;
	
	public void run(){
		System.out.println("running cleanup");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date systemdate = new Date();
		System.out.println("System time: " + dateFormat.format(systemdate));
		try{
		map = Cache.getInstance().getJotMap();		
		Session session = null;
		
		try{
			session =  HibernateUtil.getSessionFactory().openSession();
			for(Entry<Integer, HashSet<JotModel>> item : map.entrySet()){
				HashSet<JotModel> set = item.getValue();
				Iterator<JotModel> it = set.iterator();
					while(it.hasNext()){
						JotModel jot = it.next();
						if(jot.isCompleted()){
							session.beginTransaction(); 
							session.delete(jot); 
							session.getTransaction().commit();
							it.remove();

						}
					}
				}
			
			
			Criteria remindCriteria = session.createCriteria(InProgressRemind.class);
			List<InProgressRemind> reminds = remindCriteria.list();
			Long time = DateUtil.getCurrentTime().atZone(ZoneId.of(BotConfig.TIME_ZONE)).toEpochSecond();
			for(InProgressRemind item : reminds){
				Long targetSeconds = item.getTargetTime();
				if(time > targetSeconds){
					session.beginTransaction(); 
					session.delete(item); 
					session.getTransaction().commit();
				}
			}

		}catch(ConstraintViolationException e){
			System.out.println("error in cleanup");
			System.out.println(e);
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
		
		
		

		}catch(Exception e){
			System.out.println(e);
		}

		System.out.println("Jots cleaned!");
		
		
		

	}
}
