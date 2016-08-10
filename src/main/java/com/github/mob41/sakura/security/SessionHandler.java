package com.github.mob41.sakura.security;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A handler class to handle sessions
 * @author Anthony
 *
 */
public class SessionHandler {
	
	public static final int DEFAULT_TIMEOUT = 3600000;
	
	private final List<Session> sessions;
	
	private boolean allowSameIp = false;
	
	private boolean allowSameUser = false;
	
	private int timeout = DEFAULT_TIMEOUT;
	
	/**
	 * Creates a new <code>SessionHandler</code> to handle sessions
	 * @param maxusers Maximum users
	 */
	public SessionHandler(int maxusers){
		sessions = new ArrayList<Session>(maxusers);
	}
	
	/**
	 * Creates a new session into handler and returns the session key generated.<br>
	 * <br>
	 * If <code>allowSameIp</code> is <code>false</code>, the system will search
	 * for the IP in the exist sessions. If the IP was found in the exist sessions,
	 * the function will return <code>null</code><br>
	 * <br>
	 * If <code>allowSameUser</code> is <code>false</code>, the system will search
	 * for the user in the exist sessions. If the user was found in the exist sessions,
	 * the function will return <code>null</code><br>
	 * @param user A user
	 * @param ip Internet IP Address
	 * @param regtime Register Time in ms
	 * @param timeout Timeout in ms
	 * @return The session key generated. Or <code>null</code>, see details above.
	 */
	public String newSession(User user, InetAddress ip, long regtime, int timeout){
		
		if (!allowSameIp && isIPExist(ip)){
			return null;
		}
		
		if (!allowSameUser && isUserExist(user)){
			return null;
		}
		Session session = new Session(user, ip, regtime, timeout);
		sessions.add(session);
		return session.getSessionKey();
	}
	
	/**
	 * Creates a new session into handler and returns the session key generated.<br>
	 * <br>
	 * If <code>allowSameIp</code> is <code>false</code>, the system will search
	 * for the IP in the exist sessions. If the IP was found in the exist sessions,
	 * the function will return <code>null</code><br>
	 * <br>
	 * If <code>allowSameUser</code> is <code>false</code>, the system will search
	 * for the user in the exist sessions. If the user was found in the exist sessions,
	 * the function will return <code>null</code><br>
	 * @param user A user
	 * @param ip Internet IP Address
	 * @param timeout Timeout in ms
	 * @return The session key generated. Or <code>null</code>, see details above.
	 */
	public String newSession(User user, InetAddress ip, int timeout){
		return newSession(user, ip, Calendar.getInstance().getTimeInMillis(), timeout);
	}
	
	/**
	 * Creates a new session into handler and returns the session key generated.<br>
	 * <br>
	 * If <code>allowSameIp</code> is <code>false</code>, the system will search
	 * for the IP in the exist sessions. If the IP was found in the exist sessions,
	 * the function will return <code>null</code><br>
	 * <br>
	 * If <code>allowSameUser</code> is <code>false</code>, the system will search
	 * for the user in the exist sessions. If the user was found in the exist sessions,
	 * the function will return <code>null</code><br>
	 * @param user A user
	 * @param ip Internet IP Address
	 * @return The session key generated. Or <code>null</code>, see details above.
	 */
	public String newSession(User user, InetAddress ip){
		return newSession(user, ip, Calendar.getInstance().getTimeInMillis(), timeout);
	}
	
	/**
	 * Removes the session using the index
	 * @param index The session index
	 * @return whether the session is removed or not.
	 */
	public boolean removeSession(int index){
		try {
			sessions.remove(index);
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	/**
	 * Check all the session whether the session is valid or not<br>
	 * If it is invalid, it will be removed automatically.
	 */
	public void checkAllSession(){
		for (int i = 0; i < sessions.size(); i++){
			if (!sessions.get(i).isValid()){
				sessions.remove(i);
			}
		}
	}
	
	/**
	 * Returns whether the session valid or not, by:<br>
	 * <br>
	 * - Call <code>checkAllSession()</code><br>
	 * - The invalid sessions will be removed<br>
	 * - Search whether the session exist or not
	 * @param sessionkey The session key to be checked
	 * @return whether the session valid or not
	 */
	public boolean isSessionValid(String sessionkey){
		checkAllSession();
		return getSessionIndex(sessionkey) != -1;
	}
	
	/**
	 * Returns whether the session exist or not, but, not showing whether the session is valid or not
	 * @param sessionkey The session key to be searched
	 * @return whether the session exist or not
	 */
	public boolean isSessionExist(String sessionkey){
		return getSessionIndex(sessionkey) != -1;
	}
	
	/**
	 * Returns the index of the session and returns <code>-1</code> if the session does not exist
	 * @param sessionkey The session key to be searched
	 * @return the index
	 */
	public int getSessionIndex(String sessionkey){
		for (int i = 0; i < sessions.size(); i++){
			if (sessions.get(i).getSessionKey().equals(sessionkey)){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns whether the IP exist or not
	 * @param ip Internet IP Address
	 * @return whether the IP exist or not
	 */
	public boolean isIPExist(InetAddress ip){
		return getIPIndex(ip) != -1;
	}
	
	/**
	 * Returns the exist oldest IP index
	 * @param ip Internet IP Address
	 * @return the exist oldest IP index
	 */
	public int getIPIndex(InetAddress ip){
		for (int i = 0; i < sessions.size(); i++){
			if (sessions.get(i).getIP().getHostAddress().equals(ip.getHostAddress())){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns whether the user exist or not
	 * @param user The user to be searched
	 * @return whether the user exist or not
	 */
	public boolean isUserExist(User user){
		return getUserIndex(user) != -1;
	}
	
	/**
	 * Returns the exist oldest user index
	 * @param user User instance
	 * @return the exist oldest user index
	 */
	public int getUserIndex(User user){
		for (int i = 0; i < sessions.size(); i++){
			if (sessions.get(i).getUser().equals(user)){
				return i;
			}
		}
		return -1;
	}
}
