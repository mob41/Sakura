package com.github.mob41.sakura.api;

import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.mob41.sakura.appliance.Appliance;
import com.github.mob41.sakura.appliance.ApplianceManager;
import com.github.mob41.sakura.hash.AesUtil;

public class SakuraAPI {
	
	public static final int RESULT_NO_SUCH_SESSION = -8;
	
	public static final int RESULT_DECRYPTION_ERROR = -7;
	
	public static final int RESULT_ENCRYPTION_ERROR = -6;
	
	public static final int RESULT_REQUEST_DECRYPTION_ERROR = -5;
	
	public static final int RESULT_RESPONSE_ENCRYPTION_ERROR = -4;
	
	public static final int RESULT_SERVER_ERROR = -3;
	
	public static final int RESULT_PERMISSION_DENIED = -2;
	
	public static final int RESULT_INVALID_REQUEST = -1;
	
	public static final int RESULT_NO_DATA = 0;
	
	public static final int RESULT_OK = 1;
	
	public static final int RESULT_DATA_ENCRYPTED = 2;
	
	public static final int CMD_TEST = 0;
	
	public static final int CMD_GAIN_ACCESS = 1;
	
	public static final int CMD_APPLIANCE_CONTROL = 2;
	
		public static final int APP_CON_CMD_GET_APPS = 0; //Appliance Control: Command: Get appliances
	
		public static final int APP_CON_CMD_SWITCH_APP = 1; //Appliance Control: Command: Switch on/off appliances
	
	private final SakuraServer srv;
	
	private final AesUtil aesUtil;
	
	private final APIEncKeyThread encThd;

	protected SakuraAPI(SakuraServer srv) {
		this.srv = srv;
		aesUtil = new AesUtil(128, 1000);
		encThd = new APIEncKeyThread();
	}
	
	/**
	 * Request the API.<br>
	 * <br>
	 * A request is followed with a
	 * command <code>cmd</code> parameter, and different parameters
	 * required by each command.<br>
	 * <br>
	 * A response is followed with a <code>result</code> parameter
	 * and a <code>generated</code> parameter. And if the result code
	 * is <code>RESULT_OK</code>, a <code>data</code> parameter will
	 * be enclosed.<br>
	 * <br>
	 * The <code>data</code> parameter is a encrypted
	 * raw data. The data cannot be decrypted without the
	 *  <code>system_api.properties</code> keys.<br>
	 * <br>
	 * This provides a full API input/output environment.
	 * The <code>JSONObject</code> does not have to be
	 * encrypted. But the <code>data</code> parameter instead.<br>
	 * <br>
	 * A invalid request will return a Invalid Request result code.
	 * @param in
	 * @return
	 */
	public JSONObject request(JSONObject in){
		
		JSONObject out = new JSONObject();
		out.put("generated", Calendar.getInstance().getTimeInMillis());
		
		if (in == null || in.isNull("cmd") || in.isNull("data")){
			out.put("result", RESULT_INVALID_REQUEST);
			System.out.println("Missing CMD");
			return out;
		}
		
		int cmd = in.getInt("cmd");
		switch (cmd){
		case CMD_GAIN_ACCESS:
			String gainAccessJson = gainAccess(in.getString("data"));
			if (gainAccessJson == null){
				System.out.println("Null gain access json");
			}
			out.put("data", gainAccessJson);
			out.put("result", gainAccessJson != null ? RESULT_OK : RESULT_INVALID_REQUEST);
			break;
		case CMD_APPLIANCE_CONTROL:
			if (in.isNull("data")){
				out.put("result", RESULT_INVALID_REQUEST);
				break;
			}
			
			out.put("result", RESULT_OK);
			out.put("data", applianceControl(in.getString("data")));
			break;
		default:
			out.put("result", RESULT_INVALID_REQUEST);
		}
		return out;
	}
	
