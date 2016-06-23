package com.mob41.sakura;

import org.json.JSONObject;

import com.mob41.sakura.plugin.PluginDescription;
import com.mob41.sakura.plugin.PluginManager;
import com.mob41.sakura.plugin.exception.NoSuchPluginException;
import com.mob41.sakura.plugin.loginagent.LoginAgentPlugin;
import com.mob41.sakura.server.Jetty;
import com.mob41.sakura.servlets.AccessTokenSession;

public class Main {

	public static void main(String[] args) throws Exception {
		
		//This is not really a MAIN function. Just a test with Sakura's functions
		
		PluginManager.getPluginManager().loadAllPlugins();
		AccessTokenSession.runThread(50);
		
		
		PluginDescription desc = new PluginDescription("LoginAgent", "A login security system on Sakura", "SNAPSHOT", "mob41", "com.mob41.sakura.plugin.LoginAgentPlugin", "");
		
		LoginAgentPlugin loginAgPlug = new LoginAgentPlugin(desc);
		PluginManager.getPluginManager().addPlugin(loginAgPlug, desc);
		
		Jetty.start();
	}

}
