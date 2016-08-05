package de.landshut.haw.edu.model.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.landshut.haw.edu.model.Ball;
import de.landshut.haw.edu.model.Participant;
import de.landshut.haw.edu.model.Sensor;
import de.landshut.haw.edu.model.Team;
import de.landshut.haw.edu.model.soccer.Goalkeeper;
import de.landshut.haw.edu.model.soccer.SoccerField;
import de.landshut.haw.edu.util.Constants;
import de.landshut.haw.edu.util.Point3D;
import de.landshut.haw.edu.util.ConvertUtil;

public class SoccerEnvironment extends Environment {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9139548785914406273L;

	private SoccerField playfield;
	
	private ArrayList<Ball> balls;
	
	private Team teamA;
	
	private Team teamB;
	
	private ArrayList<Participant> referees;

	private HashMap<Integer, Sensor> idSensorMap;
	
	private long timestamp;
	
	
	public SoccerEnvironment() {
		super();
		
		idSensorMap = new HashMap<Integer, Sensor>();
		
		balls = new ArrayList<Ball>();
		
		referees = new ArrayList<Participant>();
	}

	
	public void init(String[] data) {
		
		String converted = ConvertUtil.stringArrayToString(data);
		
		JSONObject jsonObj = ConvertUtil.stringToJSONObject(converted);
		
		initBalls((JSONArray) jsonObj.get(Constants.KEYWORD_BALLS));
		
		initTeams((JSONArray) jsonObj.get(Constants.KEYWORD_TEAMS));
		
		initReferees((JSONArray) jsonObj.get(Constants.KEYWORD_REFEREES));
		
		initField((JSONObject) jsonObj.get(Constants.KEYWORD_FIELD));
	}

	
	private void initBalls(JSONArray balls) {
		
		// should never be null because server test meta data against schema
		if(balls != null) {
			
			@SuppressWarnings("rawtypes")
			Iterator i = balls.iterator();

			// take each value from the json array separately
			while (i.hasNext()) {
				
				JSONObject innerObj = (JSONObject) i.next();
				
				long value = (long) (innerObj.get(Constants.KEYWORD_BALL_ID));
				
				Ball b = new Ball((int) value);

				this.balls.add(b);
				
				idSensorMap.put((int) value, b.getBall()); //add ball to map (id->sensor)
			}
		} else {
			System.out.println("No balls in meta file found.");
		}   
	}
	
	
	private void initTeams(JSONArray teams) {
	
		// should never be null because server test meta data against schema
		if(teams != null) {
			if(teams.size() == Constants.KEYWORD_TEAMS_AMOUNT) {
				
				teamA = initTeam((JSONObject)teams.get(Constants.KEYWORD_TEAM_A));
					
				teamB =	initTeam((JSONObject)teams.get(Constants.KEYWORD_TEAM_B));	
				
			} else {
				System.out.println("Insufficent teams in meta file.");
			}
		} else {
			System.out.println("No teams in meta file found.");
		}
		
	}


