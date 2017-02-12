package org.usfirst.frc.team972.robot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot {
	public void teleopInit() {
		try {
			testConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testConnect() throws Exception{
		String ip = "10.9.72.37";
		int port = 9720;
		
		Socket clientSocket = new Socket(ip, port);
		DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		String confirmationToken = "972";
		String received;
		
		toServer.writeBytes(confirmationToken + '\n');
		received = fromServer.readLine();
		System.out.println(received);
		clientSocket.close();
	}
}

