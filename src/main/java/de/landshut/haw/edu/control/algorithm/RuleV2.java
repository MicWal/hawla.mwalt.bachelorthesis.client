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
import de.landshut.haw.edu.puffer.PufferHolder;
import de.landshut.haw.edu.util.Constants;
import de.landshut.haw.edu.util.Event;
import de.landshut.haw.edu.util.Point3D;
import de.landshut.haw.edu.util.PointMethods;

/**
 * Test game against kickoff, goal, corner, throw-in occurrences.
 * @author Michael
 *
 */
public class RuleV2 {
		
	
	private ArrayList<Ball> activeBalls;
	
	private ArrayList<Event> fullEventList;
	
	private SoccerEnvironment environment;
	
	private Participant markedThrowInPlayer;
	
	private Ball markedThrowInBall;
	
	private Team topHalf, bottomHalf;
	
	private boolean gameStarted, secondHalfStarted;
	
	private long startTimestamp, goal_line_Timestamp;
	
	private String goal_line, corner_line, side_line;
	
	private final long sameMassageTimeout = 83333333333L; // 5sek
														

	
	public RuleV2() {
		
		fullEventList = null;
		
		activeBalls = new ArrayList<Ball>();
		
		markedThrowInPlayer = null;
		
		markedThrowInBall = null;
		
		gameStarted = secondHalfStarted = false;
				
		startTimestamp = goal_line_Timestamp = 0L;
		
		goal_line = corner_line = side_line = "";
		
	}
	
	
	/**
	 * This checks given environment against implemented rules.
	 * And generates Events if a match is found.
	 * @param environment
	 */
	public void checkAgainstRules(SoccerEnvironment environment) {

		this.environment = environment;
		
		setActiveBalls();
		
		if(activeBalls.size() == 1) {
			
			// insert methods here who need ball on field
			
			checkPossibleKickOff();
			
			if(fullEventList != null) {
				
				checkExecutedKickOff();
								
//				//check top corner
//				checkPossibleCornerKick(environment.getPlayfield().getLeftTop(), 
//											environment.getPlayfield().getRightTop(), topHalf, bottomHalf, Event.POSSIBLE_CORNER_TOP);
//				
//				checkExecutedCornerKick(environment.getPlayfield().getLeftTop(), 
//						environment.getPlayfield().getRightTop(), topHalf, bottomHalf, Event.POSSIBLE_CORNER_TOP, Event.EXECUTE_CORNER_TOP);
//
//				//check bottom corner
//				checkPossibleCornerKick(environment.getPlayfield().getLeftBottom(), 
//											environment.getPlayfield().getRightBottom(), bottomHalf, topHalf, Event.POSSIBLE_CORNER_BOTTOM);
//				
//				checkExecutedCornerKick(environment.getPlayfield().getLeftBottom(), 
//						environment.getPlayfield().getRightBottom(), bottomHalf, topHalf, Event.POSSIBLE_CORNER_BOTTOM, Event.EXECUTE_CORNER_BOTTOM);				
				
				checkBallGoalLineExit();
				
			}
		} else if( activeBalls.size() > 1) {
					
			activeBalls.clear();
			
		} 
		
		//add methods who don't need an ball on the field
		if(fullEventList != null) {
			
			//check throw ins
			checkPossibleThrowIn();
			
			checkExecutedThrowIn();
		}
		
	}
	
