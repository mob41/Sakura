package com.mob41.sakura.plugin.loginagent;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import com.mob41.sakura.hash.AesUtil;

public class Session {

	private final User user;
	
	private final InetAddress ip;
	
	private final String sessionkey;
	
	private final long regtime;
	
	private final int timeout;
	
	/**
	 * Creates a new session.
	 * @param user Username
	 * @param ip IP Address
	 * @param regtime Register Time In MS
	 * @param timeout Timeout In MS
	 */
	public Session(User user, InetAddress ip, long regtime, int timeout){
		this.user = user;
		this.ip = ip;
		this.regtime = regtime;
		this.timeout = timeout;
		
		this.sessionkey = AesUtil.random(128/8);
	}
	
	/**
	 * Returns the user from this session
	 * @return ```User``` instance
	 */
	public User getUser(){
		return user;
	}
	
	/**
	 * Returns the Internet IP Address from this session
	 * @return ```InetAddress``` instance
	 */
	public InetAddress getIP(){
		return ip;
	}
	
	/**
	 * Returns the register time in ms
	 * @return The register time
	 */
	public long getRegisterTime(){
		return regtime;
	}
	
	/**
	 * Returns the timeout in ms
	 * @return The timeout
	 */
	public long getTimeout(){
		return timeout;
	}
	
	/**
	 * Returns the session key
	 * @return The session key
	 */
	public String getSessionKey(){
		return sessionkey;
	}
	
	/**
	 * Returns whether this session is valid or not, by:<br>
	 * <br>
	 * - Checking Timed out
	 * @return whether is it valid or not
	 */
	public boolean isValid(){
		Calendar cal = Calendar.getInstance();
		Calendar regcal = Calendar.getInstance();
		Calendar endcal = Calendar.getInstance();
		Date regdate;
		
		regdate = new Date(regtime);
		regcal.setTime(regdate);
		endcal.setTime(regdate);
		
		endcal.add(Calendar.MILLISECOND, timeout);
		
		return cal.before(endcal) && cal.after(regcal);
	}
	
	/**
	 * Compare with another session to see whether they are the same.
	 * @param anotherSession Another session to be compared
	 * @return whether they are the same or not.
	 */
	public boolean equals(Session anotherSession){
		boolean equals = false;
		
		equals = this.user.equals(anotherSession.user);
		equals = this.ip.equals(anotherSession.ip);
		equals = this.sessionkey.equals(anotherSession.sessionkey);
		equals = this.regtime == anotherSession.regtime;
		equals = this.timeout == anotherSession.timeout;
		
		return equals;
	}
	
}
