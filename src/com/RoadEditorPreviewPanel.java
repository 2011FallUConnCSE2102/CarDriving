package com;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Created on 24/ott/09
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

public class RoadEditorPreviewPanel extends JDialog implements KeyListener, PropertyChangeListener{

	int WIDTH=800;
	int BOTTOM_HEIGHT=100;
	int HEIGHT=500;
	private Graphics2D graphics2D;
	private BufferedImage buf=null;
	private JPanel center;
	Point4D[][] roadData=null;
	private RoadEditor roadEditor=null;
	
	public int DEPTH_DISTANCE=1000;
	
	private static ZBuffer[] roadZbuffer;
	private int[] rgb;
	int greenRgb= Color.GREEN.getRGB();
	public static final int NUM_WORLD_TEXTURES = 12;
	public static Texture[] worldTextures;
	
	int POSX=0;
	int POSY=0;
	int MOVZ=0;
	
	int NX=2;
	int NY=80;
	
	double deltay=.5;
	double deltax=.5;
	private int y0=200;
	private int x0=100;
	
	public  Point3D pAsso;
	double alfa=Math.PI/3;
	double cosAlfa=Math.cos(alfa);
	double sinAlfa=Math.sin(alfa);
	double s2=Math.sqrt(2);
	private boolean isUseTextures=true;
	private JPanel bottom;
	
	
	public RoadEditorPreviewPanel(Point4D[][] roadData, RoadEditor roadEditor) {
		super();
		
		setTitle("Preview 3D");
		setLayout(null);
		this.roadData=roadData;
		NY=roadData.length;
		NX=roadData[0].length;
		setSize(WIDTH,HEIGHT+BOTTOM_HEIGHT);
		center=new JPanel();
		center.setBounds(0,0,WIDTH,HEIGHT);
		add(center);
		bottom=new JPanel();
		bottom.setBounds(0,HEIGHT,WIDTH,BOTTOM_HEIGHT);
		
		JLabel usage=new JLabel();
		usage.setText("Move with arrow keys,zoom with F1,F2");
		bottom.add(usage);
		add(bottom);
		
		this.roadEditor=roadEditor;
		roadEditor.addPropertyChangeListener(this);
		
		addKeyListener(this);
		setVisible(true);
		initialize();
	}
	
	
	public void setVisible(boolean b) {
		
		super.setVisible(b);
		if(!b)
			dispose();
	}

	private void initialize() {

		buf=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		roadZbuffer=new ZBuffer[WIDTH*HEIGHT];
		pAsso=new Point3D(Math.cos(alfa)/s2,Math.sin(alfa)/s2,1/s2);
		buildNewZBuffer();

		try {

			File directoryImg=new File("lib");
			File[] files=directoryImg.listFiles();

			Vector vRoadTextures=new Vector();

			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("road_texture_")){

					vRoadTextures.add(files[i]);

				}		
			}

			worldTextures=new Texture[vRoadTextures.size()];


