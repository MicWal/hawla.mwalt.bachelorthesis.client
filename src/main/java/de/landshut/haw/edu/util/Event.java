package de.landshut.haw.edu.util;

import de.landshut.haw.edu.model.Sensor;

/**
 * Class to mark an event at a specific timestamp.
 * @author Michael
 *
 */
public class Event {
	
	public static final String BALL_EXIT_LEFT_SIDE = "Ball left over left side line";
	
	public static final String BALL_EXIT_RIGHT_SIDE = "Ball left over right side line";
	
	public static final String BALL_EXIT_TOP_GOAL = "Ball left over top goal line";
	
	public static final String BALL_EXIT_BOTTOM_GOAL = "Ball left over bottom goal line";
	
	public static final String POSSIBLE_KICK_OFF = "Possible kick-off";
	
	public static final String POSSIBLE_CORNER_BOTTOM = "Possible corner bottom";

	public static final String POSSIBLE_CORNER_TOP = "Possible corner top";
	
	public static final String POSSIBLE_THROW_IN_LEFT = "Possible throw in left";

	public static final String POSSIBLE_THROW_IN_RIGHT = "Possible throw in right";
	
	public static final String POSSIBLE_GOALKICK_TOP = "Possible goalkick top";
	
	public static final String POSSIBLE_GOALKICK_BOTTOM = "Possible goalkick bottom";

	public static final String EXECUTE_CORNER_TOP = "Corner top";
	
	public static final String EXECUTE_CORNER_BOTTOM = "Corner bottom";
	
	public static final String EXECUTE_THROW_IN_LEFT = "Throw in left";
	
	public static final String EXECUTE_THROW_IN_RIGHT = "Throw in right";
	
	public static final String EXECUTE_GOALKICK_BOTTOM = "Goalkick bottom";
	
	public static final String EXECUTE_GOALKICK_TOP = "Goalkick top";
	
	public static final String GAME_START = "Game started";
	
	public static final String SECOND_HALF_START = "2nd half started";

	public static final String GOAL = "Goal";

	public static final String SIDES_CHANGED = "Sides changed";

	public static final String NO_POSSIBLE_GOALKICK = "No possible goalkick";

	public static final String CORNER = "Corner";
	
	public static final String THROW_IN = "Throw in";





	private String event;
	
	private long timestamp;
	
	private Sensor sensor;

	
	public Event(String event, long timestamp, Sensor sensor) {
		super();
		this.event = event;
		
		this.timestamp = timestamp;
		
		this.sensor = sensor;
	}

	
	public String getEvent() {
		return event;
	}

	
	public long getTimestamp() {
		return timestamp;
	}


	public Sensor getSensor() {
		return sensor;
	}


	@Override
	public String toString() {
		return "Timestamp: " + timestamp + " Event: " + event;
	}
	
	

}
