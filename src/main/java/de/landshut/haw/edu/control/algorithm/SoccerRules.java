package de.landshut.haw.edu.control.algorithm;

import java.util.ArrayList;
import java.util.HashMap;

import de.landshut.haw.edu.model.Ball;
import de.landshut.haw.edu.model.Participant;
import de.landshut.haw.edu.model.Sensor;
import de.landshut.haw.edu.model.Team;
import de.landshut.haw.edu.model.environment.SoccerEnvironment;
import de.landshut.haw.edu.model.soccer.SoccerField;
import de.landshut.haw.edu.util.Constants;
import de.landshut.haw.edu.util.Event;
import de.landshut.haw.edu.util.Point3D;

public class SoccerRules {
	
	private HashMap<Integer, Sensor> idSensorMap;
	
	private Ball activeBall;
	
	private ArrayList<Event> eventList;
	
	private SoccerEnvironment environment;
	
	private String topHalf;
	
	private String bottomHalf;
	
	private boolean gameStarted;
	
	private long startTimestamp;
	
	
	public SoccerRules() {
		eventList = null;
		
		activeBall = null;
		
		gameStarted = false;
	}
	
	public ArrayList<Event> checkAgainstRules(HashMap<Integer, Sensor> idSensorMap, SoccerEnvironment environment) {
		this.idSensorMap = idSensorMap;
		
		this.environment = environment;
		
		checkGameStart();
		
		if(gameStarted == true) {
			checkBallGoalLineExit();
	
			checkBallSideLineExit();
		}
		
		return eventList;
	}
	
	
// analyze current situation
	
