package org.wilson.theJotBot.Util;

import java.util.HashSet;

import org.wilson.theJotBot.Commands;
import org.wilson.theJotBot.Models.JotModel;

public class JotsConstructor {

	public static void create(StringBuilder sb, HashSet<JotModel> set){
			StringBuilder active = new StringBuilder();
			StringBuilder complete = new StringBuilder();
	
			active.append("<strong>Active Jots</strong>");
			active.append(System.getProperty("line.separator"));
			complete.append("<strong>Jots Finished Today*</strong>");
			complete.append(System.getProperty("line.separator"));
			
			boolean populatedActive = false;
			boolean populatedCompleted = false;
			
			for(JotModel jot : set){
				if(jot.isCompleted()){
					populatedCompleted = true;
					complete.append(System.getProperty("line.separator"));
					complete.append("✔ ");
					complete.append(jot.getJotText());
				}else{
					
					populatedActive = true;
					active.append(System.getProperty("line.separator"));
					active.append(Commands.commandInitChar + jot.getJotCommand());
					
				}
			}
			
			
			if(!populatedActive){
				active.append(System.getProperty("line.separator"));
				active.append("<i>You have no tasks left! Hurrah!</i>");
			}
			sb.append(System.getProperty("line.separator"));
			sb.append(active.toString());
			sb.append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			
			
			if(populatedCompleted){
				sb.append(complete.toString());
				sb.append(System.getProperty("line.separator"));
				sb.append(System.getProperty("line.separator"));
				sb.append("<i>*will clear at midnight</i>");
			}
		
	}
}
