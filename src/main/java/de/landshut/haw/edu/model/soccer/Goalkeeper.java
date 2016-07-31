package de.landshut.haw.edu.model.soccer;

import de.landshut.haw.edu.model.Participant;
import de.landshut.haw.edu.model.Sensor;

public class Goalkeeper extends Participant{
	

	private Sensor idLeftArm;
	
	private Sensor idRightArm;

	
	public Goalkeeper(String name, int idLeftLeg, int idRightLeg, int idLeftArm, int idRightArm) {
		super(name, idLeftLeg, idRightLeg);

		this.idLeftArm = new Sensor(idLeftArm);
		this.idRightArm = new Sensor(idRightArm);
	}

	
	public Sensor getLeftArm() {
		return idLeftArm;
	}

	
	public void setLeftArm(Sensor leftArm) {
		this.idLeftArm = leftArm;
	}

	
	public Sensor getRightArm() {
		return idRightArm;
	}

	
	public void setRightArm(Sensor rightArm) {
		this.idRightArm = rightArm;
	}
	
}
