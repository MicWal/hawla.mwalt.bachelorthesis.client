package de.landshut.haw.edu.model;

import java.io.Serializable;

import de.landshut.haw.edu.util.Point3D;

public class Field implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7223165006018470106L;

	protected final Point3D leftBottom;
	
	protected final Point3D leftTop;
	
	protected final Point3D rightBottom;
	
	protected final Point3D rightTop;

	
	public Field(Point3D leftBottom, Point3D leftTop, Point3D rightBottom, Point3D rightTop) {
		super();
		
		this.leftBottom = leftBottom;
		this.leftTop = leftTop;
		this.rightBottom = rightBottom;
		this.rightTop = rightTop;
	}

	
	public Point3D getLeftBottom() {
		return leftBottom;
	}

	
	public Point3D getLeftTop() {
		return leftTop;
	}
	

	public Point3D getRightBottom() {
		return rightBottom;
	}

	
	public Point3D getRightTop() {
		return rightTop;
	}
	
}
