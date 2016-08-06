package de.landshut.haw.edu.model;

import java.io.Serializable;

public class Participant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4388208287017575965L;

	private String name;
	
	private Sensor leftLeg;
	
	private Sensor rightLeg;

	
	public Participant(String name, int idLeftLeg, int idRightLeg) {
		super();
		
		this.name = name;
		
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

	
	public boolean compare(Participant p) {
	
		if(name.equals(p.getName())) {
			
			if(this.leftLeg.getId() == p.getLeftLeg().getId()) {
				
				if(this.rightLeg.getId() ==  p.getRightLeg().getId()) {
					
					return true;
				}
			}
		}
		return false;
	}

}
