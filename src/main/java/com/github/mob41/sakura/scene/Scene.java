package com.github.mob41.sakura.scene;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.mob41.sakura.action.Action;
import com.github.mob41.sakura.plugin.PluginAction;

public class Scene {
	
	private String sceneName;
	
	private List<String[]> actionArgs;
	
	private List<Action> actions;
	
	public Scene(String sceneName, Action[] actions, List<String[]> actionArgs){
		this.sceneName = sceneName;
		this.actions = new ArrayList<Action>(100);
		if (actions != null){
			for (int i = 0; i < actions.length; i++){
				this.actions.add(actions[i]);
			}
		}
		this.actionArgs = actionArgs;
	}
	
	public Scene(String sceneName){
		this(sceneName, new Action[0], new ArrayList<String[]>(100));
	}
	
	public void setName(String sceneName){
		this.sceneName = sceneName;
	}
	
	public String getName(){
		return sceneName;
	}
	
	public void setActions(List<Action> actions){
		this.actions = actions;
	}
	
	public void addAction(Action action){
		addAction(action, null);
	}
	
	public void addAction(Action action, String[] args){
		if (args == null){
			args = new String[0];
		}
		
		actions.add(action);
		actionArgs.add(args);
	}
	
	public void addAction(int index, Action action) throws ArrayIndexOutOfBoundsException{
		addAction(index, action, null);
	}
	
	public void addAction(int index, Action action, String[] args) throws ArrayIndexOutOfBoundsException{
		if (args == null){
			args = new String[0];
		}
		
		if (index >= actions.size()){
			actions.add(action);
			actionArgs.add(args);
		} else {
			actions.add(index, action);
			actionArgs.add(index, args);
		}
	}
	
	public void removeAction(int index){
		actions.remove(index);
		actionArgs.remove(index);
	}
	
	public Action[] getActions(){
		Action[] arr = new Action[actions.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = actions.get(i);
		}
		return arr;
	}
	
	public void setActionArgs(String actionName, String[] args){
		int index = getActionIndex(actionName);
		
		if (index == -1){
			return;
		}
		
		setActionArgs(index, args);
	}
	
	public void setActionArgs(int index, String[] args) throws IndexOutOfBoundsException{
		actionArgs.set(index, args);
	}
	
	public void setActionArgs(List<String[]> actionArgs){
		this.actionArgs = actionArgs;
	}
	
	public String[] getActionArgs(int index){
		return actionArgs.get(index);
	}
	
	public List<String[]> getActionArgs(){
		return actionArgs;
	}
	
	public int getActionIndex(String actionName){
		for (int i = 0; i < actions.size(); i++){
			if (actions.get(i).getName().equals(actionName)){
				return i;
			}
		}
		return -1;
	}
	
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		JSONArray actionsArr = new JSONArray();
		JSONArray actionsArgsArr;
		JSONObject actionJson;
		Action action;
		for (int i = 0; i < actions.size(); i++){
			action = actions.get(i);
			actionJson = new JSONObject();
			actionsArgsArr = new JSONArray();
			if (action instanceof PluginAction){
				actionJson.put("type", "plugin");
				actionJson.put("pluginName", ((PluginAction) action).getPluginName());
				actionJson.put("name", action.getName());
				
			} else {
				actionJson.put("type", "system");
				actionJson.put("name", action.getName());
			}
			
			for (Object arg : actionArgs.get(i)){
				actionsArgsArr.put(arg);
			}
			actionJson.put("args", actionsArgsArr);
			actionsArr.put(actionJson);
		}
		json.put("actions", actionsArr);
		json.put("name", sceneName);
		return json;
	}
	
	public String toString(){
		return toJSON().toString();
	}
}
