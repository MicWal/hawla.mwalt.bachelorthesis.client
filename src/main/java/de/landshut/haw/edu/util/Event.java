package de.landshut.haw.edu.util;

import de.landshut.haw.edu.model.Sensor;

public class Event {
	
	public static final String BALL_EXIT_LEFT_SIDE = "Ball left over left side line";
	
	public static final String BALL_EXIT_RIGHT_SIDE = "Ball left over right side line";
	
	public static final String BALL_EXIT_TOP_GOAL = "Ball left over top goal line";
	
	public static final String BALL_EXIT_BOTTOM_GOAL = "Ball left over bottom goal line";

	public static final String GAME_START = "Game started";
	
	private String event;
	
	private long timestamp;
	
	private Sensor sensor;

	
	public Event(String event, long timestamp, Sensor sensor) {
		super();
		this.event = event;
		
		this.timestamp = timestamp;
		
		this.sensor = sensor;
	}

	
	public synchronized String getEvent() {
		return event;
	}

	
	public synchronized long getTimestamp() {
		return timestamp;
	}


	public synchronized Sensor getSensor() {
		return sensor;
	}

}
