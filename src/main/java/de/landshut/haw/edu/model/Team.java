package de.landshut.haw.edu.model;

import java.io.Serializable;

public class Team implements Serializable {
	

	private static final long serialVersionUID = 8456226472583640770L;

	private final String name;
	
	private Participant[] players;
	
	private int playerNextFree;
	
	
	public Team(String name, Participant[] team) {
		super();
		
		this.name = name;
		
		players = team;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public Participant[] getPlayers() {
		return players;
	}
	
	
	public void addPlayers(String name, int idLeftLeg, int idRightLeg) {
		players[playerNextFree] = new Participant(name, idLeftLeg, idRightLeg);
	}
	
	
}
