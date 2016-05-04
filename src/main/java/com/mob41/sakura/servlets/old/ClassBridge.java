package com.mob41.sakura.servlets.old;

import com.mob41.sakura.auth.SessionAuth;

/***
 * This is a bridge for functions to prevent insecure.
 * @author Anthony
 *
 */
public class ClassBridge {
	
	public static String encrypt(String string, String salt) throws Exception{
		return HashKey.encrypt(string, salt);
	}

	public static String decrypt(String string, String salt) throws Exception{
		return HashKey.decrypt(string, salt);
	}
	
	public static String getRandomSalt(){
		return HashKey.getRandomSalt();
	}
	
	public static String convertSessionKeyToUsername(String sessionkey){
		int sesindex = SessionAuth.sesthread.getSessionIndex(sessionkey);
		String[] sesarr = SessionAuth.sesthread.getSession(sesindex);
		return sesarr[1];
	}
}
