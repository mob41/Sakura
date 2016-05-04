package com.mob41.sakura.servlets.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.server.RemoteServer;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.mob41.sakura.auth.SessionAuth;
import com.rpi.ha.Conf;
import com.rpi.ha.remo.BLRemote;
import com.rpi.ha.scene.SceneSave;
import com.rpi.ha.ui.UI;

@WebServlet("/control")
public class ControlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ControlServlet.class.getName());
       
    public ControlServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("A device (" + request.getRemoteAddr() + ") was rejected from the GET Request.");
		JSONObject json = new JSONObject();
		Calendar cal = Calendar.getInstance();
		response.setContentType("application/json");
		json.put("response", "GET requests are rejected.");
		json.put("status", -1);
		json.put("generated", cal.getTimeInMillis());
		response.getWriter().println(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST");
		logger.info("A device (" + request.getRemoteAddr() + ") is now connected to API POST.");
		JSONObject json = new JSONObject();
		JSONObject rmcmd = new JSONObject();
		JSONObject rmout = new JSONObject();
		InputStream broadlinkresponse;
		String method;
		String mac;
		String data;
		String actionname;
		String actiontype;
		String actionvalue;
		String triggername;
		String triggervalue;
		String scenename;
		String sceneuid;
		String remotename;
		String remoteuuid;
		String remotetype;
		String buttonid;
		String rm2data;
		String[] build;
		String[] code;
		int remotebuttons;
		Calendar cal = Calendar.getInstance();
		String sessionkey = request.getParameter("sessionkey");
		String authkey = request.getParameter("authkey");
		String action = request.getParameter("action");
		response.setContentType("application/json");
		
		if (authkey == null || action == null){
			json.put("response", "Wrong Parameters!");
			json.put("status", -1);
			json.put("generated", cal.getTimeInMillis());
			response.getWriter().println(json);
			return; 
		}
		
		boolean auth = false;
		try {
			auth = HashKey.auth(authkey);
		} catch (Exception e){
			logger.warn("Could not decrypt password. The hashed password may be corrupted.");
		}
		
		if (!SessionAuth.sesthread.isRunning()){
			logger.warn("SessionAuth wasn't running! Session Checks are rejected!");
			json.put("response", "Session authentication service wasn't ready.");
			json.put("generated", cal.getTimeInMillis());
			json.put("status", -1);
			json.put("code", "500");
			response.getWriter().println(json);
		}

		if (SessionAuth.sesthread.isSessionAvailable(sessionkey)){
			json.put("session", true);
			logger.info("The device is session verified. IP: " + request.getRemoteAddr());
			if (auth){
				logger.info("The device is now authenticated. IP: " + request.getRemoteAddr());
				switch (action){
				case "0":
					logger.info("The device (" + request.getRemoteAddr() + ") is just testing connection.");
					json.put("response", "Connected successfully.");
					json.put("status", 1);
					break;
				case "getdefaultdevice":
					logger.info("The device (" + request.getRemoteAddr() + ") is getting the default device");
					json.put("mac", "b4:43:0d:aa:1a:e2");
					json.put("status", 1);
					break;
				case "activatescene":
					logger.info("The device (" + request.getRemoteAddr() + ") is activating a new scene");
					sceneuid = request.getParameter("uid");
					UI.sceneRun.activateScene(sceneuid);
					json.put("status", 1);
					break;
				case "addscene":
					logger.info("The device (" + request.getRemoteAddr() + ") is creating a new scene");
					scenename = request.getParameter("name");
					sceneuid = SceneSave.addScene(scenename);
					json.put("uid", sceneuid);
					json.put("status", 1);
					UI.sceneRun.restart();
					SceneSave.writeIn();
					break;
				case "insertaction":
					logger.info("The device (" + request.getRemoteAddr() + ") is inserting a action to a scene");
					String existactionname = request.getParameter("to_actionname");
					actionname = request.getParameter("name");
					sceneuid = request.getParameter("uid");
					actiontype = request.getParameter("type");
					actionvalue = request.getParameter("value");
					SceneSave.insertAction(existactionname, sceneuid, actionname, sceneuid, actiontype, actionvalue);;
					SceneSave.writeIn();
					UI.sceneRun.restart();
					json.put("status", 1);
					break;
				case "addaction":
					logger.info("The device (" + request.getRemoteAddr() + ") is adding a action to a scene");
					actionname = request.getParameter("name");
					sceneuid = request.getParameter("uid");
					actiontype = request.getParameter("type");
					actionvalue = request.getParameter("value");
					SceneSave.addAction(actionname, sceneuid, actiontype, actionvalue);
					SceneSave.writeIn();
					UI.sceneRun.restart();
					json.put("status", 1);
					break;
				case "addtrigger":
					logger.info("The device (" + request.getRemoteAddr() + ") is adding a trigger to a scene");
					triggername = request.getParameter("name");
					sceneuid = request.getParameter("uid");
					triggervalue = request.getParameter("value");
					SceneSave.addTrigger(triggername, sceneuid, triggervalue);
					SceneSave.writeIn();
					UI.sceneRun.restart();
					json.put("status", 1);
					break;
				case "removeaction":
					logger.info("The device (" + request.getRemoteAddr() + ") is removing a action from a scene");
					actionname = request.getParameter("name");
					sceneuid = request.getParameter("uid");
					SceneSave.removeAction(actionname, sceneuid);
					SceneSave.writeIn();
					UI.sceneRun.restart();
					json.put("status", 1);
					break;
				case "removetrigger":
					logger.info("The device (" + request.getRemoteAddr() + ") is removing a trigger from a scene");
					triggername = request.getParameter("name");
					sceneuid = request.getParameter("uid");
					SceneSave.removeTrigger(triggername, sceneuid);
					SceneSave.writeIn();
					UI.sceneRun.restart();
					json.put("status", 1);
					break;
				case "removescene":
					logger.info("The device (" + request.getRemoteAddr() + ") is removing a scene");
					sceneuid = request.getParameter("uid");
					int index = SceneSave.getSceneIndex(sceneuid);
					SceneSave.removeSceneAll(index);
					SceneSave.writeIn();
					json.put("status", 1);
					UI.sceneRun.restart();
					break;	
				case "listscenes":
					logger.info("The device (" + request.getRemoteAddr() + ") is listing scenes");
					json.put("scenes", SceneSave.getAllScenes());
					json.put("status", 1);
					break;
				case "listactions":
					logger.info("The device (" + request.getRemoteAddr() + ") is listing actions of a scene");
					sceneuid = request.getParameter("uid");
					json.put("actions", SceneSave.getAllActions(sceneuid));
					json.put("status", 1);
					break;
				case "listtriggers":
					logger.info("The device (" + request.getRemoteAddr() + ") is listing triggers of a scene");
					sceneuid = request.getParameter("uid");
					json.put("triggers", SceneSave.getAllTriggers(sceneuid));
					json.put("status", 1);
					break;
				case "getusingmethod":
					logger.info("The device (" + request.getRemoteAddr() + ") is getting the default learning method");
					json.put("method", Conf.rmcontrol_usingMethod);
					json.put("methods", Conf.rmcontrol_methods);
					json.put("status", 1);
					break;
				case "listremotes":
					logger.info("The device (" + request.getRemoteAddr() + ") is listing remotes");
					json.put("list", BLRemote.remotes);
					json.put("response", "Listed");
					json.put("remotes", BLRemote.remotes.size());
					json.put("status", 1);
					break;
				case "listbuttons":
					logger.info("The device (" + request.getRemoteAddr() + ") is listing buttons of a remote");
					remoteuuid = request.getParameter("uuid");
					List<String[]> buttonlist = BLRemote.getAllButtons(remoteuuid);
					int listremoteindex = BLRemote.getRemoteIndex(remoteuuid);
					String listremotetype = listremoteindex == -1 ? "invaild" : BLRemote.remotes.get(listremoteindex)[2];
					json.put("type", listremotetype);
					json.put("list", buttonlist);
					json.put("response", "Listed");
					json.put("buttons", buttonlist.size());
					json.put("status", 1);
					break;
				case "isbuttonexist":
					logger.info("The device (" + request.getRemoteAddr() + ") is asking is button exist");
					remoteuuid = request.getParameter("uuid");
					buttonid = request.getParameter("id");
					int resultindex = BLRemote.getButtonIndex(remoteuuid, buttonid);
					System.out.println(resultindex);
					if (resultindex >= 0){
						json.put("exist", true);
					} else {
						json.put("exist", false);
					}
					json.put("status", 1);
					break;
				case "addremote":
					logger.info("The device (" + request.getRemoteAddr() + ") is adding a remote");
					remotename = request.getParameter("name");
					remoteuuid = HashKey.getRandomSalt();
					remotetype = request.getParameter("type");
					if (remotename.equals(null) || remoteuuid.equals(null) ||
							remotetype.equals(null)){
						json.put("response", "Wrong parameters!");
						json.put("status", -1);
						break;
					}
					build = BLRemote.buildRemoteCode(remotename, remoteuuid, remotetype);
					BLRemote.remotes.add(build);
					BLRemote.writeIn();
					json.put("response", "Added new remote");
					json.put("name", remotename);
					json.put("uuid", remoteuuid);
					json.put("type", remotetype);
					json.put("status", 1);
					break;
				case "removeremote":
					logger.info("The device (" + request.getRemoteAddr() + ") is removing a remote");
					remoteuuid = request.getParameter("uuid");
					if (remoteuuid.equals(null)){
						json.put("response", "Wrong parameters!");
						json.put("status", -1);
						break;
					}
					BLRemote.deleteRemote(remoteuuid);
					BLRemote.writeIn();
					json.put("response", "Removed new remote");
					json.put("status", 1);
					break;
				case "addbutton":
					logger.info("The device (" + request.getRemoteAddr() + ") is adding a button");
					remoteuuid = request.getParameter("uuid");
					buttonid = request.getParameter("id");
					rm2data = request.getParameter("data");
					if (remoteuuid.equals(null) || buttonid.equals(null) ||
							rm2data.equals(null)){
						json.put("response", "Wrong parameters!");
						json.put("status", -1);
						break;
					}
					build = BLRemote.buildButtonCode(remoteuuid, buttonid, rm2data);
					BLRemote.buttons.add(build);
					BLRemote.writeIn();
					json.put("response", "Added new remote");
					json.put("uuid", remoteuuid);
					json.put("id", buttonid);
					json.put("data", rm2data);
					json.put("status", 1);
					break;
				case "sendbutton":
					logger.info("The device (" + request.getRemoteAddr() + ") is requesting to send a code by button");
					method = request.getParameter("method");
					remoteuuid = request.getParameter("uuid");
					buttonid = request.getParameter("buttonid");
					mac = request.getParameter("mac");
					
					//Get data from database
					int buttonindex = BLRemote.getButtonIndex(remoteuuid, buttonid);
					code = BLRemote.buttons.get(buttonindex);
					data = code[2];
					switch(method){
					default:
					case "rm-bridge":
						rmout = rmbridge_sendCode(mac, data);
						json.put("code", rmout.getInt("code"));
						json.put("msg", rmout.getString("msg"));
						break;
					case "sdk":
						//TODO Wait for SDK~!!!! Broadlink is too slow
						json.put("response", "SDK does not exist");
						json.put("status", -1);
						json.put("code", -1);
						break;
					}
					break;
				case "sendcode":
					logger.info("The device (" + request.getRemoteAddr() + ") is requesting to send a code");
					method = request.getParameter("method");
					mac = request.getParameter("mac");
					data = request.getParameter("data");
					
					switch(method){
					default:
					case "rm-bridge":
						rmout = rmbridge_sendCode(mac, data);
						json.put("code", rmout.getInt("code"));
						json.put("msg", rmout.getString("msg"));
						break;
					case "sdk":
						//TODO Wait for SDK~!!!! Broadlink is too slow
						json.put("response", "SDK does not exist");
						json.put("status", -1);
						json.put("code", -1);
						break;
					}
					break;
				case "learncode":
					logger.info("The device (" + request.getRemoteAddr() + ") is requesting to learn a new code");
					method = request.getParameter("method");
					mac = request.getParameter("mac");
					
					switch(method){
					default:
					case "rm-bridge":
						rmout = rmbridge_learnCode(mac);
						json.put("code", rmout.getInt("code"));
						json.put("msg", rmout.getString("msg"));
						break;
					case "sdk":
						//TODO Wait for SDK~!!!! Broadlink is too slow
						json.put("response", "SDK does not exist");
						json.put("status", -1);
						json.put("code", -1);
						break;
					}
					break;
				case "getcode":
					logger.info("The device " + request.getRemoteAddr() + ") is getting the last learned code");
					method = request.getParameter("method");
					mac = request.getParameter("mac");
					
					switch(method){
					default:
					case "rm-bridge":
						rmout = rmbridge_getCode(mac);
						System.out.println(rmout);
						data = "nodata";
						json.put("status", 1);
						try {
							data = rmout.getString("data");
						} catch (JSONException e){
							json.put("status", -1);
							data = "nodata";
						}
						json.put("data", data);
						json.put("code", rmout.getInt("code"));
						json.put("msg", rmout.getString("msg"));
						break;
					case "sdk":
						//TODO Wait for SDK~!!!! Broadlink is too slow
						json.put("response", "SDK does not exist");
						json.put("status", -1);
						json.put("code", -1);
						break;
					}
					break;
				case "getdevices":
					logger.info("The device " + request.getRemoteAddr() + ") is getting devices list");
					method = request.getParameter("method");
					
					switch(method){
					default:
					case "rm-bridge":
						rmout = rmbridge_getDevices();
						System.out.println(rmout);
						json.put("code", rmout.getInt("code"));
						json.put("list", rmout.getJSONArray("list"));
						json.put("msg", rmout.getString("msg"));
						break;
					case "sdk":
						//TODO Wait for SDK~!!!! Broadlink is too slow
						json.put("response", "SDK does not exist");
						json.put("status", -1);
						json.put("code", -1);
						break;
					}
					break;
				case "probedevices":
					logger.info("The device " + request.getRemoteAddr() + ") is getting the last learned code");
					method = request.getParameter("method");
					
					switch(method){
					default:
					case "rm-bridge":
						rmout = rmbridge_probeDevices();
						json.put("code", rmout.getInt("code"));
						json.put("msg", rmout.getString("msg"));
						break;
					case "sdk":
						//TODO Wait for SDK~!!!! Broadlink is too slow
						json.put("response", "SDK does not exist");
						json.put("status", -1);
						json.put("code", -1);
						break;
					}
					break;
				default:
					logger.info("The device (" + request.getRemoteAddr() + ") entered a unknown action number.");
					json.put("response", "Unknown operation.");
					json.put("status", -1);
					break;
				}
				json.put("generated", cal.getTimeInMillis());
				response.getWriter().println(json);
			}
			else
			{
				logger.error("Someone entered wrong AuthKey!! IP: " + request.getRemoteAddr());
				json.put("response", "Wrong AuthKey. This incient will be reported.");
				json.put("generated", cal.getTimeInMillis());
				json.put("status", -1);
				json.put("code", 403);
				response.getWriter().println(json);
				return;
			}
		}
		else
		{
			logger.warn("Session is expired/not registered: "+ sessionkey);
			json.put("session", false);
			json.put("response", "Session is expired/not registered.");
			json.put("generated", cal.getTimeInMillis());
			json.put("status", -1);
			json.put("code", 500);
			response.getWriter().println(json);
		}
		logger.info("The device (" + request.getRemoteAddr() + ") was disconnected.");
	}
	
	private static InputStream postData(String url, JSONObject data) throws IOException{
		URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true); // Triggers POST.
		connection.setRequestProperty("Content-Type", "application/json");

		try (OutputStream output = connection.getOutputStream()) {
		    output.write(data.toString().getBytes("utf-8"));
		}
		return connection.getInputStream();
	}
	
	private static JSONObject rmbridge_getCode(String mac) throws IOException{
		JSONObject rmcmd = new JSONObject();
		rmcmd.put("api_id", 1003);
		rmcmd.put("command", "get_code");
		rmcmd.put("mac", mac);
		return new JSONObject(getStringFromInputStream(postData(Conf.rmbridge_url, rmcmd)));
	}
	
	private static JSONObject rmbridge_sendCode(String mac, String data) throws IOException{
		JSONObject rmcmd = new JSONObject();
		rmcmd.put("api_id", 1004);
		rmcmd.put("command", "send_code");
		rmcmd.put("mac", mac);
		rmcmd.put("data", data);
		return new JSONObject(getStringFromInputStream(postData(Conf.rmbridge_url, rmcmd)));
	}
	
	private static JSONObject rmbridge_learnCode(String mac) throws IOException{
		JSONObject rmcmd = new JSONObject();
		rmcmd.put("api_id", 1002);
		rmcmd.put("command", "learn_code");
		rmcmd.put("mac", mac);
		return new JSONObject(getStringFromInputStream(postData(Conf.rmbridge_url, rmcmd)));
	}
	
	private static JSONObject rmbridge_getDevices() throws IOException{
		JSONObject rmcmd = new JSONObject();
		rmcmd.put("api_id", 1000);
		rmcmd.put("command", "registered_devices");
		return new JSONObject(getStringFromInputStream(postData(Conf.rmbridge_url, rmcmd)));
	}
	
	private static JSONObject rmbridge_probeDevices() throws IOException{
		JSONObject rmcmd = new JSONObject();
		rmcmd.put("api_id", 1001);
		rmcmd.put("command", "probe_devices");
		return new JSONObject(getStringFromInputStream(postData(Conf.rmbridge_url, rmcmd)));
	}

	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
}
