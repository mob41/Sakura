package com.github.mob41.sakura.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.mob41.sakura.hash.AesUtil;
import com.github.mob41.sakura.security.PermManager;
import com.github.mob41.sakura.security.UserManager;

public class APIManager {
	
	public static final String PERM_NODE = "system.api";
	
	private static final String DATA_FILE_NAME = "system_api.properties";
	
	private final SakuraServer srv;
	
	private String encKey = null;
	
	private String encIv = null;
	
	private String encSalt = null;
	
	private String parKey = null;
	
	private String parSalt = null;
	
	private String parIv = null;
	
	private String parUid = null;
	
	private String parData = null;
	
	private String apiKey = null;
	
	public APIManager(SakuraServer srv) {
		this.srv = srv;
		loadFile();
	}
	
	protected boolean authenticate(String apiKey, String username, String password){
		if (username == null || password == null || apiKey == null || !this.apiKey.equals(apiKey)){
			return false;
		}
		
		UserManager usrMgr = srv.getUserManager();
		PermManager permMgr = srv.getPermManager();
		
		return usrMgr.authenticate(username, password) &&
				permMgr.isUserPermitted(username, PERM_NODE);
	}
	
	protected String getEncIv(){
		return encIv;
	}
	
	protected String getEncSalt(){
		return encSalt;
	}
	
	protected String getEncKey(){
		return encKey;
	}
	
	protected String getParaIv(){
		return parIv;
	}
	
	protected String getParaSalt(){
		return parSalt;
	}
	
	protected String getParaKey(){
		return parKey;
	}
	
	protected String getParaData(){
		return parData;
	}
	
	protected String getParaUid(){
		return parUid;
	}
	
	protected String getApiKey(){
		return apiKey;
	}
	
	public void loadFile(){
		try {
			File file = new File(DATA_FILE_NAME);
			if (!file.exists()){
				writeFile();
				return;
			}
			FileInputStream in = new FileInputStream(file);
			Properties prop = new Properties();
			prop.load(in);
			
			String str = prop.getProperty("apiKey", null);
			
			if (str == null){
				srv.getErrorManager().report("APIMgr: APIKey is null. The API Key will be regenerated.");
				writeFile(true);
				return;
			}
			
			JSONObject dataJson;
			try {
				dataJson = new JSONObject(new String(Base64.decodeBase64(str), "UTF-8"));
			} catch (JSONException e){
				srv.getErrorManager().report("APIMgr: APIKey is invalid. The API Key will be regenerated.");
				writeFile(true);
				return;
			}
			

			
			if (dataJson.isNull("encKey") || dataJson.isNull("encIv") || dataJson.isNull("encSalt") ||
					dataJson.isNull("parKey") || dataJson.isNull("parIv") || dataJson.isNull("parSalt") ||
					dataJson.isNull("parUid") || dataJson.isNull("parSalt") || dataJson.isNull("apiKey")){
				srv.getErrorManager().report("APIMgr: APIKey is null. The API Key will be regenerated.");
				writeFile(true);
				return;
			}
			
			encKey = dataJson.getString("encKey");
			encIv = dataJson.getString("encIv");
			encSalt = dataJson.getString("encSalt");
			
			parKey = dataJson.getString("parKey");
			parIv = dataJson.getString("parIv");
			parSalt = dataJson.getString("parSalt");
			parUid = dataJson.getString("parUid");
			parData = dataJson.getString("parData");
			
			apiKey = dataJson.getString("apiKey");
			
			in.close();
		} catch (Exception e){
			srv.getErrorManager().report("APIMgr: Unable to load file: " + e);
		}
	}
	
	public void writeFile(){
		writeFile(false);
	}
	
	public void writeFile(boolean regen){
		try {
			File file = new File(DATA_FILE_NAME);
			if (!file.exists()){
				file.createNewFile();
			}
			
			if (regen || apiKey == null ||
					encKey == null || encIv == null || encSalt == null ||
					parKey == null || parIv == null || parSalt == null ||
					parData == null || parUid == null){
				apiKey = AesUtil.random(2048/8);
				
				encKey = AesUtil.random(2048/8);
				encIv = AesUtil.random(128/8);
				encSalt = AesUtil.random(128/8);
				
				parKey = AesUtil.random(2048/8);
				parIv = AesUtil.random(128/8);
				parSalt = AesUtil.random(128/8);
				parData = AesUtil.random(2048/8);
				parUid = AesUtil.random(128/8);
			}
			
			JSONObject dataJson = new JSONObject();
			dataJson.put("apiKey", apiKey);
			
			dataJson.put("encKey", encKey);
			dataJson.put("encIv", encIv);
			dataJson.put("encSalt", encSalt);
			
			dataJson.put("parKey", parKey);
			dataJson.put("parIv", parIv);
			dataJson.put("parSalt", parSalt);
			dataJson.put("parData", parData);
			dataJson.put("parUid", parUid);
			
			Properties prop = new Properties();
			prop.put("apiKey", Base64.encodeBase64String(dataJson.toString().getBytes("UTF-8")));			
			
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, 
					"SakuraAPI API Key. Keep this safe."
					+ " API applications that have this file will gain full access."
					+ " Copy this file to your API application working directory to"
					+ "let them gain access.");
			
			out.flush();
			out.close();
		} catch (Exception e){
			srv.getErrorManager().report("APIMgr: Unable to write file: " + e);
		}
	}

}
