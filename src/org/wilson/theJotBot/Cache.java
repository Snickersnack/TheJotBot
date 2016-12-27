package org.wilson.theJotBot;



import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.wilson.theJotBot.Models.JotModel;
import org.wilson.theJotBot.Models.RemindModel;


	public class Cache {

		private static Cache cache = new Cache();
		//we need a map for jots
		//jots need to have a completed or not boolean
		//and a map for reminds? we don't have to save reminds...
		private HashMap<Integer, HashSet<JotModel>> jotMap; //keyed to users
		private HashMap<Integer, RemindModel> inProgressRemind;
		private Long globalJotId;
	    ScheduledExecutorService scheduler;            


		


		private Cache() {
			setJotMap(new HashMap<Integer, HashSet<JotModel>>());
			setInProgressRemind(new HashMap<Integer, RemindModel>());
			globalJotId = 1L; //resultId CANNOT be 0
			scheduler = Executors.newScheduledThreadPool(400);
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

		public HashMap<Integer, RemindModel> getInProgressRemind() {
			return inProgressRemind;
		}

		public void setInProgressRemind(HashMap<Integer, RemindModel> hashMap) {
			this.inProgressRemind = hashMap;
		}

		public Long getGlobalJotId() {
			return globalJotId;
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
