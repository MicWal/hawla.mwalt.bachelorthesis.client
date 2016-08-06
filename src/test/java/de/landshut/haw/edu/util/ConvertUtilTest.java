package de.landshut.haw.edu.util;

import static org.junit.Assert.*;


import org.json.simple.parser.ParseException;
import org.junit.Test;

public class ConvertUtilTest {

	
	
	@Test
	public void testDeepCopy() {
		
		TransmissionObject obj2;
		
		TransmissionObject obj1 = new TransmissionObject("String1", new String[] {"this","is","one"});
		
		obj2 = (TransmissionObject) ConvertUtil.deepCopy(obj1);
		
		obj1 = new TransmissionObject("String2", new String[] {"this","is","two"});
		
		assertEquals("obj1 and obj2 state should be different", obj2.getTransmission_status(), obj1.getTransmission_status());
		
		assertEquals("obj1 and obj2 last entrie in content should be different", obj2.getContent()[2], obj1.getContent()[2]);
	}
	

	@Test
    public void testStringArrayToString() {
		String[] test1 = {"hallo","ich","lese"};
		String[] test2 = {"hallo"};
		
        // assert statements
		assertEquals("null should return null", null, ConvertUtil.stringArrayToString(null));
		assertEquals("combination of string", "hallo \nich \nlese \n", ConvertUtil.stringArrayToString(test1));
		assertEquals("combination of string", "hallo \n", ConvertUtil.stringArrayToString(test2));
    }

	
	@Test
	public void testStringToJSONObject() {
				
		assertEquals("null should return null", null, ConvertUtil.stringToJSONObject(null));

	}
	
	@Test(expected=ParseException.class)
	public void expectParseException() {
		String test2 = "{\"test\"= 2}";
		
		ConvertUtil.stringToJSONObject(test2);
	}
	

}
