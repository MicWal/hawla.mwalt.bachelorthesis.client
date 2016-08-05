package de.landshut.haw.edu.util;

import static org.junit.Assert.*;


import org.json.simple.parser.ParseException;
import org.junit.Test;

public class ConvertUtilTest {

	
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

		//assertEquals("combination of string", hm, ConvertUtil.stringToJSONObject(test));
	}
	
	@Test(expected=ParseException.class)
	public void expectParseException() {
		String test2 = "{\"test\"= 2}";
		
		ConvertUtil.stringToJSONObject(test2);
	}
	

}