	/**
	 * Returns an encrypted, cover-encrypted JSON in String (Base64 String of bytes)<br>
	 * <br>
	 * The JSON contains:<br>
	 * Session Encryption IV (random parameter, but static "parameter"'s parameter, encryption keys won't change until session expires)<br>
	 * Session Encryption Salt (same above)<br>
	 * Session Encryption Pass (same above)<br>
	 * Session UID (same above)<r>
	 * Raw encryption data (same above)<br>
	 * Random IV parameter name (static random hash)<br>
	 * Random Salt parameter name (static random hash)<br>
	 * Random Pass parameter name (static random hash)<br>
	 * Random Data parameter name (static random hash)<br>
	 * <br>
	 * Without one of the parameter, the data could not be decrypted.
	 * 
	 * @return a raw Base64 string, can be decoded to bytes using <code>common-codec</code>
	 */
	public String gainAccess(String encReq){
		//Encrypted Request decryption
		ResultString data = toDec(encReq);
		
		JSONObject json = new JSONObject();
		json.put("generated", Calendar.getInstance().getTimeInMillis());
		
		if (data.getResult() != RESULT_OK){
			json.put("result", data.getResult());
			System.out.println("Not OKAY: " + data.getResult());
			return toEnc(json.toString()).getString();
		}
		
		JSONObject req;
		try {
			req = new JSONObject(data.getString());
		} catch (JSONException e){
			json.put("result", RESULT_INVALID_REQUEST);
			return toEnc(json.toString()).getString();
		}
		//End - Encrypted request decryption
		
		System.out.println("[" + req.toString() + "]");
		if (req.isNull("apikey") || req.isNull("usr") || req.isNull("pwd")){
			json.put("result", RESULT_INVALID_REQUEST);
			System.out.println("Null usr pwd");
			return toEnc(json.toString()).getString();
		}
		
		System.out.println("authing");
		boolean auth = srv.getAPIManager().authenticate(req.getString("apikey"), req.getString("usr"), req.getString("pwd"));
		System.out.println("authdone");
		
		if (!auth){
			System.out.println("Auth failed");
			return null;
		}
		System.out.println("Auth success");
		
		JSONObject out = new JSONObject();
		
		APISession s = encThd.registerSession();
		out.put(s.getEncIVPara(), s.getEncIv());
		out.put(s.getEncPassPara(), s.getEncPass());
		out.put(s.getEncSaltPara(), s.getEncSalt());
		out.put(s.getUidPara(), s.getUid());
		out.put(s.getDataPara(), toSessEnc(s.getUid(), json.toString())); //No data at now
		
		APIManager mgr = srv.getAPIManager();
		out.put(mgr.getParaIv(), s.getEncIVPara());
		out.put(mgr.getParaKey(), s.getEncPassPara());
		out.put(mgr.getParaSalt(), s.getEncSaltPara());
		out.put(mgr.getParaUid(), s.getUidPara());
		out.put(mgr.getParaData(), s.getDataPara());
		
		System.out.println("out: [" + out + "]");
		return toEnc(out.toString()).getString();
	}
	
	/**
	 * Control appliances.<br>
	 * <br>
	 * The encrypted request must contain the control command parameter <code>cmd</code> in the json.
	 * The request is encrypted using <code>toEnc()</code> and decrypted using <code>toSessDec()</code>
	 * If the session expires, the request will no longer able to decrypt.
	 * @param encReq A encrypted request raw data in String
	 * @return Response in JSONObject
	 */
	public String applianceControl(String encReq){
		SessDecString data = toSessDec(encReq);
		
		JSONObject json = new JSONObject();
		json.put("generated", Calendar.getInstance().getTimeInMillis());
		
		if (data.getResult() != RESULT_OK){
			json.put("result", data.getResult());
			return toEnc(json.toString()).getString();
		}
		
		JSONObject req;
		try {
			req = new JSONObject(data.getString());
		} catch (JSONException e){
			System.out.println("Invalid structure: " + e);
			json.put("result", RESULT_INVALID_REQUEST);
			return toSessEnc(data.getSessionUid(), json.toString()).getString();
		}
		
		System.out.println("req: [" + req + "]");
		
		if (req.isNull("cmd") || !(req.get("cmd") instanceof Integer)){
			json.put("result", RESULT_INVALID_REQUEST);
			return toSessEnc(data.getSessionUid(), json.toString()).getString();
		}
		
		ApplianceManager mgr = srv.getApplianceManager();
		
		int cmd = req.getInt("cmd");
		switch (cmd){
		case APP_CON_CMD_GET_APPS:
			List<Appliance> apps = mgr.getAppliances();
			
			for (int i = 0; i < apps.size(); i++){
				
			}
			break;
		case APP_CON_CMD_SWITCH_APP:
			if (req.isNull("name") || req.isNull("switch")){
				json.put("result", RESULT_INVALID_REQUEST);
				System.out.println("Missing parameters");
				return toSessEnc(data.getSessionUid(), json.toString()).getString();
			}
			
			String name = req.getString("name");
			boolean turnOn = req.getBoolean("switch");
			
			Appliance app = mgr.getAppliance(name);
			
			if (app == null){
				json.put("result", RESULT_INVALID_REQUEST);
				System.out.println("Wrong App");
				return toSessEnc(data.getSessionUid(), json.toString()).getString();
			}
			
			if (turnOn){
				app.turnOn(srv);
			} else {
				app.turnOff(srv);
			}
			break;
		default:
			json.put("result", RESULT_INVALID_REQUEST);
			return toSessEnc(data.getSessionUid(), json.toString()).getString();
		}
		
		json.put("result", RESULT_OK);
		return toSessEnc(data.getSessionUid(), json.toString()).getString();
	}
	
