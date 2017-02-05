package com.github.mob41.sakura.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.mob41.sakura.api.SakuraServer;

public class PermManager {
	
	private static final String DATA_FILE_NAME = "perms.json";
	
	private static final String DEFAULT_GROUP_NAME = "default";
	
	private JSONObject perms;

	public PermManager(SakuraServer srv){
		try {
			loadFile();
		} catch (IOException e) {
			System.err.println("Error: Could not load/create permission file. Check stack trace.");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public String getDefaultGroup(){
		if (perms.isNull("default")){
			perms.put("default", DEFAULT_GROUP_NAME);
		}
		return perms.getString("default");
	}
	
	public void createUser(String username, String defgp){
		JSONObject userJson = new JSONObject();
		userJson.put("groups", new JSONArray().put(defgp));
		userJson.put("perms", new JSONArray());
		perms.getJSONObject("users").put(username, userJson);
		
		try {
			writeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createGroup(String groupName, PermNode[] nodes){
		JSONObject groupJson = new JSONObject();
		JSONArray nodesArr = new JSONArray();
		
		if (nodes != null){
			for (int i = 0; i < nodes.length; i++){
				nodesArr.put(nodes[i].getAlias());
			}
		}
		
		groupJson.put("nodes", nodesArr);
		perms.getJSONObject("groups").put(groupName, groupJson);
		
		try {
			writeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void allowUser(String username, String alias){
		JSONObject usersJson = perms.getJSONObject("users");
		
		if (usersJson.isNull(username)){
			String defgp = getDefaultGroup();
			if (defgp == null){
				return;
			}
			
			createUser(username, defgp);
			
			if (perms.getJSONObject("groups").isNull(defgp)){
				createGroup(defgp, null);
			}
		}
		
		JSONObject userJson = usersJson.getJSONObject(username);
		
		int index = -1;
		JSONArray userPerms = userJson.getJSONArray("perms");
		for (int i = 0; i < userPerms.length(); i++){
			if (userPerms.getString(i).equals(alias)){
				index = i;
				break;
			}
		}
		
		if (index == -1){
			userPerms.put(alias);
		}
		
		try {
			writeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disallowUser(String username, String alias){
		JSONObject usersJson = perms.getJSONObject("users");
		
		if (usersJson.isNull(username)){
			String defgp = getDefaultGroup();
			if (defgp == null){
				return;
			}
			
			createUser(username, defgp);
			
			if (perms.getJSONObject("groups").isNull(defgp)){
				createGroup(defgp, null);
			}
		}
		
		JSONObject userJson = usersJson.getJSONObject(username);
		
		int index = -1;
		JSONArray userPerms = userJson.getJSONArray("perms");
		for (int i = 0; i < userPerms.length(); i++){
			if (userPerms.getString(i).equals(alias)){
				index = i;
				break;
			}
		}
		
		if (index != -1){
			userPerms.remove(index);
		} else {
			userPerms.put("-" + alias);
		}
	}
	
	public boolean isUserPermitted(User user, String alias){
		return isUserPermitted(user.getUsername(), alias);
	}
	
	public boolean isUserPermitted(String username, String alias){
		JSONObject usersJson = perms.getJSONObject("users");
		
		//Create a new user and hook to the default group if not exist
		if (usersJson.isNull(username)){
			String defgp = getDefaultGroup();
			if (defgp == null){
				return false;
			}
			
			createUser(username, defgp);
			
			if (perms.getJSONObject("groups").isNull(defgp)){
				createGroup(defgp, null);
			}
			
			return isGroupPermitted(defgp, alias);
		}
		
		PermUser permUser = new PermUser(perms, username);
		
		return permUser.isPermitted(alias);
	}
	
	public PermUser getUser(String username){
		return new PermUser(perms, username);
	}
	
	public boolean isGroupPermitted(String groupName, String alias){
		return getPermGroup(groupName).isPermitted(alias);
	}
	
	public PermGroup getPermGroup(String groupName){
		return new PermGroup(perms, groupName);
	}
	
	public boolean isGroupExist(String gp){
		if (perms.isNull("groups")){
			return false;
		}
		return !perms.getJSONObject("groups").isNull(gp);
	}
	
	public boolean addPerm(String alias) throws IOException{
		return addPerm(alias, null);
	}
	
	public boolean addPerm(String alias, String comment) throws IOException{
		String[] sep = seperateAlias(alias);
		if (sep[sep.length - 1].equals("*")){
			return false;
		}
		
		JSONObject permsJson = perms.getJSONObject("perms");
		JSONObject subperm = null;
		for (int i = 0; i < sep.length; i++){
			if (subperm == null){
				if (permsJson.isNull(sep[i])){
					permsJson.put(sep[i], new JSONObject());
				}
				subperm = permsJson.getJSONObject(sep[i]);
			} else {
				if (subperm.isNull(sep[i])){
					subperm.put(sep[i], new JSONObject());
				}
				subperm = subperm.getJSONObject(sep[i]);
				
				if (i == sep.length - 1){
					subperm.put("comment", comment);
				}
			}
		}
		writeFile();
		return true;
	}
	
	public static String[] seperateAlias(String alias){
		List<String> strlist = new ArrayList<String>(20);
		int tmp = 0;
		String sep;
		for (int i = 0; i < alias.length(); i++){
			if (alias.charAt(i) == '.'){
				sep = alias.substring(tmp, i);
				strlist.add(sep);
				tmp = ++i;
			} else if (i == alias.length() - 1){
				sep = alias.substring(tmp, i + 1);
				strlist.add(sep);
			}
		}
		
		String[] strarr = new String[strlist.size()];
		for (int i = 0; i < strarr.length; i++){
			strarr[i] = strlist.get(i);
		}
		
		return strarr;
	}
	
	public static boolean inAlias(String alias, String subalias){
		String[] aarr = seperateAlias(alias);
		String[] saarr = seperateAlias(subalias);
		
		if (saarr.length < aarr.length){
			return false;
		}
		
		for (int i = 0; i < aarr.length; i++){
			if (!aarr[i].equals(saarr[i]) && i != aarr.length - 1){
				return false;
			}
		}
		return true;
	}
	
	protected void loadFile() throws IOException{
		if (perms != null){
			perms = null;
		}
		File file = new File(DATA_FILE_NAME);
		
		if (!file.exists()){
			createFile();
		}
		
		FileInputStream in = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		String data = "";
		String line;
		while ((line = reader.readLine()) != null){
			data += line;
		}
		
		reader.close();
		
		try {
			perms = new JSONObject(data);
		} catch (JSONException e){
			throw new IOException("Invalid permissions JSON: ", e);
		}
		
		if (perms.isNull("users") || perms.isNull("perms")){
			throw new IOException("Invalid permissions JSON: No \"users\" or \"perms\" object.");
		}
	}
	
	public void writeFile() throws IOException{
		File file = new File(DATA_FILE_NAME);
		
		if (!file.exists() || perms == null){
			perms = new JSONObject();
			perms.put("perms", new JSONObject());
			perms.put("users", new JSONObject());
			perms.put("groups", new JSONObject());
		}
		
		FileOutputStream out = new FileOutputStream(file);
		PrintWriter writer = new PrintWriter(out, true);
		writer.println(perms.toString(5));
		writer.close();
		
	}
	
	public void createFile() throws IOException{
		writeFile();
	}
}
