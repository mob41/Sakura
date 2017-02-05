package com.github.mob41.sakura.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.mob41.sakura.action.sys.AddAlertAction;
import com.github.mob41.sakura.action.sys.AddNotificationAction;
import com.github.mob41.sakura.action.sys.DelayAction;
import com.github.mob41.sakura.action.sys.HaltSystem;
import com.github.mob41.sakura.action.sys.ApplianceAction;
import com.github.mob41.sakura.action.sys.RestartSystem;
import com.github.mob41.sakura.api.SakuraServer;
import com.github.mob41.sakura.plugin.PluginDescription;

public class ActionManager {
	
	public static final String SYSTEM_ACTIONS = "System";
	
	private List<Action> sysActions;
	
	private Map<String, List<Action>> pluginActions;
	
	private final SakuraServer srv;
	
	public ActionManager(SakuraServer srv){
		sysActions = new ArrayList<Action>(100);
		pluginActions = new HashMap<String, List<Action>>(100);
		this.srv = srv;
		
		registerSystemAction(new HaltSystem(srv));
		registerSystemAction(new RestartSystem(srv));
		registerSystemAction(new AddAlertAction(srv));
		registerSystemAction(new AddNotificationAction(srv));
		registerSystemAction(new DelayAction(srv));
		registerSystemAction(new ApplianceAction(srv));
	}
	
	public Action getSystemAction(String actionName){
		int index = getSystemActionIndex(actionName);
		
		if (index == -1){
			return null;
		}
		
		return sysActions.get(index);
	}
	
	public Action getSystemAction(int index){
		return sysActions.get(index);
	}
	
	public int getSystemActionIndex(String actionName){
		for (int i = 0; i < sysActions.size(); i++){
			if (sysActions.get(i).getName().equals(actionName)){
				return i;
			}
		}
		return -1;
	}
	
	public Action getPluginAction(String pluginName, String actionName){
		if (!pluginActions.containsKey(pluginName)){
			return null;
		}
		
		int index = getPluginActionIndex(pluginName, actionName);
		
		if (index == -1){
			return null;
		}
		
		return pluginActions.get(pluginName).get(index);
	}
	
	public int getPluginActionIndex(String pluginName, String actionName){
		if (!pluginActions.containsKey(pluginName)){
			return -1;
		}
		
		List<Action> actions = pluginActions.get(pluginName);
		for (int i = 0; i < actions.size(); i++){
			if (actions.get(i).getName().equals(actionName)){
				return i;
			}
		}
		return -1;
	}
	
	public Map<String, List<Action>> getAllActions(){
		Map<String, List<Action>> map = new HashMap<String, List<Action>>(100);
		map.put(SYSTEM_ACTIONS, sysActions);
		
		String name;
		Iterator<String> it = pluginActions.keySet().iterator();
		while (it.hasNext()){
			name = it.next();
			map.put(name, pluginActions.get(name));
		}
		
		return map;
	}
	
	public List<Action> getAllSystemActions(){
		return sysActions;
	}
	
	public Map<String, List<Action>> getAllPluginActions(){
		return pluginActions;
	}
	
	public String[] getActionsPluginNames(){
		List<String> str = new ArrayList<String>(100);
		Iterator<String> it = pluginActions.keySet().iterator();
		while(it.hasNext()){
			str.add(it.next());
		}
		
		String[] arr = new String[str.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = str.get(i);
		}
		return arr;
	}
	
	public List<Action> getPluginActions(String pluginName){
		if (!pluginActions.containsKey(pluginName)){
			return null;
		}
		
		return pluginActions.get(pluginName);
	}
	
	private boolean registerSystemAction(Action action){
		return sysActions.add(action);
	}

	public boolean registerAction(PluginDescription desc, Action action){
		if (desc.getName().equals(SYSTEM_ACTIONS)){
			System.err.println("ActionManager: Error: Action registration failed. Plugin name cannot be \"" + SYSTEM_ACTIONS + "\" reserved names.");
			return false;
		}
		
		List<Action> actions = null;
		if (!pluginActions.containsKey(desc.getName())){
			actions = new ArrayList<Action>(100);
		} else {
			actions = pluginActions.get(desc.getName());
		}
		
		boolean added = actions.add(action);
		
		if (!added){
			return false;
		} else {
			pluginActions.put(desc.getName(), actions);
		}
		
		return true;
	}
	
	public boolean unregisterSystemAction(String systemActionName){
		int index = getSystemActionIndex(systemActionName);
		
		if (index == -1){
			return false;
		}
		
		sysActions.remove(index);
		return true;
	}
	
	public boolean unregisterAction(PluginDescription desc, int index){
		if (desc.getName().equals(SYSTEM_ACTIONS)){
			System.err.println("ActionManager: Error: Action unregistration failed. Plugin name cannot be \"" + SYSTEM_ACTIONS + "\" reserved names.");
			return false;
		}
		
		List<Action> actions = null;
		if (!pluginActions.containsKey(desc.getName())){
			actions = new ArrayList<Action>(100);
		} else {
			actions = pluginActions.get(desc.getName());
		}
		
		actions.remove(index);
		
		return true;
	}
}
