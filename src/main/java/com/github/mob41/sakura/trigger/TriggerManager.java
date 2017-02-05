package com.github.mob41.sakura.trigger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.scene.Scene;

public class TriggerManager {
	
	private static final String DATA_FILE_NAME = "triggers.json";

	private final List<Trigger> regTriggers;
	
	private final Map<String, TriggerAssign> runningTriggers;
	
	private final TriggerThread thread;
	
	private final SakuraServer srv;
	
	public TriggerManager(SakuraServer srv){
		regTriggers = new ArrayList<Trigger>(100);
		runningTriggers = new LinkedHashMap<String, TriggerAssign>(100);
		
		loadFile();
		
		this.srv = srv;
		
		thread = new TriggerThread(this);
		thread.start();
	}
	
	public SakuraServer getServer(){
		return srv;
	}
	
	public List<Trigger> getAllTriggers(){
		return regTriggers;
	}
	
	public final Map<String, TriggerAssign> getRunningTriggers(){
		return runningTriggers;
	}
	
	public Trigger getRegisteredTrigger(String name){
		int index = getRegisteredTriggerIndex(name);
		
		return index == -1 ? null : regTriggers.get(index);
	}
	
	public void addTrigger(String assignName, Trigger tr, Scene scene){
		addTrigger(assignName, tr, scene.getName());
	}
	
	public void addTrigger(String assignName, Trigger tr, String sceneName){
		addTrigger(assignName, tr.getName(), sceneName);
	}
	
	public void addTrigger(String assignName, String triggerName, String sceneName){
		runningTriggers.put(assignName, new TriggerAssign(triggerName, sceneName));
	}
	
	public void removeAssignment(String assignName){
		runningTriggers.remove(assignName);
	}
	
	public boolean registerTrigger(Trigger trigger){
		return !isTriggerRegistered(trigger.getName()) &&
				regTriggers.add(trigger);
	}
	
	public void unregisterTrigger(int index){
		regTriggers.remove(index);
	}
	
	public boolean isAssignNameAssigned(String assignName){
		return runningTriggers.containsKey(assignName);
	}
	
	public boolean isTriggerRegistered(String name){
		return getRegisteredTriggerIndex(name) != -1;
	}
	
	public int getRegisteredTriggerIndex(String name){
		for (int i = 0; i < regTriggers.size(); i++){
			if (regTriggers.get(i).getName().equals(name)){
				return i;
			}
		}
		return -1;
	}
	
	public void loadFile(){
		try {
			File file = new File(DATA_FILE_NAME);
			if (!file.exists()){
				writeFile();
			}
			FileInputStream in = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String data = "";
			String line;
			while ((line = reader.readLine()) != null){
				data += line;
			}
			
			JSONObject json = new JSONObject(data);
			
			Iterator<String> it = json.keys();
			JSONArray arr;
			JSONObject obj;
			Object[] paraarr;
			String triggerName;
			String key;
			String sceneName;
			while (it.hasNext()){
				key = it.next();
				obj = json.getJSONObject(key);
				
				if (obj.isNull("scene")){
					//TODO Hook this error to error handling
					System.err.println("!!TODO: Hook this error to error handling");
					System.err.println("Error: Wrong JSON format: No \"scene\" JSON string found. Skipping \"" + key + "\"");
					continue;
				}
				
				if (obj.isNull("trigger")){
					//TODO Hook this error to error handling
					System.err.println("!!TODO: Hook this error to error handling");
					System.err.println("Error: Wrong JSON format: No \"trigger\" JSON string found. Skipping \"" + key + "\"");
					continue;
				}
				
				triggerName = obj.getString("trigger");
				sceneName = obj.getString("scene");
				
				arr = obj.isNull("parameters") ? new JSONArray() : obj.getJSONArray("parameters");
				
				paraarr = new Object[arr.length()];
				
				for (int i = 0; i < arr.length(); i++){
					paraarr[i] = arr.get(i);
				}
				
				runningTriggers.put(key, new TriggerAssign(triggerName, sceneName, paraarr));
			}
			
			reader.close();
			in.close();
		} catch (IOException e){
			//TODO Hook into error handling system
			e.printStackTrace();
		} catch (JSONException e){
			e.printStackTrace();
		}
	}

	public void writeFile() {
		try {
			File file = new File(DATA_FILE_NAME);
			if (!file.exists() && !file.createNewFile()){
				throw new IOException("Could not create new file \"" + DATA_FILE_NAME +"\"");
			}
			
			JSONObject json = new JSONObject();
			
			Iterator<String> it = runningTriggers.keySet().iterator();
			JSONObject objjson;
			TriggerAssign assignment;
			String key;
			while (it.hasNext()){
				key = it.next();
				assignment = runningTriggers.get(key);
				objjson = new JSONObject();
				
				objjson.put("trigger", assignment.getTriggerName());
				objjson.put("scene", assignment.getSceneName());
				objjson.put("parameters", assignment.getParameters());
				
				json.put(key, objjson);
			}
			
			FileOutputStream out = new FileOutputStream(file);
			PrintWriter writer = new PrintWriter(out, true);
			
			writer.println(json.toString(5));
			
			writer.close();
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}
}
