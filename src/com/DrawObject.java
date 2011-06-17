package com;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Vector;


public class DrawObject {
	
	public static boolean IS_3D=false;
	
	double x;
	double y;
	double z;
	
	double dx;
	double dy;
	double dz;
	
	int index=0;
	String hexColor=null;
	
	Polygon3D base=null;
	boolean selected=false;
	
	PolygonMesh mesh=new PolygonMesh();
	
	int deltaX=0;
	int deltaY=0;  
	int deltaX2=0;




	protected Object clone()  {
		DrawObject dro=new DrawObject();

		dro.setX(getX());
		dro.setY(getY());
		dro.setZ(getZ());
		dro.setDx(getDx());
		dro.setDy(getDy());
		dro.setDz(getDz());
		dro.setIndex(getIndex());
		dro.setHexColor(getHexColor());

		if(getBase()!=null){

			dro.base=new Polygon3D(getBase().npoints);


			for(int i=0;i<getBase().npoints;i++){

				dro.base.xpoints[i]=getBase().xpoints[i];
				dro.base.ypoints[i]=getBase().ypoints[i];
				
				if(getBase().zpoints!=null)
				  dro.base.zpoints[i]=getBase().zpoints[i];

			}
		}
		return dro;
	}
	
    public static BufferedImage fromImageToBufferedImage(Image image,Color color){
	
    	image=Transparency.makeColorTransparent(image,color);
    	
    	int width=image.getWidth(null);
    	int height=image.getHeight(null);
    	
		BufferedImage bufImage=
			new BufferedImage(width,height,
					BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2 = (Graphics2D) bufImage.getGraphics();
	
		g2.setColor(new Color(255,255,255,0));
		g2.fillRect(0,0,width,height);
	
		g2.drawImage(image,0,0,
				width,height,null);
		
		
		return bufImage;

    }
    
    public static Texture fromImageToTexture(Image image,Color color){
    	
    	BufferedImage bi=fromImageToBufferedImage(image, color);
    	return new Texture(bi);
    }
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * @return the dx
	 */
	public double getDx() {
		return dx;
	}
	/**
	 * @param dx the dx to set
	 */
	public void setDx(double dx) {
		this.dx = dx;
	}
	/**
	 * @return the dy
	 */
	public double getDy() {
		return dy;
	}
	/**
	 * @param dy the dy to set
	 */
	public void setDy(double dy) {
		this.dy = dy;
	}
	/**
	 * @return the dz
	 */
	public double getDz() {
		return dz;
	}
	/**
	 * @param dz the dz to set
	 */
	public void setDz(double dz) {
		this.dz = dz;
	}
	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}
	/**
	 * @return the z
	 */
	public double getZ() {
		return z;
	}
	/**
	 * @param z the z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}



	/*public static Vector cloneObjectsVector(Vector drawObjects) {
		Vector newDrawObjects=new Vector();
		
		for(int i=0;i<drawObjects.size();i++){
			
			DrawObject dro=(DrawObject) drawObjects.elementAt(i);
			newDrawObjects.add(dro.clone());
			
		}
		
		return newDrawObjects;
	}*/

	public static DrawObject[] cloneObjectsArray(DrawObject[] drawObjects) {
		DrawObject[] newDrawObjects=new DrawObject[drawObjects.length];
		
		for(int i=0;i<drawObjects.length;i++){
			
			newDrawObjects[i] =(DrawObject) drawObjects[i].clone();
			
			
		}
		
		return newDrawObjects;
	}

	public Polygon3D getBase() {
		return base;
	}



	public void setBase(Polygon3D base) {
		this.base = base;
	}

	public String getHexColor() {
		return hexColor;
	}

	public void setHexColor(String hexColor) {
		this.hexColor = hexColor;
	}
	
	public PolygonMesh getMesh() {
		return mesh;
	}

	public void setMesh(PolygonMesh mesh) {
		this.mesh = mesh;
	}

	public int getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(int deltaX) {
		this.deltaX = deltaX;
	}

	public int getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(int deltaY) {
		this.deltaY = deltaY;
	}

	public int getDeltaX2() {
		return deltaX2;
	}

	public void setDeltaX2(int deltaX2) {
		this.deltaX2 = deltaX2;
	}

}
