package org.usfirst.frc.team910.robot.Vision;

import java.net.*;
import java.io.*;

public class PixyListener implements Runnable {
	PixyEvent event;
	int port;

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
		this.event = event;
		this.port = port;
	}

	public void run() {
		ServerSocket serverSocket = null;
		String inputLine;
		Socket clientSocket = null;

		try {
			serverSocket = new ServerSocket(port);

			System.out.println("Waiting for connection.....");

			clientSocket = serverSocket.accept();
			System.out.println("Connection successful");
			System.out.println("Waiting for input.....");

			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			/*
			 * while (true) { while ((inputLine = in.readLine()) != "~") { if
			 * (inputLine != null) { event.eventGet(inputLine); } } }
			 */

			while (true) {
				inputLine = in.readLine();
				if (inputLine != null)
					event.eventGet(inputLine);
			}
		} catch (IOException e) {
			System.out.println("PixyListener exception of some sort: exit");
			System.exit(1);
		}

	}
	/*
	 * public static void main(String[] args) throws IOException { PixyEvent e =
	 * new EventTest(); PixyListener test = new PixyListener(e, 10007); Thread t
	 * = new Thread(null, test, "PixyListener"); t.start(); }
	 */
}
