package com.mob41.sakura.servlets.old;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.swing.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.mob41.pushbullet.api.PBClient;
import org.mob41.pushbullet.api.PBServer;

import com.rpi.ha.ui.UI;

public class NotifySchedule implements Runnable {
	
	/*
	 A schedule code:
	 
	 <- username -> # <- schename -> # <- trigger -> # <- BOOLEAN-TimeEnabled -> #
	 <- DURING-TimeOrTrigger -> #
	*/
	
	private static final Logger logger = LogManager.getLogger(NotifySchedule.class.getName());
	
	private static NotifySchedule notifythread;
	private boolean running = false;
	private List<String> sche;
	private ActionListener checkTime = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			triggerCheck();
		}
		
	};
	private Timer clocking = new Timer(120000, checkTime);
	
	public void triggerCheck(String username){
		Calendar cal = Calendar.getInstance();
		Calendar sttime = Calendar.getInstance();
		Calendar edtime = Calendar.getInstance();
		int i;
		String[] data;
		boolean TimeEnabled;
		String title = "";
		String desc = "";
		String during = "";
		int[] time;
		boolean afst; //AFter STart time
		boolean bfed; //BeFore EnD time
		int amountOfSche = getAmountOfSche(username);
		for (i = 0; i < amountOfSche; i++){
			data = getScheduleCode(getUserSche(username)[i]);
			TimeEnabled = Boolean.parseBoolean(data[3]);
			if (TimeEnabled){
				during = data[4];
				time = transDuringIntoTime(during);
				sttime.set(Calendar.HOUR_OF_DAY, time[0]);
				sttime.set(Calendar.MINUTE, time[1]);
				edtime.set(Calendar.HOUR_OF_DAY, time[2]);
				edtime.set(Calendar.MINUTE, time[3]);
				afst = cal.after(sttime);
				bfed = cal.before(edtime);
				if (!afst && bfed){
					return;
				}
			}
			int trigger = Integer.parseInt(data[2]);
			int min = 0;
			String build = "Bus ";
			String strmin = "";
			String busname = "";
			String allbusname = "";
			int rows = 0;
			boolean exist = false;
			int amount = 0;
			triggerActions(data, title, desc, i, trigger, build, amount,
					exist, rows, strmin, min, allbusname, allbusname);
		}
	}

	public void triggerCheck(){
		Calendar cal = Calendar.getInstance();
		Calendar sttime = Calendar.getInstance();
		Calendar edtime = Calendar.getInstance();
		int i;
		String[] data;
		boolean TimeEnabled;
		String title = "";
		String desc = "";
		String during = "";
		int[] time;
		boolean afst; //AFter STart time
		boolean bfed; //BeFore EnD time
		for (i = 0; i < sche.size(); i++){
			data = getScheduleCode(i);
			TimeEnabled = Boolean.parseBoolean(data[3]);
			if (TimeEnabled){
				during = data[4];
				time = transDuringIntoTime(during);
				sttime.set(Calendar.HOUR_OF_DAY, time[0]);
				sttime.set(Calendar.MINUTE, time[1]);
				edtime.set(Calendar.HOUR_OF_DAY, time[2]);
				edtime.set(Calendar.MINUTE, time[3]);
				afst = cal.after(sttime);
				bfed = cal.before(edtime);
				if (!afst && bfed){
					continue;
				}
			}
			int trigger = Integer.parseInt(data[2]);
			int min = 0;
			String build = "Bus ";
			String strmin = "";
			String busname = "";
			String allbusname = "";
			int rows = 0;
			boolean exist = false;
			int amount = 0;
			triggerActions(data, title, desc, i, trigger, build, amount,
					exist, rows, strmin, min, allbusname, busname);
		}
	}
	
	private void triggerActions(String[] data, String title, String desc, int i, int trigger, String build, int amount,
			boolean exist, int rows, String strmin, int min, String allbusname, String busname){
		switch (trigger){
		case 0:
			//Bus Arrive >20 minutes
			build = "Bus ";
			amount = 0;
			exist = true;
			rows = UI.busArrTimeTable.getRowCount();
			for (i = 0; i < rows; i++){
				busname = (String) UI.busArrTimeTable.getModel().getValueAt(i, 0);
				strmin = (String) UI.busArrTimeTable.getModel().getValueAt(i, 3);
				if (strmin.equals("END") || strmin.equals("---") || strmin == null || strmin == ""){
					exist = false;
					break;
				}
				strmin = strmin.replaceAll("[^0-9]", "");
				min = Integer.parseInt(strmin);
				if (min <= 20){
					if (amount > 0){
						allbusname += "," + busname;
					}
					else
					{
						allbusname += busname;
					}
					amount++;
				}
			}
			build += allbusname;
			if (amount > 1){
				build += " are arriving less than 20 minutes.";
			}
			else
			{
				build += " is arriving less than 20 minutes.";
			}
			title = "Bus " + allbusname + " Arrive(s) >20 minutes";
			desc = build;
			break;
		case 1:
			//Bus Arrive >15 minutes
			build = "Bus ";
			amount = 0;
			exist = true;
			rows = UI.busArrTimeTable.getRowCount();
			for (i = 0; i < rows; i++){
				busname = (String) UI.busArrTimeTable.getModel().getValueAt(i, 0);
				strmin = (String) UI.busArrTimeTable.getModel().getValueAt(i, 3);
				if (strmin.equals("END") || strmin.equals("---") || strmin == null || strmin == ""){
					exist = false;
					break;
				}
				strmin = strmin.replaceAll("[^0-9]", "");
				min = Integer.parseInt(strmin);
				if (min <= 15){
					if (amount > 0){
						allbusname += "," + busname;
					}
					else
					{
						allbusname += busname;
					}
					amount++;
				}
			}
			build += allbusname;
			if (amount > 1){
				build += " are arriving less than 15 minutes.";
			}
			else
			{
				build += " is arriving less than 15 minutes.";
			}
			title = "Bus Arrive >15 minutes";
			desc = build;
			break;	
		case 2:
			//Bus Arrive >10 minutes
			build = "Bus ";
			amount = 0;
			exist = true;
			rows = UI.busArrTimeTable.getRowCount();
			for (i = 0; i < rows; i++){
				busname = (String) UI.busArrTimeTable.getModel().getValueAt(i, 0);
				strmin = (String) UI.busArrTimeTable.getModel().getValueAt(i, 3);
				if (strmin.equals("END") || strmin.equals("---") || strmin == null || strmin == ""){
					exist = false;
					break;
				}
				strmin = strmin.replaceAll("[^0-9]", "");
				min = Integer.parseInt(strmin);
				if (min <= 10){
					if (amount > 0){
						allbusname += "," + busname;
					}
					else
					{
						allbusname += busname;
					}
					amount++;
				}
			}
			build += allbusname;
			if (amount > 1){
				build += " are arriving less than 10 minutes.";
			}
			else
			{
				build += " is arriving less than 10 minutes.";
			}
			title = "Bus " + allbusname + " Arrive(s) >10 minutes";
			desc = build;
			break;
		case 3:
			//Bus Arrive >5 minutes
			build = "Bus ";
			amount = 0;
			exist = true;
			rows = UI.busArrTimeTable.getRowCount();
			for (i = 0; i < rows; i++){
				busname = (String) UI.busArrTimeTable.getModel().getValueAt(i, 0);
				strmin = (String) UI.busArrTimeTable.getModel().getValueAt(i, 3);
				if (strmin.equals("END") || strmin.equals("---") || strmin == null || strmin == ""){
					exist = false;
					break;
				}
				strmin = strmin.replaceAll("[^0-9]", "");
				min = Integer.parseInt(strmin);
				if (min <= 5){
					if (amount > 0){
						allbusname += "," + busname;
					}
					else
					{
						allbusname += busname;
					}
					amount++;
				}
			}
			build += allbusname;
			if (amount > 1){
				build += " are arriving less than 5 minutes.";
			}
			else
			{
				build += " is arriving less than 5 minutes.";
			}
			title = "Bus " + allbusname + " Arrive(s) >5 minutes";
			desc = build;
			break;
		default:
			title = "HANotify Error";
			desc = "Error: Schedule code not readable.";
		}
		int index = PBServer.getCodeIndex(data[0]);
		if (index <= -1){
			return;
		}
		String[] pbdata = PBServer.getPBCode(index);
		try {
			String detoken = HashKey.decrypt(pbdata[1], pbdata[2]);
			String accesstoken = PBServer.deentok(detoken);
			PBClient.pushNote(title, desc, accesstoken);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static NotifySchedule getThread(){
		return notifythread;
	}
	
	public static NotifySchedule launch(){
		logger.trace("Preparing notifying schedule system...");
		notifythread = new NotifySchedule();
		notifythread.run();
		logger.info("Done.");
		return notifythread;
	}
	
	//Functions
	
	public static String buildScheduleCode(String username, String schename, String trigger, boolean TimeEnabled, String during){
		String output = username + "@" + schename + "@" + trigger + "@" + Boolean.toString(TimeEnabled) + "@" + during + "@";
		return output;
	}
	
	public static String buildScheduleCode(String username, String schename, String trigger, String during){
		return buildScheduleCode(username, schename, trigger, true, during);
	}
	
	public static int getAmountOfSche(String username){
		String[] data;
		int amount = 0;
		int i;
		for (i = 0; i < getThread().sche.toArray().length; i++){
			data = getScheduleCode(i);
			if (data[0].equals(username)){
				amount++;
			}
		}
		return amount;
	}
	
	public static String[] getUserSche(String username){
		int amount = getAmountOfSche(username);
		if (amount <= 0){
			return null;
		}
		String[] data;
		String[] output = new String[amount];
		int i;
		int j = 0;
		for (i = 0; i < getThread().sche.size(); i++){
			data = getScheduleCode(i);
			if (data[0].equals(username)){
				output[j] = getThread().sche.get(i);
				j++;
			}
		}
		return output;
	}
	
	public static int getCodeIndex(String username, String schename){
		String[] data;
		int i;
		for (i = 0; i < getThread().sche.size(); i++){
			data = getScheduleCode(i);
			if (data[0].equals(username) && data[1].equals(schename)){
				return i;
			}
		}
		return -1;
	}
	
	public static String[] getScheduleCode(String schecode){
		String data = schecode;
		String[] output = new String[5];
		int i;
		int tmp;
		for (i = 0; i < data.length(); i++){
			if (data.charAt(i) == '@'){
				break;
			}
		}
		output[0] = data.substring(0, i);
		tmp = i + 1;
		for (i++; i < data.length(); i++){
			if (data.charAt(i) == '@'){
				break;
			}
		}
		output[1] = data.substring(tmp, i);
		tmp = i + 1;
		for (i++; i < data.length(); i++){
			if (data.charAt(i) == '@'){
				break;
			}
		}
		output[2] = data.substring(tmp, i);
		tmp = i + 1;
		for (i++; i < data.length(); i++){
			if (data.charAt(i) == '@'){
				break;
			}
		}
		output[3] = data.substring(tmp, i);
		tmp = i + 1;
		for (i++; i < data.length(); i++){
			if (data.charAt(i) == '@'){
				break;
			}
		}
		output[4] = data.substring(tmp, i);
		return output;
	}
	
	public static String[] getScheduleCode(int index){
		Object[] schearr = getThread().sche.toArray();
		if (schearr == null){
			return null;
		}
		String data = (String) schearr[index];
		String[] output = new String[5];
		int i;
		int tmp;
		for (i = 0; i < data.length(); i++){
			if (data.charAt(i) == '@'){
				break;
			}
		}
		output[0] = data.substring(0, i);
		tmp = i + 1;
		for (i++; i < data.length(); i++){
			if (data.charAt(i) == '@'){
				break;
			}
		}
		output[1] = data.substring(tmp, i);
		tmp = i + 1;
		for (i++; i < data.length(); i++){
			if (data.charAt(i) == '@'){
				break;
			}
		}
		output[2] = data.substring(tmp, i);
		tmp = i + 1;
		for (i++; i < data.length(); i++){
			if (data.charAt(i) == '@'){
				break;
			}
		}
		output[3] = data.substring(tmp, i);
		tmp = i + 1;
		for (i++; i < data.length(); i++){
			if (data.charAt(i) == '@'){
				break;
			}
		}
		output[4] = data.substring(tmp, i);
		return output;
	}
	
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
	
	//Non-static
	
	public boolean addNotify(String username, String schename, String trigger, boolean timeenabled, String during){
		String build = buildScheduleCode(username, schename, trigger, during);
		try {
			sche.add(build);
			writeIn();
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	
	public boolean removeNotify(String username, String schename){
		int index = getCodeIndex(username, schename);
		try {
			sche.remove(index);
			writeIn();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addNotify(String username, String schename, String trigger, String during){
		return addNotify(username, schename, trigger, true, during);
	}
	
	public boolean addNotify(String username, String schename, String trigger){
		return addNotify(username, schename, trigger, true, null);
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public NotifySchedule(){
		sche = new ArrayList<String>();
	}

	@Override
	public void run() {
		start();
	}
	
	public void start(){
		if (!running){
			running = true;
			loadFile();
			clocking.start();
		}
	}
	
	public void stop(){
		if (running){
			running = false;
			writeIn();
			clocking.stop();
		}
	}
	
	public void restart(){
		stop();
		start();
	}
	
	public void writeIn(){
		try {
			File file = new File("ha_notifysche.properties");
			if (!file.exists()){
				createFile();
				return;
			}
			Properties prop = new Properties();
		    int sches = sche.size();
		    int i;
		    prop.setProperty("ns", Integer.toString(sches));
		    for (i = 0; i < sches; i++){
		    	prop.setProperty("ns" + i, sche.get(i));
		    }
		    FileOutputStream out = new FileOutputStream(file);
		    prop.store(out, "HANS");
		    out.flush();
		    out.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadFile(){
		try {
			File file = new File("ha_notifysche.properties");
			if (!file.exists()){
				createFile();
				return;
			}
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(file);
			prop.load(in);
			int sches = Integer.parseInt(prop.getProperty("ns"));
			int i;
			String build;
			for (i = 0; i < sches; i++){
				build = prop.getProperty("ns" + i);
				sche.add(build);
			}
			in.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void createFile(){
		try {
			File file = new File("ha_notifysche.properties");
			if (!file.exists()){
				file.createNewFile();
			}
			Properties prop = new Properties();
			prop.setProperty("ns", "0");
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, "HANS");
			out.flush();
			out.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
