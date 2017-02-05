package com.github.mob41.sakura;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Conf {
	
	//Global Variables
	public static String startTime;
	
	//RM Control Methods (DEFAULT, Not saved)
	public static final String[] rmcontrol_methods = {"rm-bridge", "sdk"};
		
	//Device Name. MUST BE FINAL
	public static final String DeviceName = "HomeAutoTSGUI-RPi";
	
	//HKO Weather Feed URL
	public static String hkoFeedURL = "http://rss.weather.gov.hk/rss/CurrentWeather_uc.xml";
	
	//Use 24-hour clock?
	public static boolean hour24 = false;
	
	//Logging to file
	public static boolean loggingToFile = false;
	
	//Enable logging
	public static boolean logging = true;
	
	//Screensaver Timeout in Seconds
	public static int scrsaver_timeout = 300;
	public static boolean scrsaver = true;
	
	//BASE64 Bytes
	public static String base641 = "";
	public static String base642 = "";
	public static String base643 = "";
	public static String base644 = "";
	
	//PushBullet Client id
	public static String pushbullet_clientid = "";
	
	//PushBullet Client secret
	public static String pushbullet_clientsecret = "";
	
	//PushBullet API Key
	public static String pushbullet_apikey = "";
	
	//PushBullet Server Salt
	public static String pushbullet_serversalt = "";
	
	//HomeDashboard URL (Do not add dash "/" to the back)
	public static String homedash_url = "http://127.0.0.1";
	
	//API Port
	public static int api_port = 8080;
	
	//RM-Bridge URL
	public static String rmbridge_url = "http://192.168.0.1:7474";

	//RM Control Method Using
	public static String rmcontrol_usingMethod = rmcontrol_methods[0];
	
	public static void readConf(){
		File file = new File("ha_settings.properties");
		if (!file.exists()){
			writeConf();
			return;
		}
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(file);
			prop.load(in);
			
			//Screensaver
			scrsaver = Boolean.parseBoolean(prop.getProperty("scrsaver", Boolean.toString(scrsaver)));
			scrsaver_timeout = Integer.parseInt(prop.getProperty("scrsaver_timeout", Integer.toString(scrsaver_timeout)));
			
			//Clock
			hour24 = Boolean.parseBoolean(prop.getProperty("hour24", Boolean.toString(hour24)));
			
			//Logging
			loggingToFile = Boolean.parseBoolean(prop.getProperty("loggingToFile", Boolean.toString(loggingToFile)));
			logging = Boolean.parseBoolean(prop.getProperty("logging", Boolean.toString(logging)));
			
			//BASE64
			base641 = prop.getProperty("base641", base641);
			base642 = prop.getProperty("base642", base641);
			base643 = prop.getProperty("base643", base641);
			base644 = prop.getProperty("base644", base641);
			
			//PushBullet Client
			pushbullet_clientid = prop.getProperty("pushbullet_clientid", pushbullet_clientid);
			pushbullet_clientsecret = prop.getProperty("pushbullet_clientsecret", pushbullet_clientsecret);
			pushbullet_apikey = prop.getProperty("pushbullet_apikey", pushbullet_apikey);
			
			//PushBullet Server
			pushbullet_serversalt = prop.getProperty("pushbullet_serversalt", pushbullet_serversalt);
			
			//HomeDash URL
			homedash_url = prop.getProperty("homedash_url", homedash_url);
			
			//API Port
			api_port = Integer.parseInt(prop.getProperty("api_port", Integer.toString(api_port)));
			
			//RM-Bridge URL
			rmbridge_url = prop.getProperty("rmbridge_url", rmbridge_url);
			
			//RM Control Method
			rmcontrol_usingMethod = prop.getProperty("rmcontrol_usingMethod", rmcontrol_usingMethod);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void writeConf(){
		File file = new File("ha_settings.properties");
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Properties prop = new Properties();
		
		//Screensaver
		prop.setProperty("scrsaver", Boolean.toString(scrsaver));
		prop.setProperty("scrsaver_timeout", Integer.toString(scrsaver_timeout));
		
		//24-hour
		prop.setProperty("hour24", Boolean.toString(hour24));
		
		//Logging
		prop.setProperty("loggingToFile", Boolean.toString(loggingToFile));
		prop.setProperty("logging", Boolean.toString(logging));
		
		//BASE64
		prop.setProperty("base641", base641);
		prop.setProperty("base642", base642);
		prop.setProperty("base643", base643);
		prop.setProperty("base644", base644);
		
		//PushBullet Client
		prop.setProperty("pushbullet_clientid", pushbullet_clientid);
		prop.setProperty("pushbullet_clientsecret", pushbullet_clientsecret);
		prop.setProperty("pushbullet_apikey", pushbullet_apikey);
		
		//PushBullet Server
		prop.setProperty("pushbullet_serversalt", pushbullet_serversalt);
		
		//HomeDash URL
		prop.setProperty("homedash_url", homedash_url);
		
		//API Port
		prop.setProperty("api_port", Integer.toString(api_port));
		
		//RM-Bridge URL
		prop.setProperty("rmbridge_url", rmbridge_url);
		
		//RM Control Method
		prop.setProperty("rmcontrol_usingMethod", rmcontrol_usingMethod);
		
		try {
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, "HA Settings File");
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
