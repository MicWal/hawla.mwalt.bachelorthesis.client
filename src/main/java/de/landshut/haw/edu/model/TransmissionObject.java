package de.landshut.haw.edu.model;

import java.io.Serializable;

import de.landshut.haw.edu.util.Constants;

public class TransmissionObject implements Serializable {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	private String transmission_status;

	private String[] content;
	
	
	public TransmissionObject(String transmission_status, String[] content) {
		
		this.transmission_status = transmission_status;
		
		this.content = content;
	}

	
	public TransmissionObject(TransmissionObject transObj) {
		transmission_status = transObj.getTransmission_status();
		content = transObj.getContent();
	}


	public String getTransmission_status() {
		return transmission_status;
	}
	

	public String[] getContent() {
		return content;
	}
}
