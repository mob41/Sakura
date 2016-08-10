package com.github.mob41.sakura.remo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BLRemote {
	
	public static List<String[]> remotes = new ArrayList<String[]>(500);
	public static List<String[]> buttons = new ArrayList<String[]>(500);
	
	public static String[] types = {"rf-light", "rf-light-single","tv-remote"};
	/*
	 Remote Code:
	 
	 <- Remote Name -> <- Remote UUID -> <- Remote Type ->
	 
	 Button Code:
	 
	 <- Target Remote UUID -> <- Button ID -> <- RM2 Data ->
	 */
	
	public static String[] buildRemoteCode(String remotename, String remoteuuid, String remotetype){
		String[] output = new String[3];
		output[0] = remotename;
		output[1] = remoteuuid;
		output[2] = remotetype;
		return output;
	}
	
	public static String[] buildButtonCode(String remoteuuid, String buttonid, String rm2data){
		String[] output = new String[3];
		output[0] = remoteuuid;
		output[1] = buttonid;
		output[2] = rm2data;
		return output;
	}
	
	public static int getButtonIndex(String remoteuuid, String buttonid){
		Object[] data;
		for (int i = 0; i < buttons.size(); i++){
			data = buttons.get(i);
			if (data[0].equals(remoteuuid) && data[1].equals(buttonid)){
				return i;
			}
		}
		return -1;
	}
	
	public static List<String[]> getAllButtons(String remoteuuid){
		List<String[]> output = new ArrayList<String[]>(50);
		String[] data;
		for (int i = 0; i < buttons.size(); i++){
			data = buttons.get(i);
			if (data[0].equals(remoteuuid)){
				output.add(data);
			}
		}
		return output;
	}
	
	public static int getRemoteIndex(String remoteuuid){
		Object[] data;
		for (int i = 0; i < remotes.size(); i++){
			data = remotes.get(i);
			if (data[1].equals(remoteuuid)){
				return i;
			}
		}
		return -1;
	}
	
	public static void deleteAllButtons(String remoteuuid){
		String[] data;
		for (int i = 0; i < buttons.size(); i++){
			data = buttons.get(i);
			if (data[1].equals(remoteuuid)){
				buttons.remove(i);
				i--;
			}
		}
	}
	
	public static void deleteRemote(String remoteuuid){
		int remoteindex = getRemoteIndex(remoteuuid);
		if (remoteindex != -1){
			remotes.remove(remoteindex);
			deleteAllButtons(remoteuuid);
		}
	}
	
	public static void load() throws IOException{
		File file = new File("ha_remotes.properties");
		if (!file.exists()){
			create();
			return;
		}
		Properties prop = new Properties();
		FileInputStream in = new FileInputStream(file);
		prop.load(in);
		int remotes = Integer.parseInt(prop.getProperty("remotes"));
		int buttons = Integer.parseInt(prop.getProperty("buttons"));
		int i;
		String[] build;
		BLRemote.remotes = new ArrayList<String[]>(500);
		for (i = 0; i < remotes; i++){
			build = new String[3];
			build[0] = prop.getProperty("remote" + i + "-name");
			build[1] = prop.getProperty("remote" + i + "-uuid");
			build[2] = prop.getProperty("remote" + i + "-type");
			BLRemote.remotes.add(build);
		}
		BLRemote.buttons = new ArrayList<String[]>(500);
		for (i = 0; i < buttons; i++){
			build = new String[3];
			build[0] = prop.getProperty("button" + i + "-remotename");
			build[1] = prop.getProperty("button" + i + "-id");
			build[2] = prop.getProperty("button" + i + "-data");
			BLRemote.buttons.add(build);
		}
	}
	
	public static void writeIn() throws IOException{
		File file = new File("ha_remotes.properties");
		if (!file.exists()){
			file.createNewFile();
			return;
		}
		Properties prop = new Properties();
		int i;
		String[] data;
		String[] button;
		prop.setProperty("remotes", Integer.toString(remotes.size()));
		for (i = 0; i < remotes.size(); i++){
			data = remotes.get(i);
			prop.setProperty("remote"+ i + "-name", data[0]);
			prop.setProperty("remote"+ i + "-uuid", data[1]);
			prop.setProperty("remote"+ i + "-type", data[2]);
		}
		prop.setProperty("buttons", Integer.toString(buttons.size()));
		for (i = 0; i < buttons.size(); i++){
			data = buttons.get(i);
			prop.setProperty("button"+ i + "-remotename", data[0]);
			prop.setProperty("button"+ i + "-id", data[1]);
			prop.setProperty("button"+ i + "-data", data[2]);
		}
		FileOutputStream out = new FileOutputStream(file);
		prop.store(out, "HA Remotes");
		out.flush();
		out.close();
	}
	
	public static void create() throws IOException{
		try {
			File file = new File("ha_remotes.properties");
			if (!file.exists()){
				file.createNewFile();
			}
			Properties prop = new Properties();
			prop.setProperty("remotes", "0");
			prop.setProperty("buttons", "0");
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, "HA Remotes");
			out.flush();
			out.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
