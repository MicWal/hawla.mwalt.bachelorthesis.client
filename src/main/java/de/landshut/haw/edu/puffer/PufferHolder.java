package de.landshut.haw.edu.puffer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.landshut.haw.edu.model.environment.Environment;
import de.landshut.haw.edu.util.Constants;
import de.landshut.haw.edu.util.TransmissionObject;

public class PufferHolder {
	
	private PufferHolder() {}
	
	public static BlockingQueue<TransmissionObject> INPUT_PUFFER = new LinkedBlockingQueue<TransmissionObject>();
	
	private static ArrayList<Environment> HOLD_PUFFER = new ArrayList<Environment>();
	
	
	
	/**
	 * Add element to hold puffer. If puffer reached max puffer value it deletes a specified portion of the list.
	 * @param environment Element to add
	 */
	public static synchronized void addElementHoldPuffer(Environment environment) {

			if(HOLD_PUFFER.size() > Constants.MAX_PUFFER) {
				
				HOLD_PUFFER.subList(0, Constants.REMOVE_PUFFER).clear();
				
			}
			
			HOLD_PUFFER.add(environment);
		
	}
	
	
	
	/**
	 * Clear the environment puffer.
	 * @param environment
	 */
	public static synchronized void clearHoldPuffer() {

			HOLD_PUFFER.clear();
	}
	
	
	
	
	
	/**
	 * Return element on specified position if position is in range of list boundary.
	 * @param position
	 * @return Element on position or null is position is invalid
	 */
	public static synchronized Environment getElementHoldPuffer(int position) {

		if(position >= 0 && position < HOLD_PUFFER.size()) {
			
			return HOLD_PUFFER.get(position);
			
		}
		
		return null;
	}
	

	
	/**
	 * Returns size of puffer.
	 * @return 
	 */
	public static synchronized int getHoldPufferSize() {
		
		return HOLD_PUFFER.size();
		
	}
	
}
