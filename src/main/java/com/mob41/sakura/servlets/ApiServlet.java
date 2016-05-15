package com.mob41.sakura.servlets;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.mob41.sakura.hash.AesUtil;
import com.mob41.sakura.plugin.PluginManager;
import com.mob41.sakura.plugin.exception.NoSuchPluginException;

@WebServlet("/api")
public class ApiServlet extends HttpServlet {
	
	private static final Logger logger = LogManager.getLogger("API");
	private static final long serialVersionUID = 1L;
	private static final String invalidResponse = 
			"<!doctype html public \"-//w3c//dtd html 4.0 " +
				      "transitional//en\">\n" +
				      "<html>\n" +
				      "<head><title>400 Bad Request</title></head>\n"+
				      "<body bgcolor=\"#f0f0f0\">\n" +
				      "<h1 align=\"center\">400 Bad Request</h1>\n" +
				      "<p>Your request to this API is invalid.</p>\n";
	
    public ApiServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(400);
		response.setContentType("text/html");
		System.out.println("GET");
		response.getWriter().println(invalidResponse);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST");
		
		response.setStatus(200);
		response.setContentType("application/json");
		
		boolean doActions = true;
		
		JSONObject responseData = PluginManager.getPluginManager().callAll_ClientConnectAPI(request, response);
		if (responseData != null && !responseData.isNull("dis")){
			if (responseData.getString("dis").equals("immediate_disconnect")){
				return;
			} else if (responseData.getString("dis").equals("skip_to_encryption")){
				doActions = false;
			}
		}
		
		JSONObject json = new JSONObject();
		json.put("generated", Calendar.getInstance().getTimeInMillis());
		
//End2End Encryption - Get Token
		if (request.getParameter("entk") == null){
			System.out.println("No ENTK");
			response.setContentType("text/html");
			response.getWriter().println(invalidResponse);
			return;
		}
		System.out.println("New Connection");
		String token = request.getParameter("entk");
		
		//If the request asks to gain a access token
		if (token.equals("gain")){
			System.out.println("Gaining");
			//TODO Check Whitelist and Blacklist
			
			String salt = AesUtil.random(128/8);
			String pass = AesUtil.random(128/8);
			String iv = AesUtil.random(128/8);
			token = AesUtil.random(128/8);
			boolean success = AccessTokenSession.getRunnable().addSession(token, pass, salt,
					request.getRemoteAddr(), Calendar.getInstance().getTimeInMillis(),
					AccessTokenSession.DEFAULT_TIMEOUT);
			if (!success){
				json.put("status", -1);
				json.put("response", "Could not register new token.");
				response.getWriter().println(json);
				System.out.println("Connection throttled. Could not register new token.");
				return;
			}
			json.put("status", 1);
			json.put("token", token);
			json.put("iv", iv);
			json.put("reqend_s", salt);
			json.put("reqend_p", pass);
			
			response.getWriter().println(json);
			System.out.println("Gaining Token: " + json);
			return;
		}
		System.out.println("Nope");
		//Else, check token is valid
		
		int tokenIndex = AccessTokenSession.getRunnable().getIndexOfThisToken(token);
		if (tokenIndex == -1){
			System.out.println("Token is invalid");
			json.put("status", -2);
			json.put("response", "Invalid Token");
			response.getWriter().println(json);
			return;
		}
		System.out.println("Token is valid");
		
		//Then, Is the parameter specified
		
		if (request.getParameter("iv") == null){
			System.out.println("No IV Parameter! Invalidrequest");
			
			response.setStatus(400);
			response.getWriter().println(invalidResponse);
			return;
		}
		String iv = request.getParameter("iv");
		
		if (doActions){

			//Read action
			if (request.getParameter("action") == null){
				response.setStatus(400);
				response.getWriter().println(invalidResponse);
				return;
			}
			if (!isInteger(request.getParameter("action"))){
				response.setStatus(400);
				response.getWriter().println(invalidResponse);
				return;
			}
			int action = Integer.parseInt(request.getParameter("action"));
			
			switch (action){
			case 0: //Check connection only
				responseData.put("response", "OK");
				responseData.put("status", 1);
				break;
			case 1: //Access to plugins
				
				boolean accessPlugins = true;
				
				responseData = PluginManager.getPluginManager().callAll_AccessPlugins(request, response);
				if (responseData != null && !responseData.isNull("dis")){
					if (responseData.getString("dis").equals("immediate_disconnect")){
						return;
					} else if (responseData.getString("dis").equals("skip_to_encryption")){
						accessPlugins = false;
					}
				}
				
				System.out.println(responseData);
				
				if (accessPlugins){
					String pluginUid = request.getParameter("name");
					System.out.println("plugin: " + request.getParameter("plugin"));
					JSONObject revData = new JSONObject(request.getParameter("plugin"));
					System.out.println("plugJSON: " + revData);
					
					
					PluginManager.getPluginManager().callAll_AfterAccessPlugins(request, response);
					
					try {
						responseData = mergeJSON(responseData, (JSONObject) PluginManager.getPluginManager().runPluginLifeCycle(pluginUid, revData));
					} catch (NoSuchPluginException e) {
						System.out.println("The plugin requested wasn't exist");
						responseData.put("response", e);
						responseData.put("code", "no-such-plugin");
						responseData.put("status", -1);
					}
				}
				
				break;
			default: //Unknown Action
				responseData.put("response", "Unknown action");
				responseData.put("status", -1);
			}
		}
		
		//Send encrypted data
		System.out.println("Sending encrypted data");
		String pass = AccessTokenSession.getRunnable().getCurrentSessions()
				.get(tokenIndex).getString("pass");
		String salt = AccessTokenSession.getRunnable().getCurrentSessions()
				.get(tokenIndex).getString("salt");
		AccessTokenSession.getRunnable().getCurrentSessions().remove(tokenIndex);
		String en = encryptData(responseData.toString(), pass, iv, salt);

		if (json.isNull("status")){
			json.put("status", 1);
		}
		json.put("data", en);
		response.getWriter().println(json);
		
		PluginManager.getPluginManager().callAll_ClientDisconnectAPI(request, response);
	}
	
	private static String encryptData(String data, String pass, String iv, String salt){
		final int ic = 1000;
		final int size = 128;
		AesUtil util = new AesUtil(size, ic);
		System.out.println("D: " + data + " P: " + pass + " IV: " + iv + " S: " + salt);
		String passchain = util.encrypt(salt, iv, pass, pass);
		System.out.println("Chain: " + passchain);
		String en = util.encrypt(salt, iv, passchain, data);
		System.out.println("EN: " + en);
		return en;
	}
	
	private static JSONObject mergeJSON(JSONObject Obj1, JSONObject Obj2){
		System.out.println("obj1: " + Obj1);
		System.out.println("Obj2: " + Obj2);
		if (JSONObject.getNames(Obj1) == null){
			return Obj2;
		} else if (Obj2 == null){
			return Obj1;
		}
		JSONObject merged = new JSONObject(Obj1, JSONObject.getNames(Obj1));
		for(String key : JSONObject.getNames(Obj2))
		{
		  merged.put(key, Obj2.get(key));
		}
		return merged;
	}
	
	private static boolean isInteger(String intstr){
		try {
			Integer.parseInt(intstr);
			return true;
		} catch (NumberFormatException e){
			return false;
		}
	}

}
