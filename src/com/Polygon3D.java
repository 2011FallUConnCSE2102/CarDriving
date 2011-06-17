package com;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.Date;
import java.util.Vector;

/**
 * @author Piazza Francesco Giovanni ,Tecnes Milano http://www.tecnes.com
 *
 */

public class Polygon3D  extends Polygon{


	int[] 	zpoints=null;
	String hexColor="FFFFFF";
	int index=0;
	


	public Polygon3D(int npoints, int[] xpoints, int[] ypoints, int[] zpoints) {
		super(xpoints,ypoints,npoints);
		this.zpoints = zpoints;
	}

	public Polygon3D(int npoints, int[] xpoints, int[] ypoints) {
		super(xpoints,ypoints,npoints);

	}

	public Polygon3D(int npoints) {
		this.xpoints = new int[npoints];
		this.ypoints = new int[npoints];
		this.zpoints = new int[npoints];
		this.npoints=npoints;
	}


	public Polygon3D(Vector points) {

		this.npoints=points.size();
		this.xpoints = new int[this.npoints];
		this.ypoints = new int[this.npoints];
		this.zpoints = new int[this.npoints];
		


		for(int i=0;i<this.npoints;i++){

			Point3D p=(Point3D) points.elementAt(i);

			this.xpoints[i]=(int) p.x;
			this.ypoints[i]=(int) p.y;
			this.zpoints[i]=(int) p.z;

		}


	}


	public Polygon3D() {

	}
	
	public static Vector divideIntoTriangles(Polygon3D pol){
		
		Vector<Polygon3D> triangles=new Vector<Polygon3D>();
		
		if(pol.npoints==3){
			triangles.add(pol);
			return triangles;
			
		}
		
		for(int i=1;i<pol.npoints-1;i++){
			
			Polygon3D triangle=new Polygon3D(3);
			
			triangle.xpoints[0]=pol.xpoints[0];
			triangle.ypoints[0]=pol.ypoints[0];
			triangle.zpoints[0]=pol.zpoints[0];
			
			triangle.xpoints[1]=pol.xpoints[i];
			triangle.ypoints[1]=pol.ypoints[i];
			triangle.zpoints[1]=pol.zpoints[i];
			
			triangle.xpoints[2]=pol.xpoints[i+1];
			triangle.ypoints[2]=pol.ypoints[i+1];
			triangle.zpoints[2]=pol.zpoints[i+1];

			
			triangles.add(triangle);
			
		}
		
		return triangles;
		
	}


	public static Polygon3D extractSubPolygon3D(Polygon3D pol,int numAngles,int startAngle){



		int[] xpoints = new int[numAngles];
		int[] ypoints = new int[numAngles];
		int[] zpoints = new int[numAngles];

		int counter=0;

		for(int i=startAngle;i<numAngles+startAngle;i++){

			xpoints[counter]=pol.xpoints[i%pol.npoints];
			ypoints[counter]=pol.ypoints[i%pol.npoints];
			zpoints[counter]=pol.zpoints[i%pol.npoints];

			counter++;	
		}

		Polygon3D new_pol = new Polygon3D(numAngles,xpoints,ypoints,zpoints);
		new_pol.setHexColor(pol.getHexColor());
		return new_pol;
	}

	public static Polygon3D fromAreaToPolygon2D(Area area){

		Polygon3D pol=new Polygon3D();

		PathIterator pathIter = area.getPathIterator(null);
		//if(isDebug)System.out.println(p3d);


		while(!pathIter.isDone()){

			double[] coords = new double[6];

			int type=pathIter.currentSegment(coords);
			//System.out.println(type);		
			double px= coords[0];
			double py= coords[1];	


			if(type==PathIterator.SEG_MOVETO || type==PathIterator.SEG_LINETO)
			{		

				pol.addPoint((int)px,(int)py);
				//System.out.println(x+" "+y);
			}
			pathIter.next();
		}
		Polygon3D pol2=removeRedundant(pol);
		return pol2;
	}


	private static Polygon3D removeRedundant(Polygon3D pol) {

		boolean redundant=false;

		if(pol.xpoints[0]==pol.xpoints[pol.npoints-1]
		                               &&    pol.ypoints[0]==pol.ypoints[pol.npoints-1]                            
		)	
			redundant=true;

		if(!redundant)
			return pol;
		else{


			Polygon3D new_pol=new Polygon3D(pol.npoints-1);

			for(int i=0;i<pol.npoints-1;i++){

				new_pol.xpoints[i]=pol.xpoints[i];
				new_pol.ypoints[i]=pol.ypoints[i];

			}

			return new_pol;

		}

	}

