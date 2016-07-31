package de.landshut.haw.edu.model;

public class Ball {
	
	private Sensor ball;

	private boolean onField;
	
	
	public Ball( int id) {
		super();
		
		this.onField = false;
		this.ball = new Sensor(id);
	}

	
	public Sensor getBall() {
		return ball;
	}

	
	public void setBall(Sensor ball) {
		this.ball = ball;
	}


	public boolean isOnField() {
		return onField;
	}


	public void setOnField(boolean onField) {
		this.onField = onField;
	}
}
