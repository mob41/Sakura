package com.mob41.sakura.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mob41.pushbullet.api.PBServer;
import com.mob41.sakura.ann.AnnounceMem;
import com.mob41.sakura.ann.BellAlarmUI;

public class UDPServer implements Runnable {

	private static final Logger logger = LogManager.getLogger("UDPServer");
	
	private static Thread thread;
	
	private static UDPServer runnable;
	
	private boolean end = false;
	
	public static Thread getThread(){
		return thread;
	}
	
	public static UDPServer getRunnable(){
		return runnable;
	}
	
	public static void startThread(){
		runnable = new UDPServer();
		thread = new Thread(runnable);
		thread.setName("HA_UDPServer");
		thread.start();
	}
	
	public boolean isRunning(){
		return !end;
	}
	
	public void turnon(){
		run();
	}
	
	public void restart(){
		shutdown();
		turnon();
	}
	
	public void shutdown(){
		if (!end){
			end = false;
		}
	}
	
	@Override
	public void run(){
		if (!end){
			end = false;
			try {
				int port = 6099;
				DatagramSocket dsocket = new DatagramSocket(port);
			    byte[] buffer = new byte[2048];
			    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			    
			    while (!end){
			    	float voltage;
			    	int voltagepercentage;
			    	dsocket.receive(packet);
			    	String msg = new String(buffer, 0 , packet.getLength());
			    	logger.trace("Recevied UDP Packet from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + "): " + msg);
			    	if (msg == null || msg == ""){
			    		continue;
			    	}
			    	String msgNoNum = msg.replaceAll("[^A-Za-z]","");
			    	switch (msgNoNum){
			    	case "bellevent":
			    		logger.info("Reported Bell Event from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + ")");
			    		logger.info("Pushing note to all pushbullet users...");
			    		Thread pushThread = new Thread(new Runnable(){
			    			public void run(){
			    				try {
									PBServer.pushToAllUsers("Door bell is rang!", "Go and see who's there!");
						    		logger.info("Pushed!");
								} catch (Exception e) {
									e.printStackTrace();
								}
			    			}
			    		});
			    		pushThread.setName("HA_UDPServer-Pushbullet");
			    		pushThread.start();
			    		if (!AnnounceMem.isAnnouncesWithTypeExist("bellevent")){
			    			AnnounceMem.addAnnounce("bellevent", "bellevent", "Door bell is rang!", "Go and see who's there!", 1, 5000);
			    		}
			    		if (!BellAlarmUI.running){
			    			BellAlarmUI.start();
			    		}
			    		break;
			    	case "batterylow":
			    		voltage = Float.parseFloat(msg.replaceAll("[^0-9]", "")) / 100;
			    		voltagepercentage = (int) (voltage / 4.5 * 100);
			    		if (!AnnounceMem.isAnnouncesWithTypeExist("batterylow")){
			    			logger.warn("Reported BATTERY LOW from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + ")");
				    		logger.warn("Recharge / Change the batteries of the module now. Remaining voltage: " + voltage + "v " + voltagepercentage + "%");
			    			AnnounceMem.addAnnounce("batterylow", "batterylow", "BATTERY LOW: Bell Event Module", "Recharge / Change the batteries of the module now. "
			    					+ "Remaining voltage: " + voltage + "v " + voltagepercentage + "%", 3, 300000);
			    		} else {
			    			logger.trace("Duplicate report. Ignoring...");
			    		}
			    		break;
			    	case "battlevel":
			    		voltage = Float.parseFloat(msg.replaceAll("[^0-9]", "")) / 100;
			    		voltagepercentage = (int) (voltage / 4.5 * 100);
			    		logger.info("Reported Battery Level from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + ")");
			    		logger.info("The battery is running on " + voltage + "v " + voltagepercentage + "%");
			    		break;
			    	default:
			    		logger.info("Unknown operation from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + "): [" + msgNoNum + "]");
			    	}
			    	packet.setLength(buffer.length);
			    }
			    
			    dsocket.close();
			} catch (Exception e){
				logger.error(e);
			}
		}
	}
}
