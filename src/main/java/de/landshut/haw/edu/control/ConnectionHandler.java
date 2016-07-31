package de.landshut.haw.edu.control;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import de.landshut.haw.edu.util.ErrorCodes;


public class ConnectionHandler {
	
	private Socket server;
	
	
	public ConnectionHandler (String serverName, int port) {
		System.out.println("ConnectionHandler created");
		
		init(serverName, port);
	}
	
	
	/**
	 * Establish connection to server with given parameters.
	 * @param serverName Name of the server
	 * @param port Port which server listens to
	 * @throws UnknownHostException Connection to server failed
	 * @throws IOException I/O error occurred while creating the socket.
	 */
	private void init(String serverName, int port) {
		
		try {
			server = new Socket(serverName, port);
			
		} catch (UnknownHostException e) {
			System.err.println("Connection to server failed");
			System.exit(ErrorCodes.CONNECTION_START_ERR);
			
		} catch (IOException e) {
			System.err.println("I/O error occurred while creating the socket.");
			System.exit(ErrorCodes.IO_ERR);
		}
		
		System.out.printf("Connection to server %s:%d established.\n", serverName, port);
	}
	
	public void closeConnection() {
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Error while closing socket");
			System.exit(ErrorCodes.SOCKET_CLOSE_ERR);
		}
	}
  

	public Socket getSocket() {
		return server;
	}
	
}
