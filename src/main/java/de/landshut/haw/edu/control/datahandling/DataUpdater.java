package de.landshut.haw.edu.control.datahandling;


import de.landshut.haw.edu.control.algorithm.Algorithm;
import de.landshut.haw.edu.model.TransmissionObject;
import de.landshut.haw.edu.puffer.PufferHolder;

public abstract class DataUpdater extends Thread implements DataUpdaterInterface {

	
	protected Algorithm algorithm;
	
	protected TransmissionObject current;
	

	public TransmissionObject getNext() {
		
		TransmissionObject obj;
		
		try {
			obj = PufferHolder.INPUT_PUFFER.take();
			
			//actionForObject(obj);
			
			return obj;
		} catch (InterruptedException e) {
			System.out.println("Interrupted while waiting for object");
		}
		
		return null;
	}


	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}
	
}


