package de.landshut.haw.edu.control.algorithm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.landshut.haw.edu.model.Ball;
import de.landshut.haw.edu.model.Participant;
import de.landshut.haw.edu.model.Sensor;
import de.landshut.haw.edu.model.Team;
import de.landshut.haw.edu.model.environment.SoccerEnvironment;
import de.landshut.haw.edu.model.soccer.SoccerField;
import de.landshut.haw.edu.util.Constants;
import de.landshut.haw.edu.util.Event;
import de.landshut.haw.edu.util.Point3D;
import de.landshut.haw.edu.util.PointMethods;

public class RuleV0 {
		
	private Ball activeBall, possibleActiveBall;
	
	private ArrayList<Event> fullEventList;
	
	private SoccerEnvironment environment;
	
	private Team topHalf, bottomHalf;
	
	private boolean gameStarted, secondHalfStarted;
	
	private long startTimestamp, goal_line_Timestamp;
	
	private Participant markedThrowIn;
	
	private String goal_line, side_line;

	
	
	
	public RuleV0() {
		fullEventList = null;
		
		activeBall = null;
		
		gameStarted = secondHalfStarted = false;
		
		startTimestamp = goal_line_Timestamp = 0L;
		
		goal_line = side_line = "";
	}
	
	
	public void checkAgainstRules(SoccerEnvironment environment) {

		this.environment = environment;
		
		checkPossibleKickOff();
		
		if(fullEventList!= null) {
			checkExecutedKickOff();
		}
		
		if(gameStarted == true) {
			
			checkBallGoalLineExit();
			
			//check top corner
			checkPossibleCornerKick(environment.getPlayfield().getLeftTop(), 
										environment.getPlayfield().getRightTop(), Event.BALL_EXIT_TOP_GOAL, Event.POSSIBLE_CORNER_TOP);
			
			checkExecutedCornerKick(environment.getPlayfield().getLeftTop(), 
					environment.getPlayfield().getRightTop(), topHalf, Event.POSSIBLE_CORNER_TOP, Event.EXECUTE_CORNER_TOP);

			//check bottom corner
			checkPossibleCornerKick(environment.getPlayfield().getLeftBottom(), 
										environment.getPlayfield().getRightBottom(), Event.BALL_EXIT_BOTTOM_GOAL, Event.POSSIBLE_CORNER_BOTTOM);
			
			checkExecutedCornerKick(environment.getPlayfield().getLeftBottom(), 
					environment.getPlayfield().getRightBottom(), bottomHalf, Event.POSSIBLE_CORNER_BOTTOM, Event.EXECUTE_CORNER_BOTTOM);
			
			//check throw ins
			checkPossibleThrowIn();
			
			checkExecutedThrowIn();
			
			//check goal-kicks
			checkPossibleGoalKick();

			checkExecutedGoalKick();
		}
	}
	
	
	/**
	 * Check if a possible kick off occurred: <br>
	 * 1. Ball is idle. Idle is defined by <b>Constants.BALL_IDLE.</b><br>
	 * 2. Ball lies in center area. Radius in which is search is defined by <b>Constants.RADIUS_TOLERANCE.</b><br>
	 * 3. Both teams are on their sides.
	 */
	private void checkPossibleKickOff() {
		
		Point3D centerPoint = environment.getPlayfield().getCenterPoint();
			
		// check if any ball is in center point/area
		for(Ball b: environment.getBalls()) {
			
			if(velocityCheck(b, Constants.BALL_IDLE, false)) { // 1.
				
				// check if ball is in center area
				boolean ret = PointMethods.pointInsideCirle(centerPoint, Constants.RADIUS_TOLERANCE, b.getBall().getCoordinates());
			
				if(ret) { // 2.
	
					if(teamsOnDifferentSides(environment.getPlayfield().getCenterPoint(), 
												environment.getTeamA(), environment.getTeamB())) { // 3.
						
						// add game started kick off
						if(fullEventList == null) {
							fullEventList = new ArrayList<Event>();			
						} 
						
						addEvent(Event.POSSIBLE_KICK_OFF, environment.getTimestamp(), null);
						
						possibleActiveBall = b;	
					}
				}
			}
		}			
	}
	

	
	
