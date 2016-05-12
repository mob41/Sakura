package com.mob41.sakura;

import org.json.JSONObject;

import com.mob41.sakura.plugin.PluginManager;
import com.mob41.sakura.plugin.exception.NoSuchPluginException;

public class Main {

	public static void main(String[] args) {
		
		//This is not really a MAIN function. Just a test with Sakura's functions
		try {
			PluginManager.getPluginManager().loadAllPlugins();
		} catch (Exception e){
			e.printStackTrace();
		}
		try {
			System.out.println(PluginManager.getPluginManager().runPluginLifeCycle("Sakura-Plugin-Example", null));
		} catch (NoSuchPluginException e) {
			e.printStackTrace();
		}
	}

}
