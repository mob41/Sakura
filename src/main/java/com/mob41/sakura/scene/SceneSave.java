package com.mob41.sakura.scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

public class SceneSave {
	
	public static final  String[] hr = {"00","01","02","03","04","05","06","07","08","09","10",
			"11","12","13","14","15","16","17","18","19","20","21","22","23"
	};
	public static final String[] min = {"00","01","02","03","04","05","06","07","08","09","10",
			"11","12","13","14","15","16","17","18","19","20","21","22","23","24",
			"25","26","27","28","29","30","31","32","33","34","35","36","37","38",
			"39","40","41","42","43","44","45","46","47","48","49","50","51","52",
			"53","54","55","56","57","58","59"
	};

	public static final String[] colident = {"Name", "During", "Trigger"};
	public static final String[] deftriggers = {"time-period", "spec-time", "bellevent"};
	public static final String[] defactions = {"stop-scrnsave", "sendbutton", "delay"};
	
	private static List<String[]> scenesdata = new ArrayList<String[]>(500);
	private static List<String[]> actionsdata = new ArrayList<String[]>(500);
	private static List<String[]> triggersdata = new ArrayList<String[]>(500);
	
	/*
	 (): Properties Key
	 
	 Scene Array:
	 
	 [Name (name)], [Unique ID (uid)], [Running (Not saved)]
	 
	 Trigger Array:
	 
	 [Trigger's Name (name)], [Target Unique ID (uid)], [Trigger Value (value)]
	 
	 Action Array:
	 
	 [Name (name)], [Target Unique ID (uid)], [Action Type (type)], [Type Value (value)]
	 */
	/*
	 (sendbutton) Action Value Code:
	 
	 [Remote's UUID]#[Button ID]#[RM Device MAC]#
	 
	*/
	
	public static void setSceneNewData(String uid, String[] data){
		int index = getSceneIndex(uid);
		if (index == -1){
			return;
		}
		scenesdata.set(index, data);
	}
	
	public static int getAllScenesAmount(){
		return scenesdata.size();
	}
	
	public static int getAllActionsAmount(){
		return actionsdata.size();
	}
	
	public static int getAllTriggersAmount(){
		return triggersdata.size();
	}
	
	public static String[] getScene(int index){
		return scenesdata.get(index);
	}
	
	public static String[] getScene(String uid){
		int index = getSceneIndex(uid);
		if (index == -1){
			return null;
		}
		return getScene(index);
	}
	
	public static String[] getTrigger(int index){
		return triggersdata.get(index);
	}
	
	public static String[] getTrigger(String triggername, String uid){
		int index = getTriggerIndex(triggername, uid);
		if (index == -1){
			return null;
		}
		return getTrigger(index);
	}
	
	public static String[] getAction(int index){
		return actionsdata.get(index);
	}
	
	public static String[] getAction(String actionname, String uid){
		int index = getActionIndex(actionname, uid);
		if (index == -1){
			return null;
		}
		return getAction(index);
	}
	
	public static int getDefinedTriggerIndex(String triggername){
		for (int i = 0; i < deftriggers.length; i++){
			if (deftriggers[i].equals(triggername)){
				return i;
			}
		}
		return -1;
	}
	
	public static int getDefinedActionIndex(String actionname){
		for (int i = 0; i < defactions.length; i++){
			if (defactions[i].equals(actionname)){
				return i;
			}
		}
		return -1;
	}
	
	public static int getSceneIndex(String uid){
		String[] data;
		for (int i = 0; i < scenesdata.size(); i++){
			data = scenesdata.get(i);
			if (data[1].equals(uid)){
				return i;
			}
		}
		return -1;
	}
	
	public static int getTriggerIndex(String triggername, String uid){
		String[] data;
		for (int i = 0; i < triggersdata.size(); i++){
			data = triggersdata.get(i);
			if (data[0].equals(triggername) && data[1].equals(uid)){
				return i;
			}
		}
		return -1;
	}
	
	public static int getActionIndex(String actionname, String uid){
		String[] data;
		for (int i = 0; i < actionsdata.size(); i++){
			data = actionsdata.get(i);
			if (data[0].equals(actionname) && data[1].equals(uid)){
				return i;
			}
		}
		return -1;
	}
	
