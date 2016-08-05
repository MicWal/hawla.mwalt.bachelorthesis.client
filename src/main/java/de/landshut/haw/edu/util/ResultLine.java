package de.landshut.haw.edu.util;

import java.io.Serializable;

public class ResultLine implements Serializable {
	
	private static final long serialVersionUID = Constants.RESULTLINE_VERSION_UID;
	
	private int id;
	
	private long timestamp;
	
	private double x;
	
	private double y;
	
	private double z;
	
	private double velocity;
	
	private double velocityX;
	
	private double velocityY;
	
	private double velocityZ;
	
	private double acceleration;
	
	private double accelerationX;
	
	private double accelerationY;
	
	private double accelerationZ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public double getVelocityX() {
		return velocityX;
	}

	public void setVelocityX(double velocityX) {
		this.velocityX = velocityX;
	}

	public double getVelocityY() {
		return velocityY;
	}

	public void setVelocityY(double velocityY) {
		this.velocityY = velocityY;
	}

	public double getVelocityZ() {
		return velocityZ;
	}

	public void setVelocityZ(double velocityZ) {
		this.velocityZ = velocityZ;
	}

	public double getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}

	public double getAccelerationX() {
		return accelerationX;
	}

	public void setAccelerationX(double accelerationX) {
		this.accelerationX = accelerationX;
	}

	public double getAccelerationY() {
		return accelerationY;
	}

	public void setAccelerationY(double accelerationY) {
		this.accelerationY = accelerationY;
	}

	public double getAccelerationZ() {
		return accelerationZ;
	}

	public void setAccelerationZ(double accelerationZ) {
		this.accelerationZ = accelerationZ;
	}
	
	
}
