package de.landshut.haw.edu.control;

import de.landshut.haw.edu.ClientMain;
import de.landshut.haw.edu.control.algorithm.Algorithm;
import de.landshut.haw.edu.control.algorithm.AlgorithmLoader;
import de.landshut.haw.edu.control.datahandling.DataUpdater;
import de.landshut.haw.edu.control.datahandling.SoccerDataUpdater;
import de.landshut.haw.edu.model.environment.Environment;
import de.landshut.haw.edu.model.environment.SoccerEnvironment;
import de.landshut.haw.edu.util.Constants;

public class ModelLoader {
	
	private Environment environment;
	
	private DataUpdater dataUpdater;

	private Algorithm algorithm;
			
	
	public ModelLoader(){
		loadModel();
	}
		
		
	/**
	 * Load wanted algorithm. <br> 
	 * To get specific algorithm user has to enter proper number.
	 */
	private void loadModel() {
		
		boolean modelSelected = false;
		
		int modelID;
					
		String value;
		
		do {
			System.out.println("Enter number to select game type: ");
			System.out.println("1: Soccer");
			System.out.println("2: AmericanFootball (not implemented)");
	
			value = ClientMain.CONSOLE_IN.nextLine();
			
			//check if input is allowed number
			modelSelected = isAllowedNumber(value);
			
		} while(!modelSelected);
					
		modelID = Integer.parseInt(value);
	  
		switch (modelID) {
            case Constants.SOCCER_ID:  
            		environment = new SoccerEnvironment();
            		dataUpdater = new SoccerDataUpdater();
            		algorithm = AlgorithmLoader.createAlgorithm(Constants.SOCCER_ID);
                    break;
            default: 
                    break;
        }
	}
		
		
	/**
	 * Check if String is int compatible and if the number matches a value corresponding to a algorithm.
	 * @param value	
	 * @throws NumberFormatException Occurs when String isn't compatible with Integer
	 * @return 
	 */
	private boolean isAllowedNumber(String value) {
	     try {
	        int x = Integer.parseInt(value);
	        
	        return (x >= Constants.MIN_NUMBER_MODEL && x <= Constants.MAX_NUMBER_MODEL);
	        
	     } catch(NumberFormatException e) {
	        return false;
	     }
	}

	
	public Environment getEnvironment() {
		return environment;
	}

	
	public Algorithm getAlgorithm() {
		return algorithm;
	}

	
	public DataUpdater getDataUpdater() {
		return dataUpdater;
	}

}