	/**
	 * Return an ArrayList populated with balls that are active in given environment.
	 * @param environment Extract active ball from environment
	 * @return list with active balls (can have no elements)
	 */
	private ArrayList<Ball> setActiveBalls(SoccerEnvironment environment) {
		
		ArrayList<Ball> tmpBalls = environment.getBalls();
		
		ArrayList<Ball> activeBalls = new ArrayList<Ball>();
		
		Point3D[] boundary = new Point3D[] {environment.getPlayfield().getLeftBottom(), environment.getPlayfield().getRightBottom(),
								environment.getPlayfield().getLeftTop(), environment.getPlayfield().getRightTop()};
		
		for(Ball b: tmpBalls) {
			
			if(PointMethods.contains(boundary, b.getBall().getCoordinates())) {
						
				activeBalls.add(b);
			}
		}
		
		return activeBalls;
		
	}


	
	private void setActiveBalls() {
		
		ArrayList<Ball> tmpBalls = environment.getBalls();
		
		Point3D[] boundary = new Point3D[] {environment.getPlayfield().getLeftBottom(), environment.getPlayfield().getRightBottom(),
								environment.getPlayfield().getLeftTop(), environment.getPlayfield().getRightTop()};
		
		for(Ball b: tmpBalls) {
			
			if(PointMethods.contains(boundary, b.getBall().getCoordinates())) {
				
				if(!activeBalls.contains(b)) {
					
					activeBalls.add(b);
					
				}
			}
		}
	}
	
	
	
	
	/**
	 * Check if a possible kick off occurred: <br>
	 * 1. Ball is idle.<br>
	 * 2. Ball lies in center area.<br>
	 * 3. Both teams are on their sides.
	 */
	private void checkPossibleKickOff() {
		
		Point3D centerPoint = environment.getPlayfield().getCenterPoint();
		
		Ball activeBall = activeBalls.get(0);
		
		// check if any ball is in center point/area
		if(velocityCheck(activeBall, Constants.BALL_IDLE, false) && accelerationCheck(activeBall, Constants.KICKOFF_LOW_ACCELERATION, false)) { // 1.
			
			// check if ball is in center area
			boolean ret = PointMethods.pointInsideCirle(centerPoint, Constants.RADIUS_TOLERANCE_KICKOFF, activeBall.getBall().getCoordinates());
		
			if(ret) { // 2.

				if(teamsOnDifferentSides(environment.getPlayfield().getCenterPoint(), 
											environment.getTeamA(), environment.getTeamB())) { // 3.
					
					// add game started kick off
					if(fullEventList == null) {
						fullEventList = new ArrayList<Event>();			
					} 
					
					addEvent(Event.POSSIBLE_KICK_OFF, environment.getTimestamp(), null);
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
		
		Ball activeBall = activeBalls.get(0);
					
		boolean ret = PointMethods.pointInsideCirle(centerPoint, Constants.RADIUS_TOLERANCE_KICKOFF, activeBall.getBall().getCoordinates());
			
		if(ret) { // 1.
				
			if(accelerationCheck(activeBall, Constants.KICKOFF_BALL_ACCELERATION, true)) { // 2
				
				
				if(teamsOnDifferentSides(environment.getPlayfield().getCenterPoint(), 
											environment.getTeamA(), environment.getTeamB())) {
					
					// add game started kick off
					if(gameStarted == false  && checkLastEvent(Event.POSSIBLE_KICK_OFF, 1)) {
						
						startTimestamp = environment.getTimestamp();
													
						addEvent(Event.GAME_START, environment.getTimestamp(), null);
						
						gameStarted = true;
						


						
					} else if (gameStarted) { 
						
						if (!secondHalfStarted  && checkLastEvent(Event.SIDES_CHANGED, 1) && checkLastEvent(Event.POSSIBLE_KICK_OFF, 2) || 
								!secondHalfStarted && checkLastEvent(Event.SIDES_CHANGED, 2) && checkLastEvent(Event.POSSIBLE_KICK_OFF, 1)) {
							
							addEvent(Event.SECOND_HALF_START, environment.getTimestamp(), null);
	
							startTimestamp = environment.getTimestamp();
							
							secondHalfStarted = true;

							// reset values
							goal_line = "";

							goal_line_Timestamp = 0;
							

							
						} // add goal 
						else if (checkLastEvent(Event.POSSIBLE_KICK_OFF, 1)) {
							
							if(goal_line.equals(Event.BALL_EXIT_TOP_GOAL)) {
								
								addEvent(bottomHalf.getName() + " " + Event.GOAL , goal_line_Timestamp, null);
								
							} else if(goal_line.equals(Event.BALL_EXIT_BOTTOM_GOAL)) {
								
								addEvent(topHalf.getName() + " " + Event.GOAL , goal_line_Timestamp, null);
		
							}
						
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
	private void checkPossibleCornerKick(Point3D corner1, Point3D corner2, Team defending, Team attacking, String event) {
		
		Ball activeBall = activeBalls.get(0);
		
		if(!corner_line.equals("")){
			
			// check corners
			if(checkCorner(corner1, activeBall) || checkCorner(corner2, activeBall) ) {
				
				boolean noDefendingPlayerNear = true;
				
				boolean noAttackingPlayerNear = true;
				
				noDefendingPlayerNear = isPlayerFromTeamNearPoint(defending, activeBall.getBall().getCoordinates(), Constants.RADIUS_CORNER);
				
				noAttackingPlayerNear = isPlayerFromTeamNearPoint(attacking, activeBall.getBall().getCoordinates(), Constants.RADIUS_CORNER);
	
				// no player of defending team near and at least one attacking player in range
				if(noDefendingPlayerNear && !noAttackingPlayerNear) { // 2.
				
					if(velocityCheck(activeBall, Constants.BALL_IDLE, false) && 
							accelerationCheck(activeBall, Constants.CORNER_LOW_ACCELERATION, false)) { 
						
						addEvent(event, environment.getTimestamp(), null);
						
					}	
				}
			}
		}
	}

	
	
	
	
	/**
	 * Check if a possible corner was executed thus was a corner.<br>
	 * 1. Ball is still in corner. <br>
	 * 2. No defending player is near <br>
	 * 3. Ball gains acceleration. <br>
	 * @param corner1	Corner to test
	 * @param corner2	Corner to test
	 * @param defending		Defending team
	 * @param previousEvent
	 * @param event
	 */
	private void checkExecutedCornerKick(Point3D corner1, Point3D corner2, Team defending, Team attacking, String previousEvent, String event) {
		
		Ball activeBall = activeBalls.get(0);
		
		// event possible corner in previous
		if(checkLastEvent(previousEvent, 1)) {
			
			if(checkCorner(corner1, activeBall) || checkCorner(corner2, activeBall) ) { // 1.
				
				boolean noDefendingPlayerNear = true;
				
				boolean noAttackingPlayerNear = true;
				
				noDefendingPlayerNear = isPlayerFromTeamNearPoint(defending, activeBall.getBall().getCoordinates(), Constants.RADIUS_CORNER);
				
				noAttackingPlayerNear = isPlayerFromTeamNearPoint(attacking, activeBall.getBall().getCoordinates(), Constants.RADIUS_CORNER);

				// no player of defending team near and at least one attacking player in range
				if(noDefendingPlayerNear && !noAttackingPlayerNear) { // 2.
					
					if(accelerationCheck(activeBall, Constants.CORNER_BALL_ACCELERATION, true)) {

						addEvent(attacking.getName() + " " + event, environment.getTimestamp(), null);
						
						corner_line = "";
						
					}
				}
			}	
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * Test if ball has left over one of the goal lines.
	 * Add event if detected
	 */
	private void checkBallGoalLineExit() {

		Ball activeBall = activeBalls.get(0);
		
		if(activeBall != null) {
			
			Sensor ball = activeBall.getBall();
			
			double ret;
			
			//test top side
			SoccerField field = environment.getPlayfield();
			
			ret = PointMethods.pointSideOfLine(ball.getCoordinates(), field.getRightTop(), field.getLeftTop());

			if(ret > 0) {
				
				goal_line = corner_line = Event.BALL_EXIT_TOP_GOAL;
				
				goal_line_Timestamp = environment.getTimestamp();
				
				addEvent(Event.BALL_EXIT_TOP_GOAL, environment.getTimestamp(), null);
				
			}
			
			//test bottom side
			ret = PointMethods.pointSideOfLine(ball.getCoordinates(), field.getRightBottom(), field.getLeftBottom());

			if(ret < 0) {
				
				goal_line = corner_line = Event.BALL_EXIT_BOTTOM_GOAL;
				
				goal_line_Timestamp = environment.getTimestamp();
				
				addEvent(Event.BALL_EXIT_BOTTOM_GOAL, environment.getTimestamp(), null);

			}
		}
	}
	
	
	
	/**
	 * Look in stored environments if there is a ball near goal line.
	 * @return
	 */
	private long lookupBallExit() {
	
		long timestamp = 0;
		
		int position = PufferHolder.getHoldPufferSize() - 1;
		
		SoccerEnvironment currEnvironment;
		
		while(timestamp == 0 && position >= 0) {
			
			currEnvironment = (SoccerEnvironment) PufferHolder.getElementHoldPuffer(position);
			
			timestamp = possibleBallGoalLineExit(Constants.TOLERANCE_BALL_EXIT, currEnvironment);
			
			position--;
			
		}

		return timestamp;
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
		
		// check if a player from team 'team' is outside the game field
		for(Participant p: team.getPlayers()) {
					
			if(side.equals(Event.BALL_EXIT_LEFT_SIDE)) { 
				
				Point3D leftLeg = new Point3D(p.getLeftLeg().getCoordinates().getX() + Constants.TOLERANCE_Y_THROWIN, 
						p.getLeftLeg().getCoordinates().getY(), p.getLeftLeg().getCoordinates().getZ());
				
				Point3D rightLeg = new Point3D(p.getRightLeg().getCoordinates().getX() + Constants.TOLERANCE_Y_THROWIN, 
						p.getRightLeg().getCoordinates().getY(), p.getRightLeg().getCoordinates().getZ());
				
				
				Point3D leftBot = environment.getPlayfield().getLeftBottom();
				Point3D leftTop = environment.getPlayfield().getLeftTop();
				
				leg1 = PointMethods.pointSideOfLine(leftLeg, leftBot, leftTop);
				leg2 = PointMethods.pointSideOfLine(rightLeg, leftBot, leftTop);
				
				// player behind left line
				if(leg1 <= 0 && leg2 <= 0) { 
					
					for(Ball b: environment.getBalls()) {
						
						Point3D ball = b.getBall().getCoordinates();
						
						// is any ball near player and is in the air at least Constants.THROW_IN_MINIMUM_HEIGHT
						if(PointMethods.pointInsideCirle(leftLeg, Constants.RADIUS_THROW_IN, ball) &&
									ball.getZ() > leftLeg.getZ() + Constants.THROW_IN_MINIMUM_HEIGHT) {
							
//							if(velocityCheck(b, Constants.BALL_IDLE, false))	{
//									&& accelerationCheck(b, Constants.THROWIN_LOW_ACCELERATION, false)) {
								
								addEvent(Event.POSSIBLE_THROW_IN_LEFT, environment.getTimestamp(), p.getLeftLeg());
								
								markedThrowInPlayer = p;	
								
								markedThrowInBall = b;
								
								side_line = Event.BALL_EXIT_LEFT_SIDE;
								
//							}
						}
					}	
				}
			} else if (side.equals(Event.BALL_EXIT_RIGHT_SIDE)) { 
				
				Point3D leftLeg = new Point3D(p.getLeftLeg().getCoordinates().getX() - Constants.TOLERANCE_Y_THROWIN, 
						p.getLeftLeg().getCoordinates().getY(), p.getLeftLeg().getCoordinates().getZ());
				
				Point3D rightLeg = new Point3D(p.getRightLeg().getCoordinates().getX() - Constants.TOLERANCE_Y_THROWIN, 
						p.getRightLeg().getCoordinates().getY(), p.getRightLeg().getCoordinates().getZ());
				
				
				Point3D rightBot = environment.getPlayfield().getRightBottom();
				Point3D rightTop = environment.getPlayfield().getRightTop();
				
				leg1 = PointMethods.pointSideOfLine(leftLeg, rightBot, rightTop);
				leg2 = PointMethods.pointSideOfLine(rightLeg, rightBot, rightTop);

				// player behind left line
				if(leg1 >= 0 && leg2 >= 0) {
				
					// is ball behind and near player 
					for(Ball b: environment.getBalls()) {
						
						Point3D ball = b.getBall().getCoordinates();
						
						// is any ball near player and is in the air at least Constants.THROW_IN_MINIMUM_HEIGHT
						if(PointMethods.pointInsideCirle(leftLeg, Constants.RADIUS_THROW_IN, ball) &&
									ball.getZ() > leftLeg.getZ() + Constants.THROW_IN_MINIMUM_HEIGHT) {
							
//							if(velocityCheck(b, Constants.BALL_IDLE, false)) { 
//									&& accelerationCheck(b, Constants.THROWIN_LOW_ACCELERATION, false)) {
								
								
								addEvent(Event.POSSIBLE_THROW_IN_RIGHT, environment.getTimestamp(), p.getLeftLeg());
								
								markedThrowInPlayer = p;
								
								markedThrowInBall = b;
	
								side_line = Event.BALL_EXIT_RIGHT_SIDE;
								
//							}
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
		
		// is a player marked?
		if(markedThrowInPlayer != null) {
			
			double leg1, leg2;
			
			String teamName;
			
			if(isPlayerInTeam(topHalf, markedThrowInPlayer)) {
				
				teamName = topHalf.getName();
				
			} else {
				
				teamName = bottomHalf.getName();
				
			}

			
			if(checkLastEvent(Event.POSSIBLE_THROW_IN_LEFT, 1) || checkLastEvent(Event.POSSIBLE_THROW_IN_RIGHT, 1)) {
				
				if(side_line.equals(Event.BALL_EXIT_LEFT_SIDE)) { // test left side
					
					Point3D leftLeg = new Point3D(markedThrowInPlayer.getLeftLeg().getCoordinates().getX() + Constants.TOLERANCE_Y_THROWIN, 
							markedThrowInPlayer.getLeftLeg().getCoordinates().getY(), markedThrowInPlayer.getLeftLeg().getCoordinates().getZ());
					
					Point3D rightLeg = new Point3D(markedThrowInPlayer.getRightLeg().getCoordinates().getX() + Constants.TOLERANCE_Y_THROWIN, 
							markedThrowInPlayer.getRightLeg().getCoordinates().getY(), markedThrowInPlayer.getRightLeg().getCoordinates().getZ());			
					
					Point3D leftBot = environment.getPlayfield().getLeftBottom();
					Point3D leftTop = environment.getPlayfield().getLeftTop();
					
					leg1 = PointMethods.pointSideOfLine(leftLeg, leftBot, leftTop);
					leg2 = PointMethods.pointSideOfLine(rightLeg, leftBot, leftTop);
					
					if(leg1 <= 0 && leg2 <= 0) { // player behind left line
						
						double ball = PointMethods.pointSideOfLine(markedThrowInBall.getBall().getCoordinates(), leftBot, leftTop);
						
						if(ball > 0 && markedThrowInBall.getBall().getCoordinates().getZ() > leftLeg.getZ() + Constants.THROW_IN_MINIMUM_HEIGHT) {
							
							if(accelerationCheck(markedThrowInBall, Constants.THROWIN_BALL_ACCELERATION, true)) {
								
								addEvent(teamName + " " + Event.EXECUTE_THROW_IN_LEFT, environment.getTimestamp(), null);
							
								side_line = "";
							
								markedThrowInPlayer = null;
							
								markedThrowInBall = null;
								
							}
						}
					}
				} else if(side_line.equals(Event.BALL_EXIT_RIGHT_SIDE)) { // test right side
					
					Point3D leftLeg = new Point3D(markedThrowInPlayer.getLeftLeg().getCoordinates().getX() - Constants.TOLERANCE_Y_THROWIN, 
							markedThrowInPlayer.getLeftLeg().getCoordinates().getY(), markedThrowInPlayer.getLeftLeg().getCoordinates().getZ());
					
					Point3D rightLeg = new Point3D(markedThrowInPlayer.getRightLeg().getCoordinates().getX() - Constants.TOLERANCE_Y_THROWIN, 
							markedThrowInPlayer.getRightLeg().getCoordinates().getY(), markedThrowInPlayer.getRightLeg().getCoordinates().getZ());
					
					
					Point3D rightBot = environment.getPlayfield().getRightBottom();
					Point3D rightTop = environment.getPlayfield().getRightTop();
					
					leg1 = PointMethods.pointSideOfLine(leftLeg, rightBot, rightTop);
					leg2 = PointMethods.pointSideOfLine(rightLeg, rightBot, rightTop);
					
					if(leg1 >= 0 && leg2 >= 0) { // player behind left line
						
						double ball = PointMethods.pointSideOfLine(markedThrowInBall.getBall().getCoordinates(), rightBot, rightTop);
						
						// ball inside field
						if(ball < 0 && markedThrowInBall.getBall().getCoordinates().getZ() > leftLeg.getZ() + Constants.THROW_IN_MINIMUM_HEIGHT) {
					
							if(accelerationCheck(markedThrowInBall, Constants.THROWIN_BALL_ACCELERATION, true)) {
								
								addEvent(teamName + " " + Event.EXECUTE_THROW_IN_RIGHT, environment.getTimestamp(), null);
															
								side_line = "";
								
								markedThrowInPlayer = null;
								
								markedThrowInBall = null;
								
							}
						}
					}
				}								
			} 
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Check environment if ball came near one of the goal lines.
	 * @param tolerance
	 * @param environment
	 * @return 0 no match else timestamp; positive sign = top line and negative sign = bottom line
	 */
	private long possibleBallGoalLineExit(double tolerance, SoccerEnvironment environment) {
		
		long timestamp = 0;
				
		if(activeBalls.size() > 1) { // two or more balls active
			
			return timestamp;
			
		}
		
		if(activeBalls.size() > 0) {
			
			Ball activeBall = activeBalls.get(0);
			
			Sensor ball = activeBall.getBall();
			
			double ret;
			
			//test top side
			SoccerField field = environment.getPlayfield();
			
			// add tolerance to ball y position
			Point3D ball_tolerance = new Point3D(ball.getCoordinates().getX(), ball.getCoordinates().getY() + tolerance, ball.getCoordinates().getZ());
		
			ret = PointMethods.pointSideOfLine(ball_tolerance, field.getRightTop(), field.getLeftTop());
			
			// is ball within tolerance range
			if(ret > 0) {
				
				timestamp = environment.getTimestamp();
				
				return timestamp;

			}
			
			//test bottom side
			ball_tolerance = new Point3D(ball.getCoordinates().getX(), ball.getCoordinates().getY() - tolerance, ball.getCoordinates().getZ());
			
			// substract tolerance to ball y position
			ret = PointMethods.pointSideOfLine(ball_tolerance, field.getRightBottom(), field.getLeftBottom());
			
			// is ball within tolerance range
			if(ret < 0) {

				timestamp = environment.getTimestamp();
				
				return -timestamp;
				
			}
		}
		
		return timestamp;
		
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
	 * Check if a idle ball lies in given corner.
	 * @param corner
	 * @return
	 */
	private boolean checkCorner(Point3D corner, Ball b) {
	
		return PointMethods.pointInsideCirle(corner, Constants.CORNER_AREA, b.getBall().getCoordinates());
		
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
			
			if(entry.getKey() < lowestKey) {
				
				lowestKey = entry.getKey();
				
			}
		}
		return distancePlayer.get(lowestKey);
	}
	
	
	
	
	
	/** 
	 * Check if a given player is in a given team.
	 * @param team
	 * @param player
	 * @return <b>true</b> player is in team; <b>false</b> player is not in team
	 */
	private boolean isPlayerInTeam(Team team, Participant player) {
		
		Participant[] players = team.getPlayers();
		
		for(Participant p: players) {
			
			if(p.compare(player)) {
				
				return true;
			}
		}
		
		return false;
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
				
				System.out.println(event + " at " + (timestamp - startTimestamp));
				
			} //check is timeout has run out 
			else if (fullEventList.get(fullEventList.size() - 1).getTimestamp() - timestamp > sameMassageTimeout){
				
				fullEventList.add(new Event(event, timestamp, sensor));
				
				System.out.println(event + " at " + (timestamp - startTimestamp));
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
		
		ArrayList<String> ignoreList = new ArrayList<String>();
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
			
			writer.write("Corner idle(m/s)" + Constants.BALL_IDLE + "\n");
			writer.write("Corner low accel(m/s²)" + Constants.CORNER_LOW_ACCELERATION + "\n");
			writer.write("Corner execute accel(m/s²)" + Constants.CORNER_BALL_ACCELERATION + "\n");
			
			writer.write("Kickoff idle(m/s)" + Constants.BALL_IDLE + "\n");
			writer.write("Kickoff low accel(m/s²)" + Constants.KICKOFF_LOW_ACCELERATION + "\n");
			writer.write("Kickoff execute accel(m/s²)" + Constants.KICKOFF_BALL_ACCELERATION + "\n");
			
			writer.write("Throw-in idle(m/s)" + Constants.BALL_IDLE + "\n");
			writer.write("Throw-in low accel(m/s²)" + Constants.THROWIN_LOW_ACCELERATION + "\n");
			writer.write("Throw-in execute accel(m/s²)" + Constants.THROWIN_BALL_ACCELERATION + "\n");
			
			writer.write("Corner check radius(mm)" + Constants.CORNER_AREA + "\n");
			writer.write("Throw-in Y tolerance(mm)" + Constants.TOLERANCE_Y_THROWIN + "\n");
			
			
			writer.close();
			
		} catch (IOException e1) {
			System.err.println("Error while writing event list to file");
		} 
	}
	
}

