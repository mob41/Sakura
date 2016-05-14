package com.mob41.sakura.servlets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

public class AccessTokenSession implements Runnable{

	public static final int DEFAULT_TIMEOUT = 10000;
	
	protected static Thread thread;
	
	protected static AccessTokenSession runnable;
	
	public List<JSONObject> ses;
	
	private int checkTimeOut = 500;
	
	public AccessTokenSession(int maxusers){
		ses = new ArrayList<JSONObject>(maxusers);
	}
	
	public static void runThread(int maxusers){
		runnable = new AccessTokenSession(maxusers);
		thread = new Thread(runnable);
		thread.setName("AccessTokenSession-" + thread.getId());
		thread.start();
	}
	
	public static AccessTokenSession getRunnable(){
		return runnable;
	}
	
	public void checkSession(){
		while (thread.isAlive()){
			Calendar cal = Calendar.getInstance();
			Calendar regcal = Calendar.getInstance();
			Calendar endcal = Calendar.getInstance();
			Date regdate;
			for (int i = 0; i < ses.size(); i++){
				JSONObject sesdata = ses.get(i);
				if (sesdata == null){
					break;
				}
				long regtime = sesdata.getLong("reg");
				regdate = new Date(regtime);
				regcal.setTime(regdate);
				endcal.setTime(regdate);
				endcal.add(Calendar.MILLISECOND, sesdata.getInt("timeout"));
				if (!(cal.before(endcal) && cal.after(regcal))){
					ses.remove(i);
				}
			}
			synchronized(thread){
				try {
					thread.wait(checkTimeOut);
				} catch (InterruptedException ignore){}
			}
		}
	}
	
	public boolean addSession(String token, String encpass, String encsalt, String ip, Long regtime, int timeout){
		if (getAmountOfThisIp(ip) >= 2){
			return false;
		}
		JSONObject json = new JSONObject();
		json.put("token", token);
		json.put("pass", encpass);
		json.put("salt", encsalt);
		json.put("ip", ip);
		json.put("reg", regtime);
		json.put("timeout", timeout);
		ses.add(json);
		return true;
	}
	
	public void removeSession(int index){
		ses.remove(index);
	}
	
	public void removeAllSessionOfThisIP(String ip){
		for (int i = 0; i < ses.size(); i++){
			JSONObject sesdata = ses.get(i);
			if (sesdata.getString("ip").equals(ip)){
				ses.remove(sesdata);
			}
		}
	}
	
	public int getIndexOfThisToken(String token){
		for (int i = 0; i < ses.size(); i++){
			JSONObject sesdata = ses.get(i);
			if (sesdata.getString("token").equals(token)){
				return i;
			}
		}
		return -1;
	}
	
	public List<JSONObject> getCurrentSessions(){
		return ses;
	}
	
	public int getAmountOfThisIp(String ip){
		int a = 0;
		for (int i = 0; i < ses.size(); i++){
			JSONObject sesdata = ses.get(i);
			if (sesdata.getString("ip").equals(ip)){
				a++;
			}
		}
		return a;
	}

	@Override
	public void run() {
		checkSession();
		
	}
}
