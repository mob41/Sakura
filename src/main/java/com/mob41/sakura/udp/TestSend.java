package com.mob41.sakura.udp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestSend {

	public static void main(String[] args) throws Exception{
		BufferedReader inFromUser =
		         new BufferedReader(new InputStreamReader(System.in));
		      DatagramSocket clientSocket = new DatagramSocket();
		      InetAddress IPAddress = InetAddress.getByName("192.168.168.142");
		      byte[] sendData = new byte[1024];
		      String sentence = inFromUser.readLine();
		      sendData = sentence.getBytes();
		      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 6099);
		      clientSocket.send(sendPacket);
		      clientSocket.close();

	}

}
