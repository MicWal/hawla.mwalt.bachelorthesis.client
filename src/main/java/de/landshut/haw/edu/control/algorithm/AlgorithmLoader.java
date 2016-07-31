package de.landshut.haw.edu.control.algorithm;

import de.landshut.haw.edu.ClientMain;
import de.landshut.haw.edu.util.Constants;

public class AlgorithmLoader {
	
	/**
	 * Load wanted algorithm. <br> 
	 * To get specific algorithm user has to enter proper number.
	 */
	public static Algorithm createAlgorithm(int gameType) {
		
		int algoID;
		
		algoID = selectAlgorithmType();

		switch (algoID) {
            case 1:  
            	return getRuleAlgorithm(gameType);
         
            default: 
            	return null;
        }
	}


	private static int selectAlgorithmType() {
		
		boolean algorithmSelected = false;
		
		String value;
		
		do {
			System.out.println("Enter number to select algorithm: ");
			System.out.println("1: RuleAlgorithm");
			System.out.println("2: LearningAlgorithm (not implemented)");
	
			value = ClientMain.CONSOLE_IN.nextLine();
			
			//check if input is allowed number
			algorithmSelected = isAllowedNumber(value);
			
		} while(!algorithmSelected);
				
		return Integer.parseInt(value);
	}
	
	
	private static Algorithm getRuleAlgorithm(int gameType) {
		
		switch (gameType) {
			case 1:  
				return new SoccerRuleAlgorithm();
                 
			default: 
                return null;
		}
	}

	
	/**
	 * Check if String is int compatible and if the number matches a value corresponding to a algorithm.
	 * @param value	
	 * @throws NumberFormatException Occurs when String isn't compatible with Integer
	 * @return 
	 */
	private static boolean isAllowedNumber(String value) {
	     try {
	        int x = Integer.parseInt(value);
	        
	        return (x >= Constants.MIN_NUMBER_ALGORITHM && x <= Constants.MAX_NUMBER_ALGORITHM);
	        
	     } catch(NumberFormatException e) {
	        return false;
	     }
	}
}
