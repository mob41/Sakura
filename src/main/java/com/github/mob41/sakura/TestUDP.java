package com.github.mob41.sakura;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Calendar;

public class TestUDP {

	public static void main(String[] args){
		try {
			int port = 6099;
			@SuppressWarnings("resource")
			DatagramSocket dsocket = new DatagramSocket(port);
		    byte[] buffer = new byte[2048];
		    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		    
		    while (true){
		    	float voltage;
		    	int voltagepercentage;
		    	dsocket.receive(packet);
		    	String msg = new String(buffer, 0 , packet.getLength());
		    	System.out.println(Calendar.getInstance().getTime().toString() + " Recevied UDP Packet from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + "): " + msg);
		    	if (msg == null || msg == ""){
		    		continue;
		    	}
		    	String msgNoNum = msg.replaceAll("[^A-Za-z]","");
		    	switch (msgNoNum){
		    	case "bellevent":
		    		System.out.println(Calendar.getInstance().getTime().toString() + " Reported Bell Event from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + ")");
		    		break;
		    	case "batterylow":
		    		voltage = Float.parseFloat(msg.replaceAll("[^0-9]", "")) / 100;
		    		voltagepercentage = (int) (voltage / 4.5 * 100);
		    		break;
		    	case "battlevel":
		    		voltage = Float.parseFloat(msg.replaceAll("[^0-9]", "")) / 100;
		    		voltagepercentage = (int) (voltage / 4.5 * 100);
		    		System.out.println(Calendar.getInstance().getTime().toString() + " Reported Battery Level from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + ")");
		    		System.out.println(Calendar.getInstance().getTime().toString() + " The battery is running on " + voltage + "v " + voltagepercentage + "%");
		    		break;
		    	default:
		    		System.out.println(Calendar.getInstance().getTime().toString() + " Unknown operation from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + "): [" + msgNoNum + "]");
		    	}
		    	packet.setLength(buffer.length);
		    }
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
