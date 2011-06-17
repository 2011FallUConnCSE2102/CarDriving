package com;
import java.io.File;
import java.util.Vector;

/*
 * Created on 31/ott/09
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

public class CubicMesh extends PolygonMesh{

	int deltaX=0;
	int deltaY=0;  
	int deltaY2=0;
	int deltaX2=0;

	Point3D point000=null;
	Point3D point010=null;
	Point3D point001=null;
	Point3D point100=null;
	Point3D point011=null;
	Point3D point110=null;
	Point3D point101=null;
	Point3D point111=null;

	public CubicMesh(Point3D[] points, Vector<LineData> polygonData) {
		super(points,polygonData);
	}

	public CubicMesh() {
	
	}

	public static CubicMesh loadMeshFromFile(File file) {

		PolygonMesh pm= (PolygonMesh) PolygonMesh.loadMeshFromFile(file);

		CubicMesh cm=new CubicMesh(pm.points,pm.polygonData);

			
		double dx=0;
		double dy=0;
		double dz=0;
		
		for(int j=0;j<cm.points.length;j++){
			
			Point3D point= cm.points[j];
			dx=Math.max(point.x,dx);
			dy=Math.max(point.y,dy);
			dz=Math.max(point.z,dz);
		}
		
		cm.setDeltaX((int) dz);
		cm.setDeltaX2((int) (dx+dz));
		cm.setDeltaY((int) dz);
		cm.setDeltaY2((int) (dy+dz));
		
		cm.point000=new Point3D(0,0,0);
		cm.point100=new Point3D(dx,0,0);
		cm.point010=new Point3D(0,dy,0);
		cm.point001=new Point3D(0,0,dz);
		cm.point110=new Point3D(dx,dy,0);
		cm.point011=new Point3D(0,dy,dz);
		cm.point101=new Point3D(dx,0,dz);
		cm.point111=new Point3D(dx,dy,dz);

		return cm;

	}

	protected CubicMesh clone() {

		CubicMesh cm=new CubicMesh();
		cm.points=new Point3D[this.points.length];

		for(int i=0;i<this.points.length;i++){

			cm.points[i]=points[i].clone();

		}

		for(int i=0;i<this.polygonData.size();i++){

			cm.addPolygonData(polygonData.elementAt(i).clone());
		}
		for(int i=0;i<this.normals.size();i++){

			cm.normals.add(normals.elementAt(i).clone());
		}

	
	
		
		cm.setDeltaX(getDeltaX());
		cm.setDeltaX2(getDeltaX2());
		cm.setDeltaY(getDeltaY());
		
		cm.point000=point000.clone();
		cm.point100=point100.clone();
		cm.point010=point010.clone();
		cm.point001=point001.clone();
		cm.point110=point110.clone();
		cm.point011=point011.clone();
		cm.point101=point101.clone();
		cm.point111=point111.clone();
		
		return cm;
	}
	
	public void translate(double i, double j, double k) {
		
		
		super.translate(i,j,k);
		point000.translate(i,j,k);
		point100.translate(i,j,k);
		point010.translate(i,j,k);
		point001.translate(i,j,k);
		point110.translate(i,j,k);
		point011.translate(i,j,k);
		point101.translate(i,j,k);
		point111.translate(i,j,k);
	
	}
	
	public Point3D getXAxis(){
		
		return point100.substract(point000).calculateVersor();
		
	}
	
	public Point3D getYAxis(){
		
		return point010.substract(point000).calculateVersor();
		
	}
	
	public Point3D getZAxis(){
		
		return point001.substract(point000).calculateVersor();
		
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

	public int getDeltaY2() {
		return deltaY2;
	}

	public void setDeltaY2(int deltaY2) {
		this.deltaY2 = deltaY2;
	}

}
