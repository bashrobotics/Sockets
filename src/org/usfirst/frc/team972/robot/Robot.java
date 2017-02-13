package org.usfirst.frc.team972.robot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot {
	public void teleopInit() {
		System.out.println();
		System.out.println();
		try {
			String ip = null;
			//do {
				ip = getJetsonIP();
			//} while (ip == null);
			connect(ip);
			
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("ERROR: " + e.getMessage());
		}
	}
	
	public void connect(String ip) throws Exception{
		int port = 9720;
		String confirmationToken = "972";
		System.out.println("Connecting to " + ip + ":" + port + " with confirmation token:'" + confirmationToken + "'");
		
		Socket clientSocket = new Socket(ip, port);
		DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	
		String received;
		toServer.writeBytes(confirmationToken + '\n');
		received = fromServer.readLine();
		System.out.println("We received " + received + " back from the server.");
		clientSocket.close();
	}
	
	public String getJetsonIP() throws Exception{ //attempt at dynamically getting the Jetson's ip
		String localIP = InetAddress.getLocalHost().getHostAddress();
		System.out.println("RoboRio is on IP address " + localIP);
		String subnet = localIP.substring(0,localIP.length() - 3);
		System.out.println("We need to search subnet " + subnet);
		int timeout = 5;
		String jetsonIP = null;
		System.out.println("Reachable IPs:");
		for(int i = 1; i < 255; i++){
			String ip = subnet + "." + i;
			if(InetAddress.getByName(ip).isReachable(timeout)){
				System.out.print("    " + ip + " is reachable ");
				if(i == 1){
					System.out.print("(Gateway)");
				} else if (ip.equals(localIP)) {
					System.out.print("(RoboRio)");
				} else if (jetsonIP == null) {
					System.out.print("(Jetson)");
					jetsonIP = ip;
				} else {
					System.out.print("(Unknown)");
				}
				System.out.println();
			}
		}
		if(jetsonIP == null){
			System.out.println("No Jetson detected on the network. Try again.");
		}
		return jetsonIP;
	}
}

