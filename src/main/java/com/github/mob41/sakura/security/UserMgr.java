package com.github.mob41.sakura.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.table.DefaultTableModel;

import org.json.JSONObject;

import com.github.mob41.sakura.hash.AES_SHA512;
import com.github.mob41.sakura.plugin.Plugin;

public class UserMgr {
	
	private static final String DATA_FILE_NAME = "users.upf";
	
	private final List<User> users;
	
	private static final SessionHandler sessionHandler = new SessionHandler(30);
	
	private static final UserMgr userMgr = new UserMgr(30);
	
	public static final int STATUS_NO_PERMISSION = 0;
	
	public static final int STATUS_INVALID_USER_PWD = 1;
	
	public UserMgr(int maxusers){
		users = new ArrayList<User>(maxusers);
		try {
			loadFile();
		} catch (Exception e) {
			System.err.println("Error: Could not load/create login file. Check stack trace.");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static UserMgr getInstance(){
		return userMgr;
	}
	
	public static SessionHandler getSessionHandler(){
		return sessionHandler;
	}
	
	public boolean authenticate(String username, String password){
		User user = getUserByUsername(username);
		if (user == null){
			return false;
		}
		
		String hash = null;
		try {
			hash = AES_SHA512.encrypt(password, user.getSalt());
		} catch (Exception e) {
			return false;
		}
		
		return hash.equals(user.getPassHash());
	}
	
	protected boolean addUser(String username, String password){
		if (isUsernameExist(username)){
			return false;
		}
		
		User user = null;
		try {
			user = new User(username, password);
		} catch (Exception e) {
			return false;
		}
		
		users.add(user);
		try {
			writeFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	protected boolean removeUser(int index){
		try {
			users.remove(index);
			writeFile();
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	public List<User> getUsersList(){
		return users;
	}
	
	protected boolean isUserExist(User user){
		return getUserIndex(user) != -1;
	}
	
	protected boolean isUsernameExist(String username){
		return getUsernameIndex(username) != -1;
	}
	
	protected User getUserByUsername(String username){
		int index = getUsernameIndex(username);
		if (index == -1){
			return null;
		}
		return users.get(index);
	}
	
	protected int getUserIndex(User user){
		for (int i = 0; i < users.size(); i++){
			if (users.get(i).equals(user)){
				return i;
			}
		}
		return -1;
	}
	
	protected int getUsernameIndex(String username){
		for (int i = 0; i < users.size(); i++){
			if (users.get(i).getUsername().equals(username)){
				return i;
			}
		}
		return -1;
	}
	
	protected void loadFile() throws Exception{
		if (users != null){
			users.clear();
		}
		File file = new File(DATA_FILE_NAME);
		
		if (!file.exists()){
			createFile();
		}
		
		FileInputStream in = new FileInputStream(file);
		Properties prop = new Properties();
		prop.load(in);
		int users = Integer.parseInt(prop.getProperty("users"));
		User user;
		String key;
		String usr;
		String hash;
		String salt;
		for (int i = 0; i < users; i++){
			key = "user" + i;
			usr = prop.getProperty(key + "-usr");
			hash = prop.getProperty(key + "-hash");
			salt = prop.getProperty(key + "-salt");
			user = new User(usr, hash, salt);
			this.users.add(user);
		}
		in.close();
		
	}
	
	protected void writeFile() throws IOException{
		File file = new File(DATA_FILE_NAME);
		
		if (!file.exists()){
			createFile();
		}
		
		FileOutputStream out = new FileOutputStream(file);
		Properties prop = new Properties();
		prop.setProperty("users", Integer.toString(users.size()));
		String key;
		User user;
		for (int i = 0; i < users.size(); i++){
			key = "user" + i;
			user = users.get(i);
			prop.setProperty(key + "-usr", user.getUsername());
			prop.setProperty(key + "-hash", user.getPassHash());
			prop.setProperty(key + "-salt", user.getSalt());
		}
		prop.store(out, "SakuraSys Save");
		out.flush();
		out.close();
		out.flush();
		out.close();
	}
	
	public void createFile(){
		addUser("admin", "admin");
	}
	
}