	public static String[] convertSaveDataIntoTableData(String[] savedata){
		String[] output = new String[3];
		String build = "";
		List<String[]> actions = getAllActions(savedata[1]);
		List<String[]> triggers = getAllTriggers(savedata[1]);
		output[0] = savedata[0];
		for (int i = 0; i < actions.size(); i++){
			build += actions.get(i)[0];
			if (i != actions.size() - 1){
				build += ", ";
			}
		}
		output[1] = build;
		build = "";
		for (int i = 0; i < triggers.size(); i++){
			build += triggers.get(i)[0];
			if (i != triggers.size() - 1){
				build += ", ";
			}
		}
		output[2] = build;
		return output;
	}
	
	public static String[] seperateActionValueCode(String code){
		String[] output = new String[3];
		int i;
		int tmp;
		for (i = 0; i < code.length(); i++){
			if (code.charAt(i) == '#'){
				break;
			}
		}
		output[0] = code.substring(0, i);
		tmp = i + 1;
		for (i++; i < code.length(); i++){
			if (code.charAt(i) == '#'){
				break;
			}
		}
		output[1] = code.substring(tmp, i);
		tmp = i + 1;
		for (i++; i < code.length(); i++){
			if (code.charAt(i) == '#'){
				break;
			}
		}
		output[2] = code.substring(tmp, i);
		tmp = i + 1;
		return output;
	}
	
	public static List<String[]> getAllScenes(){
		return scenesdata;
	}
	
	public static List<String[]> getAllActions(String uid){
		List<String[]> out = new ArrayList<String[]>(50);
		String[] data;
		for (int i = 0; i < getAllActionsAmount(); i++){
			data = actionsdata.get(i);
			if (data[1].equals(uid)){
				out.add(data);
			}
		}
		return out;
	}
	
	public static List<String[]> getAllTriggers(String uid){
		List<String[]> out = new ArrayList<String[]>(50);
		String[] data;
		for (int i = 0; i < getAllTriggersAmount(); i++){
			data = triggersdata.get(i);
			if (data[1].equals(uid)){
				out.add(data);
			}
		}
		return out;
	}
	
	
	
	public static String addScene(String name){
		String uid = getRandomEncodedString();
		String[] build = new String[]{name, uid, "false"};
		addScene(build);
		return uid;
	}
	
	public static void addScene(String[] data){
		scenesdata.add(data);
	}
	
	public static void insertAction(String at_actionname, String at_uid, String name, String uid, String type, String value){
		String[] build = new String[]{name, uid, type, value};
		int index = getActionIndex(at_actionname, at_uid);
		if (index == -1){
			return;
		}
		insertAction(index, build);
	}
	
	public static void insertAction(int index, String name, String uid, String type, String value){
		String[] build = new String[]{name, uid, type, value};
		insertAction(index, build);
	}
	
	public static void insertAction(String at_actionname, String at_uid, String[] data){
		int index = getActionIndex(at_actionname, at_uid);
		if (index == -1){
			return;
		}
		insertAction(index, data);
	}
	
	public static void insertAction(int index, String[] data){
		actionsdata.add(index, data);
	}
	
	public static void addAction(String name, String uid, String type, String value){
		String[] build = new String[]{name, uid, type, value};
		addAction(build);
	}
	
	public static void addAction(String[] data){
		actionsdata.add(data);
	}
	
	public static void addTrigger(String name, String uid, String value){
		String[] build = new String[]{name, uid, value};
		addTrigger(build);
	}
	
	public static void addTrigger(String[] data){
		triggersdata.add(data);
	}
	
	public static void removeAction(int index){
		actionsdata.remove(index);
	}
	
	public static void removeAction(String name, String uid){
		int index = getActionIndex(name, uid);
		actionsdata.remove(index);
	}
	
	public static void removeTrigger(int index){
		triggersdata.remove(index);
	}
	
	public static void removeTrigger(String name, String uid){
		int index = getTriggerIndex(name, uid);
		triggersdata.remove(index);
	}
	
