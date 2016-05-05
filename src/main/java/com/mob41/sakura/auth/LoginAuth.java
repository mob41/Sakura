package com.mob41.sakura.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.mob41.sakura.hash.AES_SHA512;

/***
 * <h1>Login Authentication System (Module)</h1>
 * <br>
 * A HashLogin system to be used for security.
 * @author Anthony Law
 *
 */
public class LoginAuth {
	private List<JSONObject> usrpwd;
	private static final Logger logger = LogManager.getLogger("LoginAuth");
	private final int maxusers;
	
	public LoginAuth(int maxusers){
		this.maxusers = maxusers;
		logger.info("Preparing for authentication...");
		logger.info("Loading users from file...");
		load();
		logger.info("Done.");
	}
	
	public boolean auth(String usr, String pwd){
		int i;
		logger.info("Authenticating user...");
		JSONObject urpdkey;
		String pwdhash;
		for (i = 0; i < usrpwd.size(); i++){
			urpdkey = usrpwd.get(i);
			if (urpdkey.getString("username").equals(usr)){
				try {
					pwdhash = AES_SHA512.encrypt(pwd, urpdkey.getString("salt"));
				} catch (Exception e){
					logger.error("Wrong password, could not encrypt hash.");
					return false;
				}
				if (urpdkey.getString("hash").equals(pwdhash)){
					logger.info("The user is authenticated successfully.");
					return true;
				} else {
					logger.error("Wrong password.");
					return false;
				}
			}
		}
		logger.error("The user does not exist.");
		return false;
	}
	
	/*
	 * JSONObject:
	 * 
	 * {salt}, {username}, {hash} (SHA512 X AES)
	 * 
	 */
	
	public void load(){
		try {
			File file = new File("secret.hap");
			if (!file.exists()){
				create();
				return;
			}
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(file);
			prop.load(in);
			int users = Integer.parseInt(prop.getProperty("users"));
			usrpwd = new ArrayList<JSONObject>(maxusers);
			String key;
			for (int i = 0; i < users; i++){
				key = "hap" + i;
				usrpwd.add(new JSONObject(prop.getProperty(key)));
			}
			in.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void create(){
		try {
			File file = new File("secret.hap");
			if (!file.exists()){
				file.createNewFile();
			}
			Properties prop = new Properties();
			prop.setProperty("users", "0");
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, "HAP");
			out.flush();
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void writeIn(){
		try {
			Object[] data = usrpwd.toArray();
			File file = new File("secret.hap");
			if (file.exists()){
				file.delete();
			}
			Properties prop = new Properties();
			int amount = data.length;
			int i;
			String key;
			prop.setProperty("users", Integer.toString(amount));
			for (i = 0; i < amount; i++){
				key = "hap" + i;
				prop.setProperty(key, (String) data[i]);
			}
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, "HAP");
			out.flush();
			out.close();
		} catch (IOException e){
			
		}
	}

	public int getUserCount(){
		return usrpwd.size();
	}
	
	public void removeUser(String username){
		int index = getUserIndex(username);
		usrpwd.remove(index);
	}
	
	public void removeUser(int index){
		usrpwd.remove(index);
	}
	
	public int getUserIndex(String username){
		int i;
		JSONObject urpd;
		for (i = 0; i < usrpwd.size(); i++){
			urpd = usrpwd.get(i);
			if (urpd.getString("username").equals(username)){
				return i;
			}
		}
		return -1;
	}
	
	public boolean addUser(String username, String password){
		//Check is that user exists
		if (getUserIndex(username) != -1){
			return false;	
		}
		
		//Else, start encryption and save to file
		try {
			String salt = AES_SHA512.getRandomSalt();
			String pwdhash = AES_SHA512.encrypt(password, salt);
			JSONObject data = new JSONObject();
			data.put("username", username);
			data.put("hash", pwdhash);
			data.put("salt", salt);
			usrpwd.add(data);
			writeIn();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addUser(String salt, String username, String pwdhash){
		//Check is that user exists
		if (getUserIndex(username) != -1){
			return false;
		}
		
		//Else, start encryption and save to file
		try {
			JSONObject data = new JSONObject();
			data.put("username", username);
			data.put("salt", salt);
			data.put("hash", pwdhash);
			usrpwd.add(data);
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
