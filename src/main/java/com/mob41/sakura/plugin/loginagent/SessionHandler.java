package com.mob41.sakura.plugin.loginagent;

import java.util.ArrayList;
import java.util.List;

public class SessionHandler {
	
	public static final int DEFAULT_TIMEOUT = 36
	
	public final List<Session> sessions;
	
	public boolean allowSameIp = false;
	
	public boolean allowSameUser = false;
	
	public int timeout = DEFAULT_TIMEOUT;
	
	public SessionHandler(int maxusers){
		sessions = new ArrayList<Session>(maxusers);
	}
	
	
}