	/**
	 * Check if a kick-off is executed.
	 * Conditions that have to be met: <br>
	 * 1. Ball at center point <br>
	 * 2. Ball gains acceleration and ball still has no velocity<br>
	 * 3. Teams on either side. <br>
	 * If these conditions are met add event depending on previous events.
	 */
	private void checkExecutedKickOff() {
		
		// check if ball is in near center point
		Point3D centerPoint = environment.getPlayfield().getCenterPoint();
					
		boolean ret = PointMethods.pointInsideCirle(centerPoint, Constants.RADIUS_TOLERANCE, possibleActiveBall.getBall().getCoordinates());
			
		if(ret) { // 1.
				
			if(velocityCheck(possibleActiveBall, Constants.BALL_SLOW, false) && accelerationCheck(possibleActiveBall, Constants.KICKOFF_BALL_ACCELERATION, true)) { // 2
				
				
				if(teamsOnDifferentSides(environment.getPlayfield().getCenterPoint(), 
										environment.getTeamA(), environment.getTeamB())) {
					
					// add game started kick off
					if(gameStarted == false && checkLastEvent(Event.POSSIBLE_KICK_OFF, 1)) {
						
						startTimestamp = environment.getTimestamp();
													
						addEvent(Event.GAME_START, environment.getTimestamp(), null);
						
						gameStarted = true;
						
						activeBall = possibleActiveBall;
						
					} else if (gameStarted) { 
						
						if (!secondHalfStarted  && checkLastEvent(Event.SIDES_CHANGED, 1) && checkLastEvent(Event.POSSIBLE_KICK_OFF, 2)  ||
								!secondHalfStarted && checkLastEvent(Event.SIDES_CHANGED, 2) && checkLastEvent(Event.POSSIBLE_KICK_OFF, 1)) {
							
							addEvent(Event.SECOND_HALF_START, environment.getTimestamp(), null);
	
							startTimestamp = environment.getTimestamp();
							
							activeBall = possibleActiveBall;
							
							secondHalfStarted = true;

							// reset values
							goal_line = side_line = "";

							goal_line_Timestamp = 0;
							
						} // add goal 
						else if (goal_line_Timestamp > 0 && goal_line.equals(Event.BALL_EXIT_BOTTOM_GOAL) 
										&& (checkLastEvent(Event.POSSIBLE_KICK_OFF, 1) || checkLastEvent(Event.POSSIBLE_KICK_OFF, 2)) ) {
							
							addEvent(topHalf.getName() + " " + Event.GOAL , goal_line_Timestamp, null);
							
							activeBall = possibleActiveBall;
							
							// reset values
							goal_line_Timestamp = 0;
							
							goal_line = "";
							
						} // add goal
						else if (goal_line_Timestamp > 0 && goal_line.equals(Event.BALL_EXIT_TOP_GOAL)
										&& (checkLastEvent(Event.POSSIBLE_KICK_OFF, 1) || checkLastEvent(Event.POSSIBLE_KICK_OFF, 2)) ) {
							
							addEvent(bottomHalf.getName() + " " + Event.GOAL, goal_line_Timestamp, null);
							
							activeBall = possibleActiveBall;
							
							// reset values
							goal_line_Timestamp = 0;
							
							goal_line = "";
						}
					}
				}
			}
		}			
	}
	
	
	
	
	/**
	 * Check if a corner is possible.
	 */
	private void checkPossibleCornerKick(Point3D corner1, Point3D corner2, String previousEvent ,String event) {
		
		// check corners
		if(checkCorner(corner1) || checkCorner(corner2) ) {
				addEvent(event, environment.getTimestamp(), null);
		}
	}


	
	
	
	/** 
	 * Check if a idle ball lies in given corner.
	 * @param corner
	 * @return
	 */
	private boolean checkCorner(Point3D corner) {
		
		for(Ball b: environment.getBalls()) {
			boolean ret = PointMethods.pointInsideCirle(corner, Constants.CORNER_AREA, b.getBall().getCoordinates());
			
			if(ret) {
				if(velocityCheck(b, Constants.BALL_IDLE, false) && accelerationCheck(b, Constants.KICKOFF_LOW_ACCELERATION, false)) { 
					possibleActiveBall = b;
					
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	
	/**
	 * Check if a possible corner was executed thus was a corner.<br>
	 * 1. Ball is still in corner. <br>
	 * 2. No opposing player is near <br>
	 * 3. Ball gains acceleration. <br>
	 */
	private void checkExecutedCornerKick(Point3D corner1, Point3D corner2, Team defending, String previousEvent, String event) {
		
		// event possible corner in previous
		if(checkLastEvent(previousEvent, 1)) {
			
			if(checkCorner(corner1) || checkCorner(corner2) ) { // 1.
				
				// no player of defending team near
				boolean noDefendingPlayerNear = true;
				
				noDefendingPlayerNear = isPlayerFromTeamNearPoint(defending, possibleActiveBall.getBall().getCoordinates(), Constants.RADIUS_CORNER);

				if(noDefendingPlayerNear) { // 2.
					
					if(velocityCheck(possibleActiveBall, Constants.BALL_SLOW, false) && accelerationCheck(possibleActiveBall, Constants.CORNER_BALL_ACCELERATION, true)) {

						if(event.equals(Event.BALL_EXIT_BOTTOM_GOAL)) {	
							addEvent(topHalf.getName() + " " + event, environment.getTimestamp(), null);
							
						} else {
							addEvent(bottomHalf.getName() + " " + event, environment.getTimestamp(), null);
						}
						
							activeBall = possibleActiveBall;
					}
				}
			}	
		}
	}
	
	
	
	
	/**
	 * Check if a player from any team is standing at a side line with a ball behind him. 
	 */
	private void checkPossibleThrowIn() {
				
		// check left side
		throwInCheckTeam(topHalf, Event.BALL_EXIT_LEFT_SIDE);
		
		throwInCheckTeam(bottomHalf, Event.BALL_EXIT_LEFT_SIDE);
				
		// check right side 			
		throwInCheckTeam(topHalf, Event.BALL_EXIT_RIGHT_SIDE);
			
		throwInCheckTeam(bottomHalf, Event.BALL_EXIT_RIGHT_SIDE);
	}
	
	
	
	
	/**
	 * Checks all players from a <b>team</b> if they stand at a given <b>side</b>.
	 * @param team
	 * @param side
	 */
	private void throwInCheckTeam(Team team, String side) {
		
		double leg1, leg2;

		for(Participant p: team.getPlayers()) {
			
			Point3D leftLeg = p.getLeftLeg().getCoordinates();
			Point3D rightLeg = p.getRightLeg().getCoordinates();
			
			if(side.equals(Event.BALL_EXIT_LEFT_SIDE)) { 
				
				Point3D leftBot = environment.getPlayfield().getLeftBottom();
				Point3D leftTop = environment.getPlayfield().getLeftTop();
				
				leg1 = PointMethods.pointSideOfLine(leftLeg, leftBot, leftTop);
				leg2 = PointMethods.pointSideOfLine(rightLeg, leftBot, leftTop);
				
				if(leg1 < 0 && leg2 < 0) { // player behind left line
					
					// is ball behind player
					for(Ball b: environment.getBalls()) {
						
						Point3D ball = b.getBall().getCoordinates();
						
						if(PointMethods.pointInsideCirle(leftLeg, Constants.RADIUS_THROW_IN, ball) &&
									ball.getZ() > leftLeg.getY() + Constants.THROW_IN_MINIMUM_HEIGHT) {
							
							addEvent(Event.POSSIBLE_THROW_IN_LEFT, environment.getTimestamp(), p.getLeftLeg());
							
							markedThrowIn = p;
							 
							possibleActiveBall = b;
							
							side_line = Event.BALL_EXIT_LEFT_SIDE;
						}
					}	
				}
			} else if (side.equals(Event.BALL_EXIT_RIGHT_SIDE)) { 
				
				Point3D rightBot = environment.getPlayfield().getRightBottom();
				Point3D rightTop = environment.getPlayfield().getRightTop();
				
				leg1 = PointMethods.pointSideOfLine(leftLeg, rightBot, rightTop);
				leg2 = PointMethods.pointSideOfLine(rightLeg, rightBot, rightTop);

				if(leg1 > 0 && leg2 > 0) { // player behind left line
				
					// is ball behind and near player 
					for(Ball b: environment.getBalls()) {
						
						Point3D ball = b.getBall().getCoordinates();
						
						if(PointMethods.pointInsideCirle(leftLeg, Constants.RADIUS_THROW_IN, ball) &&
									ball.getZ() > leftLeg.getY() + Constants.THROW_IN_MINIMUM_HEIGHT) {
							
							addEvent(Event.POSSIBLE_THROW_IN_RIGHT, environment.getTimestamp(), p.getLeftLeg());
							
							markedThrowIn = p;
							
							possibleActiveBall = b;
							
							side_line = Event.BALL_EXIT_RIGHT_SIDE;
						}
					}	
				}
			}
		}
	}
	
	
	
	
	/**
	 * Check is marked player executed a throw-in.
	 */
	private void checkExecutedThrowIn() {
		
		if(markedThrowIn != null) {
			double leg1, leg2;
			
			String teamName;
			
			if(isPlayerInTeam(topHalf, markedThrowIn)) {
				teamName = topHalf.getName();
			} else {
				teamName = bottomHalf.getName();
			}
			
			Point3D leftLeg = markedThrowIn.getLeftLeg().getCoordinates();
			
			Point3D rightLeg = markedThrowIn.getRightLeg().getCoordinates();
			
			if(checkLastEvent(Event.POSSIBLE_THROW_IN_LEFT, 1) || checkLastEvent(Event.POSSIBLE_THROW_IN_RIGHT, 1)) {
				
				if(side_line.equals(Event.BALL_EXIT_LEFT_SIDE)) { // test left side
					
					Point3D leftBot = environment.getPlayfield().getLeftBottom();
					Point3D leftTop = environment.getPlayfield().getLeftTop();
					
					leg1 = PointMethods.pointSideOfLine(leftLeg, leftBot, leftTop);
					leg2 = PointMethods.pointSideOfLine(rightLeg, leftBot, leftTop);
					
					if(leg1 < 0 && leg2 < 0) { // player behind left line
						
						double ball = PointMethods.pointSideOfLine(possibleActiveBall.getBall().getCoordinates(), leftBot, leftTop);
						
						if(ball > 0 && possibleActiveBall.getBall().getCoordinates().getZ() > leftLeg.getZ() + Constants.THROW_IN_MINIMUM_HEIGHT) {
							
							addEvent(teamName + " " + Event.EXECUTE_THROW_IN_LEFT, environment.getTimestamp(), null);
							
							activeBall = possibleActiveBall;
							
							goal_line = "";
							
							side_line = "";
							
							markedThrowIn = null;
						}
					}
				} else if(side_line.equals(Event.BALL_EXIT_RIGHT_SIDE)) { // test right side
					
					Point3D rightBot = environment.getPlayfield().getRightBottom();
					Point3D rightTop = environment.getPlayfield().getRightTop();
					
					leg1 = PointMethods.pointSideOfLine(leftLeg, rightBot, rightTop);
					leg2 = PointMethods.pointSideOfLine(rightLeg, rightBot, rightTop);
					
					if(leg1 > 0 && leg2 > 0) { // player behind left line
						
						double ball = PointMethods.pointSideOfLine(possibleActiveBall.getBall().getCoordinates(), rightBot, rightTop);
						
						if(ball < 0 && possibleActiveBall.getBall().getCoordinates().getZ() > leftLeg.getZ() + Constants.THROW_IN_MINIMUM_HEIGHT) {
					
							addEvent(teamName + " " + Event.EXECUTE_THROW_IN_RIGHT, environment.getTimestamp(), null);
							
							activeBall = possibleActiveBall;
							
							goal_line = "";
							
							side_line = "";
							
							markedThrowIn = null;
						}
					}
				}								
			} 
		}
	}
	
	

	
	
	private void checkPossibleGoalKick() {	
		
		Point3D leftBot = environment.getPlayfield().getLeftBottom();
		Point3D rightBot = environment.getPlayfield().getRightBottom();
		
		Point3D leftTop = environment.getPlayfield().getLeftTop();
		Point3D rightTop = environment.getPlayfield().getRightTop();
		
		boolean noEnemyPlayerNear = true;
		
		boolean noDefendingPlayerNear = true;
		
		for(Ball b: environment.getBalls()) {
			
			Point3D ball = b.getBall().getCoordinates();
			
			// ball lies idle in goal area	
			if(velocityCheck(b, Constants.BALL_IDLE, false)) {
				
				// check if ball is in bottom goal area
				if(ball.getY() >= leftBot.getY() && ball.getY() < leftBot.getY() + Constants.GOAL_AREA
						&& ball.getX() > leftBot.getX() && ball.getX() < rightBot.getX()) {
					
					noEnemyPlayerNear = isPlayerFromTeamNearPoint(topHalf, ball, Constants.RADIUS_GOALKICK);
					
					noDefendingPlayerNear = isPlayerFromTeamNearPoint(bottomHalf, ball, Constants.RADIUS_GOALKICK);					
					
					if(noEnemyPlayerNear && !noDefendingPlayerNear) {
						
						addEvent(Event.POSSIBLE_GOALKICK_BOTTOM, environment.getTimestamp(), b.getBall());
						
						possibleActiveBall = b;	
						
					}
				} // check if ball is in top goal area
				else if (ball.getY() <= leftTop.getY() && ball.getY() > leftTop.getY() - Constants.GOAL_AREA
						&& ball.getX() > leftTop.getX() && ball.getX() < rightTop.getX()) {
					
					noEnemyPlayerNear = isPlayerFromTeamNearPoint(bottomHalf, ball, Constants.RADIUS_GOALKICK);
					
					noDefendingPlayerNear = isPlayerFromTeamNearPoint(topHalf, ball, Constants.RADIUS_GOALKICK);
					
					if(noEnemyPlayerNear && !noDefendingPlayerNear) {
						
						addEvent(Event.POSSIBLE_GOALKICK_TOP, environment.getTimestamp(), new Sensor(b.getBall()));
						
						possibleActiveBall = b;	
					}
				}
			}
		}	
	}

	
	
	
	
	
	
	
	
	
	private void checkExecutedGoalKick() {
		
		boolean noEnemyPlayerNear = true;
		boolean noDefendingPlayerNear = true;
		
		// checkPossibleGoalKick as last event
		if(checkLastEvent(Event.POSSIBLE_GOALKICK_BOTTOM, 1) || checkLastEvent(Event.POSSIBLE_GOALKICK_TOP, 1) ){
			
			Point3D ball = fullEventList.get(fullEventList.size() -1 ).getSensor().getCoordinates();
			
			String event = fullEventList.get(fullEventList.size() -1 ).getEvent();
			
			// ball lies idle in/near same position
			if(velocityCheck(possibleActiveBall, Constants.BALL_IDLE, false) 
					&& PointMethods.pointInsideCirle(ball, Constants.RADIUS_TOLERANCE_BALL_MOVED, possibleActiveBall.getBall().getCoordinates())) {
				
				// ball gains acceleration
				if(accelerationCheck(possibleActiveBall, Constants.KICK_ACCELERATION, true)) {
					
					Point3D possibleBall = possibleActiveBall.getBall().getCoordinates();
					
					// no enemy player in radius x && player near ball
					if(event.equals(Event.POSSIBLE_GOALKICK_BOTTOM)) {
						
						noEnemyPlayerNear = isPlayerFromTeamNearPoint(topHalf, possibleBall, Constants.RADIUS_GOALKICK);
						
						noDefendingPlayerNear = isPlayerFromTeamNearPoint(bottomHalf, possibleBall, Constants.RADIUS_GOALKICK);
						
						if(noEnemyPlayerNear && !noDefendingPlayerNear) {
							
							addEvent(Event.EXECUTE_GOALKICK_BOTTOM, environment.getTimestamp(), null);
							
							activeBall = possibleActiveBall;	
						}
					} else {
						
						noEnemyPlayerNear = isPlayerFromTeamNearPoint(bottomHalf, possibleBall, Constants.RADIUS_GOALKICK);
						
						noDefendingPlayerNear = isPlayerFromTeamNearPoint(topHalf, possibleBall, Constants.RADIUS_GOALKICK);
						
						if(noEnemyPlayerNear && !noDefendingPlayerNear) {
							
							addEvent(Event.EXECUTE_GOALKICK_TOP, environment.getTimestamp(), null);
							
							activeBall = possibleActiveBall;	
						}
					}
				}
				
			} else {
				addEvent(Event.NO_POSSIBLE_GOALKICK, environment.getTimestamp(), null);
			}
		}
	}
	
	private void checkPossibleFreeKick() {
		// ball lies idle in field
				
		// no enemy player in radius x
		
		// player near ball
	}
	
	private void checkExecutedFreeKick() {
		// checkPossibleFreeKick as last event
		
		// ball lies idle in goal area
		
		// ball gains acceleration
				
		// no enemy player in radius x
		
		// player near ball
	}
	
	
	
	/**
	 * Test if ball has left over one of the side lines.
	 * Add event if detected
	 */
//	private void checkBallSideLineExit() {
//
//		Sensor ball = activeBall.getBall();
//		
//		double ret;
//		
//		//test left side
//		SoccerField field = environment.getPlayfield();
//		
//		ret = PointMethods.pointSideOfLine(ball.getCoordinates(), field.getLeftBottom(), field.getLeftTop());
//		
//		if(ret < 0) {
//			addEvent(Event.BALL_EXIT_LEFT_SIDE, environment.getTimestamp(), new Sensor(ball));
//		}
//		
//		//test right side
//		ret = PointMethods.pointSideOfLine(ball.getCoordinates(), field.getRightBottom(), field.getRightTop());
//		
//		if(ret > 0) {
//			addEvent(Event.BALL_EXIT_RIGHT_SIDE, environment.getTimestamp(), new Sensor(ball));
//		}
//		
//	}
	
	
	/**
	 * Test if ball has left over one of the lines.
	 * Add event if detected.
	 */
//	private void checkBallFieldExit() {
//
//		Sensor ball = activeBall.getBall();
//
//		SoccerField field = environment.getPlayfield();
//		
//		double leftSide, rightSide, topSide, bottomSide;
//		
//		// test left line
//		leftSide = PointMethods.pointSideOfLine(ball.getCoordinates(), field.getLeftBottom(), field.getLeftTop());
//		
//		if(leftSide <= 0) {
//			side_line = Constants.FIELD_EXIT_LEFT;
//		}
//		
//		// test right line
//		rightSide = PointMethods.pointSideOfLine(ball.getCoordinates(), field.getRightBottom(), field.getRightTop());
//		
//		if(rightSide >= 0) {
//			side_line = Constants.FIELD_EXIT_RIGHT;
//		}
//		
//		// test top line
//		topSide = PointMethods.pointSideOfLine(ball.getCoordinates(), field.getRightTop(), field.getLeftTop());
//		
//		if(topSide >= 0) {		
//			goal_line = Constants.FIELD_EXIT_TOP;
//		}
//		
//		// test bottom line
//		bottomSide = PointMethods.pointSideOfLine(ball.getCoordinates(), field.getRightBottom(), field.getLeftBottom());
//		
//		if(bottomSide <= 0) {
//			goal_line = Constants.FIELD_EXIT_BOTTOM;
//		}
//		
//		
//		// exit over one of the side lines
//		if(goal_line.equals("") && !side_line.equals("")) { 
//			
//			if(side_line.equals(Constants.FIELD_EXIT_LEFT)){
//				
//				addEvent(Event.BALL_EXIT_LEFT_SIDE, environment.getTimestamp(), new Sensor(ball));
//				
//			} else {
//				
//				addEvent(Event.BALL_EXIT_RIGHT_SIDE, environment.getTimestamp(), new Sensor(ball));
//			}
//		// exit over one of the goal lines	
//		} else if (!goal_line.equals("") && side_line.equals("")) { 
//			
//			if(goal_line.equals(Constants.FIELD_EXIT_TOP)){
//				
//				addEvent(Event.BALL_EXIT_TOP_GOAL, environment.getTimestamp(), new Sensor(ball));
//				
//			} else {
//				
//				addEvent(Event.BALL_EXIT_BOTTOM_GOAL, environment.getTimestamp(), new Sensor(ball));
//			}
//		}
//		
//	}
	
	
	


	
	/**
	 * Test if ball has left over one of the goal lines.
	 * Add event if detected
	 */
	private void checkBallGoalLineExit() {
		
		if(activeBall != null) {
			
			Sensor ball = activeBall.getBall();
			
			double ret;
			
			//test top side
			SoccerField field = environment.getPlayfield();
			
			ret = PointMethods.pointSideOfLine(ball.getCoordinates(), field.getRightTop(), field.getLeftTop());
			
			if(ret > 0) {
				
				goal_line = Event.BALL_EXIT_TOP_GOAL;
				
				goal_line_Timestamp = environment.getTimestamp();
					
				addEvent(Event.BALL_EXIT_TOP_GOAL, environment.getTimestamp(), new Sensor(ball));
				
				activeBall = null;
			}
			
			//test bottom side
			ret = PointMethods.pointSideOfLine(ball.getCoordinates(), field.getRightBottom(), field.getLeftBottom());
			
			if(ret < 0) {
				
				goal_line = Event.BALL_EXIT_BOTTOM_GOAL;
				
				goal_line_Timestamp = environment.getTimestamp();
					
				addEvent(Event.BALL_EXIT_BOTTOM_GOAL, environment.getTimestamp(), new Sensor(ball));
				
				activeBall = null;
			}
		}
	}
		
	
	
	
	
	/**
	 * See if the players of both teams are on one half of the field.
	 * @param centerPoint Defines point which divides field in halves
	 * @param teamA
	 * @param teamB
	 * @return
	 */
	private boolean teamsOnDifferentSides(Point3D centerPoint, Team teamA, Team teamB) {
		
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
					
				if(topHalf != null && bottomHalf != null && fullEventList != null) {
					
					//check if sides changed 
					if(topHalf.getName().equals(teamA.getName()) && bottomHalf.getName().equals(teamB.getName())) {						
						addEvent(Event.SIDES_CHANGED, environment.getTimestamp(), null);
					}
				}
				
				topHalf = teamB;
				bottomHalf = teamA;
				
				return true;
			}
		}
		
		// is team B false and team A true
		if(countFalseTeamB + Constants.TOLERANCE_PLAYER >= sameSideTeamB.length) {
			
			if(countTrueTeamA + Constants.TOLERANCE_PLAYER >= sameSideTeamA.length) {
				
				if(topHalf != null && bottomHalf != null && fullEventList != null) {
					
					//check if sides changed
					if(topHalf.getName().equals(teamB.getName()) && bottomHalf.getName().equals(teamA.getName())) {
						addEvent(Event.SIDES_CHANGED, environment.getTimestamp(), null);
					}	
				}
				topHalf = teamA;
				bottomHalf = teamB;

				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	
	/**
	 * Is any player from given <b>team</b> inside a <b>radius</b> with <b>cirlePoint</b> as circle centerPoitn
	 * @param team
	 * @param cirlePoint
	 * @param radius
	 * @return <b>true</b> = no player is inside circle radius; <b>false</b> = player is inside circle radius
	 */
	private boolean isPlayerFromTeamNearPoint(Team team, Point3D cirlePoint, double radius) {
		
		boolean isAnyPlayerTeamNearby = true;

		for(Participant p: team.getPlayers()) { 
			
			// is player p near corner (check both legs)
			if(PointMethods.pointInsideCirle(cirlePoint, radius, p.getLeftLeg().getCoordinates()) ||
					PointMethods.pointInsideCirle(cirlePoint, radius, p.getRightLeg().getCoordinates())) {
				
				isAnyPlayerTeamNearby = false;
			} 
		}
		return isAnyPlayerTeamNearby;
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
	 * Check if a given player is in a given team.
	 * @param team
	 * @param player
	 * @return <b>true</b> player is in team; <b>false</b> player is not in team
	 */
	private boolean isPlayerInTeam(Team team, Participant player){
		Participant[] players = team.getPlayers();
		
		for(Participant p: players) {
			
			if(p.compare(player)) {
				return true;
			}
		}
		return false;
	}

	
	
	
	
	/**
	 * Get the nearest player from a team to a given point.
	 * @param point
	 * @param team
	 * @return
	 */
	private Participant nearestPlayerToPoint(Point3D point, Participant[] team) {
		
		HashMap<Double, Participant> distancePlayer = new HashMap<Double, Participant>();
		
		double lowestKey = Double.MAX_VALUE;
		
		//populate with players from team
		for(Participant p: team) {
			// calculate distance from legs to point
			double distLeftLeg = PointMethods.distanceBetweenTwoPoint(point, p.getLeftLeg().getCoordinates());
			double distRightLeg = PointMethods.distanceBetweenTwoPoint(point, p.getRightLeg().getCoordinates());
			
			// get medium distance
			double distanceMedian = (distLeftLeg + distRightLeg) / 2;
			
			distancePlayer.put(distanceMedian, p);
		}

		// getLowestKey
		for (Map.Entry<Double, Participant> entry : distancePlayer.entrySet()) {
		   if(entry.getKey() < lowestKey){
			   lowestKey = entry.getKey();
		   }
		}
		return distancePlayer.get(lowestKey);
	}


	
	
	
	/**
	 * Test if ball velocity is greater / smaller then <b>compare</b> value
	 * @param b
	 * @param compare
	 * @param greater true check if ball velocity is greater then compare else if smaller
	 * @return
	 */
	private boolean velocityCheck(Ball b, double compare, boolean greater) {
		double value;
		
		if(greater) {
			value = PointMethods.getAbsoluteValueofPoint3D(b.getBall().getVelocityInMeterPerSeconds());	
			return (value > compare);	
		} else {
			value = PointMethods.getAbsoluteValueofPoint3D(b.getBall().getVelocityInMeterPerSeconds());
			return (value <= compare);	
		}
	}
	
	
	
	
	/**
	 * Test if ball acceleration is greater / smaller then <b>compare</b> value
	 * @param b
	 * @param compare
	 * @param greater true check if ball acceleration is greater then compare else if smaller
	 * @return
	 */
	private boolean accelerationCheck(Ball b, double compare, boolean greater) {
		if(greater) {
			return (PointMethods.getAbsoluteValueofPoint3D(b.getBall().getAccelerationInMeterPerSecondsPow()) > compare);	
		} else {
			return (PointMethods.getAbsoluteValueofPoint3D(b.getBall().getAccelerationInMeterPerSecondsPow()) < compare);	
		}
	}
	

	


	/**
	 * Add event if it doesn't goes against logic or it is last entry is the same event.
	 * @param event
	 * @param timestamp
	 * @param sensor
	 */
	private void addEvent(String event, long timestamp, Sensor sensor) {
		
		if(fullEventList != null) {
			
			// if event type is not the same as the last
			if(!checkLastEvent(event, 1)) {
				fullEventList.add(new Event(event, timestamp, sensor));
//				if(!event.startsWith("Possible")){
					System.out.println(event + " at " + (timestamp - startTimestamp));
//				}
			}
		}
	}
	
	
	
	/**
	 * Check from last entry - offset if event is equal against <b>event</b>. <br>
	 * If arrays size is 0 return false.
	 * @param event
	 * @param offset 
	 * @return
	 */
	private boolean checkLastEvent(String event, int offset) {
		
		if(fullEventList == null) {
			return false;
			
		} else if (fullEventList.size() == 0) {
			return false;
			
		} else {
			if(fullEventList.get(fullEventList.size() - offset).getEvent().equals(event)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	/**
	 * Write important events to file and ignore unimportant events.
	 */
	public void writeEventListToFile() {
		
		FileWriter writer;
		
		ArrayList<String>ignoreList = new ArrayList<String>();
			ignoreList.add(Event.BALL_EXIT_BOTTOM_GOAL);
			ignoreList.add(Event.BALL_EXIT_TOP_GOAL);
			ignoreList.add(Event.BALL_EXIT_LEFT_SIDE);
			ignoreList.add(Event.BALL_EXIT_RIGHT_SIDE);
			ignoreList.add(Event.POSSIBLE_CORNER_BOTTOM);
			ignoreList.add(Event.POSSIBLE_CORNER_TOP);
			ignoreList.add(Event.POSSIBLE_THROW_IN_LEFT);
			ignoreList.add(Event.POSSIBLE_THROW_IN_RIGHT);
			ignoreList.add(Event.POSSIBLE_KICK_OFF);
			ignoreList.add(Event.POSSIBLE_GOALKICK_BOTTOM);
			ignoreList.add(Event.POSSIBLE_GOALKICK_TOP);

		
		try {
			writer = new FileWriter("eventList.txt");
			for(Event str: fullEventList) {
				
				if(!ignoreList.contains(str.getEvent())) {
					writer.write(str.toString() + "\n");
				}	
			}
			writer.close();
			
		} catch (IOException e1) {
			System.err.println("Error while writing event list to file");
		} 
	}
	
}

