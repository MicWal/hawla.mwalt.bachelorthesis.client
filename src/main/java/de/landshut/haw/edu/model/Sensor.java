package de.landshut.haw.edu.model;

import java.io.Serializable;

import de.landshut.haw.edu.util.Point3D;

public class Sensor implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1728469664516017900L;

	private int id;
	
	private Point3D coordinates;
	
	private double velocity;
	
	private Point3D velocityVector;
	
	private double acceleration;
	
	private Point3D accelerationVector;

	
	public Sensor(int id) {
		super();
		
		this.id = id;
		
		coordinates = new Point3D(0, 0, 0);
		
		velocity = 0;
		
		velocityVector = new Point3D(0, 0, 0);
		
		acceleration = 0;
		
		accelerationVector = new Point3D(0, 0, 0);
	}

	
	public Sensor(Sensor ball) {

		id = ball.getId();
		
		coordinates = new Point3D(ball.getCoordinates().getX(), ball.getCoordinates().getY(), ball.getCoordinates().getZ());
		
		velocity = ball.getVelocity();
		
		velocityVector = new Point3D(ball.getVelocityVector().getX(), ball.getVelocityVector().getY(), ball.getVelocityVector().getZ());
		
		acceleration = ball.getAcceleration();
		
		accelerationVector = new Point3D(ball.getAccelerationVector().getX(), ball.getAccelerationVector().getY(), ball.getAccelerationVector().getZ());
	}


	public Point3D getCoordinates() {
		return coordinates;
	}

	
	public void setCoordinates(Point3D coordinates) {
		this.coordinates = coordinates;
	}
	
	
	public void setCoordinates(double x, double y, double z) {
		coordinates.setX(x);
		coordinates.setY(y);
		coordinates.setZ(z);
	}

	
	public int getId() {
		return id;
	}

	
	public void setId(int id) {
		this.id = id;
	}


	public double getVelocity() {
		return velocity;
	}


	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}


	public Point3D getVelocityVector() {
		return velocityVector;
	}


	public void setVelocityVector(Point3D velocity_vector) {
		this.velocityVector = velocity_vector;
	}
	
	
	public void setVelocityVector(double x, double y, double z) {
		velocityVector.setX(x);
		velocityVector.setY(y);
		velocityVector.setZ(z);
	}


	public double getAcceleration() {
		return acceleration;
	}


	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}


	public Point3D getAccelerationVector() {
		return accelerationVector;
	}


	public void setAccelerationVector(Point3D acceleration_vector) {
		this.accelerationVector = acceleration_vector;
	}
	
	
	public void setAccelerationVector(double x, double y, double z) {
		accelerationVector.setX(x);
		accelerationVector.setY(y);
		accelerationVector.setZ(z);
	}
	
	
	public Point3D getAccelerationInMeterPerSecondsPow(){
		double x = acceleration * Math.pow(10, -10) * accelerationVector.getX();
		double y = acceleration * Math.pow(10, -10) * accelerationVector.getY();
		double z = acceleration * Math.pow(10, -10) * accelerationVector.getZ();
		
		return new Point3D(x, y, z);
	}
	
	
	public Point3D getVelocityInMeterPerSeconds(){
		double x = velocity * Math.pow(10, -10) * velocityVector.getX();
		double y = velocity * Math.pow(10, -10) * velocityVector.getY();
		double z = velocity * Math.pow(10, -10) * velocityVector.getZ();
		
		return new Point3D(x, y, z);
	}
	

}
