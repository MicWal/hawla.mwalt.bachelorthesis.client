package de.landshut.haw.edu.control.datahandling;

import de.landshut.haw.edu.model.TransmissionObject;
import de.landshut.haw.edu.model.environment.Environment;

public interface  DataUpdaterInterface {

	/**
	 * Update all sensors with latest information in content.
	 * @param content
	 */
	void update(String[] content);
	
	
	void initEnvironment(String[] content);
	

	void setEnvironment(Environment environment);
	
	/**
	 * Select appropriate action depending on status of the object.
	 */
	void actionForObject(TransmissionObject transObj);
	
	
	/**
	 * Get next object from input puffer.
	 * @return
	 */
	TransmissionObject getNext();
	
	
	/**
	 * Create mapping of column names and their sequence number.<br>
	 * Extract line with mapping information from content. <br>
	 * See Constant.MAPPING_LINE.
	 * @param content 
	 */
	void createColumnMapping(String[] content);
	
	
	/**
	 * Get the order by which the data is ordered. <br>
	 * Extract line with order information from content. <br>
	 * See Constant.ORDER_LINE. 
	 * @param content
	 */
	void getSQLOrder(String[] content);
	
	
	
}
