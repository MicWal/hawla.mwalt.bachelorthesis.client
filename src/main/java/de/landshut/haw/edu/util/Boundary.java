package de.landshut.haw.edu.util;


public class Boundary {
   
	/**
     * Return true if the given point is contained inside the boundary.
     * See: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     * @param test The point to check
     * @return true if the point is inside the boundary, false otherwise
     * @author Copyright (c) 1970-2003, Wm. Randolph Franklin 
     */
    public static boolean contains(Point2D[] points, Point2D test) {
    	int i;
    	int j;
	    boolean result = false;
	    for (i = 0, j = points.length - 1; i < points.length; j = i++) {
	        if ((points[i].getY() > test.getY()) != (points[j].getY() > test.getY()) &&
	            (test.getX() < (points[j].getX() - points[i].getX()) * (test.getY() - points[i].getY()) / (points[j].getY()-points[i].getY()) + points[i].getX())) {
	          result = !result;
	        }
	    }
	    return result;
    }
}