	public static void removeScene(int index){
		scenesdata.remove(index);
	}
	
	public static void removeSceneAll(int index){
		String uid = getScene(index)[1];
		scenesdata.remove(index);
		List<String[]> data;
		data = getAllActions(uid);
		int arrindex;
		for (int i = 0; i < data.size(); i++){
			arrindex = getActionIndex(data.get(i)[0], uid);
			actionsdata.remove(arrindex);
		}
		data = getAllTriggers(uid);
		for (int i = 0; i < data.size(); i++){
			arrindex = getTriggerIndex(data.get(i)[0], uid);
			triggersdata.remove(arrindex);
		}
	}
	
	public static String buildActionCode(String remoteuuid, String buttonid, String devicemac){
		return remoteuuid + "#" + buttonid + "#" + devicemac + "#";
	}
	
	public static void load(){
		try {
			File file = new File("ha_scenesave.properties");
			if (!file.exists()){
				createFile();
				return;
			}
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(file);
			prop.load(in);
			int scenes;
			int i;
			int triggers;
			int actions;
			String[] build;
			
			scenes = Integer.parseInt(prop.getProperty("scenes"));
			for (i = 0; i < scenes; i++){
				build = new String[3];
				build[0] = prop.getProperty("scene-" + i + "-name");
				build[1] = prop.getProperty("scene-" + i + "-uid");
				build[2] = "false";
				scenesdata.add(build);
			}
			
			triggers = Integer.parseInt(prop.getProperty("triggers"));
			for (i = 0; i < triggers; i++){
				build = new String[3];
				build[0] = prop.getProperty("trigger-" + i + "-name");
				build[1] = prop.getProperty("trigger-" + i + "-uid");
				build[2] = prop.getProperty("trigger-" + i + "-value");
				triggersdata.add(build);
			}
			
			actions = Integer.parseInt(prop.getProperty("actions"));
			for (i = 0; i < actions; i++){
				build = new String[4];
				build[0] = prop.getProperty("action-" + i + "-name");
				build[1] = prop.getProperty("action-" + i + "-uid");
				build[2] = prop.getProperty("action-" + i + "-type");
				build[3] = prop.getProperty("action-" + i + "-value");
				actionsdata.add(build);
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static void writeIn(){
		try {
			File file = new File("ha_scenesave.properties");
			if (!file.exists()){
				file.createNewFile();
			}
			Properties prop = new Properties();
			int i;
			int scenes = scenesdata.size();
			int triggers = triggersdata.size();
			int actions = actionsdata.size();
			prop.setProperty("scenes", Integer.toString(scenes));
			prop.setProperty("triggers", Integer.toString(triggers));
			prop.setProperty("actions", Integer.toString(actions));
			String[] data;
			for (i = 0; i < scenes; i++){
				data = scenesdata.get(i);
				prop.setProperty("scene-" + i + "-name", data[0]);
				prop.setProperty("scene-" + i + "-uid", data[1]);
			}
			for (i = 0; i < triggers; i++){
				data = triggersdata.get(i);
				prop.setProperty("trigger-" + i + "-name", data[0]);
				prop.setProperty("trigger-" + i + "-uid", data[1]);
				prop.setProperty("trigger-" + i + "-value", data[2]);
			}
			for (i = 0; i < actions; i++){
				data = actionsdata.get(i);
				prop.setProperty("action-" + i + "-name", data[0]);
				prop.setProperty("action-" + i + "-uid", data[1]);
				prop.setProperty("action-" + i + "-type", data[2]);
				prop.setProperty("action-" + i + "-value", data[3]);
			}
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, "HA Scene Save");
			out.flush();
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static void createFile(){
		try {
			File file = new File("ha_scenesave.properties");
			if (!file.exists()){
				file.createNewFile();
			}
			Properties prop = new Properties();
			prop.setProperty("scenes", "0");
			prop.setProperty("triggers", "0");
			prop.setProperty("actions", "0");
			
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, "HA Scene Save");
			out.flush();
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	//Functions
	
	public static int[] transDuringIntoTime(String during){
		String strshr;
		String strsmin;
		String strehr;
		String stremin;
		int tmp;
		int[] output = new int[4];
		int i;
		for (i = 0; i < during.length(); i++){
			if (during.charAt(i) == '#'){
				break;
			}
		}
		strshr = during.substring(0,i);
		tmp = i + 1;
		for (i++; i < during.length(); i++){
			if (during.charAt(i) == '#'){
				break;
			}
		}
		strsmin = during.substring(tmp, i);
		tmp = i + 1;
		for (i++; i < during.length(); i++){
			if (during.charAt(i) == '#'){
				break;
			}
		}
		strehr = during.substring(tmp, i);
		tmp = i + 1;
		for (i++; i < during.length(); i++){
			if (during.charAt(i) == '#'){
				break;
			}
		}
		stremin = during.substring(tmp, i);
		try {
			output[0] = Integer.parseInt(strshr);
			output[1] = Integer.parseInt(strsmin);
			output[2] = Integer.parseInt(strehr);
			output[3] = Integer.parseInt(stremin);
		} catch (NumberFormatException e){
			return null;
		}
		return output;
	}
	
	public static int[] transSpecTimeDuringIntoTime(String during){
		String strhr;
		String strmin;
		int tmp;
		int[] output = new int[2];
		int i;
		
		for (i = 0; i < during.length(); i++){
			if (during.charAt(i) == '#'){
				break;
			}
		}
		strhr = during.substring(0,i);
		tmp = i + 1;
		for (i++; i < during.length(); i++){
			if (during.charAt(i) == '#'){
				break;
			}
		}
		strmin = during.substring(tmp, i);
		try {
			output[0] = Integer.parseInt(strhr);
			output[1] = Integer.parseInt(strmin);
		} catch (NumberFormatException e){
			return null;
		}
		return output;
	}
	
	public static String transTimeIntoDuring(int shr, int smin, int ehr, int emin){
		String output = shr + "#" + smin + "#" + ehr + "#" + emin + "#";
		return output;
	}
	
	public static String transTimeIntoReadableDuring(int shr, int smin, int ehr, int emin){
		String shour;
		String sminute;
		String ehour;
		String eminute;
		
		shour = shr < 10 ? "0" + shr : Integer.toString(shr);
		sminute = smin < 10 ? "0" + smin : Integer.toString(smin);
		
		ehour = ehr < 10 ? "0" + ehr : Integer.toString(ehr);
		eminute = emin < 10 ? "0" + emin : Integer.toString(emin);
		
		String output = shour + ":" + sminute + "~" + ehour + ":" + eminute;
		return output;
	}
	
	public static int[] transReadableDuringIntoTime(String timeduring_readable){
		int[] output = new int[4];
		int i;
		int tmp;
		String strshr;
		String strsmin;
		String strehr;
		String stremin;
		
		for (i = 0; i < timeduring_readable.length(); i++){
			if (timeduring_readable.charAt(i) == ':'){
				break;
			}
		}
		strshr = timeduring_readable.substring(0,i);
		tmp = i + 1;
		for (i++; i < timeduring_readable.length(); i++){
			if (timeduring_readable.charAt(i) == '~'){
				break;
			}
		}
		strsmin = timeduring_readable.substring(tmp,i);
		tmp = i + 1;
		for (i++; i < timeduring_readable.length(); i++){
			if (timeduring_readable.charAt(i) == ':'){
				break;
			}
		}
		strehr = timeduring_readable.substring(tmp,i);
		i++;
		stremin = timeduring_readable.substring(i,timeduring_readable.length());
		try {
			output[0] = Integer.parseInt(strshr);
			output[1] = Integer.parseInt(strsmin);
			output[2] = Integer.parseInt(strehr);
			output[3] = Integer.parseInt(stremin);
		} catch (NumberFormatException e){
			e.printStackTrace();
			return null;
		}
		return output;
	}
	
	private static String getRandomEncodedString(){
    	Random rand = new Random();
    	byte[] key = new byte[16];
    	rand.nextBytes(key);
    	return Base64.encodeBase64String(key);
    }

}
