package de.landshut.haw.edu.util;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class ConvertUtil {
	
	private ConvertUtil() {}
	
	/**
	 * Serialize and deserialize an object to get a deep copy. <br>
	 *  
	 * @param originalObj
	 * @return
	 */
	public static Object deepCopy(Object originalObj) {
		
	      ObjectOutputStream oos = null;
	      ObjectInputStream ois = null;
	      
	      try {
	         ByteArrayOutputStream bos = new ByteArrayOutputStream();
	         
	         oos = new ObjectOutputStream(bos);
	         
	         // serialize and pass the object
	         oos.writeObject(originalObj);
	         oos.flush();
	         
	         ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
	         
	         ois = new ObjectInputStream(bin);
	         
	         // return the new object
	         return ois.readObject(); 
	         
	      } catch(Exception e) {
	         System.err.println("Exception in cloning object.");
	         e.printStackTrace();
	         
	      } finally {
	    	  try {
	        	 oos.close();
	        	 ois.close();
	        	 
	    	  } catch (IOException e) {
	    		  System.err.println("Exception closing cloning object stream.");
				e.printStackTrace();
	    	  }
	      }
	      
		return null;
	}
	   
	
	/**
	 * Creates a string from a string array each element is separated by a new line command.
	 * @param data
	 * @return null is array dosen't exists, else content of arrays as string
	 */
	public static String stringArrayToString(String[] data){
		
		if(data != null) {
			
			StringBuilder builder = new StringBuilder();
			
			for(int i = 0; i < data.length; i++) {
				builder.append(data[i] + " \n");
			}
			return builder.toString();
			
		} else {
			return null;
		}
	}
	
	
	
	public static HashMap<String, Integer> stringArrayToHashMap(String[] data) {
		
		if(data != null) {
			
			HashMap<String, Integer> builder = new HashMap<String, Integer>();
			
			for(int i=0; i < data.length; i++) {
				builder.put(data[i], i);
			}
			
			return builder;
		}
		else {
			return null;
		}	
	}
	
	
	
	
	/**
	 * Converts a string into a JSONObject.
	 * @param data
	 * @throws ParseException string has no valid JSON syntax.
	 * @return null is string is empty/points to null, else returns JSONOBject
	 */
	public static JSONObject stringToJSONObject(String data) {
		
		if(data != null && !data.equals("")) {
		
			JSONParser parser = new JSONParser();
			
			JSONObject jsonObj;
			
			try {
				jsonObj = (JSONObject) parser.parse(data);
				
				return jsonObj;
				
			} catch (ParseException e) {
				System.err.println("Parse error while parsing string to JSON object");
				System.exit(ErrorCodes.PARSE_ERR);
			}
		} 
		return null;
	}
}
