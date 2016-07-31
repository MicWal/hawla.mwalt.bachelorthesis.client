package de.landshut.haw.edu.model;

public class Participant{

	private String name;

	private boolean isActive;
	
	private Sensor leftLeg;
	
	private Sensor rightLeg;

	
	public Participant(String name, int idLeftLeg, int idRightLeg) {
		super();
		
		this.name = name;
		this.isActive = false;
		this.leftLeg = new Sensor(idLeftLeg);
		this.rightLeg = new Sensor(idRightLeg);
	}


	public Sensor getLeftLeg() {
		return leftLeg;
	}

	
	public void setLeftLeg(Sensor leftLeg) {
		this.leftLeg = leftLeg;
	}

	
	public Sensor getRightLeg() {
		return rightLeg;
	}

	
	public void setRightLeg(Sensor rightLeg) {
		this.rightLeg = rightLeg;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public boolean isActive() {
		return isActive;
	}


	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

}
