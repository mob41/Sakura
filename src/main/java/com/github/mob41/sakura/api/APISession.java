package com.github.mob41.sakura.api;

import java.util.Calendar;

import com.github.mob41.sakura.hash.AesUtil;

public class APISession {

	private final String uid;
	
	private final String iv;
	
	private final String salt;
	
	private final String pass;
	
	private final String para_iv;
	
	private final String para_salt;
	
	private final String para_pass;
	
	private final String para_data;
	
	private final String para_session_uid;
	
	private final int timeoutMs;
	
	private Calendar timeoutCal;
	
	public APISession(int timeoutMs) {
		uid = AesUtil.random(128/8);
		iv = AesUtil.random(128/8);
		salt = AesUtil.random(128/8);
		pass = AesUtil.random(2048/8);
		
		para_iv = AesUtil.random(128/8);
		para_salt = AesUtil.random(128/8);
		para_pass = AesUtil.random(128/8);
		
		para_data = AesUtil.random(128/8);
		para_session_uid = AesUtil.random(128/8);
		
		this.timeoutMs = timeoutMs;
		
		timeoutCal = Calendar.getInstance();
		timeoutCal.add(Calendar.MILLISECOND, timeoutMs);
	}
	
	public String getUid(){
		return uid;
	}
	
	public String getEncIv(){
		return iv;
	}
	
	public String getEncSalt(){
		return salt;
	}
	
	public String getEncPass(){
		return pass;
	}
	
	public String getEncIVPara(){
		return para_iv;
	}
	
	public String getEncSaltPara(){
		return para_salt;
	}
	
	public String getEncPassPara(){
		return para_pass;
	}
	
	public String getDataPara(){
		return para_data;
	}
	
	public String getUidPara(){
		return para_session_uid;
	}
	
	public Calendar getTimeoutCal(){
		return timeoutCal;
	}
	
	public void resetTimeout(){
		timeoutCal = Calendar.getInstance();
		timeoutCal.add(Calendar.MILLISECOND, timeoutMs);
	}
	
	public boolean isTimedOut(){
		return Calendar.getInstance().after(timeoutCal);
	}

}
