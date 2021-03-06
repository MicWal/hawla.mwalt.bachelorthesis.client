package de.landshut.haw.edu.control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import de.landshut.haw.edu.puffer.PufferHolder;
import de.landshut.haw.edu.util.Constants;
import de.landshut.haw.edu.util.ErrorCodes;
import de.landshut.haw.edu.util.TransmissionObject;

public class DataHandler {

	private Socket server;
	
	private boolean endTransmission;

	
	public DataHandler(Socket server) {
		super();
		
		this.server = server;
		
		endTransmission = false;
				
		communicateWithServer();
	}

	

	/**
	 * Communicate with server until terminated
	 * @throws IOException	Closing socket failed
	 */
	private void communicateWithServer() {
		try ( ObjectInputStream inObj = new ObjectInputStream(server.getInputStream());){
			
			transmission(inObj);
			
			if (!server.isClosed()) {
					server.close();
			}
			
		} catch (IOException e) {
			System.err.println("Input/Outputstream failed");
			System.exit(ErrorCodes.IO_ERR);
		}
	}

	
	
	/**
	 * Start communication with server: <p>
	 * Listen until server send terminate status.
	 */
	private void transmission(ObjectInputStream inObj) {

		TransmissionObject transObj;

		// listen until server sends terminate status
		while (!endTransmission) {
			try {
				
				transObj = (TransmissionObject) inObj.readObject();
				
				PufferHolder.INPUT_PUFFER.put(new TransmissionObject(transObj));
								
				if(transObj.getTransmission_status().equals(Constants.END_TRANSMISSION)) {
					endTransmission = true;
				}
				
				transObj.unreferenceArrayList();
				transObj = null;
				
			} catch (ClassNotFoundException e) {
				System.err.println("Class of a serialized object cannot be found");
				System.exit(ErrorCodes.CLASS_NOT_FOUND);
				
			} catch (IOException e) {
				System.err.println("I/O error in startTransmission");
				System.exit(ErrorCodes.IO_ERR);
				
			} catch (InterruptedException e) {
				System.err.println("Interrupt while waiting to add item to puffer. Continue but object is lost.");
				System.exit(ErrorCodes.SLEEP_INTERUPT);
			}	
		}
	}

	

	
}
