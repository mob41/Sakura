package com.mob41.sakura.plugin.loginagent;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.mob41.sakura.plugin.Plugin;

public class LoginAgentPlugin extends Plugin{
	
	private JSONObject jsonData;
	
	public LoginAgentPlugin(){
		System.out.println("Created");
	}

	@Override
	public void onCallPlugin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPluginReceiveData(Object data) {
		jsonData = (JSONObject) data;
	}

	@Override
	public Object onPluginSendData() {
		if (jsonData == null){
			return null;
		}
		
		String username = jsonData.getString("user");
		String password = jsonData.getString("pwd");
		
		
		return null;
	}

	@Override
	public void onEndPlugin() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onAccessPlugins(HttpServletRequest request, HttpServletResponse response){
		String sessionkey = request.getParameter("sessionkey");
		
		System.err.println("LoginAgent: Reject!");
		JSONObject json = new JSONObject();
		json.put("status", -1);
		json.put("response", "Your request is rejected");
		try {
			response.getWriter().println(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	
	@Override
	public boolean onClientConnectAPI(HttpServletRequest request, HttpServletResponse response){
		System.err.println("LoginAgent: Reject connection!");
		JSONObject json = new JSONObject();
		json.put("status", -1);
		json.put("response", "Your request is rejected");
		try {
			response.getWriter().println(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
