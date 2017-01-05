package org.wilson.theJotBot;



import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.wilson.theJotBot.Config.BotConfig;
import org.wilson.theJotBot.Models.InProgressRemind;
import org.wilson.theJotBot.Models.JotModel;
import org.wilson.theJotBot.Models.RemindCreationModel;
import org.wilson.theJotBot.ReminderService.ReminderService;
import org.wilson.theJotBot.Util.DateUtil;



	public class Cache {

		//TODO: Add a separate field for persisting scheduled events...list of remind time/name?
		private static Cache cache = new Cache();
		
		private HashMap<Integer, HashSet<JotModel>> jotMap; //keyed to users
		private HashMap<Integer, RemindCreationModel> inProgressRemind;
		private HashMap<Integer, List<InProgressRemind>> creationModels;
	    ScheduledExecutorService scheduler;        
		Long globalJotId = 1L; //resultId CANNOT be 0



		


		private Cache() {
			setJotMap(new HashMap<Integer, HashSet<JotModel>>());
			setInProgressRemind(new HashMap<Integer, RemindCreationModel>());
			scheduler = Executors.newScheduledThreadPool(400);
		}

		public void init(){
			Session session = HibernateUtil.getSessionFactory().openSession();
			Criteria crit = session.createCriteria(JotModel.class);
			List<JotModel> Jots = crit.list();
			
			Long maxId = 0L;
			for(JotModel jot : Jots){
				if(jot.getId()>maxId){
					maxId = jot.getId();
				}
				Integer userId = jot.getUserId();
				if(!jotMap.containsKey(userId)){
					jotMap.put(userId, null);
				}
				if(jotMap.get(userId) == null){
					jotMap.put(userId, new HashSet<JotModel>());
				}
				HashSet<JotModel> jotSet = jotMap.get(userId);
				jotSet.add(jot);
				
				System.out.println("adding jot: " + jot.getJotText());
			}
			globalJotId = maxId+1;
			
			Criteria remindCriteria = session.createCriteria(InProgressRemind.class);
			List<InProgressRemind> reminds = remindCriteria.list();
			Long time = DateUtil.getCurrentTime().atZone(ZoneId.of(BotConfig.TIME_ZONE)).toEpochSecond();
			for(InProgressRemind item : reminds){
				Long targetSeconds = item.getTargetTime();
				if(targetSeconds > time + 60){
					Long difference = targetSeconds - time;
					SendMessage reminderMessage = new SendMessage();
					reminderMessage.setText(item.getReminderText());
					reminderMessage.setChatId(item.getChatId());
					ScheduledExecutorService scheduler = getScheduler();
					scheduler.schedule(new ReminderService(reminderMessage), difference, TimeUnit.SECONDS);
					System.out.println("adding remind: " + item.getReminderText());
				}
			}
			
			
		}
		
		public static Cache getInstance() {
			return cache;
		}

		

		public HashMap<Integer, HashSet<JotModel>> getJotMap() {
			return jotMap;
		}

		public void setJotMap(HashMap<Integer, HashSet<JotModel>> jotMap) {
			this.jotMap = jotMap;
		}

		public HashMap<Integer, RemindCreationModel> getInProgressRemind() {
			return inProgressRemind;
		}

		public void setInProgressRemind(HashMap<Integer, RemindCreationModel> hashMap) {
			this.inProgressRemind = hashMap;
		}



		public ScheduledExecutorService getScheduler() {
			return scheduler;
		}

		public void setScheduler(ScheduledExecutorService scheduler) {
			this.scheduler = scheduler;
		}



		public JotModel registerJot(JotModel jot){
			jot.setId(globalJotId);
			globalJotId++;
			return jot;
		}

}
