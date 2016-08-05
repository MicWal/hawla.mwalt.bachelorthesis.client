package de.landshut.haw.edu.util;


public class PointMethods {
   
	private PointMethods() {}
	
	
	/**
     * Return true if the given point is contained inside the boundary.
     * See: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     * @param test The point to check
     * @param points Points that mark the boundaries
     * @return true if the point is inside the boundary, false otherwise
     * @author Copyright (c) 1970-2003, Wm. Randolph Franklin 
     */
    public static boolean contains(Point3D[] points, Point3D test) {
    	int i;
    	
    	int j;
    	
	    boolean result = false;
	    
	    for (i = 0, j = points.length - 1; i < points.length; j = i++) {
	        
	    	if ((points[i].getY() > test.getY()) != (points[j].getY() > test.getY()) &&
	            (test.getX() < (points[j].getX() - points[i].getX()) * (test.getY() - points[i].getY()) / 
	            			( points[j].getY()-points[i].getY() ) + points[i].getX())) {
	          
	    		result = !result;
	        }
	    }
	    
	    return result;
    }
    
    
    /**
     * Calculate on which side of a line a given point is. <br>
     * Line is represented through 2 points <b>lineP1</b> and <b>lineP2</b>.
     * @param p3
     * @param lineP2 
     * @param lineP1 
     * @return
     */
    public static double pointSideOfLine(Point3D p3, Point3D lineP2, Point3D lineP1) {
		
		// (Bx - Ax) * (Cy - Ay) - (By - Ay) * (Cx - Ax)
		// http://www.gamedev.net/topic/542870-determine-which-side-of-a-line-a-point-is/
		return (lineP2.getX() - lineP1.getX()) * (p3.getY() - lineP1.getY()) - (lineP2.getY() - lineP1.getY()) * (p3.getX() - lineP1.getX());	
	}
	
    
    /**
     * Calculate if a point is in a circle. <br>
     * Mathematics calculation (d = (pX - cX)^2 + (pY -cY)^2 ): http://math.stackexchange.com/questions/198764/how-to-know-if-a-point-is-inside-a-circle
     * @param center 
     * @param radius
     * @param p
     * @return <b>true</b> point is in circle, <b>false</b> point is not in circle
     */
    public static boolean pointInsideCirle(Point3D center, double radius, Point3D p) {
		
		double dPow = Math.pow(p.getX() - center.getX(), 2) + Math.pow(p.getY() - center.getY(), 2);
		
		double rPow = Math.pow(radius, 2);
		
		if(dPow <= rPow) {
			return true;
			
		} else {
			return false;
		}
	}
    
    /**
     * Calculate distance between two points in 2d space.
     * @param p1
     * @param p2
     * @return distance or -1 if points are null
     */
    public static double distanceBetweenTwoPoint(Point3D p1, Point3D p2) {
    	
    	if(p1 != null && p2 != null) {
    		
        	double value = Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2);
        	
    		return Math.sqrt(value);
    	}
    	return -1;
    }
    
    
    public static double getAbsoluteValueofPoint3D(Point3D vector) {
    	
    	double x = Math.pow(vector.getX(), 2);
    	
    	double y = Math.pow(vector.getY(), 2);
    	
    	double z = Math.pow(vector.getZ(), 2);
    	
    	return Math.sqrt(x + y + z);
    }
    
    
}