package com.github.mob41.sakura.notification;

import java.util.Calendar;

import com.github.mob41.sakura.hash.AesUtil;

public class Notification {
	
	private final String title;
	
	private final String message;
	
	private final String uid;
	
	private final Calendar time;
	
	private boolean read = false;
	
	public Notification(String title, String message){
		this.message = message;
		this.title = title;
		this.time = Calendar.getInstance();
		this.uid = AesUtil.random(128/8);
	}
	
	public Calendar getTime(){
		return time;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getUID(){
		return uid;
	}
	
	public boolean isRead(){
		return read;
	}
	
	public void setAsUnread(){
		read = false;
	}
	
	public void setAsRead(){
		read = true;
	}
	
	public boolean equals(Notification obj){
		if (obj == null){
			return false;
		}
		return this.uid.equals(obj.getUID()) && this.time.equals(obj.time);
	}
}
