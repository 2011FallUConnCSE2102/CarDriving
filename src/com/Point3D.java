package com;





/**
 * @author Piazza Francesco Giovanni ,Tecnes Milano http://www.tecnes.com
 *
 */

public class Point3D{


		double x;
		double y;
		double z;
		
		double p_x;
		double p_y;
		double p_z;
		
		boolean isSelected=false;

		public boolean isSelected() {
			return isSelected;
		}


		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}


		public Point3D(double x, double y, double z) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public Point3D(double x, double y, double z, double p_x, double p_y,
				double p_z) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
			this.p_x = p_x;
			this.p_y = p_y;
			this.p_z = p_z;
		}
		
		
		public Point3D() {
			// TODO Auto-generated constructor stub
		}

		protected Point3D clone()  {

			Point3D p=new Point3D(this.x,this.y,this.z,this.p_x,this.p_y,this.p_z);
			return p;

		}

		public static double calculateCosin(Point3D a, Point3D b) {

			double prod=(calculateSquareNorm(b.substract(a))-calculateSquareNorm(a)-calculateSquareNorm(b))
			/(2*calculateNorm(a)*calculateNorm(b));

			return prod;
		}

		public static double calculateDotProduct(Point3D a,
				Point3D b) {

			return a.x*b.x+a.y*b.y+a.z*b.z;
		}

		private static double calculateNorm(Point3D a) {

			return Math.sqrt(calculateDotProduct(a,a));
		}

		private static double calculateSquareNorm(Point3D a) {

			return calculateDotProduct(a,a);
		}
		
		public static double distance(Point3D a,Point3D b){
			
			
			return calculateNorm(a.substract(b));
		}
		
		
	   public static double distance(double x1,double y1,double z1,double x2,double y2,double z2){
			
			
		   return distance(new Point3D (x1,y1,z1),new Point3D (x2,y2,z2));
		}


	   public static Point3D calculateOrthogonal(Point3D a){
		   Point3D orth=new Point3D(-a.y,a.x,0);
		   
		   return orth;
	   }
	   
	   public Point3D calculateVersor(){
		   
		   double norm=calculateNorm(this);
		   if(norm==0)
			   return new Point3D(0,0,0);
		   double i_norm=1.0/norm;
		   Point3D versor=new Point3D(this.x*i_norm,this.y*i_norm,this.z*i_norm);
		   
		   return versor;
	   }
	   
		public static Point3D calculateCrossProduct(Point3D a,
				Point3D b) {

			double x=a.y*b.z-b.y*a.z;
			double y=b.x*a.z-a.x*b.z;
			double z=a.x*b.y-b.x*a.y;

			Point3D pRes=new Point3D(x,y,z);

			return pRes;
		}

		public Point3D substract(Point3D p0) {

			Point3D pRes=new Point3D(this.x-p0.x,this.y-p0.y,this.z-p0.z);

			return pRes;
		}
		
		public static double foundXIntersection(Point3D p1, Point3D p2,
				double y) {

			if(p2.y-p1.y<1 && p2.y-p1.y>-1)
				return p1.x;

			return p1.x+((p2.x-p1.x)*(y-p1.y))/(p2.y-p1.y);
		

		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public double getZ() {
			return z;
		}

		public void setZ(double z) {
			this.z = z;
		}
		
		public void translate(double dx, double dy, double dz) {
			
			setX(this.getX()+dx);
	    	setY(this.getY()+dy);
	    	setZ(this.getZ()+dz);
			
		}

	}
