package com.mob41.sakura.ann;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mob41.sakura.servlets.old.ClassBridge;

public class AnnounceMem {
	
	/*
	 * Announce Array:
	 * 
	 * [Announce Name], [UID], [Type], [Title], [Message], [Level (Integer)], [Timeout (Time in MS)]
	 */
	private static List<String[]> announces = new ArrayList<String[]>(50);
	
	public static void clear(){
		announces.clear();
	}
	
	public static boolean isAnnouncesWithTypeExist(String type){
		String[] data;
		for (int i = 0; i < announces.size(); i++){
			data = announces.get(i);
			if (data[2].equals(type)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean addAnnounce(String[] data){
		return announces.add(data);
	}
	
	public static String addAnnounce(String announcename, String type, String title, String message, int level, int timeout){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(cal.getTimeInMillis() + timeout);
		String uid = ClassBridge.getRandomSalt();
		String[] data = new String[]{announcename, uid, type, title, message, Integer.toString(level), Long.toString(cal.getTimeInMillis())};
		return announces.add(data) ? uid : null;
	}
	
	public static List<String[]> getAllData(){
		return announces;
	}
	
	public static int getAmountOfAnnouncement(){
		return announces.size();
	}
	
	public static Object[] getData(String uid){
		int index = getIndexByUID(uid);
		if (index == -1){
			return null;
		}
		return announces.get(index);
	}
	
	public static boolean removeAnnounceByUID(String uid){
		int index = getIndexByUID(uid);
		if (index == -1){
			return false;
		}
		announces.remove(index);
		return true;
	}
	
	public static int getIndexByUID(String uid){
		String[] data;
		for (int i = 0; i < announces.size(); i++){
			data = announces.get(i);
			if (data[1].equals(uid)){
				return i;
			}
		}
		return -1;
	}
}
