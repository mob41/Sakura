package com.github.mob41.sakura;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

public class Configuration {

	public static final String DATA_FILE_NAME = "configuration.json";
	
	private JSONObject json = null;
	
	public Configuration(){
		try {
			if (!loadFile()){
				System.err.println("Could not load file! Wrong JSON format. Using default configuration instead.");
			}
		} catch (IOException e){
			System.err.println("Could not create new file! Using default configuration instead.");
			e.printStackTrace();
		}
	}
	
	public boolean setProperty(String group, String property, String value){
		if (json.isNull(group) || json.getJSONObject(group).isNull("type") ||
				!json.getJSONObject(group).getString("type").equals("group") ||
				json.getJSONObject(group).isNull(property) ||
				!json.getJSONObject(group).getJSONObject(property).getString("type").equals("property")){
			return false;
		}
		json.getJSONObject(group).getJSONObject(property).put("value", value);
		return true;
	}
	
	public String getProperty(String group, String property){
		if (json.isNull(group) || json.getJSONObject(group).isNull("type") ||
				!json.getJSONObject(group).getString("type").equals("group") ||
				json.getJSONObject(group).isNull(property) ||
				!json.getJSONObject(group).getJSONObject(property).getString("type").equals("property")){
			return null;
		}
		return json.getJSONObject(group).getJSONObject(property).getString("value");
	}
	
	public void applyDefaultConfigruation(){
		
	}
	
	public boolean loadFile() throws IOException{
		File file = new File(DATA_FILE_NAME);
		if (!file.exists()){
			if (!file.createNewFile()){
				throw new IOException("Could not create new file.");
			}
		}
		FileInputStream in = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		String data = "";
		String line;
		
		while ((line = reader.readLine()) != null){
			data += line;
		}
		
		reader.close();
		in.close();
		
		try {
			json = new JSONObject(data);
		} catch (JSONException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
