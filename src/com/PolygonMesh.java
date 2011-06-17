package com;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

public class PolygonMesh implements Cloneable{
	
	
	public Point3D[] points=null;
	public Vector <LineData>polygonData=null;
	public Vector <Point3D>normals=null;
	



	public PolygonMesh() {
		points= null;
		polygonData= new Vector <LineData>();
		normals= new Vector <Point3D>();
	}
	
	public PolygonMesh(Point3D[] points, Vector<LineData> polygonData) {
		this.points=points;
		this.polygonData=polygonData;
		calculateNormals();
	}
	
	public PolygonMesh(Vector <Point3D> points, Vector <LineData> polygonData) {
		
		if(points!=null){
			this.points=new Point3D[points.size()];
			for(int i=0;i<points.size();i++)
				this.points[i] = points.elementAt(i);
		}	
		else
			this.points= null;
		
		if(polygonData!=null)
			this.polygonData = polygonData;
		else
			this.polygonData= new Vector <LineData>();
		
		calculateNormals();
	}



	private void calculateNormals() {
		
		normals=new Vector<Point3D>();
		for(int l=0;l<polygonData.size();l++){

			LineData ld=(LineData) polygonData.elementAt(l);

			normals.add(getNormal(0,ld,points));
			

		}
		
	}
	
	private static Point3D getNormal(int position, LineData ld,
			Point3D[] points) {

		int n=ld.size();


		Point3D p0=points[ld.getIndex((n+position-1)%n)];
		Point3D p1=points[ld.getIndex(position)];
		Point3D p2=points[ld.getIndex((1+position)%n)];

		Point3D normal=Point3D.calculateCrossProduct(p1.substract(p0),p2.substract(p1));

		return normal.calculateVersor();
	}

	public void addPoints(Point3D[] addPoints){
		
		int newSize=points.length+addPoints.length;	
		Point3D[] newPoints=new Point3D[newSize];
		
		
		for(int i=0;i<points.length;i++)
			newPoints[i]=points[i];
		for(int i=0;i<addPoints.length;i++)
			newPoints[i+points.length]=addPoints[i];
		
	}
	
	public void addPolygonData(LineData polygon){
		polygonData.add(polygon);		
	}
	
	protected PolygonMesh clone() {
		
		PolygonMesh pm=new PolygonMesh();
		pm.points=new Point3D[this.points.length];
		
		for(int i=0;i<this.points.length;i++){
	
			pm.points[i]=points[i].clone();
			
		}
		
		for(int i=0;i<this.polygonData.size();i++){

			pm.addPolygonData(polygonData.elementAt(i).clone());
		}
		for(int i=0;i<this.normals.size();i++){

			pm.normals.add(normals.elementAt(i).clone());
		}
	
		return pm;
	}
	
	public static Polygon3D getBodyPolygon(Point3D[] points,LineData ld) {
		 
		Polygon3D pol=new Polygon3D(ld.size());
		
		for(int i=0;i<ld.size();i++){
			int index=ld.getIndex(i);
			
			pol.xpoints[i]=(int) points[index].x;
			pol.ypoints[i]=(int) points[index].y;
			pol.zpoints[i]=(int) points[index].z;
		} 
		
		return pol;
		
	}
	
	public static Vector getBodyPolygons(PolygonMesh pm) {

		Vector pols=new Vector();


		for(int j=0;j<pm.polygonData.size();j++){

			LineData ld=pm.polygonData.elementAt(j);

			Polygon3D pol=new Polygon3D(ld.size());

			for(int i=0;i<ld.size();i++){
				int index=ld.getIndex(i);

				pol.xpoints[i]=(int) pm.points[index].x;
				pol.ypoints[i]=(int) pm.points[index].y;
				pol.zpoints[i]=(int) pm.points[index].z;
			} 
			pols.add(pol);
		}
		return pols;

	}
	
	public static void buildPoints(Vector points, String str) {

		StringTokenizer sttoken=new StringTokenizer(str,"_");

		while(sttoken.hasMoreElements()){

			String[] vals = sttoken.nextToken().split(",");

			Point3D p=new Point3D();

			p.x=Double.parseDouble(vals[0]);
			p.y=Double.parseDouble(vals[1]);
			p.z=Double.parseDouble(vals[2]);

			points.add(p);
		}




	}

	public static void buildLines(Vector lines, String str) {

		StringTokenizer sttoken=new StringTokenizer(str,"_");

		while(sttoken.hasMoreElements()){

			String[] vals = sttoken.nextToken().split(",");

			LineData ld=new LineData();

			for(int i=0;i<vals.length;i++)
				ld.addIndex(Integer.parseInt(vals[i]));


			lines.add(ld);
		}
	}

	public void translate(double i, double j, double k) {
		
		for(int p=0;p<points.length;p++){
			points[p].translate(i,j,k);
	
		}
	
	}

	public static PolygonMesh loadMeshFromFile(File file) {
		Vector points=new Vector();
		Vector lines=new Vector();
		
		PolygonMesh pm=null;

		try {
			BufferedReader br=new BufferedReader(new FileReader(file));


			String str=null;
			int rows=0;
			while((str=br.readLine())!=null){
				if(str.indexOf("#")>=0 || str.length()==0)
					continue;

				if(str.startsWith("P="))
					buildPoints(points,str.substring(2));
				else if(str.startsWith("L="))
					buildLines(lines,str.substring(2));


			}

			br.close();
			//checkNormals();
		} catch (Exception e) {

			e.printStackTrace();
		}
		pm=new PolygonMesh(points,lines);
		
	
		return pm;
	}
	




}