	private Team initTeam(JSONObject team) {
		
		ArrayList<Participant> memberList = new ArrayList<Participant>();
		
		String name = (String) (team.get(Constants.KEYWORD_TEAMNAME));
		
		JSONArray members = (JSONArray) (team.get(Constants.KEYWORD_MEMBERS));
		
		@SuppressWarnings("rawtypes")
		Iterator i = members.iterator();

		// take each value from the json array separately
		while (i.hasNext()) {
			
			JSONObject innerObj = (JSONObject) i.next();
			if(innerObj.size() == Constants.GOALKEEPER_ELEMENTS) {
				
				Goalkeeper p = new Goalkeeper( (String) innerObj.get(Constants.KEYWORD_NAME), 
								(int) (long) innerObj.get(Constants.KEYWORD_LEFT_LEG), 
									(int) (long) innerObj.get(Constants.KEYWORD_RIGHT_LEG),
										(int) (long) innerObj.get(Constants.KEYWORD_LEFT_ARM), 
											(int) (long) innerObj.get(Constants.KEYWORD_RIGHT_ARM));
				memberList.add(p);
				
				//add goalkeeper sensors with id to map
				idSensorMap.put((int) (long) innerObj.get(Constants.KEYWORD_LEFT_LEG), p.getLeftLeg());
				
				idSensorMap.put((int) (long) innerObj.get(Constants.KEYWORD_RIGHT_LEG), p.getRightLeg());
				
				idSensorMap.put((int) (long) innerObj.get(Constants.KEYWORD_LEFT_ARM), p.getLeftArm());
				
				idSensorMap.put((int) (long) innerObj.get(Constants.KEYWORD_RIGHT_ARM), p.getRightArm());
				
			} else if (innerObj.size() == Constants.PLAYER_ELEMENTS) {
				
				Participant p = new Participant( (String) innerObj.get(Constants.KEYWORD_NAME), 
									(int) (long) innerObj.get(Constants.KEYWORD_LEFT_LEG), 
										(int) (long) innerObj.get(Constants.KEYWORD_RIGHT_LEG));
				memberList.add(p);
				
				//add player sensors with id to map
				idSensorMap.put((int) (long) innerObj.get(Constants.KEYWORD_LEFT_LEG), p.getLeftLeg());
				
				idSensorMap.put((int) (long) innerObj.get(Constants.KEYWORD_RIGHT_LEG), p.getRightLeg());
			}
			
		}
		
		Participant[] teamArr = new Participant[memberList.size()];
		
		teamArr = memberList.toArray(teamArr);

		return new Team(name, teamArr);
	}
	
	
	private void initReferees(JSONArray referees) {
	 	
		// should never be null because server test meta data against schema
		if(referees != null) {
			
			@SuppressWarnings("rawtypes")
			Iterator i = referees.iterator();

			// take each value from the json array separately
			while (i.hasNext()) {
				
				JSONObject innerObj = (JSONObject) i.next();
				
				Participant p = new Participant( (String) innerObj.get(Constants.KEYWORD_NAME), 
								(int) (long) innerObj.get(Constants.KEYWORD_LEFT_LEG), 
									(int) (long) innerObj.get(Constants.KEYWORD_RIGHT_LEG));
				
				this.referees.add(p);
				
				//add player sensors with id to map
				idSensorMap.put((int) (long) innerObj.get(Constants.KEYWORD_LEFT_LEG), p.getLeftLeg());
				
				idSensorMap.put((int) (long) innerObj.get(Constants.KEYWORD_RIGHT_LEG), p.getRightLeg());
			}
		} else {
			System.out.println("No referee in meta file found.");
		}
	}
	
	
	private void initField(JSONObject field) {
		
		// should never be null because server test meta data against schema
		if(field != null) {

			JSONObject bottomLeft = (JSONObject) (field.get(Constants.KEYWORD_BOTTOM_LEFT));
			
			JSONObject bottomRight = (JSONObject) (field.get(Constants.KEYWORD_BOTTOM_RIGHT));
			
			JSONObject topLeft = (JSONObject) (field.get(Constants.KEYWORD_TOP_LEFT));
			
			JSONObject topRight = (JSONObject) (field.get(Constants.KEYWORD_TOP_RIGHT));

			playfield = new SoccerField(getCoordinates(bottomLeft), getCoordinates(topLeft), 
											getCoordinates(bottomRight), getCoordinates(topRight), 
												0, 0);
		} else {
			System.out.println("No field in meta file found.");
		}
		
		
	}


	private Point3D getCoordinates(JSONObject obj) {
		
		if(obj != null){
			double x = (double) (long) obj.get(Constants.KEYWORD_X);
			
			double y = (double) (long) obj.get(Constants.KEYWORD_Y);
			
			double z = (double) (long) obj.get(Constants.KEYWORD_Z);
			
			return new Point3D(x, y, z);
			
		} else {
			System.out.println("Coordinates are empty.");
			
			return null;
		}
	}

	
	public ArrayList<Ball> getBalls() {
		return balls;
	}
	
	
	public Team getTeamA() {
		return teamA;
	}


	public Team getTeamB() {
		return teamB;
	}
	
	
	public ArrayList<Participant> getReferees() {
		return referees;
	}


	@Override
	public SoccerField getPlayfield() {
		return playfield;
	}


	public HashMap<Integer, Sensor> getIdSensorMap() {
		return idSensorMap;
	}


	public synchronized long getTimestamp() {
		return timestamp;
	}


	public synchronized void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
