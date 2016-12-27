package org.wilson.theJotBot.Util;

public class JotConverter {

	public static String convertToCommand(String jotText){
		StringBuilder sb = new StringBuilder();
		String[] arr = jotText.split(" ");
		for(int i =0; i<arr.length; i++){
			if(i== arr.length-1){
				sb.append(arr[i]);
			}else{
				sb.append(arr[i] + "_");
			}
		}
		String jotCommand = sb.toString();
		return jotCommand;
	}
	
	public static String convertToJot(String jotCommand){
		StringBuilder sb = new StringBuilder();
		String jotText = jotCommand.substring(1);
		String[] arr = jotText.split("_");
		for(int i =0; i<arr.length; i++){
			if(i== arr.length-1){
				sb.append(arr[i]);
			}else{
				sb.append(arr[i] + " ");
			}
		}
		String jot = sb.toString();
		return jot;
	}
}
