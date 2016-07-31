package de.landshut.haw.edu.model.soccer;

import de.landshut.haw.edu.util.Constants;

public class GoalGate {
	
	// length in cm
	private final double goalLength;
	
	// length in cm
	private final double goalWidth;


	public GoalGate(double goalLength, double goalWidth) {
		super();
		
		if(goalLength == 0 && goalWidth == 0) {
			this.goalLength = Constants.GATE_DEFAULT_LENGTH;
			
			this.goalWidth = Constants.GATE_DEFAULT_WIDTH;
			
		} else {
			this.goalLength = goalLength;
			
			this.goalWidth = goalWidth;
		}
	}


	public double getGoalLength() {
		return goalLength;
	}


	public double getGoalWidth() {
		return goalWidth;
	}
	
	
}
