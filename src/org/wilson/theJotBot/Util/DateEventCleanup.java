package org.wilson.theJotBot.Util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.wilson.theJotBot.Cache;
import org.wilson.theJotBot.Models.JotModel;


public class DateEventCleanup implements Runnable{

	private Map<Integer, HashSet<JotModel>> map;
	
	public void run(){
		try{
		map = Cache.getInstance().getJotMap();

		for(Entry<Integer, HashSet<JotModel>> item : map.entrySet()){
			HashSet<JotModel> set = item.getValue();
			Iterator<JotModel> it = set.iterator();
				while(it.hasNext()){
					JotModel jot = it.next();
					if(jot.isCompleted()){
						it.remove();
					}
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}

		System.out.println("Jots cleaned!");
	}
}
