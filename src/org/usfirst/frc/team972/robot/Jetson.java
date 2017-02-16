package org.usfirst.frc.team972.robot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Jetson implements Runnable {

	public static Socket mainSocket = null;
	private static final String STATIC_IP = "10.9.72.2";

	public void run() {
		openSocket();
	}

	public static void openSocket() {
		// Wait until we're connected to the network
		waitUntilConnected(); // 45 seconds

		// Attempt to connect to Jetson's Static IP
		boolean reachable;
		try {
			reachable = InetAddress.getByName(STATIC_IP).isReachable(10);
		} catch (Exception e) {
			reachable = false;
		}
		
		if (reachable) {
			System.out.println("Jetson is reachable at " + STATIC_IP);
			mainSocket = connect(STATIC_IP);
		} else {
			System.out.println("Jetson is not reachable at it's static ip.");
			String ip = getJetsonIP(); // 20 seconds
			mainSocket = connect(ip);
		}
	}

	public static boolean isConnected() {
		return (mainSocket != null);
	}

	public static boolean closeConnection() {
		if (isConnected()) {
			sendMessage("C");
			try {
				mainSocket.close();
				mainSocket = null;
				return true;
			} catch (Exception e) {
				System.out.println("ERROR closing socket: " + e.getMessage());
				return false;
			}
		} else {
			System.out.println("Warning: Not connected. Cannot close connection.");
			return false;
		}
	}

	public static void gearVision() {
		sendMessage("G");
	}

	public static void boilerVision() {
		sendMessage("B");
	}

	private static void sendMessage(String message) {
		if (isConnected()) {
			try {
				DataOutputStream toServer = new DataOutputStream(mainSocket.getOutputStream());
				toServer.writeBytes(message + '\n');
			} catch (Exception e) {
				System.out.println("ERROR sending message: " + e.getMessage());
			}
		} else {
			System.out.println("Warning: Not connected. Cannot send message.");
		}
	}

	private static void waitUntilConnected() {
		boolean connected = false;
		System.out.println("Waiting until we are connected to gateway...");
		do {
			try {
				String ip = InetAddress.getLocalHost().getHostAddress();
				if (ip != null) {
					connected = true;
				} else {
					Thread.sleep(2000);
				}
			} catch (Exception e) {
			}
		} while (!connected);

		System.out.println("Connected to gateway.");
	}

	private static String getJetsonIP() {
		System.out.println("Finding Jetson on network...");
		String jetsonIP = null;
		int n = 0;
		do {
			n++;
			try {
				String localIP = InetAddress.getLocalHost().getHostAddress();
				// System.out.println("RoboRio is on IP address " + localIP);
				String subnet = localIP.substring(0, localIP.length() - 3);
				// System.out.println("We need to search subnet " + subnet);
				int timeout = 5;
				// System.out.println("Reachable IPs:");
				for (int i = 1; i < 255; i++) {
					String ip = subnet + "." + i;
					if (InetAddress.getByName(ip).isReachable(timeout)) {
						// System.out.print(" " + ip + " is reachable ");
						if (i == 1) {
							// System.out.print("(Gateway)");
						} else if (ip.equals(localIP)) {
							// System.out.print("(RoboRio)");
						} else if (jetsonIP == null) {
							// System.out.print("(Jetson)");
							jetsonIP = ip;
						} else {
							// System.out.print("(Unknown)");
						}
						// System.out.println();
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("ERROR on getting Jetson: " + e.getMessage());
				try {
					Thread.sleep(3000);
				} catch (Exception e2) {
				}
			}
		} while (jetsonIP == null);
		System.out.println("Found Jetson at " + jetsonIP + " in " + n + " tries!");
		return jetsonIP;
	}

	private static Socket connect(String ip) {
		boolean connected = false;
		do {
			try {
				int port = 9720;
				String confirmationToken = "972";
				System.out.println("Connecting to " + ip + ":" + port + " with confirmation token:'" + confirmationToken + "'");

				Socket clientSocket = new Socket(ip, port);
				DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
				BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				connected = true;

				String received;
				toServer.writeBytes(confirmationToken + '\n');
				received = fromServer.readLine();
				System.out.println("We received " + received + " back from the server.");
				if (confirmationToken.equals(received)) {
					connected = true;
					System.out.println("Connected to the Jetson!");
					clientSocket.close();
					return clientSocket;
				}
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("ERROR on connection: " + e.getMessage());
				try {
					Thread.sleep(3000);
				} catch (Exception e2) {
				}
			}
		} while (!connected);
		return null;
	}
}
