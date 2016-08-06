package de.landshut.haw.edu.control.algorithm;

import de.landshut.haw.edu.model.environment.Environment;
import de.landshut.haw.edu.model.environment.SoccerEnvironment;
import de.landshut.haw.edu.puffer.PufferHolder;
import de.landshut.haw.edu.util.ConvertUtil;


public class SoccerRuleAlgorithm extends Algorithm {
	
	/**
	 * Reference to environment to be able to update data.
	 */
	private SoccerEnvironment environment;
	
	private RuleV2 rules3;
	


	public SoccerRuleAlgorithm() {
		
		endTransmission = false;
		
		rules3 = new RuleV2();
	}
		
	
	@Override
	public void processData() {
		
		environment.analyze();
		
		// check environment for new event	
		rules3.checkAgainstRules(environment);
		
//		addSnapshotToHoldPuffer();
		
		environment.isAnalyzed();
		
	}

	
	@Override
	public void run() {
		
		while(!endTransmission) {
			processData();
		}
		
//		rules.writeEventListToFile();
		
//		rules2.writeEventListToFile();
		
		rules3.writeEventListToFile();
		
	}


	@Override
	public void setEnvironment(Environment environment) {
		
		this.environment = (SoccerEnvironment) environment;
		
		start();
	}


	@Override
	public void endTransmission() {
		endTransmission = true;
	}
	
	/**
	 * Add the current environment as deep copy to the hold buffer.
	 */
	private void addSnapshotToHoldPuffer() {

		SoccerEnvironment tmp = (SoccerEnvironment) ConvertUtil.deepCopy(environment);
		
		if(tmp != null) {
			
			PufferHolder.addElementHoldPuffer(tmp);
			
		}
	}
	
	
	
}
