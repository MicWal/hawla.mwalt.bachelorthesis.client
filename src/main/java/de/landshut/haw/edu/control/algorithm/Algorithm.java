package de.landshut.haw.edu.control.algorithm;


import de.landshut.haw.edu.model.environment.Environment;

public abstract class Algorithm extends Thread{

	
	protected boolean endTransmission;
	
	
	public abstract void processData();
	
	
	public abstract void endTransmission();
	
	
	public abstract void setEnvironment(Environment environment);


//	public abstract void setIdSensorMap(HashMap<Integer, Sensor> idSensorMap);
	
}