	public String toString() {
		StringBuffer sb=new StringBuffer();

		if(zpoints!=null)

			for(int i=0;i<npoints;i++){
				sb.append(xpoints[i]+","+ypoints[i]+","+zpoints[i]+"_");

			}

		else 

			for(int i=0;i<npoints;i++){
				sb.append(xpoints[i]+","+ypoints[i]+"_");

			}	

		return sb.toString();
	}
	
	
	public Area clipPolygonToArea2D(Area area_out){


		Area area_in = new Area(this);

		Area new_area_out = (Area) area_out.clone();
		new_area_out.intersect(area_in);

		return new_area_out;

	}

	/**
	 * 
	 * USING SUTHERLAND-HODGMAN ALGORITHM FOR CLIPPING
	 * 
	 * @param p_in
	 * @param p_out
	 * @return
	 */
	public static Polygon3D clipPolygon3D(Polygon3D p_in,Polygon3D  p_out){



		//build all vertices adding border points


		for(int i=0;i<p_out.npoints;i++){

			Polygon3D p_new=new Polygon3D();

			int x1=p_out.xpoints[i];
			int y1=p_out.ypoints[i];
			int z1=p_out.zpoints[i];



			int x2=0;
			int y2=0;
			int z2=0;

			if(i==p_out.npoints-1) {

				x2=p_out.xpoints[0];
				y2=p_out.ypoints[0];
				z2=p_out.zpoints[0];

			}
			else{

				x2=p_out.xpoints[i+1];
				y2=p_out.ypoints[i+1];
				z2=p_out.zpoints[i+1];

			}
			System.out.println("clipping side : "+i);


			Point ps=new Point(p_in.xpoints[0],p_in.ypoints[0]);

			for(int j=0;j<p_in.npoints;j++){

				//System.out.println("clipping vertex:"+j);

				Point po=new Point(p_in.xpoints[j],p_in.ypoints[j]);

				if(isInsideClipPlane(po.x-x1,po.y-y1,x2-x1,y2-y1)){
					if(!isInsideClipPlane(ps.x-x1,ps.y-y1,x2-x1,y2-y1)){

						Point pm=insersect(ps,po,x2,x1,y2,y1);
						if(pm!=null) p_new.addPoint(pm.x,pm.y);

					}

					p_new.addPoint(po.x,po.y);
				}
				else if(isInsideClipPlane(ps.x-x1,ps.y-y1,x2-x1,y2-y1)){

					Point pm=insersect(po,ps,x2,x1,y2,y1);
					if(pm!=null) p_new.addPoint(pm.x,pm.y);
				}

				ps.x=po.x;
				ps.y=po.y;
			}

			p_in=new Polygon3D();
			for(int j=0;j<p_new.npoints;j++){
				p_in.addPoint(p_new.xpoints[j],p_new.ypoints[j]);
				//System.out.println(p_new.xpoints[j]+" "+p_new.ypoints[j]);
			}

		}	

		return p_in;

	}


	private static Point insersect(Point p1, Point p2, int x2, int x1, int y2, int y1) {

		Line2D.Double line1=new Line2D.Double(x2,y2,x1,y1);
		Line2D.Double line2=new Line2D.Double(p2.x,p2.y,p1.x,p1.y);

		//if(!line1.intersectsLine(line2))
		//	return null;

		Point insersection=new Point();

		if(x2!=x1 && p2.x!=p1.x){

			double a1=(y2-y1)/(x2-x1);
			double a2=(p2.y-p1.y)/(p2.x-p1.x);
			double b1=(-x1*y2+y1*x2)/(x2-x1);
			double b2=(-p2.y*p1.x+p1.y*p2.x)/(p2.x-p1.x);


			insersection.x=(int)((-b2+b1)/(a2-a1));
			insersection.y=(int)((a2*b1-b2*a1)/(a2-a1));

		}
		else if(x2==x1 && p2.x!=p1.x){

			double a2=(p2.y-p1.y)/(p2.x-p1.x);
			double b2=(-p2.y*p1.x+p1.y*p2.x)/(p2.x-p1.x);

			insersection.x=x2;
			insersection.y=(int) (a2*x2+b2);
		}
		else if(x2!=x1 && p2.x==p1.x){

			double a1=(y2-y1)/(x2-x1);
			double b1=(-x1*y2+y1*x2)/(x2-x1);

			insersection.x=p2.x;
			insersection.y=(int) (a1*p2.x+b1);
		}


		return insersection;
	}

	private static boolean isInsideClipPlane(int pox,int poy, int ax, int ay) {


		return (ax*poy-ay*pox)>=0;
	}


