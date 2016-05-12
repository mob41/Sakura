package com.mob41.sakura.plugin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginAgentPlugin extends Plugin{

	@Override
	public void onCallPlugin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPluginReceiveData(Object data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object onPluginSendData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onEndPlugin() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onAccessPlugins(HttpServletRequest request, HttpServletResponse response){
		return true;
		
	}

}
