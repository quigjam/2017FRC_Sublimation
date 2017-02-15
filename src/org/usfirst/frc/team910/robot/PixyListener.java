package org.usfirst.frc.team910.robot;

import java.net.*;
import java.io.*;

public class PixyListener implements Runnable {
	
	PixyEvent event; // Call the listener with this reference
	int port;        // Network port number to which to send data

	/**
	 * Create a PixyListener
	 * 
	 * @param event
	 *            method that is called when an object detect event is received
	 *            from a Pixy
	 * @param port
	 *            network port that this listener will receive object detect
	 *            events from a Pixy
	 */
	public PixyListener(PixyEvent event, int port) {
		this.event = event;    // Save event interface provided 
		this.port = port;      // Save port provided
	}

	public void run() {
		// Be a socket server 
		ServerSocket serverSocket = null;
		String inputLine;
		Socket clientSocket = null;

		try {
			serverSocket = new ServerSocket(port); // Create socket on specified port

			System.out.println("Waiting for connection.....");

			clientSocket = serverSocket.accept(); 
			System.out.println("Connection successful");
			System.out.println("Waiting for input.....");

			// Retrieve string from socket 
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			while (true) {
				inputLine = in.readLine();    // Get message
				if (inputLine != null)
					event.eventGet(inputLine); // Send message
			}
		} catch (IOException e) {
			System.out.println("PixyListener exception of some sort: exit");
			System.exit(1);
		}

	}
}
