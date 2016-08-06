package de.landshut.haw.edu.model;

import java.io.Serializable;

public class Ball implements Serializable {
	

	private static final long serialVersionUID = -2265566560955144999L;

	private Sensor ball;

	
	
	public Ball( int id) {
		super();

		this.ball = new Sensor(id);
	}

	
	public Sensor getBall() {
		return ball;
	}

	
	public void setBall(Sensor ball) {
		this.ball = ball;
	}

}
