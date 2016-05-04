package com.mob41.sakura.auth;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Session controlling for LoginServlet in HomeAutoSys WebUI
 */
public class SessionAuth extends Thread implements Runnable{
	/***
	 * <h1>Session authentication running thread</h1>
	 * This session thread can be only used by calling the function: launch()
	 */
	public static SessionAuth sesthread;
	
	public static final int DEFAULT_TIMEOUT = 3600000; //Default 1 hour
	
	private static final Logger logger = LogManager.getLogger(SessionAuth.class.getName());
	
	private boolean running = false;
	private List<JSONObject> sessions;
	
	/*
	A session JSONObject
	
	{sessionkey} (Something unique), {username} (identical with LoginAuth), {ip} (IP address), {regtime}, {timeout}
	
	*/
	public SessionAuth(){
		setup();
	}
	
	private void setup(){
		logger.trace("SessionAuth: Setting up...");
		sessions = new ArrayList<JSONObject>();
		logger.info("SessionAuth: Success.");
	}
	
	public String registerSession(String sessionkey, String username, String ip, long registertime, int timeout){
		logger.info("SessionAuth: Registering session at " + sessionkey + " at " + ip + "...");
		JSONObject ses;
		int i;
		for (i = 0; i < sessions.size(); i++){
			ses = sessions.get(i);
			if (sessionkey.equals(ses.getString("sessionkey")) || username.equals(ses.getString("username")) ||
					ip.equals(ses.getString(ses.getString("ip"))) || registertime == ses.getLong("regtime") ||
					timeout == ses.getInt("timeout")){
				logger.info("SessionAuth: Session exists. Using: " + ses.getString("sessionkey"));
				return ses.getString("sessionkey");
			}
		}
		//Write session to memory
		JSONObject data = new JSONObject();
		data.put("sessionkey", sessionkey);
		data.put("username", username);
		data.put("ip", ip);
		data.put("regtime", registertime);
		data.put("timeout", timeout);
		try {
			sessions.add(data);
		} catch (Exception e){
			return "-1";
		}
		return sessionkey;
	}
	
	@Override
	public void run() {
		start();
	}
	
	public void removeTimedOutSession(){
		Calendar cal = Calendar.getInstance();
		Calendar regcal = Calendar.getInstance();
		Calendar endcal = Calendar.getInstance();
		Date regdate;
		int i;
		for (i = 0; i < sessions.size(); i++){
			JSONObject ses = sessions.get(i);
			if (ses == null){
				break;
			}
			long regtime = ses.getLong("regtime");
			regdate = new Date(regtime);
			regcal.setTime(regdate);
			endcal.setTime(regdate);
			endcal.add(Calendar.MILLISECOND, ses.getInt("timeout"));
			if (!(cal.before(endcal) && cal.after(regcal))){
				logger.info("SessionAuth: Session timeout on " + ses.getString("ip") + " of " + ses.getString("username"));
				sessions.remove(i);
			}
		}
	}
	
	public boolean unregisterSession(String sessionkey){
		int index = getSessionIndex(sessionkey);
		if (index == -1){
			return false;
		}
		sessions.remove(index);
		return true;
	}
	
	public boolean isSessionAvailable(String sessionkey){
		int index = getSessionIndex(sessionkey);
		return index != -1;
	}
	
	public int getSessionIndex(String sessionkey){
		int i;
		JSONObject ses;
		for (i = 0; i < sessions.size(); i++){
			ses = sessions.get(i);
			if (ses.getString("sessionkey").equals(sessionkey)){
				return i;
			}
		}
		return -1;
	}
	
	public void startup(){
		if (!running){
			running = true;
			while (running){
				removeTimedOutSession();
				synchronized(this){
					try {
						this.wait(500);
					} catch (InterruptedException ignore) {}
				}
			}
		}
	}
	
	public void shutdown(){
		if (running){
			running = false;
		}
	}
	
	public void restart(){
		shutdown();
		startup();
	}
	
	public boolean isRunning(){
		return running;
	}
}
