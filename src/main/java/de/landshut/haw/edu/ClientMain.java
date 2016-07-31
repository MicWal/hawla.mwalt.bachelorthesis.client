package de.landshut.haw.edu;

import java.util.Scanner;

import de.landshut.haw.edu.control.ConnectionHandler;
import de.landshut.haw.edu.control.DataHandler;
import de.landshut.haw.edu.control.ModelLoader;
import de.landshut.haw.edu.control.algorithm.Algorithm;
import de.landshut.haw.edu.control.datahandling.DataUpdater;
import de.landshut.haw.edu.model.environment.Environment;
import de.landshut.haw.edu.util.Constants;


public class ClientMain {
	
	public static Scanner CONSOLE_IN;

	
	public static void main(String[] args) {
		
		// check if necessary arguments are available
		if(args.length < Constants.REQUIRED_ARGUMENTS) {
			System.out.println("Missing argument(s). Please add missing argument(s)");
			
		} else if(args[1].matches(Constants.ONLY_NUMBER_REGEX)) {
			
			// create console scanner
			CONSOLE_IN = new Scanner(System.in);
						
			
			// load game type
			ModelLoader moLoader = new ModelLoader();
						
			Environment environment = moLoader.getEnvironment();
			
			DataUpdater dataUpdater = moLoader.getDataUpdater();
			
			Algorithm algorithm = moLoader.getAlgorithm();
			
			dataUpdater.setEnvironment(environment);
			
			dataUpdater.setAlgorithm(algorithm);
			
			algorithm.setEnvironment(environment);
			System.out.println("algo set environment");
			
			// establish connection to server
		  	ConnectionHandler conHandler = new ConnectionHandler(args[0], Integer.parseInt(args[1]));

		  	// create data handling
			@SuppressWarnings("unused")
			DataHandler dHandler = new DataHandler(conHandler.getSocket());	
			
			// close connection after transmission
			conHandler.closeConnection();
			
			CONSOLE_IN.close();
		}
		else {
			System.out.println("Port is invalid. Restart client with valid argument");
		}
	}
}


