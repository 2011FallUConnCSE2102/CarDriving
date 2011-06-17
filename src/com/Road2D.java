package com;
/**
 * @author Piazza Francesco Giovanni ,Tecnes Milano http://www.tecnes.com
 *
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFileChooser;


/*
 * Created on 12/apr/08
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

public class Road2D {
	
	CarFrame2D carFrame2D=null;

	int NX=2;
	int NY=80;

	int NXVISIBLE=NX;
	int NYVISIBLE=20;//20 orig

	int dx=0;
	int dy=600/NYVISIBLE;//600 orig
	
	int deltax=2;
	int deltay=2;
	
	int START_X=-260;
	int POSX=START_X;
	int POSY=0;

	int MOVZ=0;
	int SPACE_SCALE_FACTOR=62;
	
	int ROAD_LENGHT=600;


	int XFOCUS=0;
	int YFOCUS=0;
	int SCREEN_DISTANCE=50;
	int HEIGHT=0;
	int WIDTH=0;
	
	int CAR_WIDTH=90;
	int CAR_HEIGHT=160;
	int xCarCenter;
	int yCarCenter;
	int CAR_X;
	int CAR_Y;
	int WHEEL_BASE=100;
	int CURVATURE_RADIUS=200;
	int TILE_SIDE=4;
	
	static double  xSteeringCenter=0;
	static double  ySteeringCenter=0;
	
	public static byte FORWARD=1;
	public CarDynamics carDynamics=null;
   
	public static String[] hexRoadColors={"888888","888888","888888","CCCCCC"};

	Point4D[][] roadData=new Point4D[NY][NX];
	DrawObject[] drawObjects=null;

	Point4D [][] origRoadData=null;
	DrawObject[]  origDrawObjects=null;

	private JFileChooser fc;
	boolean start=true;
	private int APOSX;
	private Area totalVisibleField=null;


	public static double turningAngle=0;
	public static double dTurningAngle=0.1;
	
	public static int steerAngle=0;
	
	public static Point3D direction=new Point3D(Math.sin(turningAngle),Math.cos(turningAngle),0);


	public Road2D(int WITDH,int HEIGHT, CarFrame2D carFrame2D){
		
		
		this.carFrame2D=carFrame2D;

		dx=WITDH/(NXVISIBLE-1);
		//dy=HEIGHT/(NYVISIBLE-1);

		this.HEIGHT=HEIGHT;
		this.WIDTH=WITDH;
		YFOCUS=HEIGHT/2;
		XFOCUS=WITDH/2;
		
		xCarCenter=(WIDTH)/2;
		yCarCenter=(HEIGHT+CAR_HEIGHT)/2;
		CAR_X=(WIDTH-CAR_WIDTH)/2;
		CAR_Y=HEIGHT/2;
		
		loadRoad();
		totalVisibleField=buildVisibileArea(0,HEIGHT);
	}

	//road with white border to delete
	public void loadRoad() {

		loadRoadFromFile(new File("lib/road_default"));
		loadObjectsFromFile(new File("lib/objects_default"));
		initializaCarDynamics();
	
		/*NX=4;

		 roadData=new Point4D[NY][NX]; 
		 for(int j=0;j<NY;j++){

			for(int i=0;i<NX;i++){

				int delx=0;//(int)(100*Math.cos(Math.PI*j/6));
				int delx2=(int)(100*Math.cos(Math.PI*j/36));
				//if(i==0 || i==3) delz=0;
				if(i==0) delx=0;
				else if(i==1) delx=50;
				else if(i==2) delx=450;
				else if(i==3) delx=500;

				roadData[j][i]=new Point4D();
				
				roadData[j][i].x=delx+delx2;
				roadData[j][i].y=dy*j;
				roadData[j][i].z=0;

				roadData[j][i].setHexColor(hexRoadColors[j%4]);
			}
		}
		System.out.println("#NX="+NX);
		System.out.println("#NY="+NY);
		for(int j=0;j<NY;j++){



				System.out.print(decomposeRow(roadData[j]));
				System.out.println();
		}*/
		



	}
	
	/*public static void main(String[] args) {

		Road2D road=new Road2D();
		road.loadRoad();
	}*/


	/**
	 * SCALING VALUES:
	 * 
	 * L=2.6 METRES =160 px
	 * REAL TIME dt=0.05 sec.
	 * 
	 */
	private void initializaCarDynamics() {
		
		carDynamics=new CarDynamics(1000,1.2,1.4,1,1,1,0,1680,100000,100000);
		carDynamics.setAerodynamics(1.3, 1.8, 0.35);
		carDynamics.setForces(3000, 3000);
		carDynamics.setInitvalues(0, 0, 0, 0);
		
			
	}

	public void drawRoad(Graphics2D bufGraphics){

		
		
		bufGraphics.setColor(CarFrame2D.BACKGROUND_COLOR);
		bufGraphics.fillRect(0,0,WIDTH,HEIGHT);
		
		
		MOVZ=0;
		boolean firstValue=true;

		int startX=WIDTH/2;
		int startY=5;

		

		Vector visibleMap=new Vector();



		int PARTIAL_MOVZ=0;
	
		boolean found=false;
		//Date t=new Date();
		       
		MOVZ=PARTIAL_MOVZ;
		
		for(int j=0;j<NY-1;j++)	{
			
			int lenght=roadData[j].length;

			for(int i=0;i<lenght-1;i++){
				//if(j>0 || i>0) continue;
				Polygon3D p3D=buildConvertedPolygon3D(i,roadData[j],roadData[j+1]);
				
				if(!p3D.clipPolygonToArea2D(totalVisibleField).isEmpty())
					visibleMap.add(p3D);
			}
		}
		
	
		 
		
		int visibleLenght=visibleMap.size();
		
		for(int i=0;i<visibleLenght;i=i+1){

			Polygon3D p3D= (Polygon3D) visibleMap.elementAt(i);
			
			 drawRoadPolygon(p3D,totalVisibleField,ZBuffer.fromHexToColor(p3D.getHexColor()),CarFrame2D.worldImages[p3D.getIndex()],true,bufGraphics);
			
			  	
		}
		
		drawObjects(drawObjects,totalVisibleField,bufGraphics);
		//System.out.println("1-"+((new Date()).getTime()-t.getTime()));
		
		
	}



	
	private void drawRoadPolygon(Polygon3D p3d, Area totalVisibleField, Color fromHexToColor,
			Image texture, boolean b, Graphics2D bufGraphics) {
		
	
		
		Area partialArea= p3d.clipPolygonToArea2D(totalVisibleField);
          
     
		if(partialArea.isEmpty())
				return;
		
		bufGraphics.setColor(fromHexToColor);
		
		if(texture!=null){
			
			Rectangle rect = p3d.getBounds();
			//bufGraphics.setPaint(new TexturePaint(CarFrame2D.worldImages[0],rect));
			//bufGraphics.fill(partialArea);
			bufGraphics.setClip(partialArea);
			bufGraphics.drawImage(texture,rect.x,rect.y,rect.width,rect.height,null);
			bufGraphics.setClip(null);
		}
		else 
			bufGraphics.fill(partialArea);
		
		
	}
	
	private int convertX(double i) {

		return (int) (i/deltax-POSX);
	}
	private int convertY(double j) {

		return (int) (HEIGHT-(j/deltay+POSY));
	}
	
	private int convertX(double i,int POSX) {

		return (int) (i/deltax-POSX);
	}
	private int convertY(double j,int POSY) {

		return (int) (HEIGHT-(j/deltay+POSY));
	}

	private int pickColorFromTexture(Texture texture, 
			double xp, double yp,double zp,Point3D xDirection,Point3D yDirection) {
		
		
		Point3D p=new Point3D(xp,yp,zp);
		
		
		
		return ZBuffer.pickRGBColorFromTexture(texture,p,xDirection,yDirection);
	}


	
     private Area buildVisibileArea(int y1, int y2) {
		int[] cx=new int[4];
		int[] cy=new int[4];



		cx[0]=0;
		cy[0]=y1;
		cx[1]=WIDTH;
		cy[1]=y1;
		cx[2]=WIDTH;
		cy[2]=y2;
		cx[3]=0;
		cy[3]=y2;

		Polygon p=new Polygon(cx,cy,4);

		Area a = new Area(p);

		//System.out.println(Polygon3D.fromAreaToPolygon2D(a));

		return a;
	}





	public Polygon3D buildConvertedPolygon3D(int i,Point4D[] is,  Point4D[] is2){

		int[] cx=new int[4];
		int[] cy=new int[4];
		int[] cz=new int[4];	

		cx[0]=convertX((int) (is[i].x));
		cy[0]=convertY((int) (is[i].y));
		cz[0]=(int) (is[i].z);
		cx[1]=convertX((int) (is[i+1].x));
		cy[1]=convertY((int) (is[i+1].y));
		cz[1]=(int) (is[i+1].z);
		cx[2]=convertX((int) (is2[i+1].x));
		cy[2]=convertY((int) (is2[i+1].y));
		cz[2]=(int) (is2[i+1].z);
		cx[3]=convertX((int) (is2[i].x));
		cy[3]=convertY((int) (is2[i].y));
		cz[3]=(int) (is2[i].z);
		
        //here was a useless line delete by Dylan Watson
				
		Polygon3D p3D=new Polygon3D(4,cx,cy,cz);

		p3D.setHexColor(is[i].getHexColor());
		p3D.setIndex(is[i].getIndex());
		
		return p3D;
	}



	public Polygon3D buildInitRectangle3D(double x,double y,double z,double dx,double dy,double dz){

		int[] cx=new int[4];
		int[] cy=new int[4];
		int[] cz=new int[4];

		cx[0]=(int) x;
		cy[0]=(int) y;
		cz[0]=(int) z;
		cx[1]=(int) (x+dx);
		cy[1]=(int) y;
		cz[1]=(int) z;
		cx[2]=(int) (x+dx);
		cy[2]=(int) (y+dy);
		cz[2]=(int) z;
		cx[3]=(int) x;
		cy[3]=(int) (y+dy);
		cz[3]=(int) z;

		Polygon3D base=new Polygon3D(4,cx,cy,cz);

		return base;

	}






	private Polygon3D calculateTranslated3D(Polygon3D base) {

		Polygon3D translatedBase=new Polygon3D(base.npoints);

		for(int i=0;i<base.npoints;i++){

			translatedBase.xpoints[i]=base.xpoints[i]-POSX;
			translatedBase.ypoints[i]=base.ypoints[i]-POSY;
			translatedBase.zpoints[i]=base.zpoints[i]+MOVZ;

		}

		return translatedBase;
	}


	


	public void drawObjects(com.DrawObject[] drawObjects2,Area totalVisibleField, Graphics2D bufGraphics){



		for(int i=0;i<drawObjects2.length;i++){

			drawObject2D( drawObjects2[i], totalVisibleField,bufGraphics);
			

		}		

	}	

	public void drawObject2D(DrawObject dro,Area totalVisibleField, Graphics2D bufGraphics){

	    int index=dro.getIndex();
		
		if(index<0 || index> CarFrame2D.objects.length)
			return;
		
		BufferedImage image = CarFrame2D.objects[index];
		
		int h=image.getHeight(null);
    	int w=image.getWidth(null);
    	
    
    	
    	int dw=(int) (dro.dx/deltax);
    	int dh=(int) (dro.dy/deltay);
		
		int x=convertX(dro.x);
    	int y=convertY(dro.y);
    	
    	
		if(!totalVisibleField.intersects(new Rectangle(x,y,dw,dh)))
			return;

	    //clip to avoid drawing out of the panel :is it necessary ?
		bufGraphics.setClip(new Rectangle(0,0,WIDTH,HEIGHT));	
    	bufGraphics.drawImage(image,x,y,dw,dh,null);
    	
    	//bufGraphics.drawRect(x, y, dw, dh);
	}

	

	public void reset(Graphics2D g2) {
		g2.setColor(CarFrame2D.BACKGROUND_COLOR);
		g2.fillRect(0,YFOCUS,WIDTH,HEIGHT-YFOCUS);
		Road2D.turningAngle=0;
		POSX=START_X;
		POSY=0;
		APOSX=0;

		roadData=cloneRoadData(origRoadData);
		drawObjects=DrawObject.cloneObjectsArray(origDrawObjects);
	}

	public void up(Graphics2D graphics2D) {

		carDynamics.move(Engine.ddt);
		carFrame2D.setCarSpeed(3.6*Math.abs(carDynamics.u));

		//cast this way to cut off very small speeds
		int NEW_POSY=POSY-(int)( SPACE_SCALE_FACTOR*carDynamics.dx);
		int NEW_POSX=POSX-(int)( SPACE_SCALE_FACTOR*carDynamics.dy);
		turningAngle-=carDynamics.dpsi;
		
		//System.out.println(NEW_POSX+" "+NEW_POSY);	

		if(!checkIsWayFree(NEW_POSX,NEW_POSY))
			return;

		POSY=NEW_POSY;
		POSX=NEW_POSX;




	}
	
	private boolean checkIsWayFree(int new_posx, int new_posy) {
		
		//AffineTransform rotation=AffineTransform.getRotateInstance(Road2D.turningAngle,xCarCenter,yCarCenter);
    	//Rectangle carRect=new Rectangle(xCarCenter-CAR_WIDTH/2, yCarCenter-CAR_HEIGHT/2, CAR_WIDTH, CAR_HEIGHT);
    	//Shape shape = rotation.createTransformedShape(carRect);	 

		for(int i=0;i<drawObjects.length;i++){

			DrawObject dro=drawObjects[i];

			int dw=(int) (dro.dx/deltax);
	    	int dh=(int) (dro.dy/deltay);
			
			int x=convertX(dro.x,new_posx);
	    	int y=convertY(dro.y,new_posy);
	    	Rectangle rect = new Rectangle(x,y,dw,dh);
	    	
	       	
	    	
	    	if(rect.contains(xCarCenter,yCarCenter))
	    	 return false;
	    	
	    	//the orientation of the y axis is upside-down
			/*if(rect.contains(xCarCenter+CAR_HEIGHT*direction.x/2,yCarCenter-CAR_HEIGHT*direction.y/2)
					||
				rect.contains(xCarCenter-CAR_HEIGHT*direction.x/2,yCarCenter+CAR_HEIGHT*direction.y/2)		
			)
				return true;
				//return false;
			*/

		}
		
		return true;
	}

	public void loadRoadFromFile(){	

		fc=new JFileChooser();
		fc.setDialogType(JFileChooser.OPEN_DIALOG);

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			loadRoadFromFile(file);


		}
	}

	public void loadRoadFromFile(File file){

		try {
			BufferedReader br=new BufferedReader(new FileReader(file));

			String snx=br.readLine();
			String sny=br.readLine();

			if(snx==null || sny==null) {

				br.close();
				return;
			}

			NX=Integer.parseInt(snx.substring(4));
			NY=Integer.parseInt(sny.substring(4));
			roadData=new Point4D[NY][NX];

			String str=null;
			int rows=0;
			while((str=br.readLine())!=null){

				roadData[rows]=buildRow(str);

				rows++;	

			}

			br.close();

			origRoadData=cloneRoadData(roadData);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private Point4D[][] cloneRoadData(Point4D[][] roadData2) {

		Point4D[][] newRoadData=new Point4D[NY][NX];


		for(int j=0;j<NY;j++)
			for(int i=0;i<NX;i++)
				for(int k=0;k<3;k++)
					newRoadData[j][i]=(Point4D) roadData2[j][i].clone();

		return newRoadData;
	}

	public void loadObjectsFromFile(){	

		fc=new JFileChooser();
		fc.setDialogType(JFileChooser.OPEN_DIALOG);

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			loadObjectsFromFile(file);


		}
	}

	public void loadObjectsFromFile(File file){
		Vector vdrawObjects=new Vector();
		try {
			BufferedReader br=new BufferedReader(new FileReader(file));


			String str=null;
			int rows=0;
			while((str=br.readLine())!=null){
				if(str.indexOf("#")>=0 || str.length()==0)
					continue;
				DrawObject dro=buildDrawObject(str);
				dro.base=buildInitRectangle3D(dro.x,dro.y,dro.z,dro.dx,dro.dy,dro.dz);
				vdrawObjects.add(dro);

			}

			br.close();
			
			drawObjects=new DrawObject[vdrawObjects.size()];
			
			
			for (int i = 0; i < vdrawObjects.size(); i++) {
				drawObjects[i]=(DrawObject) vdrawObjects.elementAt(i);
			}
			
			origDrawObjects=DrawObject.cloneObjectsArray(drawObjects);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private DrawObject buildDrawObject(String str) {
		DrawObject dro=new DrawObject();

		StringTokenizer tok=new StringTokenizer(str,"_");
		dro.x=Double.parseDouble(tok.nextToken());
		dro.y=Double.parseDouble(tok.nextToken());
		dro.z=Double.parseDouble(tok.nextToken());
		dro.dx=Double.parseDouble(tok.nextToken());
		dro.dy=Double.parseDouble(tok.nextToken());
		dro.dz=Double.parseDouble(tok.nextToken());
		dro.index=Integer.parseInt(tok.nextToken());
		dro.hexColor=tok.nextToken();
		return dro;
	}

	private Point4D[] buildRow(String string) {
		StringTokenizer stk=new StringTokenizer(string,"_");

		Point4D[] row = new Point4D[NX];
		int columns=0;
		while(stk.hasMoreTokens()){

			String[] vals=stk.nextToken().split(",");

			row[columns]=new Point4D();

			row[columns].x=Double.parseDouble(vals[0]);
			row[columns].y=Double.parseDouble(vals[1]);
			row[columns].z=Double.parseDouble(vals[2]);
			row[columns].setHexColor(vals[3]);
			row[columns].setIndex(Integer.parseInt(vals[4]));
			columns++;
		}

		return row;
	}

	public static String decomposeColor(Color tcc) {
		return addZeros(tcc.getRed())+","+addZeros(tcc.getGreen())+","+addZeros(tcc.getBlue());

	}

	public static String addZeros(int numb){
		String newStr=""+numb;
		newStr="00"+newStr;
		newStr=newStr.substring(newStr.length()-3,newStr.length());

		return newStr;

	}

	public static Color buildColor(String colorString) {

		if(colorString==null) return null;
		Color tcc=null;
		String[] colorComponents = colorString.split(",");
		tcc=new Color(Integer.parseInt(colorComponents[0]),Integer.parseInt(colorComponents[1]),Integer.parseInt(colorComponents[2]));
		return tcc;

	}



	public void setPOSX(int posx) {
		POSX = posx;
	}
	
	
	
	  
	/* 	public void rotate() {
		
	
		double angle=steerSign*CarFrame2D.CAR_SPEED*SPEED_SCALE_FACTOR/CURVATURE_RADIUS;
		
		
		turningAngle+=angle;
		CarFrame2D.setSteeringAngle();
		
		direction=new Point3D(Math.sin(turningAngle),Math.cos(turningAngle),0);
		if(Math.abs(turningAngle)<0.01 ||  Math.abs(turningAngle)>Math.PI*2)
			turningAngle=0;
		
		double directionAngle=(Math.PI/2-turningAngle);
		// in rotation the positive versus is anti-clockwise
		calculateSteeringCenter(directionAngle,-angle);
		translateWithSteering(-angle*FORWARD);
		
	
		
	} 
	  
	private void translateWithSteering(double angle) {
		
	
		
		int dxt=(int) (xSteeringCenter+((xCarCenter-xSteeringCenter)*Math.cos(angle)
				-(yCarCenter-ySteeringCenter)*Math.sin(angle))-xCarCenter);
		int dyt=(int)(ySteeringCenter+((xCarCenter-xSteeringCenter)*Math.sin(angle)
				+(yCarCenter-ySteeringCenter)*Math.cos(angle))-yCarCenter);
		
		//int dxt=(int) (-(yCarCenter-ySteeringCenter)*angle);
		//int dyt=(int)((xCarCenter-xSteeringCenter)*angle);
		
		//the orientation of the y axis is upside-down
		int NEW_POSY=POSY-dyt;
		int NEW_POSX=POSX+dxt;
		
		if(!checkIsWayFree(NEW_POSX,NEW_POSY))
			return;
		
		POSY=NEW_POSY;
		POSX=NEW_POSX;
		
		//System.out.println(dxt+" "+dyt);
	}*/

	private void calculateSteeringCenter(double directionAngle, double angle) {
		
		int VERSUS=+1;
		if(angle>0)
			VERSUS=-1;
		
		xSteeringCenter=(xCarCenter-WHEEL_BASE/2*Math.cos(directionAngle)+VERSUS*CURVATURE_RADIUS*Math.sin(directionAngle));
		ySteeringCenter=(yCarCenter-WHEEL_BASE/2*Math.sin(directionAngle)-VERSUS*CURVATURE_RADIUS*Math.cos(directionAngle));
		//System.out.println(xSteeringCenter+" "+ySteeringCenter);
	}

	public void drawCar(Graphics2D bufGraphics) {
		
		//AffineTransform rotation=AffineTransform.getRotateInstance(Road2D.turningAngle,xCarCenter,yCarCenter);
		bufGraphics.rotate(Road2D.turningAngle,xCarCenter,yCarCenter);
		//bufGraphics.transform(rotation);
		bufGraphics.drawImage(CarFrame2D.supercar,CAR_X,CAR_Y,CAR_WIDTH,CAR_HEIGHT,null,null);
	
		
	}

	public void setSteerAngle(double angle) {
		
		carDynamics.setDelta(angle);
		
	}

	public void setAccelerationVersus(int i) {
		
		
		
		carDynamics.setIsbraking(false);
		
		if(i>0){
			carDynamics.setFx2Versus(FORWARD);
			
		}
		else
			carDynamics.setFx2Versus(0);
			
		
	}

	public void setIsBraking(boolean b) {
		
		if(b)
			carDynamics.setFx2Versus(0);
		carDynamics.setIsbraking(b);
		
	}

	

}
