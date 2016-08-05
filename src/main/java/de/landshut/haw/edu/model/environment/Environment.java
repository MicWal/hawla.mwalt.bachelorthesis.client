package de.landshut.haw.edu.model.environment;

import java.io.Serializable;
import java.util.ArrayList;

import de.landshut.haw.edu.model.Ball;
import de.landshut.haw.edu.model.Field;
import de.landshut.haw.edu.model.Participant;
import de.landshut.haw.edu.model.Team;

public abstract class Environment implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3994597867183033723L;
	
	/**
	 * isUpdated represent state of environment.<br>
	 * <b>true</b> - environment is up to date.<br>
	 * <b>false</b> - environment is analyzed. 
	 */
	private boolean isUpdated;
	
	
	public Environment() {
		super();

		isUpdated = false;
	}

	public abstract void init(String[] metaData);
	
	
	public abstract ArrayList<Ball> getBalls();
	
	
	public abstract Team getTeamA();


	public abstract Team getTeamB();
	
	
	public abstract ArrayList<Participant> getReferees();
	
	
	public abstract Field getPlayfield();	

	
	/**
	 * Wait until environment is marked updated.
	 */
	public synchronized void analyze() {
        if (!isUpdated) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
	
	/**
	 * Mark environment analyzed and wake up waiting (update) thread. 
	 */
	public synchronized void isAnalyzed() {
		isUpdated = false;
	    notify();
	}
	
	
	/**
	 * Wait until environment is marked analyzed.
	 */
    public synchronized void update() {
        if (isUpdated) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    /**
	 * Mark environment updated and wake up waiting (analyze) thread. 
	 */
    public synchronized void isUpdated() {
    	 isUpdated = true;
         notify();
    }
	
}
