package com.github.mob41.sakura.notification;

import java.util.Calendar;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class Alert {

	public static final int ERROR = -1;
	
	public static final int NOTICE = 0;
	
	public static final int WARNING = 1;
	
	public static final String GLOBAL_PERM = "system.alerts.global";
	
	public static final String ADMIN_PERM = "system.alerts.admin";
	
	public static final String USER_PERM = "system.alerts.user";
	
	private final String perm;
	
	private final int level;
	
	private final String message;
	
	private final Calendar time;
	
	public Alert(int level, String message){
		this(level, message, GLOBAL_PERM);
	}
	
	public Alert(int level, String message, String perm){
		this.level = level;
		this.message = message;
		this.perm = perm;
		this.time = Calendar.getInstance();
	}
	
	public Calendar getTime(){
		return time;
	}
	
	public String getFormattedTime(){
		Calendar cal = Calendar.getInstance();
		long minago = TimeUnit.MILLISECONDS.toMinutes(cal.getTimeInMillis() - time.getTimeInMillis());
		
		if (minago < 5){
			return "Now";
		} else if (minago < 60) {
			return minago + " minutes ago";
		} else if (minago < 60 * 24){
			return TimeUnit.MINUTES.toHours(minago) + " hour(s) ago";
		} else if (minago < 60 * 24 * 2){
			return "Yesterday";
		} else {
			return "Long time ago";
		}
	}
	
	public int getLevel(){
		return level;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getPermission(){
		return perm;
	}
	
	public String getFAStatusStr(){
		switch (level){
		case ERROR:
			return "danger";
		case NOTICE:
			return "info";
		case WARNING:
			return "warning";
		default:
			return "default";
		}
	}
	
	public String getFAIconStr(){
		switch (level){
		case ERROR:
			return "times-circle";
		case NOTICE:
			return "info-circle";
		case WARNING:
			return "warning";
		default:
			return "question-circle";
		}
	}
}
