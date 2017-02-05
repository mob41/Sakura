package com.github.mob41.sakura.api;

import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.mob41.sakura.appliance.Appliance;
import com.github.mob41.sakura.appliance.ApplianceManager;

/**
 * This provides a "development/debuggable" API for developers.
 * This is not intent for using in production.<br>
 * <br>
 * In production, all API clients should use the end-to-end encrypted
 * <code>SakuraAPI</code> instead.
 * @author Anthony
 *
 */
public class EasyAPI {
	
	public static final int RESULT_INVALID_REQUEST = -1;
	
	public static final int RESULT_NO_DATA = 0;
	
	public static final int RESULT_OK = 1;
	
	public static final int RESULT_RESULT_IN_DATA = 2;

	public static final int CMD_APPLIANCE_CONTROL = 0;
	
	public static final int CMD_APPLIANCE_INFO = 1;
	
	private final SakuraServer srv;
	
	public EasyAPI(SakuraServer srv) {
		this.srv = srv;
	}

	/**
	 * Requests the API with a <code>cmd</code> parameter
	 * @param req
	 * @return
	 */
	public JSONObject request(JSONObject req){
		System.out.println("JSON: " + req);
		final JSONObject outJson = new JSONObject();
		final Calendar cal = Calendar.getInstance();
		
		outJson.put("generated", cal.getTimeInMillis());
		
		if (!(req.isNull("user") || req.isNull("pass") || req.isNull("cmd"))){
			if (req.get("user") instanceof String && req.get("pass") instanceof String){
				String user = req.getString("user");
				String pass = req.getString("pass");
				
				boolean auth = srv.getUserManager().authenticate(user, pass);
				boolean perm = srv.getPermManager().isUserPermitted(user, "system.easyapi");
				
				if (auth && perm){
					if (!req.isNull("cmd")){
						int reqInt = req.getInt("cmd");
						
						System.out.println("ReqInt: " + reqInt);
						JSONObject rtnJson = null;
						switch (reqInt){
						case CMD_APPLIANCE_CONTROL:
							System.out.println("APPCON");
							rtnJson = applianceControl(user, req);
							break;
						case CMD_APPLIANCE_INFO:
							System.out.println("APPINFo");
							rtnJson = applianceInfo(user);
							break;
						}
						
						if (rtnJson == null){
							System.out.println("RtnJson is null");
							outJson.put("result", RESULT_INVALID_REQUEST);
						} else {
							System.out.println("Returned json: " + rtnJson);
							outJson.put("data", rtnJson);
							outJson.put("result", RESULT_RESULT_IN_DATA);
						}
					} else {
						System.out.println("No cmd para");
						outJson.put("result", RESULT_INVALID_REQUEST);
					}
				} else {
					System.out.println("Auth: " + auth + " Perm: " + perm + " Not both");
					outJson.put("result", RESULT_INVALID_REQUEST);
				}
			} else {
				System.out.println("Not String user, pass");
				outJson.put("result", RESULT_INVALID_REQUEST);
			}
		} else {
			System.out.println("No user, pass ");
			outJson.put("result", RESULT_INVALID_REQUEST);
		}
		
		System.out.println("OutJson: " + outJson);
		
		return outJson;
	}
	
	public JSONObject applianceControl(String user, JSONObject req){
		if (req.isNull("appName") || req.isNull("turnOn") ||
				!(req.get("appName") instanceof String) ||
				!(req.get("turnOn") instanceof Boolean)){
			System.out.println("Missing para, or invalid para");
			return null;
		}
		
		final JSONObject outJson = new JSONObject();
		final Calendar cal = Calendar.getInstance();
		
		outJson.put("generated", cal.getTimeInMillis());
		
		boolean perm = srv.getPermManager().isUserPermitted(user, "system.control");
		
		if (perm){
			String appName = req.getString("appName");
			boolean turnOn = req.getBoolean("turnOn");
			
			ApplianceManager mgr = srv.getApplianceManager();
			Appliance app = mgr.getAppliance(appName);
			
			if (app != null){
				if (turnOn){
					System.out.println("turnOn");
					app.turnOn(srv);
				} else {
					System.out.println("turnOff");
					app.turnOff(srv);
				}
				
				outJson.put("result", RESULT_OK);
			} else {
				System.out.println("No such App");
				outJson.put("result", RESULT_INVALID_REQUEST);
			}
		} else {
			System.out.println("No perm to do this");
			outJson.put("result", RESULT_INVALID_REQUEST);
		}
		
		return outJson;
	}
	
	public JSONObject applianceInfo(String user){
		final JSONObject outJson = new JSONObject();
		final Calendar cal = Calendar.getInstance();
		
		outJson.put("generated", cal.getTimeInMillis());
		
		boolean perm = srv.getPermManager().isUserPermitted(user, "system.control");
		
		if (perm){
			System.out.println("Getting appliances");
			ApplianceManager mgr = srv.getApplianceManager();
			
			JSONArray arrJson = new JSONArray();
			JSONObject appJson;
			for (Appliance app : mgr.getAppliances()){
				appJson = new JSONObject();
				
				appJson.put("name", app.getName());
				appJson.put("powerCalc", app.getPowerUseCalcSource());
				appJson.put("powerUseKw", app.getPowerUseKiloWatts());
				appJson.put("powerUseW", app.getPowerUseWatts());
				appJson.put("isTurnedOn", app.isTurnedOn());
				
				arrJson.put(appJson);
			}
			System.out.println("Len: " + arrJson.length());
			
			outJson.put("apps", arrJson);
			
			outJson.put("result", RESULT_OK);
		} else {
			System.out.println("No Perm to do this");
			outJson.put("result", RESULT_INVALID_REQUEST);
		}
		
		return outJson;
	}
}
