package de.landshut.haw.edu.util;

/**
 * Two dimensional cartesian point.
 */
public class Point2D {
	
	private double x;
	
	private double y;
  
	
	public Point2D(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	
	public synchronized double getX() {
		return x;
	}

	
	public synchronized void setX(double x) {
		this.x = x;
	}

	
	public synchronized double getY() {
		return y;
	}

	
	public synchronized void setY(double y) {
		this.y = y;
	}
}