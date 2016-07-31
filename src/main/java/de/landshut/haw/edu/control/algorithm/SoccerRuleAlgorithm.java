package de.landshut.haw.edu.control.algorithm;

import java.util.HashMap;

import de.landshut.haw.edu.model.Sensor;
import de.landshut.haw.edu.model.environment.Environment;
import de.landshut.haw.edu.model.environment.SoccerEnvironment;


public class SoccerRuleAlgorithm extends Algorithm {
	
	/**
	 * Reference to environment to be able to update data.
	 */
	private SoccerEnvironment environment;
	
	/**
	 * Contains the id and the related sensor object.
	 */
	private HashMap<Integer, Sensor> idSensorMap;
	
	private SoccerRules rules;


	public SoccerRuleAlgorithm() {
		
		endTransmission = false;
		
		rules = new SoccerRules();
		
		start();
	}
		
	
	@Override
	public void processData() {
		
		environment.analyze();
		
		// check environment for new event
//		eventList = rules.checkAgainstRules(idSensorMap, environment);
		
//		System.out.println("analyzed");
		environment.isAnalyzed();
	}
	
	
	@Override
	public void run() {
		
		while(!endTransmission) {
			processData();
		}
	}


	@Override
	public void setEnvironment(Environment environment) {
		this.environment = (SoccerEnvironment) environment;
	}


	@Override
	public void endTransmission() {
		endTransmission = true;
	}


	@Override
	public void setIdSensorMap(HashMap<Integer, Sensor> idSensorMap) {
		this.idSensorMap = idSensorMap;
		
	}
	
}
