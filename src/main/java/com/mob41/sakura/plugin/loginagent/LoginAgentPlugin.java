package com.mob41.sakura.plugin.loginagent;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.mob41.sakura.plugin.Plugin;
import com.mob41.sakura.plugin.PluginDescription;

public class LoginAgentPlugin extends Plugin{
	
	private final SessionHandler sessionHandler;
	
	private final LoginHandler loginHandler;
	
	private JSONObject jsonData;
	
	public LoginAgentPlugin(PluginDescription pluginDesc) throws Exception{
		super(pluginDesc);
		sessionHandler = new SessionHandler(30);
		loginHandler = new LoginHandler(this, 30);
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
	public JSONObject onAccessPlugins(HttpServletRequest request, HttpServletResponse response) throws IOException{
		JSONObject json = new JSONObject();
		json.put("generated", Calendar.getInstance().getTimeInMillis());
		
		if (request.getParameter("name") != null && request.getParameter("name").equals("login")){
			String username = request.getParameter("user");
			String password = request.getParameter("pwd");
			if (username == null || password == null){
				json.put("code", "must-not-blank");
				json.put("response", "Must not be blank");
				json.put("status", -1);
				
				setImmediateDisconnect(json);
				response.getWriter().println(json);
				return json;
			}
			
			boolean auth = loginHandler.authenticate(username, password);
			
			if (auth){
				User user = loginHandler.getUserByUsername(username);
				String sessionkey = sessionHandler.newSession(user, InetAddress.getByName(request.getRemoteAddr()));
				System.out.println("sessionkey: " + sessionkey);
				if (sessionkey == null){
					System.out.println("Same IP USER");
					json.put("code", "same-ip-user");
					json.put("response", "Could not register session on same IP or user");
					json.put("status", -1);
					setImmediateDisconnect(json);
					response.getWriter().println(json);
					return json;
				}
				System.out.println("Success");
				json.put("sessionkey", sessionkey);
				json.put("code", "login-success");
				json.put("response", "Logged in successfully");
				json.put("status", 1);
				
				System.out.println(json);
				setSkipToEncryption(json);
				return json;
			} else {
				json.put("code", "login-failed");
				json.put("response", "Wrong password or user does not exist");
				json.put("status", -1);
				response.getWriter().println(json);
				setImmediateDisconnect(json);
				return json;
			}
		}
		
		String sessionkey = request.getParameter("sessionkey");
		
		if (sessionkey == null){
			json.put("status", -1);
			json.put("code", "no-session-key");
			json.put("response", "No session key is specified");
			System.err.println("No session key specified");
			setImmediateDisconnect(json);
			response.getWriter().println(json);
			return json;
		}
		
		if (!sessionHandler.isSessionValid(sessionkey)){
			json.put("status", -1);
			json.put("code", "invalid-session-key");
			json.put("response", "Session key is invalid");
			System.err.println("Session key is invalid");
			setImmediateDisconnect(json);
			response.getWriter().println(json);
			return json;
		}
		
		System.out.println("LoginAgentPlugin: Access granted from " + request.getRemoteAddr());

		return null;
		
	}
	
	@Override
	public JSONObject onClientConnectAPI(HttpServletRequest request, HttpServletResponse response){
		return null;
	}

}
