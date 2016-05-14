package com.mob41.sakura.plugin.loginagent;

import com.mob41.sakura.hash.AesUtil;

public class Session {

	private final String user;
	
	private final String ip;
	
	private final String sessionkey;
	
	private final long regtime;
	
	private final long timeout;
	
	/**
	 * Creates a new session.
	 * @param user Username
	 * @param ip IP Address
	 * @param regtime Register Time In MS
	 * @param timeout Timeout In MS
	 */
	public Session(String user, String ip, long regtime, long timeout){
		this.user = user;
		this.ip = ip;
		this.regtime = regtime;
		this.timeout = timeout;
		
		this.sessionkey = AesUtil.random(128/8);
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