			if(isUseTextures){


				for(int i=0;i<vRoadTextures.size();i++){

					worldTextures[i]=new Texture(ImageIO.read(new File("lib/road_texture_"+i+".jpg")));
				}





			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void paint(Graphics g) {
		super.paint(g);
		drawRoad();
	}


	private void drawRoad() {
		
		if(graphics2D==null)
			graphics2D=(Graphics2D) center.getGraphics();

		drawRoad(buf);
		
	
		graphics2D.drawImage(buf,0,0,WIDTH,HEIGHT,null);
		
	}


	private void drawRoad(BufferedImage buf) {
		
		cleanZBuffer();
		
		for(int j=0;j<NY-1;j++)	{

			int lenght=roadData[j].length;
			for(int i=0;i<lenght-1;i++)	{

				Polygon3D p3D=buildTranslatedPolygon3D(i,roadData[j],roadData[j+1]);
				 decomposeClippedPolygonIntoZBuffer(p3D,ZBuffer.fromHexToColor(p3D.getHexColor()),worldTextures[p3D.getIndex()],roadZbuffer);
		
			}


		}
		
		buildScreen(buf); 
	}
	
    private void decomposeClippedPolygonIntoZBuffer(Polygon3D p3d,Color color,Texture texture,ZBuffer[] zbuffer){
   	 
   	 Point3D origin=new Point3D(p3d.xpoints[0],p3d.ypoints[0],p3d.zpoints[0]);
   	 decomposeClippedPolygonIntoZBuffer(p3d, color, texture,zbuffer,null,null,origin,0,0);
   	 
    }
    
    private void decomposeClippedPolygonIntoZBuffer(Polygon3D p3d,Color color,Texture texture,ZBuffer[] zbuffer,
   		 Point3D xDirection,Point3D yDirection,Point3D origin,int deltaX,int deltaY){
   	 
    	//avoid clipping:
   	 Polygon3D clippedPolygon=p3d;//Polygon3D.clipPolygon3DInY(p3d,0);
   	 
   	 Vector triangles = Polygon3D.divideIntoTriangles(clippedPolygon);

   	 if(texture!=null && xDirection==null && yDirection==null){
   		 
   		 Point3D p0=new Point3D(p3d.xpoints[0],p3d.ypoints[0],p3d.zpoints[0]);
   		 Point3D p1=new Point3D(p3d.xpoints[1],p3d.ypoints[1],p3d.zpoints[1]);

   		 xDirection=(p1.substract(p0)).calculateVersor();
   		 Point3D normal=Polygon3D.findNormal(p3d);
   		 yDirection=Point3D.calculateCrossProduct(normal,xDirection).calculateVersor();

   		 //yDirection=Point3D.calculateOrthogonal(xDirection);
   	 }

   	 for(int i=0;i<triangles.size();i++){
 
   		 Polygon3D tri=(Polygon3D) triangles.elementAt(i);

   		 decomposeTriangleIntoZBufferEdgeWalking( tri,color, texture,zbuffer, xDirection,yDirection,origin, deltaX, deltaY);
   		 
   	 }

    }	
    
    /**
	 * 
	 * DECOMPOSE PROJECTED TRIANGLE USING EDGE WALKING AND
	 * PERSPECTIVE CORRECT MAPPING
	 * 
	 * @param p3d
	 * @param color
	 * @param texture
	 * @param useLowResolution
	 * @param xDirection
	 * @param yDirection
	 * @param origin 
	 */
	private void decomposeTriangleIntoZBufferEdgeWalking(Polygon3D p3d,Color color,Texture texture,ZBuffer[] zbuffer, Point3D xDirection, Point3D yDirection, Point3D origin,int deltaX,int deltaY) {

		int rgbColor=color.getRGB();

		Point3D p0=new Point3D(p3d.xpoints[0],p3d.ypoints[0],p3d.zpoints[0]);
		Point3D p1=new Point3D(p3d.xpoints[1],p3d.ypoints[1],p3d.zpoints[1]);
		Point3D p2=new Point3D(p3d.xpoints[2],p3d.ypoints[2],p3d.zpoints[2]);
		
	    

		//System.out.println(p3d+" "+rgbColor);

		double x0=calcAssX(p0);
		double y0=calcAssY(p0);
		double z0=DEPTH_DISTANCE+Point3D.calculateDotProduct(p0,pAsso);


		double x1=calcAssX(p1);
		double y1=calcAssY(p1);
		double z1=DEPTH_DISTANCE+Point3D.calculateDotProduct(p1,pAsso);

		double x2=calcAssX(p2);
		double y2=calcAssY(p2);
		double z2=DEPTH_DISTANCE+Point3D.calculateDotProduct(p2,pAsso);
		
		Point3D[] points=new Point3D[3];
		
		points[0]=new Point3D(x0,y0,z0,p0.x,p0.y,p0.z);
		points[1]=new Point3D(x1,y1,z1,p1.x,p1.y,p1.z);
		points[2]=new Point3D(x2,y2,z2,p2.x,p2.y,p2.z);
		
		
		int upper=0;
		int middle=1;
		int lower=2;
		
		for(int i=0;i<3;i++){
			
			if(points[i].y>points[upper].y)
				upper=i;
			
			if(points[i].y<points[lower].y)
				lower=i;
			
		}
		for(int i=0;i<3;i++){
			
			if(i!=upper && i!=lower)
				middle=i;
		}
	
		
    	//double i_depth=1.0/zs;
    	//UPPER TRIANGLE
		
		double inv_up_lo_y=1.0/(points[upper].y-points[lower].y);
		double inv_up_mi_y=1.0/(points[upper].y-points[middle].y);
		double inv_lo_mi_y=1.0/(points[lower].y-points[middle].y);
		
    	for(int j=(int) points[middle].y;j<points[upper].y;j++){
    		
    		//if(j>258)
    		//	continue;

    		double middlex=Point3D.foundXIntersection(points[upper],points[lower],j);
    		double middlex2=Point3D.foundXIntersection(points[upper],points[middle],j);

    		double l1=(j-points[lower].y)*inv_up_lo_y;
			double l2=(j-points[middle].y)*inv_up_mi_y;
			double zs=l1*points[upper].p_z+(1-l1)*points[lower].p_z;
			double ze=l2*points[upper].p_z+(1-l2)*points[middle].p_z;
			double ys=l1*points[upper].p_y+(1-l1)*points[lower].p_y;
			double ye=l2*points[upper].p_y+(1-l2)*points[middle].p_y;
			double xs=l1*points[upper].p_x+(1-l1)*points[lower].p_x;
			double xe=l2*points[upper].p_x+(1-l2)*points[middle].p_x;
    		
			Point3D pstart=new Point3D(middlex,j,zs,xs,ys,zs);
    		Point3D pend=new Point3D(middlex2,j,ze,xe,ye,ze);
    		
    		if(pstart.x>pend.x){

    			Point3D swap= pend.clone();
    			pend= pstart.clone();
    			pstart=swap;

    		}
    		
    		int start=(int)pstart.x;
    		int end=(int)pend.x;
    		
    		double inverse=1.0/(end-start);
    		//double i_pstart_p_y=1.0/(SCREEN_DISTANCE+pstart.p_y);
    		//double i_end_p_y=1.0/(SCREEN_DISTANCE+pend.p_y);
    		
    		for(int i=start;i<end;i++){

    			if(i<0 || i>=WIDTH || j<0 || j>= HEIGHT)
    				continue;
               
    			double l=(i-start)*inverse;
    			
    			
    			double xi=(1-l)*pstart.p_x+l*pend.p_x;
    			double yi=(1-l)*pstart.p_y+l*pend.p_y;
    			double zi=(1-l)*pstart.p_z+l*pend.p_z;
    			
    			
    			if(texture!=null)
    			  rgbColor=ZBuffer.pickRGBColorFromTexture(texture,new Point3D(xi,yi,zi),xDirection,yDirection,origin,deltaX, deltaY);
    			if(rgbColor==greenRgb)
    				continue;
    			int tot=WIDTH*j+i;
    			//System.out.println(x+" "+y+" "+tot);

    			ZBuffer zb=zbuffer[tot];

    			zb.update(yi,rgbColor);
    			
    		} 


    	}
    
      	//LOWER TRIANGLE
    	for(int j=(int) points[lower].y;j<points[middle].y;j++){

    		double middlex=Point3D.foundXIntersection(points[upper],points[lower],j);
    		double middlex2=Point3D.foundXIntersection(points[lower],points[middle],j);

			double l1=(j-points[lower].y)*inv_up_lo_y;
			double l2=(j-points[middle].y)*inv_lo_mi_y;
			double zs=l1*points[upper].p_z+(1-l1)*points[lower].p_z;
			double ze=l2*points[lower].p_z+(1-l2)*points[middle].p_z;
			double ys=l1*points[upper].p_y+(1-l1)*points[lower].p_y;
			double ye=l2*points[lower].p_y+(1-l2)*points[middle].p_y;
			double xs=l1*points[upper].p_x+(1-l1)*points[lower].p_x;
			double xe=l2*points[lower].p_x+(1-l2)*points[middle].p_x;
    		
			Point3D pstart=new Point3D(middlex,j,zs,xs,ys,zs);
    		Point3D pend=new Point3D(middlex2,j,ze,xe,ye,ze);
    	
    		    		
       		if(pstart.x>pend.x){
       			
       		
    			Point3D swap= pend.clone();
    			pend= pstart.clone();
    			pstart=swap;

    		}
       	
    		int start=(int)pstart.x;
    		int end=(int)pend.x;
        		
    		double inverse=1.0/(end-start);
    		
    		//double i_pstart_p_y=1.0/(SCREEN_DISTANCE+pstart.p_y);
    		//double i_end_p_y=1.0/(SCREEN_DISTANCE+pend.p_y);
    		
    		for(int i=start;i<end;i++){
    			
    			if(i<0 || i>=WIDTH || j<0 || j>= HEIGHT)
    				continue;
    			
    			double l=(i-start)*inverse;
    			
    			double xi=(1-l)*pstart.p_x+l*pend.p_x;
    			double yi=(1-l)*pstart.p_y+l*pend.p_y;
    			double zi=(1-l)*pstart.p_z+l*pend.p_z;
    		
    			
    			if(texture!=null)
    			  rgbColor=ZBuffer.pickRGBColorFromTexture(texture,new Point3D(xi,yi,zi),xDirection,yDirection,origin, deltaX,deltaY);
    			if(rgbColor==greenRgb)
    				continue;
    			int tot=WIDTH*j+i;
    			//System.out.println(x+" "+y+" "+tot);

    			ZBuffer zb=zbuffer[tot];

    			zb.update(yi,rgbColor);
    			
    		}


    	}	




	}
	
	public int calcAssX(Point3D p){


		return calcAssX(p.x,p.y,p.z);
	}
	public int calcAssY(Point3D p){


		return calcAssY(p.x,p.y,p.z);
	}

	public int calcAssX(double sx,double sy,double sz){

		
		//return x0+(int) (deltax*(sy-sx*sinAlfa));//axonometric formula
		return x0+(int) (deltax*(sy*sinAlfa+sx*sinAlfa));
	}

	public int calcAssY(double sx,double sy,double sz){

		
		//return y0+(int) (deltay*(sz-sx*cosAlfa));
		return y0-(int) (deltay*(sz+sy*cosAlfa-sx*cosAlfa));
	}
	
	public Polygon3D buildTranslatedPolygon3D(int i,Point4D[] is,  Point4D[] is2){

		int[] cx=new int[4];
		int[] cy=new int[4];
		int[] cz=new int[4];	

		cx[0]=(int) (is[i].x-POSX);
		cy[0]=(int) (is[i].y-POSY);
		cz[0]=(int) (is[i].z+MOVZ);
		cx[1]=(int) (is[i+1].x-POSX);
		cy[1]=(int) (is[i+1].y-POSY);
		cz[1]=(int) (is[i+1].z+MOVZ);
		cx[2]=(int) (is2[i+1].x-POSX);
		cy[2]=(int) (is2[i+1].y-POSY);
		cz[2]=(int) (is2[i+1].z+MOVZ);
		cx[3]=(int) (is2[i].x-POSX);
		cy[3]=(int) (is2[i].y-POSY);
		cz[3]=(int) (is2[i].z+MOVZ);
		
		Polygon3D p3D=new Polygon3D(4,cx,cy,cz);

		p3D.setHexColor(is[i].getHexColor());
		p3D.setIndex(is[i].getIndex());
		
		return p3D;
	}



	private void buildNewZBuffer() {
		
       
		
		for(int i=0;i<roadZbuffer.length;i++){
			
			roadZbuffer[i]=new ZBuffer(greenRgb,0);
			
			
		}
		 int lenght=roadZbuffer.length;
		 rgb = new int[lenght];	

				
	}

	private void cleanZBuffer() {

		for(int i=0;i<roadZbuffer.length;i++){
			
			roadZbuffer[i].setRgbColor(greenRgb);
			roadZbuffer[i].setZ(0);

		}
		
	}
	
	private void buildScreen(BufferedImage buf) {

		int length=rgb.length;
		
		 for(int i=0;i<length;i++){
			   
			   rgb[i]=roadZbuffer[i].getRgbColor(); 
				  
		 }	   

	     buf.setRGB(0,0,WIDTH,HEIGHT,rgb,0,WIDTH); 
         

	}
	
	private void zoom(int i) {
		if(i>0){
			deltax=deltax*2;
			deltay=deltay*2;
			
		}	
		else if(i<0){
			deltax=deltax/2;
			deltay=deltay/2;
			
		}
	}


	public void keyPressed(KeyEvent arg0) {
		
		int code =arg0.getKeyCode();
		
		if(code==KeyEvent.VK_F1  )
		{ 
		  zoom(-1);
		  drawRoad();
		}
		else if(code==KeyEvent.VK_F2  )
		{  zoom(+1);
		   drawRoad();
		}
		else if(code==KeyEvent.VK_LEFT  )
		{ 
			POSX-=10/deltax;
			drawRoad();
		}
		else if(code==KeyEvent.VK_RIGHT  )
		{ 
			POSX+=10/deltax;
			drawRoad();
		}
		else if(code==KeyEvent.VK_UP  )
		{ 
			POSY+=10/deltax;
			drawRoad();
		}
		else if(code==KeyEvent.VK_DOWN  )
		{ 
			POSY-=10/deltax;
			drawRoad();
		}
	}


	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void propertyChange(PropertyChangeEvent evt) {
		//System.out.println(evt.getPropertyName());
		if("RoadEditorUndo".equals(evt.getPropertyName()) 
				|| "RoadAltimetryUndo".equals(evt.getPropertyName()) 
				|| "RoadEditorUpdate".equals(evt.getPropertyName())
		)
		{
			this.roadData=roadEditor.roadData;
			drawRoad();
		}
		
	

		
	}

}
