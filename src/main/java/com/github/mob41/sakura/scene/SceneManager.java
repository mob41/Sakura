package com.github.mob41.sakura.scene;

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

import com.github.mob41.sakura.action.Action;
import com.github.mob41.sakura.api.SakuraServer;

public class SceneManager {

	public static final String DATA_FILE_NAME = "scenes.json";
	
	private SceneProcessThread thread;
	
	private JSONObject json = null;
	
	private final SakuraServer srv;
	
	public SceneManager(SakuraServer srv){
		this.srv = srv;
		try {
			loadFile();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public boolean isSceneExist(String sceneName){
		return getSceneIndex(sceneName) != -1;
	}
	
	public int getSceneIndex(String sceneName){
		JSONArray arr = json.getJSONArray("scenes");
		JSONObject json;
		for (int i = 0; i < arr.length(); i++){
			json = arr.getJSONObject(i);
			if (json.getString("name").equals(sceneName)){
				return i;
			}
		}
		return -1;
	}
	
	public SceneProcessThread getProcessThread(){
		return thread;
	}
	
	public boolean runScene(String sceneName){
		int index = getSceneIndex(sceneName);
		
		if (index == -1){
			return false;
		}
		
		return runScene(index);
	}
	
	public boolean runScene(int index){
		Scene scene = getScene(index);
		
		if (thread != null){
			thread.shutdown();
			thread.interrupt();
		}
		
		thread = new SceneProcessThread(scene);
		
		thread.start();
		
		return true;
	}
	
	public boolean addScene(Scene scene){
		JSONArray arr = json.getJSONArray("scenes");
		
		JSONObject json;
		for (int i = 0; i < arr.length(); i++){
			json = arr.getJSONObject(i);
			if (json.getString("name").equals(scene.getName())){
				return false;
			}
		}
		
		arr.put(scene.toJSON());
		return true;
	}
	
	public boolean removeScene(String sceneName){
		int index = getSceneIndex(sceneName);
		
		if (index == -1){
			return false;
		}
		
		JSONArray arr = json.getJSONArray("scenes");
		
		arr.remove(index);
		return true;
	}
	
	public boolean removeScene(int index){
		JSONArray arr = json.getJSONArray("scenes");
		if (index < 0 || index >= arr.length()){
			return false;
		}
		
		arr.remove(index);
		return true;
	}
	
	public boolean updateScene(String sceneName, Scene scene){
		int index = getSceneIndex(sceneName);
		
		if (index == -1){
			return false;
		}
		
		json.getJSONArray("scenes").put(index, scene.toJSON());
		
		try {
			writeFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public Scene[] getScenes(){
		JSONArray arr = json.getJSONArray("scenes");
		Scene[] scenes = new Scene[arr.length()];
		for (int i = 0; i < arr.length(); i++){
			scenes[i] = getScene(i);
		}
		return scenes;
	}
	
	public Scene getScene(String sceneName){
		int index = getSceneIndex(sceneName);
		
		return index == -1 ? null : getScene(index);
	}
	
	public JSONObject getSceneJSON(String sceneName){
		int index = getSceneIndex(sceneName);
		
		return index == -1 ? null : json.getJSONArray("scenes").getJSONObject(index);
	}
	
	public Scene getScene(int index){
		JSONArray arr = json.getJSONArray("scenes");
		JSONObject sceneJson = arr.getJSONObject(index);
		JSONArray actionsArr = sceneJson.getJSONArray("actions");
		
		JSONObject actionJson;
		String type;
		JSONArray actionArgsArr;
		
		Action[] actions = new Action[actionsArr.length()];
		List<String[]> actionsArgs = new ArrayList<String[]>(100);
		String[] actionArgs;
		
		for (int i = 0; i < actions.length; i++){
			actionJson = actionsArr.getJSONObject(i);
			actionArgsArr = actionJson.getJSONArray("args");
			
			actionArgs = new String[actionArgsArr.length()];
			for (int j = 0; j < actionArgsArr.length(); j++){
				if (actionArgsArr.isNull(j)){
					actionArgs[j] = null;
					continue;
				}
				actionArgs[j] = actionArgsArr.getString(j);
			}
			actionsArgs.add(actionArgs);
			
			type = actionJson.getString("type");
			if (type.equals("system")){
				actions[i] = srv.getActionManager().getSystemAction(actionJson.getString("name"));
			} else if (type.equals("plugin")){
				actions[i] = srv.getActionManager().getPluginAction(actionJson.getString("pluginName"), actionJson.getString("name"));
			} else {
				srv.getErrorManager().report("Error: Wrong action type \"" + type + "\" in scene JSON on " + actionJson.getString("name") + ". Replaced with \"null\"");
				actions[i] = null;
			}
		}
		
		return new Scene(sceneJson.getString("name"), actions, actionsArgs);
	}
	
	public void loadFile() throws IOException{
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
			json = new JSONObject(data);
		} catch (JSONException e){
			return;
		}
	}
	
	public void writeFile() throws IOException{
		File file = new File(DATA_FILE_NAME);
		if (!file.exists() || json == null){
			if (!file.createNewFile()){
				throw new IOException("Unable to create new file!");
			}
			
			json = new JSONObject();
			json.put("scenes", new JSONArray());
		}
		FileOutputStream out = new FileOutputStream(file);
		PrintWriter writer = new PrintWriter(out, true);
		
		writer.println(json.toString(5));
		writer.close();
	}
	
	public void createFile() throws IOException{
		writeFile();
	}
}