	private void checkGameStart() {
		
		// check if ball is in near center point
		if(activeBall == null) {
			Point3D centerPoint = environment.getPlayfield().getCenterPoint();
			
			// check if any ball is in center point/area
			for(Ball b: environment.getBalls()) {
				boolean ret = pointInsiderCirle(centerPoint, Constants.TOLERANCE_RADIUS, b.getBall().getCoordinates());
				if(ret == true) {
					
					if(isBallIdle(b)) { 
						
						if(teamsOnDifferenSides(environment.getPlayfield().getCenterPoint(), 
												environment.getTeamA(), environment.getTeamB())) {
							activeBall = b;
							
							System.out.print(activeBall.getBall().getCoordinates().getX() + "  ");
							System.out.print(activeBall.getBall().getCoordinates().getY() + "  ");
							System.out.println(activeBall.getBall().getCoordinates().getZ() + "  ");
							
							// add event
							if(eventList == null) {
								eventList = new ArrayList<Event>();
								System.out.println("Spiel startet: " + environment.getTimestamp());
								startTimestamp = environment.getTimestamp();
								eventList.add(new Event(Event.GAME_START, environment.getTimestamp(), null));
								gameStarted = true;
							}
						}
					}
				}
			}	
		}	
	}

	
	private boolean teamsOnDifferenSides(Point3D centerPoint, Team teamA, Team teamB) {
		
		boolean[] sameSideTeamA = new boolean[teamA.getPlayers().length];
		
		boolean[] sameSideTeamB = new boolean[teamB.getPlayers().length];
		
		double centerY = centerPoint.getY();
		
		Participant[] players = teamA.getPlayers();
		
		checkPlayerPosition(sameSideTeamA, centerY, players);
		
		players = teamB.getPlayers();
		
		checkPlayerPosition(sameSideTeamB, centerY, players);
		
		// count how much player are on either side of the field for team A
		int countTrueTeamA = 0;
		int countFalseTeamA = 0;
		for (boolean b: sameSideTeamA) {
			if(b == true) {
				countTrueTeamA++;
			} else {
				countFalseTeamA++;
			}
		}
		
		// count how much player are on either side of the field for team B
		int countTrueTeamB = 0;
		int countFalseTeamB = 0;
		for (boolean b: sameSideTeamB) {
			if(b == true) {
				countTrueTeamB++;
			} else {
				countFalseTeamB++;
			}
		}

		// is team B true and team A false
		if(countTrueTeamB + Constants.TOLERANCE_PLAYER >= sameSideTeamB.length) {
			if(countFalseTeamA + Constants.TOLERANCE_PLAYER >= sameSideTeamA.length) {
				topHalf = teamB.getName();
				bottomHalf = teamA.getName();
				return true;
			}
		}
		
		// is team B false and team A true
		if(countFalseTeamB + Constants.TOLERANCE_PLAYER >= sameSideTeamB.length) {
			if(countTrueTeamA + Constants.TOLERANCE_PLAYER >= sameSideTeamA.length) {
				topHalf = teamA.getName();
				bottomHalf = teamB.getName();
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Set element in sameSideTeamX <b>true</b> if corresponding player Y position >= center Y <br>
	 * Set element in sameSideTeamX <b>false</b> if corresponding player Y position < center Y
	 * @param sameSideTeamX
	 * @param centerY
	 * @param players
	 */
	private void checkPlayerPosition(boolean[] sameSideTeamX, double centerY, Participant[] players) {
		for(int i = 0; i < players.length; i++) {
			
			if(players[i].getLeftLeg().getCoordinates().getY() > centerY && players[i].getRightLeg().getCoordinates().getY() >= centerY) {
				sameSideTeamX[i] = true;
			} else {
				sameSideTeamX[i] = false;
			}
		}
	}
	
	
	/**
	 * Check if ball has velocity that marks it as idle.
	 * @param b
	 * @return <b>true</b> if ball is idle, else  <b>false</b>
	 */
	private boolean isBallIdle(Ball b) {
		return (b.getBall().getVelocity() <= Constants.BALL_IDLE);	
	}

	
	/**
	 * Test if ball has left over one of the side lines.
	 * Add event if detected
	 */
	private void checkBallSideLineExit() {

		Sensor ball = idSensorMap.get(activeBall.getBall().getId());
		
		double ret;
		
		//test left side
		SoccerField field = environment.getPlayfield();
		
		ret = pointSideOfLine(ball.getCoordinates(), field.getLeftBottom(), field.getLeftTop());
		
		if(ret < 0) {
			
			if(!eventList.get(eventList.size() - 1).getEvent().equals(Event.BALL_EXIT_LEFT_SIDE)) {
				System.out.println("Ball ist aus links: " + (environment.getTimestamp() - startTimestamp));
				eventList.add(new Event(Event.BALL_EXIT_LEFT_SIDE, environment.getTimestamp(), new Sensor(ball)));
			}
		}
		
		//test right side
		ret = pointSideOfLine(ball.getCoordinates(), field.getRightBottom(), field.getRightTop());
		
		if(ret > 0) {
			
			if(!eventList.get(eventList.size()-1).getEvent().equals(Event.BALL_EXIT_RIGHT_SIDE)) {
				System.out.println("Ball ist aus rechts: " + (environment.getTimestamp() - startTimestamp));
				eventList.add(new Event(Event.BALL_EXIT_RIGHT_SIDE, environment.getTimestamp(), new Sensor(ball)));
			}
		}
		
	}


	
	// ball left field over goal line -> add event (BALL_LEFT_GOAL_LINE, timestamp) and unmark active ball
	private void checkBallGoalLineExit() {
		Sensor ball = idSensorMap.get(activeBall.getBall().getId());
		
		double ret;
		
		//test left side
		SoccerField field = environment.getPlayfield();
		
		ret = pointSideOfLine(ball.getCoordinates(), field.getRightTop(), field.getLeftTop());
		
		if(ret > 0) {
			
			if(!eventList.get(eventList.size() - 1).getEvent().equals(Event.BALL_EXIT_TOP_GOAL)) {
				System.out.println("Ball ist aus oben: " + (environment.getTimestamp() - startTimestamp));
				eventList.add(new Event(Event.BALL_EXIT_TOP_GOAL, environment.getTimestamp(), new Sensor(ball)));
			}
		}
		
		//test right side
		ret = pointSideOfLine(ball.getCoordinates(), field.getRightBottom(), field.getLeftBottom());
		
		if(ret < 0) {
			
			if(!eventList.get(eventList.size()-1).getEvent().equals(Event.BALL_EXIT_BOTTOM_GOAL)) {
				System.out.println("Ball ist aus unten: " + (environment.getTimestamp() - startTimestamp));
				eventList.add(new Event(Event.BALL_EXIT_BOTTOM_GOAL, environment.getTimestamp(), new Sensor(ball)));
			}
		}
	}
	// ball is idle in one of the corners -> add event (POSSIBLE_CORNER, timestamp)

	
	
	// ball and one player behind side line and ball is behind player -> add event (POSSIBLE_THROW_IN, timestamp) mark THROW_IN player	
	
	// ball comes on field -> add event (BALL_ON_FIELD, timestamp) and mark ACTIVE ball with ball on field
	
	// ball is idle in center point ->	add event (POSSIBLE_KICK_OFF, timestamp)
	
	// ball is idle in any point of the field -> add event (POSSIBLE_TIME_OUT, timestamp)
	
	// ball is in goal area; goalkeeper near ball; ball gains acceleration and last event is BALL_LEFT_GOAL_LINE -> add event (POSSIBLE_GOAL_KICK, timestamp)
	
	// all player left field 
	
		// is first element in list POSSIBLE_KICK_OFF -> add event (HALF_TIME, timestamp)
	
		// eventList contains HALF_TIME -> add event (GAME_END, timestamp)
	

// lookup event
	
	// last event POSSIBLE_KICK_OFF
		// all player teamA behind ball and all player teamB other side of ball 
	
			// no event before POSSIBLE_KICK_OFF -> add event (START_FIRST_HALF, timestamp)
	
				// init active ball and init sides
	
			// event HALF_TIME before POSSIBLE_KICK_OFF -> add event (START_SECOND_HALF, timestamp)
	
				// init active ball and init sides
	
			// event BALL_GOAL_LINE before POSSIBLE_KICK_OFF
	
				// get team whose player is closest to ball then select the other team -> add event GOAL_TEAMX (depending which team selected) 
	
	
	// last event POSSIBLE_CORNER
		// event BALL_LEFT_GOAL_LINE before POSSIBLE_CORNER
			// get side with corner lies and player near ball 
				// is player from team of the other side
					// if player is from team A -> add event (CORNER_TEAM_A, timestamp)
					// if player is from team B -> add event (CORNER_TEAM_B, timestamp)
	
	// last event POSSIBLE_
	
	
// not found in eventList backtrack
	
	private double pointSideOfLine(Point3D p3, Point3D p2, Point3D p1) {
		
		// (Bx - Ax) * (Cy - Ay) - (By - Ay) * (Cx - Ax)
		// http://www.gamedev.net/topic/542870-determine-which-side-of-a-line-a-point-is/
		return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());	
	}
	
	
	private boolean pointInsiderCirle(Point3D center, double radius, Point3D p) {
		// http://math.stackexchange.com/questions/198764/how-to-know-if-a-point-is-inside-a-circle
		// d = (pX - cX)^2 + (pY -cY)^2
		double dPow = Math.pow(p.getX() - center.getX(), 2) + Math.pow(p.getY() - center.getY(), 2);
		double rPow = Math.pow(radius, 2);
		
		if(dPow <= rPow) {
			return true;
		} else {
			return false;
		}
	}
}
