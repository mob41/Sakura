package com.mob41.sakura.servlets;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mob41.kmbeta.api.ArrivalManager;
import com.mob41.kmbeta.api.MultiArrivalManager;
import com.mob41.sakura.AesUtil;
import com.mob41.sakura.Conf;
import com.mob41.sakura.hash.AES;
import com.mob41.sakura.info.InformFetcher;
import com.mob41.sakura.remo.BLRemote;
import com.mob41.sakura.remo.RMBridgeAPI;
import com.mob41.sakura.scene.SceneSave;
import com.mob41.sakura.scene.SceneThread;
import com.mob41.sakura.servlets.old.ControlServlet;

@WebServlet("/api")
public class ApiServlet extends HttpServlet {
	
	private static final Logger logger = LogManager.getLogger(ControlServlet.class.getName());
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
		if (InformFetcher.getFetcher() == null){
			try {
				InformFetcher.runFetcher(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST");
		response.setStatus(200);
		response.setContentType("application/json");
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
			token = AesUtil.random(128);
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
		
		JSONObject responseData = new JSONObject();
		
		InformFetcher fetcher = InformFetcher.getFetcher();
		
		switch (action){
		case 0: //Check connection only
			responseData.put("response", "OK");
			responseData.put("status", 1);
			break;
// Info
		case 1: //Gain basic data (Weather, Default KMB ETAs)
			if (fetcher == null){
				responseData.put("response", "Fetcher wasn't initialized.");
				responseData.put("status", -1);
				break;
			}
			try {
				fetcher.fetchData();
			} catch (Exception e) {
				responseData.put("resposne", "Error: " + e);
				responseData.put("status", -1);
				e.printStackTrace();
				break;
			}
			responseData.put("weather", fetcher.getWeatherManager().getRawJSON());
			JSONArray kmbETAarr = new JSONArray();
			MultiArrivalManager mularr = fetcher.getMultiArrivalManager();
			mularr.fetchAllData();
			for (int i = 0; i < mularr.getArrivalManagers().size(); i++){
				JSONObject eta = new JSONObject();
				ArrivalManager arrman = mularr.getArrivalManagers().get(i);
				arrman.getServerTime();
				eta.put("no", arrman.getBusNo());
				eta.put("stopcode", arrman.getStopCode());
				eta.put("stopseq", arrman.getStopSeq());
				eta.put("bound", arrman.getBound());
				eta.put("lang", arrman.getLang());
				try {
					eta.put("arrival", arrman.getArrivalTime_Formatted());
				} catch (Exception ignore){}
				eta.put("eta", arrman.getArrivalTimeRemaining_Formatted());
				kmbETAarr.put(eta);
			}
			responseData.put("busarrival", kmbETAarr);
			break;
		case 2: //Quick check ETA
			if (request.getParameter("no") == null || request.getParameter("stopcode") == null||
			request.getParameter("stopseq") == null || request.getParameter("bound") == null ||
			request.getParameter("lang") == null){
				response.setContentType("text/html");
				response.getWriter().println(invalidResponse);
				return;
			}
			String reqBusNo = request.getParameter("no");
			String reqStopCode = request.getParameter("stopcode");
			String reqStopSeqStr = request.getParameter("stopseq");
			String reqBoundStr = request.getParameter("bound");
			String reqLangStr = request.getParameter("lang");
			if (!isInteger(reqStopSeqStr) || !isInteger(reqBoundStr) || !isInteger(reqLangStr)){
				response.setContentType("text/html");
				response.getWriter().println(invalidResponse);
				return;
			}
			int reqBound = Integer.parseInt(reqBoundStr);
			int reqLang = Integer.parseInt(reqLangStr);
			ArrivalManager arrman = null;
			try {
				arrman = new ArrivalManager(reqBusNo, reqStopCode, reqBound, reqLang);
			} catch (Exception e){
				e.printStackTrace();
				responseData.put("response", "Error: " + e);
				responseData.put("status", -1);
				break;
			}
			arrman.getServerTime();
			arrman.fetchNewData();
			responseData.put("no", reqBusNo);
			responseData.put("stopcode", reqStopCode);
			try {
				responseData.put("arrival", arrman.getArrivalTime_Formatted());
			} catch (Exception e){
				responseData.put("response", "Error: " + e);
				responseData.put("status", -1);
				break;
			}
			responseData.put("eta", arrman.getArrivalTimeRemaining_Min());
			break;
		case 3: //Control Mode
			
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
			JSONObject rmout;
			int remotebuttons;
			
			String sessionkey = request.getParameter("sessionkey");
			String authkey = request.getParameter("authkey");
			
			String controlactionstr = request.getParameter("control");
			if (controlactionstr == null){
				response.setContentType("text/html");
				response.getWriter().println(invalidResponse);
				return;
			}
			
			if (!isInteger(controlactionstr)){
				response.setContentType("text/html");
				response.getWriter().println(invalidResponse);
				return;
			}
			
			int controlaction = Integer.parseInt(controlactionstr);
			
			switch (controlaction){
//RM Remote
			case 1: //Get Default RM MAC
				responseData.put("mac", "b4:43:0d:aa:1a:e2");
				responseData.put("status", 1);
				break;
	//Scene Tools
			case 2: //Activate Scene
				sceneuid = request.getParameter("uid");
				if (sceneuid == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				SceneThread.getRunnable().activateScene(sceneuid);
				responseData.put("status", 1);
				break;
		//Adders and Inserters
			case 3: //Add Scene
				scenename = request.getParameter("name");
				if (scenename == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				sceneuid = SceneSave.addScene(scenename);
				responseData.put("uid", sceneuid);
				responseData.put("status", 1);
				SceneThread.getRunnable().restart();
				SceneSave.writeIn();
				break;
			case 4: //Insert Action
				logger.info("The device (" + request.getRemoteAddr() + ") is inserting a action to a scene");
				String existactionname = request.getParameter("to_actionname");
				actionname = request.getParameter("name");
				sceneuid = request.getParameter("uid");
				actiontype = request.getParameter("type");
				actionvalue = request.getParameter("value");
				if (actionname == null || sceneuid == null || actiontype == null || actionvalue == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				SceneSave.insertAction(existactionname, sceneuid, actionname, sceneuid, actiontype, actionvalue);;
				SceneSave.writeIn();
				SceneThread.getRunnable().restart();
				responseData.put("status", 1);
				break;
			case 5: //Add Action
				logger.info("The device (" + request.getRemoteAddr() + ") is adding a action to a scene");
				actionname = request.getParameter("name");
				sceneuid = request.getParameter("uid");
				actiontype = request.getParameter("type");
				actionvalue = request.getParameter("value");
				if (actionname == null || sceneuid == null || actiontype == null || actionvalue == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				SceneSave.addAction(actionname, sceneuid, actiontype, actionvalue);
				SceneSave.writeIn();
				SceneThread.getRunnable().restart();
				responseData.put("status", 1);
				break;
			case 6: //Add Trigger
				logger.info("The device (" + request.getRemoteAddr() + ") is adding a trigger to a scene");
				triggername = request.getParameter("name");
				sceneuid = request.getParameter("uid");
				triggervalue = request.getParameter("value");
				if (triggername == null || sceneuid == null || triggervalue == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				SceneSave.addTrigger(triggername, sceneuid, triggervalue);
				SceneSave.writeIn();
				SceneThread.getRunnable().restart();
				responseData.put("status", 1);
				break;
		//Removers
			case 7: //Remove Action
				logger.info("The device (" + request.getRemoteAddr() + ") is removing a action from a scene");
				actionname = request.getParameter("name");
				sceneuid = request.getParameter("uid");
				if (actionname == null || sceneuid == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				SceneSave.removeAction(actionname, sceneuid);
				SceneSave.writeIn();
				SceneThread.getRunnable().restart();
				responseData.put("status", 1);
				break;
			case 8: //Remove Trigger
				logger.info("The device (" + request.getRemoteAddr() + ") is removing a trigger from a scene");
				triggername = request.getParameter("name");
				sceneuid = request.getParameter("uid");
				if (triggername == null || sceneuid == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				SceneSave.removeTrigger(triggername, sceneuid);
				SceneSave.writeIn();
				SceneThread.getRunnable().restart();
				responseData.put("status", 1);
				break;
			case 9: //Remove Scene
				logger.info("The device (" + request.getRemoteAddr() + ") is removing a scene");
				sceneuid = request.getParameter("uid");
				if (sceneuid == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				int index = SceneSave.getSceneIndex(sceneuid);
				SceneSave.removeSceneAll(index);
				SceneSave.writeIn();
				responseData.put("status", 1);
				SceneThread.getRunnable().restart();
				break;
		//'List'ers
			case 10: //List Scenes
				logger.info("The device (" + request.getRemoteAddr() + ") is listing scenes");
				responseData.put("scenes", SceneSave.getAllScenes());
				responseData.put("status", 1);
				break;
			case 11: //List Actions
				logger.info("The device (" + request.getRemoteAddr() + ") is listing actions of a scene");
				sceneuid = request.getParameter("uid");
				responseData.put("actions", SceneSave.getAllActions(sceneuid));
				responseData.put("status", 1);
				break;
			case 12: //List Triggers
				logger.info("The device (" + request.getRemoteAddr() + ") is listing triggers of a scene");
				sceneuid = request.getParameter("uid");
				responseData.put("triggers", SceneSave.getAllTriggers(sceneuid));
				responseData.put("status", 1);
				break;
	//RM Remotes
			case 13: //Get using method
				logger.info("The device (" + request.getRemoteAddr() + ") is getting the default learning method");
				responseData.put("method", Conf.rmcontrol_usingMethod);
				responseData.put("methods", Conf.rmcontrol_methods);
				responseData.put("status", 1);
				break;
		//'List'ers
			case 14: //List remotes
				logger.info("The device (" + request.getRemoteAddr() + ") is listing remotes");
				responseData.put("list", BLRemote.remotes);
				responseData.put("response", "Listed");
				responseData.put("remotes", BLRemote.remotes.size());
				responseData.put("status", 1);
				break;
			case 15: //List buttons
				logger.info("The device (" + request.getRemoteAddr() + ") is listing buttons of a remote");
				remoteuuid = request.getParameter("uuid");
				if (remoteuuid == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				List<String[]> buttonlist = BLRemote.getAllButtons(remoteuuid);
				int listremoteindex = BLRemote.getRemoteIndex(remoteuuid);
				String listremotetype = listremoteindex == -1 ? "invaild" : BLRemote.remotes.get(listremoteindex)[2];
				responseData.put("type", listremotetype);
				responseData.put("list", buttonlist);
				responseData.put("response", "Listed");
				responseData.put("buttons", buttonlist.size());
				responseData.put("status", 1);
				break;
			case 16: //Is Button Exist
				logger.info("The device (" + request.getRemoteAddr() + ") is asking is button exist");
				remoteuuid = request.getParameter("uuid");
				buttonid = request.getParameter("id");
				if (remoteuuid == null || buttonid == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				int resultindex = BLRemote.getButtonIndex(remoteuuid, buttonid);
				System.out.println(resultindex);
				if (resultindex >= 0){
					responseData.put("exist", true);
				} else {
					responseData.put("exist", false);
				}
				responseData.put("status", 1);
				break;
		//Remote
			case 17: //Add Remote
				logger.info("The device (" + request.getRemoteAddr() + ") is adding a remote");
				remotename = request.getParameter("name");
				remoteuuid = AES.getRandomByte();
				remotetype = request.getParameter("type");
				if (remotename == null || remoteuuid == null || remotetype == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				build = BLRemote.buildRemoteCode(remotename, remoteuuid, remotetype);
				BLRemote.remotes.add(build);
				BLRemote.writeIn();
				responseData.put("response", "Added new remote");
				responseData.put("name", remotename);
				responseData.put("uuid", remoteuuid);
				responseData.put("type", remotetype);
				responseData.put("status", 1);
				break;
			case 18: //Remove remote
				logger.info("The device (" + request.getRemoteAddr() + ") is removing a remote");
				remoteuuid = request.getParameter("uuid");
				if (remoteuuid == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				BLRemote.deleteRemote(remoteuuid);
				BLRemote.writeIn();
				responseData.put("response", "Removed new remote");
				responseData.put("status", 1);
				break;
		//Buttons
			case 19: //Add button
				logger.info("The device (" + request.getRemoteAddr() + ") is adding a button");
				remoteuuid = request.getParameter("uuid");
				buttonid = request.getParameter("id");
				rm2data = request.getParameter("data");
				if (remoteuuid == null || buttonid == null || rm2data == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				build = BLRemote.buildButtonCode(remoteuuid, buttonid, rm2data);
				BLRemote.buttons.add(build);
				BLRemote.writeIn();
				responseData.put("response", "Added new remote");
				responseData.put("uuid", remoteuuid);
				responseData.put("id", buttonid);
				responseData.put("data", rm2data);
				responseData.put("status", 1);
				break;
			case 20: //Send code via button
				logger.info("The device (" + request.getRemoteAddr() + ") is requesting to send a code by button");
				method = request.getParameter("method");
				remoteuuid = request.getParameter("uuid");
				buttonid = request.getParameter("buttonid");
				mac = request.getParameter("mac");
				
				if (method == null || remoteuuid == null || buttonid == null || mac == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				
				//Get data from database
				int buttonindex = BLRemote.getButtonIndex(remoteuuid, buttonid);
				code = BLRemote.buttons.get(buttonindex);
				data = code[2];
				switch(method){
				default:
				case "rm-bridge":
					try {
						RMBridgeAPI rmapi = new RMBridgeAPI(Conf.rmbridge_url);
						rmout = rmapi.rmbridge_sendCode(mac, data);
					} catch (IOException e){
						responseData.put("response", "Could not connect to RM Bridge.");
						responseData.put("exception", e);
						responseData.put("status", -1);
						e.printStackTrace();
						break;
					}
					responseData.put("code", rmout.getInt("code"));
					responseData.put("msg", rmout.getString("msg"));
					break;
				case "sdk":
					//TODO Wait for SDK~!!!! Broadlink is too slow
					responseData.put("response", "SDK does not exist");
					responseData.put("status", -1);
					responseData.put("code", -1);
					break;
				}
				break;
		//Generic RM-Bridge Controls
			case 21: //Send Code
				logger.info("The device (" + request.getRemoteAddr() + ") is requesting to send a code");
				method = request.getParameter("method");
				mac = request.getParameter("mac");
				data = request.getParameter("data");
				
				if (method == null || mac == null || data == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				
				switch(method){
				default:
				case "rm-bridge":
					try {
						RMBridgeAPI rmapi = new RMBridgeAPI(Conf.rmbridge_url);
						rmout = rmapi.rmbridge_sendCode(mac, data);
					} catch (IOException e){
						responseData.put("response", "Could not connect to RM Bridge.");
						responseData.put("exception", e);
						responseData.put("status", -1);
						e.printStackTrace();
						break;
					}
					responseData.put("code", rmout.getInt("code"));
					responseData.put("msg", rmout.getString("msg"));
					break;
				case "sdk":
					//TODO Wait for SDK~!!!! Broadlink is too slow
					responseData.put("response", "SDK does not exist");
					responseData.put("status", -1);
					responseData.put("code", -1);
					break;
				}
				break;
			case 22: //Learn Code
				logger.info("The device (" + request.getRemoteAddr() + ") is requesting to learn a new code");
				method = request.getParameter("method");
				mac = request.getParameter("mac");
				
				if (method == null || mac == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				
				switch(method){
				default:
				case "rm-bridge":
					try {
						RMBridgeAPI rmapi = new RMBridgeAPI(Conf.rmbridge_url);
						rmout = rmapi.rmbridge_learnCode(mac);
					} catch (IOException e){
						responseData.put("response", "Could not connect to RM Bridge.");
						responseData.put("exception", e);
						responseData.put("status", -1);
						e.printStackTrace();
						break;
					}
					responseData.put("code", rmout.getInt("code"));
					responseData.put("msg", rmout.getString("msg"));
					break;
				case "sdk":
					//TODO Wait for SDK~!!!! Broadlink is too slow
					responseData.put("response", "SDK does not exist");
					responseData.put("status", -1);
					responseData.put("code", -1);
					break;
				}
				break;
			case 23: //Get learned code
				logger.info("The device " + request.getRemoteAddr() + ") is getting the last learned code");
				method = request.getParameter("method");
				mac = request.getParameter("mac");
				
				if (method == null || mac == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				
				switch(method){
				default:
				case "rm-bridge":
					try {
						RMBridgeAPI rmapi = new RMBridgeAPI(Conf.rmbridge_url);
						rmout = rmapi.rmbridge_getCode(mac);
					} catch (IOException e){
						responseData.put("response", "Could not connect to RM Bridge.");
						responseData.put("exception", e);
						responseData.put("status", -1);
						e.printStackTrace();
						break;
					}
					System.out.println(rmout);
					data = "nodata";
					responseData.put("status", 1);
					try {
						data = rmout.getString("data");
					} catch (JSONException e){
						responseData.put("status", -1);
						data = "nodata";
					}
					responseData.put("data", data);
					responseData.put("code", rmout.getInt("code"));
					responseData.put("msg", rmout.getString("msg"));
					break;
				case "sdk":
					//TODO Wait for SDK~!!!! Broadlink is too slow
					responseData.put("response", "SDK does not exist");
					responseData.put("status", -1);
					responseData.put("code", -1);
					break;
				}
				break;
			case 24: //Get Devices
				logger.info("The device " + request.getRemoteAddr() + ") is getting devices list");
				method = request.getParameter("method");
				
				if (method == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				
				switch(method){
				default:
				case "rm-bridge":
					try {
						RMBridgeAPI rmapi = new RMBridgeAPI(Conf.rmbridge_url);
						rmout = rmapi.rmbridge_getDevices();
					} catch (IOException e){
						responseData.put("response", "Could not connect to RM Bridge.");
						responseData.put("exception", e);
						responseData.put("status", -1);
						e.printStackTrace();
						break;
					}
					System.out.println(rmout);
					responseData.put("code", rmout.getInt("code"));
					responseData.put("list", rmout.getJSONArray("list"));
					responseData.put("msg", rmout.getString("msg"));
					break;
				case "sdk":
					//TODO Wait for SDK~!!!! Broadlink is too slow
					responseData.put("response", "SDK does not exist");
					responseData.put("status", -1);
					responseData.put("code", -1);
					break;
				}
				break;
			case 25: //Probe devices
				logger.info("The device " + request.getRemoteAddr() + ") is probing devices");
				method = request.getParameter("method");
				
				if (method == null){
					response.setContentType("text/html");
					response.getWriter().println(invalidResponse);
					return;
				}
				
				switch(method){
				default:
				case "rm-bridge":
					try {
						RMBridgeAPI rmapi = new RMBridgeAPI(Conf.rmbridge_url);
						rmout = rmapi.rmbridge_probeDevices();
					} catch (IOException e){
						responseData.put("response", "Could not connect to RM Bridge.");
						responseData.put("exception", e);
						responseData.put("status", -1);
						e.printStackTrace();
						break;
					}
					responseData.put("code", rmout.getInt("code"));
					responseData.put("msg", rmout.getString("msg"));
					break;
				case "sdk":
					//TODO Wait for SDK~!!!! Broadlink is too slow
					responseData.put("response", "SDK does not exist");
					responseData.put("status", -1);
					responseData.put("code", -1);
					break;
				}
				break;
			default:
				responseData.put("response", "Unknown control action");
				responseData.put("status", -1);
			}
		case 4: //Login
		default: //Unknown Action
			responseData.put("response", "Unknown action");
			responseData.put("status", -1);
		}
		
		//Send encrypted data
		System.out.println("Sending encrypted data");
		String pass = AccessTokenSession.getRunnable().getCurrentSessions()
				.get(tokenIndex).getString("pass");
		String salt = AccessTokenSession.getRunnable().getCurrentSessions()
				.get(tokenIndex).getString("salt");
		String en = encryptData(responseData.toString(), pass, iv, salt);
		AccessTokenSession.getRunnable().getCurrentSessions().remove(tokenIndex);
		json.put("data", en);
		response.getWriter().println(json);
	}
	
	private String encryptData(String data, String pass, String iv, String salt){
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
	
	private boolean isInteger(String intstr){
		try {
			Integer.parseInt(intstr);
			return true;
		} catch (NumberFormatException e){
			return false;
		}
	}

}
