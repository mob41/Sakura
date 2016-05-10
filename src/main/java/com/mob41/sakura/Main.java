package com.mob41.sakura;

import org.json.JSONObject;

import com.mob41.sakura.plugin.PluginManager;

public class Main {

	public static void main(String[] args) {
		System.out.println("Loading...");
		try {
			PluginManager.getPluginManager().loadAllPlugins();
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("Loaded.");
		JSONObject json = new JSONObject();
		json.put("ext", false);
		System.out.println((JSONObject) PluginManager.getPluginManager().runPluginLifeCycle("Sakura-Plugin-HKOWeather", json));
	}

}
