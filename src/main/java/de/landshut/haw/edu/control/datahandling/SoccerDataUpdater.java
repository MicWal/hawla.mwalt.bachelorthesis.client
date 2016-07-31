package de.landshut.haw.edu.control.datahandling;

import java.util.ArrayList;
import java.util.HashMap;

import de.landshut.haw.edu.model.Sensor;
import de.landshut.haw.edu.model.TransmissionObject;
import de.landshut.haw.edu.model.environment.Environment;
import de.landshut.haw.edu.model.environment.SoccerEnvironment;
import de.landshut.haw.edu.util.Constants;
import de.landshut.haw.edu.util.ConvertUtil;

public class SoccerDataUpdater extends DataUpdater{
	
	/**
	 * Reference to environment to be able to update data.
	 */
	private SoccerEnvironment environment;
	
	/**
	 * Contains the id and the related sensor object.
	 */
	private HashMap<Integer, Sensor> idSensorMap;
	
	/**
	 * Contains names of the column and their column number in the data result.
	 */
	private HashMap<String, Integer> dataMap;
	
	/**
	 * Signal if transmission has ended.
	 */
	private boolean endTransmission;
	
	/**
	 * True => data comes in descending order
	 */
	private boolean sql_order_desc;
	
	/**
	 * Count of how much sensors environment has.
	 */
	private int countSensors;
	
	/**
	 * List of sensor id which are already updated in current update.
	 */
	private ArrayList<Integer> markedIds;
	
	
	public SoccerDataUpdater() {
		super();
	}

	

	@Override
	public void run() {
		
		endTransmission = false;
		
		while(!endTransmission) {
			current = getNext();
						
			actionForObject(current);
			
			current = null;
		}
	}

	
	@Override
	public void actionForObject(TransmissionObject transObj) {
		
		switch (transObj.getTransmission_status()) {
		
		    case Constants.METADATA_TRANSMISION:  
		    	System.out.println(Constants.METADATA_TRANSMISION);
		    	
		    	initEnvironment(transObj.getContent());
		        break;
		        
		    case Constants.START_TRANSMISSION:  
		    	System.out.println(Constants.START_TRANSMISSION);
		    	
		    	createColumnMapping(transObj.getContent());
		    	
		    	getSQLOrder(transObj.getContent());
		    	
		        break;
		        
		    case Constants.ACTIVE_TRANSMISSION: 
		    	
//		    	update(transObj.getContent());
		    	
		        break;
		        
		    case Constants.END_TRANSMISSION:  
		    	
		    	System.out.println(Constants.END_TRANSMISSION);
		    	
		    	endTransmission = true;
		    	
		    	algorithm.endTransmission();
		    	
		    	environment.isUpdated();
		        break;
  
		    default: 
		    	break;
		}
	}	
	
	
	
	@Override
	public void update(String[] data) {
				
		markedIds = new ArrayList<Integer>();
			
		environment.update();	
		
		// read data 
		if(sql_order_desc) { // is sorted descending
			
			for(int i = 0; i < data.length; i++){
				
				if(i == 0) {
					updateTimestamp(data[i].split(Constants.DELIMETER));
				}
				
				updateSingleSensor(data[i].split(Constants.DELIMETER));
				
				//are all sensor marked end loop
				if(markedIds.size() == countSensors){
					break;
				}
			}
		} else { // is sorted ascending
			
			for(int i = data.length-1; i >= 0; i--) {
				
				if(i == data.length-1) {
					updateTimestamp(data[i].split(Constants.DELIMETER));
				}
				
				updateSingleSensor(data[i].split(Constants.DELIMETER));
				
				// are all sensor marked end loop
				if(markedIds.size() == countSensors){
					break;
				}
			}
		}
//		System.out.println("updated");
		environment.isUpdated();	
	}


	private void updateTimestamp(String[] values) {
		long timestamp = Long.parseLong(values[dataMap.get("timestamp")]);
		
		environment.setTimestamp(timestamp);
	}



	/**
	 * If sensor id is not already marked then update sensor data
	 * @param values
	 */
	private void updateSingleSensor(String[] values) {
		
		int id = Integer.parseInt(values[dataMap.get("id")]);

		// update sensor only if not in marked list
		if(!markedIds.contains(id)) {
			
			Sensor referenceObject = idSensorMap.get(id);
			
			// update position 
			double x = Double.parseDouble(values[dataMap.get("x")]);
			
			double y = Double.parseDouble(values[dataMap.get("y")]);
			
			double z = Double.parseDouble(values[dataMap.get("z")]);
			
			referenceObject.setCoordinates(x, y, z);
			
			// update velocity + vector
			double velocity = Double.parseDouble(values[dataMap.get("velocity")]);
			
			double v_x = Double.parseDouble(values[dataMap.get("velocity_x")]);
			
			double v_y = Double.parseDouble(values[dataMap.get("velocity_y")]);
			
			double v_z = Double.parseDouble(values[dataMap.get("velocity_z")]);
			
			referenceObject.setVelocity(velocity);
			
			referenceObject.setVelocityVector(v_x, v_y, v_z);
			
			// update acceleration + vector
			double acceleration = Double.parseDouble(values[dataMap.get("acceleration")]);
			
			double a_x = Double.parseDouble(values[dataMap.get("acceleration_x")]);
			
			double a_y = Double.parseDouble(values[dataMap.get("acceleration_y")]);
			
			double a_z = Double.parseDouble(values[dataMap.get("acceleration_z")]);
			
			referenceObject.setAcceleration(acceleration);
			
			referenceObject.setAccelerationVector(a_x, a_y, a_z);
			
			// add id to marked list
			markedIds.add(id);
		}
	}
	
	
	@Override
	public void createColumnMapping(String[] content) {
		dataMap = new HashMap<String, Integer>();

		dataMap = ConvertUtil.stringArrayToHashMap(	content[Constants.MAPPING_LINE].split(Constants.DELIMETER));
		
		
	}


	@Override
	public void getSQLOrder(String[] content) {
		
		if(content[Constants.ORDER_LINE] == "DESC") {
			sql_order_desc = true;
		} else {
			sql_order_desc = false;
		}
	}

	
	@Override
	public void initEnvironment(String[] metaData) {
		environment.init(metaData);
		
		idSensorMap = environment.getIdSensorMap();
		
		algorithm.setIdSensorMap(idSensorMap);
		
		countSensors = idSensorMap.size();
		
	}


	@Override
	public void setEnvironment(Environment environment) {
		this.environment = (SoccerEnvironment) environment;
		
		start();
	}


}
