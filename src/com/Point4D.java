package com;
/**
 * @author Piazza Francesco Giovanni ,Tecnes Milano http://www.tecnes.com
 *
 */

public class Point4D extends Point3D {

	
	String hexColor=null;
	int index=0;

	

	public Point4D(double x, double y, double z) {
		super(x, y, z);
	}
	
	public Point4D(double x, double y, double z,String hexColor) {
		super(x, y, z);
		this.hexColor=hexColor;
	}
	
	public Point4D(double x, double y, double z, String hexColor, int index) {
		super(x, y, z);
		this.hexColor = hexColor;
		this.index = index;
	}
	
	public Point4D() {
		super();
	}
	
	public Point4D clone(){
		
		Point4D p4=new Point4D(this.x,this.y,this.z,this.hexColor,this.index);
		return p4;
	}
	
	public String getHexColor() {
		return hexColor;
	}

	public void setHexColor(String hexColor) {
		this.hexColor = hexColor;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}


}