	/**
	 * Control actions<br>
	 * <br>
	 * The encrypted request must contain <code>name</code> and <code>switch</code> in the json.
	 * The request is encrypted using <code>toEnc()</code> and decrypted using <code>toSessDec()</code>
	 * If the session expires, the request will no longer able to decrypt.
	 * @param encReq A encrypted request raw data in String
	 * @return Response in JSONObject
	 */
	public String actionsControl(String encReq){
		SessDecString data = toSessDec(encReq);
		
		JSONObject json = new JSONObject();
		json.put("generated", Calendar.getInstance().getTimeInMillis());
		
		if (data.getResult() != RESULT_OK){
			json.put("result", data.getResult());
			return toEnc(json.toString()).getString();
		}
		
		JSONObject req;
		try {
			req = new JSONObject(data.getString());
		} catch (JSONException e){
			System.out.println("Invalid structure: " + e);
			json.put("result", RESULT_INVALID_REQUEST);
			return toSessEnc(data.getSessionUid(), json.toString()).getString();
		}
		
		System.out.println("req: [" + req + "]");
		
		if (req.isNull("name") || req.isNull("switch")){
			json.put("result", RESULT_INVALID_REQUEST);
			System.out.println("Missing parameters");
			return toSessEnc(data.getSessionUid(), json.toString()).getString();
		}
		
		ApplianceManager mgr = srv.getApplianceManager();
		
		String name = req.getString("name");
		boolean turnOn = req.getBoolean("switch");
		
		Appliance app = mgr.getAppliance(name);
		
		if (app == null){
			json.put("result", RESULT_INVALID_REQUEST);
			System.out.println("Wrong App");
			return toSessEnc(data.getSessionUid(), json.toString()).getString();
		}
		
		if (turnOn){
			app.turnOn(srv);
		} else {
			app.turnOff(srv);
		}
		
		json.put("result", RESULT_OK);
		return toSessEnc(data.getSessionUid(), json.toString()).getString();
	}
	
	public JSONObject accessExtApi(String encReq){
		return null;
	}
	
	public SessDecString toSessDec(String data){
		String dec;
		APIManager mgr = srv.getAPIManager();
		try {
			dec = aesUtil.decrypt(mgr.getEncSalt(), mgr.getEncIv(), mgr.getEncKey(), data);
			
			JSONObject json;
			try {
				json = new JSONObject(dec);
			} catch (JSONException e){
				return new SessDecString(RESULT_INVALID_REQUEST, null, null);
			}
			
			if (json.isNull("session") || json.isNull("data")){
				return new SessDecString(RESULT_INVALID_REQUEST, null, null);
			}
			
			APISession s = encThd.getSession(json.getString("session"));
			
			if (s == null){
				return new SessDecString(RESULT_NO_SUCH_SESSION, null, null);
			}
			
			dec = aesUtil.decrypt(s.getEncSalt(), s.getEncIv(), s.getEncPass(), json.getString("data"));

			return new SessDecString(RESULT_OK, s.getUid(), dec);
		} catch (Exception e){
			return new SessDecString(RESULT_DECRYPTION_ERROR, null, null);
		}
	}
	
	public ResultString toDec(String data){
		APIManager mgr = srv.getAPIManager();
		String dec;
		try {
			dec = aesUtil.decrypt(mgr.getEncSalt(), mgr.getEncIv(), mgr.getEncKey(), data);
		} catch (Exception e){
			return new ResultString(RESULT_ENCRYPTION_ERROR, null);
		}
		
		return new ResultString(RESULT_OK, dec);
	}
	
	public ResultString toEnc(String data){
		APIManager mgr = srv.getAPIManager();
		String enc;
		try {
			enc = aesUtil.encrypt(mgr.getEncSalt(), mgr.getEncIv(), mgr.getEncKey(), data);
		} catch (Exception e){
			return new ResultString(RESULT_ENCRYPTION_ERROR, null);
		}
		
		return new ResultString(RESULT_OK, enc);
	}
	
	public ResultString toSessEnc(String sessionStr, String data){
		APISession s = encThd.getSession(sessionStr);
		
		if (s == null){
			return new ResultString(RESULT_NO_SUCH_SESSION, null);
		}
		
		APIManager mgr = srv.getAPIManager();
		String enc;
		try {
			enc = aesUtil.encrypt(s.getEncSalt(), s.getEncIv(), s.getEncPass(), data);
			
			JSONObject json = new JSONObject();
			json.put("session", sessionStr);
			json.put("data", enc);
			
			enc = aesUtil.encrypt(mgr.getEncSalt(), mgr.getEncIv(), mgr.getEncKey(), json.toString());
		} catch (Exception e){
			return new ResultString(RESULT_ENCRYPTION_ERROR, null);
		}
		
		return new ResultString(RESULT_OK, enc);
	}
	
	

}
