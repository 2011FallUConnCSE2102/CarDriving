package com;
/*
 * Created on 02/ago/08
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

public class Plain {
	
	double a=0;
	double b=0;
	double c=0;
	double d=0;
	
	
	public double calculateZ(double x,double y){
		
		if(c==0)return 0;
		
		return (-d-a*x-b*y)/c;
	}
	
	
	public static Plain calculatePlain(int[] x,int[] y,int[] z){
		
		double det1=((y[1]-y[0])*(z[2]-z[0]))-((y[2]-y[0])*(z[1]-z[0]));
		double det2=((x[1]-x[0])*(z[2]-z[0]))-((x[2]-x[0])*(z[1]-z[0]));
		double det3=((x[1]-x[0])*(y[2]-y[0]))-((x[2]-x[0])*(y[1]-y[0]));		
		
		double a=det1;
		double b=-det2;
		double c=det3;
		double d=-x[0]*a-y[0]*b-z[0]*c;
		
		Plain plain=new Plain(a,b,c,d);
		
		return plain;
	}
	
	public static Plain calculatePlain(Polygon3D pol){
		
		return calculatePlain(pol.xpoints,pol.ypoints,pol.zpoints);
	}


	public Plain(double a, double b, double c, double d) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	


}