	public static boolean isFacing(Polygon3D pol,Point3D observer){

		Point3D p0=new Point3D(pol.xpoints[0],pol.ypoints[0],pol.zpoints[0]);

	
		Point3D vectorObs=observer.substract(p0);

		Point3D normal=findNormal(pol);

		double cosin=Point3D.calculateCosin(normal,vectorObs);

		return cosin>=0;
	}
	
	public static Point3D findNormal(Polygon3D pol){

		Point3D p0=new Point3D(pol.xpoints[0],pol.ypoints[0],pol.zpoints[0]);

		Point3D p1=new Point3D(pol.xpoints[1],pol.ypoints[1],pol.zpoints[1]);
		Point3D p2=new Point3D(pol.xpoints[2],pol.ypoints[2],pol.zpoints[2]);

		Point3D vector1=p1.substract(p0); 
		Point3D vector2=p2.substract(p0);

		

		Point3D normal=Point3D.calculateCrossProduct(vector1,vector2);

		

		return normal;
	}


	public boolean hasInsidePoint(double x,double y){

		for(int i=0;i<npoints;i++){

			AnalyticLine line=new AnalyticLine(xpoints[i],ypoints[i],xpoints[(i+1)%npoints],ypoints[(i+1)%npoints]);


			double valPoint=line.signum(x,y);

			//near the border the precise calcutation is very difficult
			if(Math.abs(valPoint)<0.01) valPoint=0;

			for(int j=2;j<npoints;j++){

				double valVertex = line.signum(xpoints[(j+i)%npoints],ypoints[(j+i)%npoints]);


				if(valVertex*valPoint<0)
					return false;
			}

		}

		return true;
	}


	


	public static class AnalyticLine{


		double a;
		double b;
		double c;

		public AnalyticLine(double a, double b, double c) {
			super();
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public AnalyticLine(double x1, double y1, double x0,double y0) {
			super();
			this.a = (y0-y1);
			this.b = -(x0-x1);
			this.c = (x0*y1-y0*x1);
		}

		public double signum(double x,double y){

			return a*x+b*y+c;

		}


	}

	public static void main(String[] args) {

		int[] cx=new int[4];
		int[] cy=new int[4];
		int[] cz=new int[4];


		cx[0]=10;
		cy[0]=10;
		cx[1]=0;
		cy[1]=50;
		cx[2]=50;
		cy[2]=60;
		cx[3]=50;
		cy[3]=-10;



		int[] cx1=new int[3];
		int[] cy1=new int[3];
		int[] cz1=new int[3];

		Polygon3D p1=new Polygon3D(4,cx,cy,cz);

		cx1[0]=10;
		cy1[0]=0;
		cx1[1]=50;
		cy1[1]=0;
		cx1[2]=30;
		cy1[2]=40;

		Polygon3D p2=new Polygon3D(3,cx1,cy1,cz1);

		//System.out.println(p2.hasInsidePoint(20,0));
		//System.out.println(p2.hasInsidePoint(30,40));
		//System.out.println(p2.hasInsidePoint(40,10));

		Area out=new Area(p1); 
		Area in=new Area(p2);


		Polygon3D p3=fromAreaToPolygon2D(out);

		System.out.println(p3);
		out.intersect(in);
		Polygon3D p_res=clipPolygon3D(p1,p2);
	}
	
	public Polygon3D buildUpperBase( double height){

		Polygon3D upperBase=new Polygon3D(this.npoints);

		for(int i=0;i<this.npoints;i++){

			upperBase.xpoints[i]=this.xpoints[i];
			upperBase.ypoints[i]=this.ypoints[i];
			upperBase.zpoints[i]=this.zpoints[i]+(int)height;

		}

		return upperBase;

	}
	
	public static Polygon3D buildPrismIFace(Polygon3D upperBase,Polygon3D lowerBase,int i){

		int n=upperBase.npoints;

		int[] cx=new int[4];
		int[] cy=new int[4];
		int[] cz=new int[4];
		
		
		cx[0]=upperBase.xpoints[i%n];
		cy[0]=upperBase.ypoints[i%n];
		cz[0]=upperBase.zpoints[i%n];

		cx[1]=upperBase.xpoints[(i+1)%n];
		cy[1]=upperBase.ypoints[(i+1)%n];
		cz[1]=upperBase.zpoints[(i+1)%n];

		cx[2]=lowerBase.xpoints[(i+1)%n];
		cy[2]=lowerBase.ypoints[(i+1)%n];
		cz[2]=lowerBase.zpoints[(i+1)%n];

		cx[3]=lowerBase.xpoints[i%n];
		cy[3]=lowerBase.ypoints[i%n];
		cz[3]=lowerBase.zpoints[i%n];
		

		
		

		Polygon3D base=new Polygon3D(4,cx,cy,cz);

		return base;

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
