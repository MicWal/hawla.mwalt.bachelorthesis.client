package de.landshut.haw.edu.util;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConvertUtil {
	
	
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
			System.out.println("Content in START TRANSMISSON object im empty");
			return null;
		}	
	}
	
	
	
	
	
	public static JSONObject stringToJSONObject(String data) {
		
		if(data != null) {
		
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
