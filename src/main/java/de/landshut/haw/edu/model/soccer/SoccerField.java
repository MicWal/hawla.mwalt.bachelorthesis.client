package de.landshut.haw.edu.model.soccer;

import de.landshut.haw.edu.model.Field;
import de.landshut.haw.edu.util.Point3D;

public class SoccerField extends Field {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2165079877804110148L;

	private final Point3D centerPoint;
	
	private final GoalGate goalTop;
	
	private final GoalGate goalBottom;
	
	
	public SoccerField(Point3D leftBottom, Point3D leftTop, Point3D rightBottom, Point3D rightTop, double gateLength, double gateWidth) {
		super(leftBottom, leftTop, rightBottom, rightTop);
		
		// calculate center point
		double x1 = (leftBottom.getX() + leftTop.getX()) / 2;
		double x2 = (rightBottom.getX() + rightTop.getX()) / 2;
		
		double y1 = (leftBottom.getY() + rightBottom.getY()) / 2;
		double y2 = (leftTop.getY() + rightTop.getY()) / 2;
		
		double x = (x1 + x2) / 2;
		double y = (y1 + y2) / 2;
		
		centerPoint = new Point3D(x, y, 0);

		goalTop = new GoalGate(gateLength, gateWidth);
			
		goalBottom = new GoalGate(gateLength, gateWidth);
	}

	
	public Point3D getCenterPoint() {
		return centerPoint;
	}


	public GoalGate getGoalTop() {
		return goalTop;
	}


	public GoalGate getGoalBottom() {
		return goalBottom;
	}

}
