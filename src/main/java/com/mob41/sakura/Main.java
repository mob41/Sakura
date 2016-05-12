package com.mob41.sakura;

import org.json.JSONObject;

import com.mob41.sakura.plugin.PluginManager;
import com.mob41.sakura.plugin.exception.NoSuchPluginException;

public class Main {

	public static void main(String[] args) {
		
		//This is not really a MAIN function. Just a test with Sakura's functions
		System.out.println("Loading...");
		try {
			PluginManager.getPluginManager().loadAllPlugins();
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("Loaded.");
		JSONObject json = new JSONObject();
		json.put("ext", true);
		json.put("code", "WTS");
		try {
			System.out.println((JSONObject) PluginManager.getPluginManager().runPluginLifeCycle("Sakura-Plugin-HKOWeather", json));
		} catch (NoSuchPluginException e) {
			e.printStackTrace();
		}
	}

}
