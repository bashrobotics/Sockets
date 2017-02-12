package org.usfirst.frc.team972.robot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot {
	public void teleopInit() {
		try {
			String ip = getJetsonIP();
			connect(ip);
		} catch (Exception e) {
			e.printStackTrace();
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
		System.out.println(received);
		clientSocket.close();
	}
	
	public String getJetsonIP() throws Exception{ //attempt at dynamically getting the Jetson's ip
		String localIP = InetAddress.getLocalHost().getHostAddress();
		System.out.println("RoboRio is on IP address " + localIP);
		String subnet = localIP.substring(0,localIP.length() - 3);
		System.out.println("We need to search subnet " + subnet);
		int timeout = 5;
		for(int i = 1; i < 255; i++){
			String ip = subnet + "." + i;
			if(InetAddress.getByName(ip).isReachable(timeout)){
				System.out.println(ip + " is reachable");
				if(!ip.equals(localIP) && i != 1 && i != 255){
					return ip;
				}
			}
		}
		return null;
	}
}

