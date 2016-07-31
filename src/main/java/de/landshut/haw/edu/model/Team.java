package de.landshut.haw.edu.model;

public class Team {
	
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
