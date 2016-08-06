package de.landshut.haw.edu.util;

/**
 * Contains all constants.
 * @author Michael
 *
 */
public class Constants {
	
	private Constants()  {}
		
	/*
	 * 				Main area
	 */
	
	public final static int REQUIRED_ARGUMENTS = 2;
	
	public final static String ONLY_NUMBER_REGEX = "\\d*";
	
	/*
	 * 				Control area
	 */
	
	public final static String START_TRANSMISSION = "START_TRANSMISSION";
	
	public final static String ACTIVE_TRANSMISSION = "ACTIVE_TRANSMISSION";
	
	public final static String END_TRANSMISSION = "END_TRANSMISSION";
	
	public final static String METADATA_TRANSMISION = "METADATA_TRANSMISION";
	
	/* 
	 * 				Algorithm area 
	 */
	
	public final static int MAX_NUMBER_ALGORITHM = 1;
	
	public final static int MIN_NUMBER_ALGORITHM = 1;
	
	
	/*
	 * 				JSON keywords
	 */
	
	public final static String KEYWORD_BALLS = "balls";
	
	public final static String KEYWORD_TEAMS = "teams";
	
	public final static String KEYWORD_REFEREES = "referees";
	
	public final static String KEYWORD_FIELD = "field";
	
	public final static String KEYWORD_BALL_ID = "id";
	
	public final static String KEYWORD_NAME = "name";
	
	public final static String KEYWORD_TEAMNAME = "teamname";
	
	public final static String KEYWORD_MEMBERS = "members";
	
	public final static String KEYWORD_LEFT_LEG = "left leg";
	
	public final static String KEYWORD_RIGHT_LEG = "right leg";
	
	public final static String KEYWORD_LEFT_ARM = "left arm";
	
	public final static String KEYWORD_RIGHT_ARM = "right arm";
	
	public final static String KEYWORD_POSITION = "position";
	
	public final static String KEYWORD_COORDINATES = "coordinates";
	
	public final static String KEYWORD_X = "x";
	
	public final static String KEYWORD_Y = "y";
	
	public final static String KEYWORD_Z = "z";
	
	public final static String KEYWORD_BOTTOM_RIGHT = "right bottom";
	
	public final static String KEYWORD_BOTTOM_LEFT = "left bottom";
	
	public final static String KEYWORD_TOP_RIGHT = "right top";
	
	public final static String KEYWORD_TOP_LEFT = "left top";
	
	public final static int KEYWORD_TEAMS_AMOUNT = 2;
	
	public final static int KEYWORD_TEAM_A = 0;
	
	public final static int KEYWORD_TEAM_B = 1;
	
	public static final int GOALKEEPER_ELEMENTS = 5;
	
	public static final int PLAYER_ELEMENTS = 3;
	
	/* 
	 * 				Model area
	 */
	
	public final static double GATE_DEFAULT_LENGTH = 732; 	// cm
	
	public final static double GATE_DEFAULT_WIDTH = 244;	// cm
	
	public final static int MAX_NUMBER_MODEL = 1;
	
	public final static int MIN_NUMBER_MODEL = 1;
	
	public final static int SOCCER_ID = 1;
	
	/*
	 * 				Puffer area
	 */
	
	public final static int PUFFER_SIZE_IN_= 1000;
	
	public final static int PUFFER_SIZE_OUT = 1000;

	/*
	 *				Transmisison object ID 
	 */
	
	public static final String DELIMETER = " ";

	public static final int MAPPING_LINE = 0;
	
	public static final int ORDER_LINE = 1;

	/*
	 * 				Algorithm area
	 */	
	
	public static final int TOLERANCE_PLAYER = 2;
	
	public static final double THROW_IN_MINIMUM_HEIGHT = 1000; // in millimeter

	public static final double RADIUS_TOLERANCE = 500; // in millimeter
	
	public static final double RADIUS_TOLERANCE_KICKOFF = 2500;
	
	public static final double RADIUS_TOLERANCE_BALL_MOVED = 500; // in millimeter
	
	public static final double RADIUS_CORNER = 2500; // in millimeter

	public static final double RADIUS_THROW_IN = 500; // in millimeter
	
	public static final double RADIUS_GOALKICK = 3000; // in millimeter
	
	public static final double TOLERANCE_X_THROWIN = 100; // in mm 
	
	public static final double TOLERANCE_BALL_EXIT = 100; // in mm
	
	public static final double GOAL_AREA = 14000; // in millimeter
	
	public static final double CORNER_AREA = 1200; // in millimeter 

	public static final double BALL_IDLE = 1; // in m/s 
	
	public static final double BALL_SLOW = 3; // in m/s 
	
	public static final double BALL_THROW_IDLE = 2; // in m/s 
	
	public static final double CORNER_BALL_ACCELERATION = 35.0; // in m/s²
	
	public static final double CORNER_LOW_ACCELERATION = 10.0; // in m/s²
	
	public static final double KICKOFF_BALL_ACCELERATION = 40.0; // in m/s²
	
	public static final double KICKOFF_LOW_ACCELERATION = 5.0; // in m/s²
	
	public static final double THROWIN_LOW_ACCELERATION = 10.0; // in m/s²
	
	public static final double THROWIN_BALL_ACCELERATION = 20; // in m/s²
	
	public static final String FIELD_EXIT_LEFT = "LEFT";
	
	public static final String FIELD_EXIT_RIGHT = "RIGHT";
	
	public static final String FIELD_EXIT_TOP = "TOP";
	
	public static final String FIELD_EXIT_BOTTOM = "BOTTOM";

	public static final int MAX_PUFFER = 50000;
	
	public static final int REMOVE_PUFFER = 5000;

	public static final double KICK_ACCELERATION = 0;










}
