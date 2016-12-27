package org.wilson.theJotBot.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.wilson.theJotBot.Cache;
import org.wilson.theJotBot.Models.JotModel;



public class JotFinder {

	
	public static JotModel findJotByName(String jotName){
		HashMap<Integer, HashSet<JotModel>> jotMap = Cache.getInstance().getJotMap();
		for(Entry<Integer, HashSet<JotModel>> item : jotMap.entrySet()){
			for(JotModel jot : item.getValue()){
				if(jot.getJotText().equals(jotName)){
					return jot;
				}
			}
		}
		return null;
	}
	
	public static JotModel findJotByUser(String jotName, Integer userId){
		HashMap<Integer, HashSet<JotModel>> jotMap = Cache.getInstance().getJotMap();
		HashSet<JotModel> set = jotMap.get(userId);
		if(set == null){
			return null;
		}
		for(JotModel item : set){
			if(item.getJotText().equals(jotName)){
				return item;
			}
			
		}
		return null;
	}
}
