package com.mob41.sakura.plugin.loginagent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mob41.sakura.hash.AES_SHA512;

public class LoginHandler {
	
	private static final String DATA_FILE_NAME = "users.upf";
	
	private final List<User> users;
	
	private final LoginAgentPlugin plug;
	
	public LoginHandler(LoginAgentPlugin plug, int maxusers) throws Exception{
		users = new ArrayList<User>(maxusers);
		this.plug = plug;
		loadFile();
	}
	
	protected boolean authenticate(String username, String password){
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
		
		FileInputStream in = plug.getDataFileInputStream(DATA_FILE_NAME);
		if (in == null){
			createFile();
			return;
		}
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
		FileOutputStream out = plug.getDataFileOutputStream(DATA_FILE_NAME);
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
