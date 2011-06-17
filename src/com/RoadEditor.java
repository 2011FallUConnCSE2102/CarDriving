package com;
/**
 * @author Piazza Francesco Giovanni ,Tecnes Milano http://www.tecnes.com
 *
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RepaintManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


/**
 * @author Piazza Francesco Giovanni ,Tecnes Milano http://www.tecnes.com
 *
 */

public class RoadEditor extends JFrame implements MenuListener,ActionListener,MouseListener,MouseWheelListener,PropertyChangeListener,MouseMotionListener,KeyListener, ItemListener{


	private JPanel center;
	int HEIGHT=580;
	int WIDTH=500;
	int LEFT_BORDER=240;
	int RIGHT_BORDER=240;
	int BOTTOM_BORDER=100;
	int RIGHT_SKYP=10;
	int NX=4;
	int NY=0;

	int MOVX=50;
	int MOVY=100;

	int dx=2;
	int dy=2;

	int deltay=200;
	int deltax=200;

	Point4D[][] roadData=new Point4D[NY][NX];
	Vector drawObjects=new Vector();
	Graphics2D g2;
	Graphics2D g2Alias;
	Stack oldObjects=new Stack();
	Stack oldRoads=new Stack();
	int MAX_STACK_SIZE=10;


	private JFileChooser fc;
	private JMenuBar jmb;
	private JMenu jm;
	private JMenuItem jmt11;
	private JPanel right;
	private JTextField coordinatesx;
	private JTextField coordinatesy;
	private JTextField coordinatesz;
	private JCheckBox checkCoordinatesx;
	private JCheckBox checkCoordinatesy;
	private JCheckBox checkCoordinatesz;
	private JButton addRow;
	private JButton deleteRow;
	private JButton changePoint;
	private JTextField colorRoadChoice;
	private JButton addObject;
	private JButton delObject;
	private boolean redrawAfterMenu=false;
	private AbstractButton jmt12;
	private JMenuItem jmt21;
	private JMenuItem jmt22;
	private JMenu jm2;
	private JMenu jm5;
	private JMenuItem jmt51;

	public boolean ISDEBUG=false;
	private JButton deselectAll;
	private JPanel bottom;
	private JLabel screenPoint;

	
	private JCheckBox checkRoadColor;
	private JCheckBox checkMultiplePointsSelection;
	private JButton chooseNextTexture;
	private JButton choosePrevTexture;
	private JLabel objectLabel;
	private JComboBox chooseObject;
	private JButton choosePrevObject;
	private JButton chooseNextObject;
	private JCheckBox checkMultipleObjectsSelection;

	boolean isUseTextures=true;
	private JLabel textureLabel;
	private JComboBox chooseTexture;
	private JMenu jm3;
	private JCheckBoxMenuItem jmt31;
	
	private JButton deleteColumn;
	private JMenu jm4;
	private JMenuItem jmt41;
	private JMenuItem jmt42;	
	private JPanel left;
	
	private DoubleTextField objcoordinatesx;
	private JCheckBox objcheckCoordinatesx;
	private DoubleTextField objcoordinatesy;	
	private JCheckBox objcheckCoordinatesy;
	private DoubleTextField objcoordinatesz;
	private JCheckBox objcheckCoordinatesz;
	
	private JButton changeObject;
	private JTextField colorObjChoice;
	private JCheckBox checkObjColor;
	
	private JTextField objcoordinatesdx;
	private JTextField objcoordinatesdy;
	private JTextField objcoordinatesdz;
	private JCheckBox objcheckCoordinatesdx;
	private JCheckBox objcheckCoordinatesdy;
	private JCheckBox objcheckCoordinatesdz;
	private Rectangle currentRect;
	private DoubleTextField roadMove;
	private JButton moveRoadUp;
	private JButton moveRoadDown;
	private JButton moveRoadRight;
	private JButton moveRoadLeft;
	private JButton moveRoadTop;
	private JButton moveRoadBottom;
	private DoubleTextField objMove;
	private JButton moveObjUp;
	private JButton moveObjDown;
	private JButton moveObjLeft;
	private JButton moveObjRight;
	private JButton moveObjTop;
	private JButton moveObjBottom;
	private CubicMesh[] objectMeshes;
	private JButton addLeftColumn;
	private JButton addRightColumn;

	


	public static BufferedImage[] worldImages;
	public static BufferedImage[] objectImages;

	public static Color BACKGROUND_COLOR=new Color(0,0,0);
	public File currentDirectory=null;
	private JMenuItem jmt52;

	public RoadEditor(){

		super("Road editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setLocation(10,10);
		setSize(WIDTH+RIGHT_BORDER+LEFT_BORDER+RIGHT_SKYP,HEIGHT+BOTTOM_BORDER);
		center=new JPanel();
		center.setBackground(BACKGROUND_COLOR);
		center.setBounds(LEFT_BORDER,0,WIDTH,HEIGHT);
		center.addMouseListener(this);
		center.addMouseWheelListener(this);
		center.addMouseMotionListener(this);
		addKeyListener(this);
		addPropertyChangeListener(this);
		add(center);
		buildMenuBar();
		buildLeftObjectPanel();
		buildRightRoadPanel();
		buildBottomPanel();

		RepaintManager.setCurrentManager( 
				new RepaintManager(){

					public void paintDirtyRegions() {


						super.paintDirtyRegions();
						firePropertyChange("paintDirtyRegions",false,true);
						//if(redrawAfterMenu ) {displayAll();redrawAfterMenu=false;}
					}

				}				
		);

		setVisible(true);


	}



	/**
	 * 
	 */
	public void initialize() {


		g2=(Graphics2D) center.getGraphics();
		g2Alias=(Graphics2D) center.getGraphics();
		g2Alias.setColor(Color.GRAY);
		Stroke stroke=new BasicStroke(0.1f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);
		g2Alias.setStroke(stroke);
		
		File directoryImg=new File("lib");
		File[] files=directoryImg.listFiles();
		
		Vector vObjects=new Vector();
		
		if(DrawObject.IS_3D)
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("object3D_")
				   && 	!files[i].getName().startsWith("object3D_texture")	
				){
					
					vObjects.add(files[i]);
					
				}		
			}
		else{
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("object_")
					
				){
					
					vObjects.add(files[i]);
					
				}		
			}
			
		}
		
		try{	


		
			
			Vector vRoadTextures=new Vector();
			
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("road_texture_")){
					
					vRoadTextures.add(files[i]);
					
				}		
			}
			
			worldImages=new BufferedImage[vRoadTextures.size()];
			
			
			if(isUseTextures){
				
				
				for(int i=0;i<vRoadTextures.size();i++){
					
					worldImages[i]=ImageIO.read(new File("lib/road_texture_"+i+".jpg"));
					chooseTexture.addItem(new ValuePair(""+i,""+i));
				}

				
			}
			
		
			
			objectImages=new BufferedImage[vObjects.size()];
			objectMeshes=new CubicMesh[vObjects.size()];
			
			for(int i=0;i<vObjects.size();i++){
				
				chooseObject.addItem(new ValuePair(""+i,""+i));
				objectImages[i]=ImageIO.read(new File("lib/object_"+i+".gif"));
					
				
				if(DrawObject.IS_3D){
					objectMeshes[i]=CubicMesh.loadMeshFromFile(new File("lib/object3D_"+i));
				}
				
					
					
					
				
			}
						
			
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}





	private void displayObjects(Graphics2D bufGraphics) {

		Rectangle totalVisibleField=new Rectangle(0,0,WIDTH,HEIGHT);

		for(int i=0;i<drawObjects.size();i++){

			DrawObject dro=(DrawObject) drawObjects.elementAt(i);

			int y=convertY(dro.y);
			int x=convertX(dro.x);

			int index=dro.getIndex();

			int dw=(int) (dro.dx/dx);
			int dh=(int) (dro.dy/dy);


			if(!totalVisibleField.intersects(new Rectangle(x,y,dw,dh)) && 
					!totalVisibleField.contains(x,y)	)
				continue;

			if(dro.isSelected())
				bufGraphics.setColor(Color.RED);
			else
				bufGraphics.setColor(Color.WHITE);
			bufGraphics.drawString(""+index,x-5,y+5);
			drawObject(bufGraphics,dro);

		}	
	}




	private void deselectAll() {
		cleanPoints();
		deselectAllPoints();
		displayAll();
		coordinatesx.requestFocus();
	}

	private void displayAll() {


		BufferedImage buf=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);

		Graphics2D bufGraphics=(Graphics2D)buf.getGraphics();

		displayRoad(bufGraphics);
		displayObjects(bufGraphics);

		g2.drawImage(buf,0,0,WIDTH,HEIGHT,null);

	}

	private void displayRoad(Graphics2D bufGraphics) {

		bufGraphics.setColor(BACKGROUND_COLOR);
		bufGraphics.fillRect(0,0,WIDTH,HEIGHT);

		for(int j=0;j<NY-1;j++){

			drawPolygon(roadData[j],roadData[j+1],j,bufGraphics);

		} 
		//mark row angles
		for(int j=0;j<NY;j++){

			for(int i=0;i<NX;i++){

				int xo=convertX(roadData[j][i].x);
				int yo=convertY(roadData[j][i].y);

				if(roadData[j][i].isSelected())
					bufGraphics.setColor(Color.RED);
				else
					bufGraphics.setColor(Color.white);
				bufGraphics.fillOval(xo,yo,5,5);

			}
		}	
	}

	private int convertX(double i) {

		return (int) (i/dx-MOVX);
	}
	private int convertY(double j) {

		return (int) (HEIGHT-(j/dy+MOVY));
	}

	private int invertX(int i) {

		return (i+MOVX)*dx;
	}
	private int invertY(int j) {

		return dy*(HEIGHT-j-MOVY);
	}



	public void paint(Graphics g) {
		super.paint(g);
		displayAll();
	}

	private void drawObject(Graphics2D bufGraphics, DrawObject dro) {

		int[] cx=new int[4];
		int[] cy=new int[4];
		
		int versus=1;
		if(!DrawObject.IS_3D)
			versus=-1;

		cx[0]=convertX(dro.x);
		cy[0]=convertY(dro.y);
		cx[1]=convertX(dro.x);
		cy[1]=convertY(dro.y+versus*dro.dy);
		cx[2]=convertX(dro.x+dro.dx);
		cy[2]=convertY(dro.y+versus*dro.dy);
		cx[3]=convertX(dro.x+dro.dx);
		cy[3]=convertY(dro.y);

		Polygon p_in=new Polygon(cx,cy,4);

		Area totArea=new Area(new Rectangle(0,0,WIDTH,HEIGHT));
		Area partialArea = clipPolygonToArea2D( p_in,totArea);

		if(partialArea.isEmpty())
			return;

		Polygon pTot=Polygon3D.fromAreaToPolygon2D(partialArea);
		//if(cy[0]<0 || cy[0]>HEIGHT || cx[0]<0 || cx[0]>WIDTH) return;
		bufGraphics.setColor(ZBuffer.fromHexToColor(dro.getHexColor()));
		bufGraphics.drawPolygon(pTot);
		
		if(!DrawObject.IS_3D){
	
			bufGraphics.drawImage(DrawObject.fromImageToBufferedImage(objectImages[dro.index],Color.WHITE)
					,cx[0],cy[0],cx[2]-cx[0],cy[2]-cy[0],null);
		}
	}


	private void drawPolygon(Point4D[] is, Point4D[] is2,int j, Graphics2D bufGraphics) {
		for(int i=0;i<is.length-1;i++){


			bufGraphics.setColor(ZBuffer.fromHexToColor(is[i].getHexColor()));

			int[] cx=new int[4];
			int[] cy=new int[4];

			cx[0]=convertX(is[i].x);
			cy[0]=convertY(is[i].y);
			cx[1]=convertX(is[i+1].x);
			cy[1]=convertY(is[i+1].y);
			cx[2]=convertX(is2[i+1].x);
			cy[2]=convertY(is2[i+1].y);
			cx[3]=convertX(is2[i].x);
			cy[3]=convertY(is2[i].y);

			Polygon p_in=new Polygon(cx,cy,4);

			Area totArea=new Area(new Rectangle(0,0,WIDTH,HEIGHT));
			Area partialArea = clipPolygonToArea2D( p_in,totArea);

			if(partialArea.isEmpty())
				continue;


			if(isUseTextures){

				Rectangle rect = partialArea.getBounds();

				int index=is[i].getIndex();

				bufGraphics.setClip(partialArea);
				bufGraphics.drawImage(worldImages[index],rect.x,rect.y,rect.width,rect.height,null);
				bufGraphics.setClip(null);
			}
			else
				bufGraphics.fill(partialArea); 

		}
	}

	public Area clipPolygonToArea2D(Polygon p_in,Area area_out){


		Area area_in = new Area(p_in);

		Area new_area_out = (Area) area_out.clone();
		new_area_out.intersect(area_in);

		return new_area_out;

	}


	private void buildMenuBar() {
		jmb=new JMenuBar();
		jm=new JMenu("Load");
		jm.addMenuListener(this);
		jmb.add(jm);

		jmt11 = new JMenuItem("Load road");
		jmt11.addActionListener(this);
		jm.add(jmt11);

		jmt12 = new JMenuItem("Load objects");
		jmt12.addActionListener(this);
		jm.add(jmt12);

		jm2=new JMenu("Save");
		jm2.addMenuListener(this);

		jmt21 = new JMenuItem("Save road");
		jmt21.addActionListener(this);
		jm2.add(jmt21);

		jmt22 = new JMenuItem("Save objects");
		jmt22.addActionListener(this);
		jm2.add(jmt22);

		jmb.add(jm2);

		jm3=new JMenu("Textures");
		jm3.addMenuListener(this);

		jmt31 = new JCheckBoxMenuItem("Use textures");
		jmt31.setState(true);
		jmt31.addActionListener(this);
		jm3.add(jmt31);

		jmb.add(jm3);

		jm4=new JMenu("Change");
		jm4.addMenuListener(this);
		jmt41 = new JMenuItem("Undo last object");
		jmt41.setEnabled(false);
		jmt41.addActionListener(this);
		jm4.add(jmt41);
		jmt42 = new JMenuItem("Undo last road");
		jmt42.setEnabled(false);
		jmt42.addActionListener(this);
		jm4.add(jmt42);	
		
		jmb.add(jm4);
		
		jm5=new JMenu("Other");
		jm5.addMenuListener(this);
		jmt51 = new JMenuItem("Preview");
		jmt51.addActionListener(this);
		jm5.add(jmt51);

		jmt52 = new JMenuItem("Advanced Altimetry");
		jmt52.addActionListener(this);
		jm5.add(jmt52);
		
		if(DrawObject.IS_3D)
			jmb.add(jm5);

		setJMenuBar(jmb);
	}

	private void buildLeftObjectPanel() {

		String header="<html><body>";
		String footer="</body></html>";

		left=new JPanel();
		left.setBounds(0,0,LEFT_BORDER,HEIGHT);
		left.setLayout(null);
        Border leftBorder=BorderFactory.createTitledBorder("Objects");
        left.setBorder(leftBorder);
		        
		int r=5;


		r+=30;
		
		checkMultipleObjectsSelection=new JCheckBox("Multiple selection");
		checkMultipleObjectsSelection.setBounds(30,r,150,20);
		checkMultipleObjectsSelection.addKeyListener(this);
		left.add(checkMultipleObjectsSelection);
		
		r+=30;

		JLabel lx=new JLabel("x:");
		lx.setBounds(5,r,20,20);
		left.add(lx);
		objcoordinatesx=new DoubleTextField(8);
		objcoordinatesx.setBounds(30,r,120,20);
		objcoordinatesx.addKeyListener(this);
		left.add(objcoordinatesx);
		objcheckCoordinatesx=new JCheckBox();
		objcheckCoordinatesx.setBounds(170,r,50,20);
		objcheckCoordinatesx.addKeyListener(this);
		left.add(objcheckCoordinatesx);

		r+=30;

		JLabel ly=new JLabel("y:");
		ly.setBounds(5,r,20,20);
		left.add(ly);
		objcoordinatesy=new DoubleTextField(8);
		objcoordinatesy.setBounds(30,r,120,20);
		objcoordinatesy.addKeyListener(this);
		left.add(objcoordinatesy);
		objcheckCoordinatesy=new JCheckBox();
		objcheckCoordinatesy.setBounds(170,r,50,20);
		objcheckCoordinatesy.addKeyListener(this);
		left.add(objcheckCoordinatesy);

		r+=30;

		JLabel lz=new JLabel("z:");
		lz.setBounds(5,r,20,20);
		left.add(lz);
		objcoordinatesz=new DoubleTextField(8);
		objcoordinatesz.setBounds(30,r,120,20);
		objcoordinatesz.addKeyListener(this);
		left.add(objcoordinatesz);
		objcheckCoordinatesz=new JCheckBox();
		objcheckCoordinatesz.setBounds(170,r,50,20);
		objcheckCoordinatesz.addKeyListener(this);
		left.add(objcheckCoordinatesz);

		r+=30;

		JLabel ldx=new JLabel("dx:");
		ldx.setBounds(5,r,20,20);
		left.add(ldx);
		objcoordinatesdx=new DoubleTextField(8);
		objcoordinatesdx.setBounds(30,r,120,20);
		objcoordinatesdx.addKeyListener(this);
		left.add(objcoordinatesdx);
		objcheckCoordinatesdx=new JCheckBox();
		objcheckCoordinatesdx.setBounds(170,r,50,20);
		objcheckCoordinatesdx.addKeyListener(this);
		left.add(objcheckCoordinatesdx);

		r+=30;

		JLabel ldy=new JLabel("dy:");
		ldy.setBounds(5,r,20,20);
		left.add(ldy);
		objcoordinatesdy=new DoubleTextField(8);
		objcoordinatesdy.setBounds(30,r,120,20);
		objcoordinatesdy.addKeyListener(this);
		left.add(objcoordinatesdy);
		objcheckCoordinatesdy=new JCheckBox();
		objcheckCoordinatesdy.setBounds(170,r,50,20);
		objcheckCoordinatesdy.addKeyListener(this);
		left.add(objcheckCoordinatesdy);

		r+=30;

		JLabel ldz=new JLabel("dz:");
		ldz.setBounds(5,r,20,20);
		left.add(ldz);
		objcoordinatesdz=new DoubleTextField(8);
		objcoordinatesdz.setBounds(30,r,120,20);
		objcoordinatesdz.addKeyListener(this);
		left.add(objcoordinatesdz);
		objcheckCoordinatesdz=new JCheckBox();
		objcheckCoordinatesdz.setBounds(170,r,50,20);
		objcheckCoordinatesdz.addKeyListener(this);
		left.add(objcheckCoordinatesdz);
		
		if(DrawObject.IS_3D){
			
			objcoordinatesdx.setEnabled(false);
			objcoordinatesdy.setEnabled(false);
			objcoordinatesdz.setEnabled(false);
			objcheckCoordinatesdx.setEnabled(false);
			objcheckCoordinatesdy.setEnabled(false);
			objcheckCoordinatesdz.setEnabled(false);
		}

		r+=30;

		JLabel jlb=new JLabel("Obj");
		jlb.setBounds(10,r,30,20);
		left.add(jlb);
		chooseObject=new JComboBox();
		chooseObject.addItem(new ValuePair("",""));
		chooseObject.setBounds(50,r,50,20);
		chooseObject.addItemListener(this);
		chooseObject.addKeyListener(this);
		left.add(chooseObject);

		r+=30;

		objectLabel=new JLabel();
		objectLabel.setFocusable(false);
		objectLabel.setBounds(10,r,100,100);

		Border border=BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		objectLabel.setBorder(border);
		left.add(objectLabel);
		
		JPanel moveObject=buildObjectMovePanel(120,r);
		left.add(moveObject);

		r+=100;

		choosePrevObject=new JButton("<");
		choosePrevObject.setBounds(10,r,50,20);
		choosePrevObject.addActionListener(this);
		choosePrevObject.addKeyListener(this);
		left.add(choosePrevObject);

		chooseNextObject=new JButton(">");
		chooseNextObject.setBounds(60,r,50,20);
		chooseNextObject.addActionListener(this);
		chooseNextObject.addKeyListener(this);
		left.add(chooseNextObject);		

		r+=30;
		
		colorObjChoice=new JTextField();
		colorObjChoice.setBounds(30,r,150,20);
		colorObjChoice.addKeyListener(this);
		left.add(colorObjChoice);
		JButton cho = new JButton(">");
		cho.setBorder(new LineBorder(Color.gray,1));
		cho.addActionListener(
				new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						Color tcc = JColorChooser.showDialog(null,"Choose color",null);
						if(tcc!=null) {
							colorObjChoice.setBackground(tcc);
						}

					}


				}
		);
		cho.addKeyListener(this);
		cho.setBounds(5,r,20,20);
		left.add(cho);
		checkObjColor=new JCheckBox();
		checkObjColor.setBounds(200,r,50,20);
		checkObjColor.addKeyListener(this);
		checkObjColor.setOpaque(false);
		left.add(checkObjColor);

		r+=30;

		changeObject=new JButton(header+"Change O<u>b</u>ject"+footer);
		changeObject.addActionListener(this);
		changeObject.setFocusable(false);
		changeObject.setBounds(5,r,150,20);
		left.add(changeObject);

		r+=30;

		addObject=new JButton(header+"<u>I</u>nsert object"+footer);
		addObject.addActionListener(this);
		addObject.setFocusable(false);
		addObject.setBounds(5,r,150,20);
		left.add(addObject);

		r+=30;

		delObject=new JButton("Del object");
		delObject.addActionListener(this);
		delObject.setFocusable(false);
		delObject.setBounds(5,r,150,20);
		left.add(delObject);

		r+=30;

		add(left);

	}

	private void buildRightRoadPanel() {

		String header="<html><body>";
		String footer="</body></html>";

		right=new JPanel();
		right.setBounds(WIDTH+LEFT_BORDER,0,RIGHT_BORDER,HEIGHT);
		right.setLayout(null);
		
		Border leftBorder=BorderFactory.createTitledBorder("Road");
		right.setBorder(leftBorder);

		int r=25;

		checkMultiplePointsSelection=new JCheckBox("Multiple selection");
		checkMultiplePointsSelection.setBounds(30,r,150,20);
		checkMultiplePointsSelection.addKeyListener(this);
		right.add(checkMultiplePointsSelection);

		r+=30;

		JLabel lx=new JLabel("x:");
		lx.setBounds(5,r,20,20);
		right.add(lx);
		coordinatesx=new DoubleTextField(8);
		coordinatesx.setBounds(30,r,120,20);
		coordinatesx.addKeyListener(this);
		right.add(coordinatesx);
		checkCoordinatesx=new JCheckBox();
		checkCoordinatesx.setBounds(180,r,30,20);
		checkCoordinatesx.addKeyListener(this);
		right.add(checkCoordinatesx);

		r+=30;

		JLabel ly=new JLabel("y:");
		ly.setBounds(5,r,20,20);
		right.add(ly);
		coordinatesy=new DoubleTextField(8);
		coordinatesy.setBounds(30,r,120,20);
		coordinatesy.addKeyListener(this);
		right.add(coordinatesy);
		checkCoordinatesy=new JCheckBox();
		checkCoordinatesy.setBounds(180,r,30,20);
		checkCoordinatesy.addKeyListener(this);
		right.add(checkCoordinatesy);

		r+=30;

		JLabel lz=new JLabel("z:");
		lz.setBounds(5,r,20,20);
		right.add(lz);
		coordinatesz=new DoubleTextField(8);
		coordinatesz.setBounds(30,r,120,20);
		coordinatesz.addKeyListener(this);
		right.add(coordinatesz);
		checkCoordinatesz=new JCheckBox();
		checkCoordinatesz.setBounds(180,r,30,20);
		checkCoordinatesz.addKeyListener(this);
		right.add(checkCoordinatesz);

		r+=30;



		JLabel jlb=new JLabel("Txt");
		jlb.setBounds(5,r,50,20);
		right.add(jlb);
		chooseTexture=new JComboBox();
		chooseTexture.addItem(new ValuePair("",""));
		chooseTexture.setBounds(35,r,50,20);
		chooseTexture.addItemListener(this);
		chooseTexture.addKeyListener(this);
		right.add(chooseTexture);

		r+=30;

		textureLabel=new JLabel();
		textureLabel.setFocusable(false);
		textureLabel.setBounds(5,r,100,100);
		Border border=BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		textureLabel.setBorder(border);
		right.add(textureLabel);

		JPanel moveRoad=buildRoadMovePanel(120,r);
		right.add(moveRoad);

		r+=100;

		choosePrevTexture=new JButton("<");
		choosePrevTexture.setBounds(5,r,50,20);
		choosePrevTexture.addActionListener(this);
		choosePrevTexture.addKeyListener(this);
		right.add(choosePrevTexture);

		chooseNextTexture=new JButton(">");
		chooseNextTexture.setBounds(55,r,50,20);
		chooseNextTexture.addActionListener(this);
		chooseNextTexture.addKeyListener(this);
		right.add(chooseNextTexture);
		
	
		
		r+=30;

		colorRoadChoice=new JTextField();
		colorRoadChoice.setBounds(30,r,120,20);
		colorRoadChoice.addKeyListener(this);
		right.add(colorRoadChoice);
		JButton cho = new JButton(">");
		cho.setBorder(new LineBorder(Color.gray,1));
		cho.addActionListener(
				new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						Color tcc = JColorChooser.showDialog(null,"Choose color",null);
						if(tcc!=null) {
							colorRoadChoice.setBackground(tcc);
						}

					}


				}
		);
		cho.addKeyListener(this);
		cho.setBounds(5,r,20,20);
		right.add(cho);
		checkRoadColor=new JCheckBox();
		checkRoadColor.setBounds(180,r,30,20);
		checkRoadColor.addKeyListener(this);
		checkRoadColor.setOpaque(false);
		right.add(checkRoadColor);

		r+=30;


		addRow=new JButton(header+"<u>A</u>dd last row"+footer);
		addRow.addActionListener(this);
		addRow.setFocusable(false);
		addRow.setBounds(5,r,150,20);
		right.add(addRow);

		r+=30;

		deleteRow=new JButton(header+"<u>D</u>elete last row"+footer);
		deleteRow.addActionListener(this);
		deleteRow.setFocusable(false);
		deleteRow.setBounds(5,r,150,20);
		right.add(deleteRow);

		r+=30;
		
		addLeftColumn=new JButton("Add left column ");
		addLeftColumn.addActionListener(this);
		addLeftColumn.setFocusable(false);
		addLeftColumn.setBounds(5,r,150,20);
		right.add(addLeftColumn);

		r+=30;

		addRightColumn=new JButton("Add right column ");
		addRightColumn.addActionListener(this);
		addRightColumn.setFocusable(false);
		addRightColumn.setBounds(5,r,150,20);
		right.add(addRightColumn);

		r+=30;
		
		deleteColumn=new JButton("Delete column");
		deleteColumn.addActionListener(this);
		deleteColumn.setFocusable(false);
		deleteColumn.setBounds(5,r,150,20);
		right.add(deleteColumn);

		r+=30;



		changePoint=new JButton(header+"Change <u>P</u>oint"+footer);
		changePoint.addActionListener(this);
		changePoint.setFocusable(false);
		changePoint.setBounds(5,r,150,20);
		right.add(changePoint);

		r+=30;

		deselectAll=new JButton(header+"D<u>e</u>select all"+footer);
		deselectAll.addActionListener(this);
		deselectAll.setFocusable(false);
		deselectAll.setBounds(5,r,150,20);
		right.add(deselectAll);

		add(right);


	}


	private JPanel buildRoadMovePanel(int i, int r) {

		JPanel move=new JPanel();
		move.setBounds(i,r,100,100);
		move.setLayout(null);

		Border border = BorderFactory.createEtchedBorder();
		move.setBorder(border);
		
		roadMove=new DoubleTextField();
		roadMove.setBounds(30,40,40,20);
		move.add(roadMove);
		roadMove.addKeyListener(this);
		
		moveRoadUp=new JButton(new ImageIcon("lib/trianglen.jpg"));
		moveRoadUp.setBounds(40,10,20,20);
		moveRoadUp.addActionListener(this);
		moveRoadUp.setFocusable(false);
		move.add(moveRoadUp);
		
		moveRoadDown=new JButton(new ImageIcon("lib/triangles.jpg"));
		moveRoadDown.setBounds(40,70,20,20);
		moveRoadDown.addActionListener(this);
		moveRoadDown.setFocusable(false);
		move.add(moveRoadDown);
		
		moveRoadLeft=new JButton(new ImageIcon("lib/triangleo.jpg"));
		moveRoadLeft.setBounds(5,40,20,20);
		moveRoadLeft.addActionListener(this);
		moveRoadLeft.setFocusable(false);
		move.add(moveRoadLeft);
		
		moveRoadRight=new JButton(new ImageIcon("lib/trianglee.jpg"));
		moveRoadRight.setBounds(75,40,20,20);
		moveRoadRight.addActionListener(this);
		moveRoadRight.setFocusable(false);
		move.add(moveRoadRight);
		
		moveRoadTop=new JButton(new ImageIcon("lib/up.jpg"));
		moveRoadTop.setBounds(5,70,20,20);
		moveRoadTop.addActionListener(this);
		moveRoadTop.setFocusable(false);
		move.add(moveRoadTop);
		
		moveRoadBottom=new JButton(new ImageIcon("lib/down.jpg"));
		moveRoadBottom.setBounds(75,70,20,20);
		moveRoadBottom.addActionListener(this);
		moveRoadBottom.setFocusable(false);
		move.add(moveRoadBottom);

		return move;

	}

	private JPanel buildObjectMovePanel(int i, int r) {

		JPanel move=new JPanel();
		move.setBounds(i,r,100,100);
		move.setLayout(null);

		Border border = BorderFactory.createEtchedBorder();
		move.setBorder(border);
		
		objMove=new DoubleTextField();
		objMove.setBounds(30,40,40,20);
		move.add(objMove);
		objMove.addKeyListener(this);
		
		moveObjUp=new JButton(new ImageIcon("lib/trianglen.jpg"));
		moveObjUp.setBounds(40,10,20,20);
		moveObjUp.addActionListener(this);
		moveObjUp.setFocusable(false);
		move.add(moveObjUp);
		
		moveObjDown=new JButton(new ImageIcon("lib/triangles.jpg"));
		moveObjDown.setBounds(40,70,20,20);
		moveObjDown.addActionListener(this);
		moveObjDown.setFocusable(false);
		move.add(moveObjDown);
		
		moveObjLeft=new JButton(new ImageIcon("lib/triangleo.jpg"));
		moveObjLeft.setBounds(5,40,20,20);
		moveObjLeft.addActionListener(this);
		moveObjLeft.setFocusable(false);
		move.add(moveObjLeft);
		
		moveObjRight=new JButton(new ImageIcon("lib/trianglee.jpg"));
		moveObjRight.setBounds(75,40,20,20);
		moveObjRight.addActionListener(this);
		moveObjRight.setFocusable(false);
		move.add(moveObjRight);
		
		moveObjTop=new JButton(new ImageIcon("lib/up.jpg"));
		moveObjTop.setBounds(5,70,20,20);
		moveObjTop.addActionListener(this);
		moveObjTop.setFocusable(false);
		move.add(moveObjTop);
		
		moveObjBottom=new JButton(new ImageIcon("lib/down.jpg"));
		moveObjBottom.setBounds(75,70,20,20);
		moveObjBottom.addActionListener(this);
		moveObjBottom.setFocusable(false);
		move.add(moveObjBottom);

		return move;

	}

	private void buildBottomPanel() {
		bottom=new JPanel();
		bottom.setBounds(0,HEIGHT,LEFT_BORDER+WIDTH+RIGHT_BORDER,BOTTOM_BORDER);
		bottom.setLayout(null);
		JLabel lscreenpoint = new JLabel();
		lscreenpoint.setText("Position x,y: ");
		lscreenpoint.setBounds(2,2,100,20);
		bottom.add(lscreenpoint);
		screenPoint=new JLabel();
		screenPoint.setText(",");
		screenPoint.setBounds(120,2,300,20);
		bottom.add(screenPoint);
		add(bottom);
	}

	private void undoObjects() {


		drawObjects=(Vector) oldObjects.pop();
		if(oldObjects.size()==0)
			jmt41.setEnabled(false);
	}

	private void undoRoad() {

		roadData=(Point4D[][]) oldRoads.pop();

		NY=roadData.length;
		if(NY==0)
			NX=4;
		else
			NX=roadData[0].length;   
		if(oldRoads.size()==0)
			jmt42.setEnabled(false);
		
		firePropertyChange("RoadEditorUndo", false, true);
	}

	private void prepareUndo() {
		prepareUndoObjects();
		prepareUndoRoad();
	}

	private void prepareUndoRoad() {
		jmt42.setEnabled(true);
		if(oldRoads.size()==MAX_STACK_SIZE){

			oldRoads.removeElementAt(0);

		}
		oldRoads.push(cloneRoadData(roadData));


	}

	private void prepareUndoObjects() {
		jmt41.setEnabled(true);
		if(oldObjects.size()==MAX_STACK_SIZE){
			oldObjects.removeElementAt(0);
		}
		oldObjects.push(cloneObjectsVector(drawObjects));
	}

	private Point4D[][] cloneRoadData(Point4D[][] roadData2) {

		Point4D[][] newRoadData=new Point4D[NY][NX];


		for(int j=0;j<NY;j++)
			for(int i=0;i<NX;i++)
				for(int k=0;k<3;k++)
					newRoadData[j][i]=(Point4D) roadData2[j][i].clone();

		return newRoadData;
	}

	private Vector cloneObjectsVector(Vector drawObjects) {
		Vector newDrawObjects=new Vector();

		for(int i=0;i<drawObjects.size();i++){

			DrawObject dro=(DrawObject) drawObjects.elementAt(i);
			newDrawObjects.add(dro.clone());

		}

		return newDrawObjects;
	}

	private void changeSelectedObject() {
		
		prepareUndoObjects();

		for(int i=0;i<drawObjects.size();i++){
			
			DrawObject dro=(DrawObject) drawObjects.elementAt(i);

			if(!dro.isSelected())
				continue;

			(dro).setX(Double.parseDouble(objcoordinatesx.getText()));
			(dro).setY(Double.parseDouble(objcoordinatesy.getText()));
			(dro).setZ(Double.parseDouble(objcoordinatesz.getText()));
			
			if(!DrawObject.IS_3D){
				
				(dro).setDx(Double.parseDouble(objcoordinatesdx.getText()));
				(dro).setDy(Double.parseDouble(objcoordinatesdy.getText()));
				(dro).setDz(Double.parseDouble(objcoordinatesdz.getText()));
			 
			}
			ValuePair vp=(ValuePair) chooseObject.getSelectedItem();
			if(vp!=null && !vp.getValue().equals("")){
				
				int index=Integer.parseInt(vp.getId());
				 dro.setIndex(index);
				 
				 if(DrawObject.IS_3D){
					 
				     CubicMesh cm=objectMeshes[index]; 
				     dro.setDx(cm.getDeltaX2()-cm.getDeltaX());
				     dro.setDy(cm.getDeltaY2()-cm.getDeltaY());
				     dro.setDz(cm.getDeltaX());
				 }
			}	 
			dro.setHexColor(ZBuffer.fromColorToHex(colorObjChoice.getBackground()));

			dro.setSelected(false);
		}

		cleanObjects();
		displayAll();

	}
	

	private void moveSelectedObject(int dx, int dy, int dk) { 
		
		String sqty=objMove.getText();
		
		if(sqty==null || sqty.equals(""))
			return;
		
		double qty=Double.parseDouble(sqty);
		
		prepareUndoObjects();
		
		for(int i=0;i<drawObjects.size();i++){
			
			DrawObject dro=(DrawObject) drawObjects.elementAt(i);
			
			if(!dro.isSelected())
				continue;

			dro.setX(dro.getX()+dx*qty);
			dro.setY(dro.getY()+dy*qty);
			dro.setZ(dro.getZ()+dk*qty);
			dro.setSelected(false);
		}

		cleanObjects();
		displayAll();
		
	}

	private void changeSelectedRoadPoint() {

		prepareUndoRoad();

		for(int j=0;j<NY;j++){

			for(int i=0;i<NX;i++){

				if(roadData[j][i].isSelected()){

					if(!"".equals(coordinatesx.getText()))
						roadData[j][i].x=Double.parseDouble(coordinatesx.getText());
					if(!"".equals(coordinatesy.getText()))
						roadData[j][i].y=Double.parseDouble(coordinatesy.getText());
					if(!"".equals(coordinatesz.getText()))
						roadData[j][i].z=Double.parseDouble(coordinatesz.getText());

					roadData[j][i].setHexColor(ZBuffer.fromColorToHex(colorRoadChoice.getBackground()));

					ValuePair vp=(ValuePair) chooseTexture.getSelectedItem();
					if(!vp.getId().equals(""))
						roadData[j][i].setIndex(Integer.parseInt(vp.getId()));

					roadData[j][i].setSelected(false);
				}


			}
		}	
        firePropertyChange("RoadEditorUpdate", false, true);

		cleanPoints();
		displayAll();
	}
	
	private void moveSelectedRoadPoints(int dx, int dy,int dk) {
		
		String sqty=roadMove.getText();
		
		if(sqty==null || sqty.equals(""))
			return;
		
		double qty=Double.parseDouble(sqty);
		


		prepareUndoRoad();

		for(int j=0;j<NY;j++){

			for(int i=0;i<NX;i++){

				if(roadData[j][i].isSelected()){

				
						roadData[j][i].x+=qty*dx;
				
						roadData[j][i].y+=qty*dy;
				
						roadData[j][i].z+=qty*dk;

					roadData[j][i].setSelected(false);
				}


			}
		}	
		firePropertyChange("RoadEditorUpdate", false, true);

		cleanPoints();
		displayAll();
	
	}

	private void addRow() {

		prepareUndoRoad();

		Point4D[][] newRoadData = new Point4D[NY+1][NX];

		for(int j=0;j<NY;j++)

			for(int i=0;i<NX;i++){

				newRoadData[j][i]=new Point4D();

				newRoadData[j][i].x=roadData[j][i].x;
				newRoadData[j][i].y=roadData[j][i].y;
				newRoadData[j][i].z=roadData[j][i].z;
				newRoadData[j][i].setHexColor(roadData[j][i].getHexColor());
				newRoadData[j][i].setIndex(roadData[j][i].getIndex());
			}

		for(int i=0;i<NX;i++){

			if(NY>0){

				newRoadData[NY][i]=new Point4D();

				newRoadData[NY][i].x=newRoadData[NY-1][i].x;
				newRoadData[NY][i].y=newRoadData[NY-1][i].y+deltay;
				newRoadData[NY][i].z=newRoadData[NY-1][i].z;
				newRoadData[NY][i].setHexColor(newRoadData[NY-1][i].getHexColor());
				newRoadData[NY][i].setIndex(newRoadData[NY-1][i].getIndex());
			}		 
			else{

				newRoadData[NY][i]=new Point4D();

				newRoadData[NY][i].x=deltax*i;
				newRoadData[NY][i].y=deltay*NY;
				newRoadData[NY][i].z=0; 
				newRoadData[NY][i].setHexColor("888888");
				newRoadData[NY][i].setIndex(0);
			}

		}

		roadData=newRoadData;
		NY=NY+1;

		if(NY==1)
			addRow();
		if(NY==0)
			addRow();

	}

	private void deleteRow() {

		prepareUndoRoad();

		if(NY<1) return;

		Point4D[][] newRoadData = new Point4D[NY-1][NX];



		for(int j=0;j<NY-1;j++)

			for(int i=0;i<NX;i++){

				newRoadData[j][i]=new Point4D();

				newRoadData[j][i].x=roadData[j][i].x;
				newRoadData[j][i].y=roadData[j][i].y;
				newRoadData[j][i].z=roadData[j][i].z;
				newRoadData[j][i].setHexColor(roadData[j][i].getHexColor());
				newRoadData[j][i].setIndex(roadData[j][i].getIndex());
			}


		roadData=newRoadData;
		NY=NY-1;

	}

	private void addColumn(int left_right) {

		prepareUndoRoad();
		Point4D[][] newRoadData = new Point4D[NY][NX+1];
		
		int skip=0;
		
		if(left_right<0)
			skip=1;

		for(int j=0;j<NY;j++)

			for(int i=0;i<NX;i++){

				newRoadData[j][i+skip]=new Point4D();

				newRoadData[j][i+skip].x=roadData[j][i].x;
				newRoadData[j][i+skip].y=roadData[j][i].y;
				newRoadData[j][i+skip].z=roadData[j][i].z;
				newRoadData[j][i+skip].setHexColor(roadData[j][i].getHexColor());
				newRoadData[j][i+skip].setIndex(roadData[j][i].getIndex());
			}

		for(int i=0;i<NY;i++){

		
			if(left_right>0){
			
				if(NX>0){
	
					newRoadData[i][NX]=new Point4D();
	
					newRoadData[i][NX].x=newRoadData[i][NX-1].x+deltax;
					newRoadData[i][NX].y=newRoadData[i][NX-1].y;
					newRoadData[i][NX].z=newRoadData[i][NX-1].z;
					newRoadData[i][NX].setHexColor(newRoadData[i][NX-1].getHexColor());
					newRoadData[i][NX].setIndex(newRoadData[i][NX-1].getIndex());
				}		 
				else{
	
					newRoadData[i][NX]=new Point4D();
	
					newRoadData[i][NX].x=deltax*NX;
					newRoadData[i][NX].y=deltay*i;
					newRoadData[i][NX].z=0; 
					newRoadData[i][NX].setHexColor("888888");
					newRoadData[i][NX].setIndex(0);
				}
			}
			else {
			
				if(NX>0){
	
					newRoadData[i][0]=new Point4D();
	
					newRoadData[i][0].x=newRoadData[i][1].x-deltax;
					newRoadData[i][0].y=newRoadData[i][1].y;
					newRoadData[i][0].z=newRoadData[i][1].z;
					newRoadData[i][0].setHexColor(newRoadData[i][1].getHexColor());
					newRoadData[i][0].setIndex(newRoadData[i][1].getIndex());
				}		 
				else{
	
					newRoadData[i][0]=new Point4D();
	
					newRoadData[i][0].x=deltax*NX;
					newRoadData[i][0].y=deltay*i;
					newRoadData[i][0].z=0; 
					newRoadData[i][0].setHexColor("888888");
					newRoadData[i][0].setIndex(0);
				}
			}
				

		}

		roadData=newRoadData;
		NX=NX+1;

		if(NX==1)
			addColumn(left_right);
		if(NY==0)
			addRow();

	}

	private void deleteColumn() {

		prepareUndoRoad();

		if(NX<1) return;

		Point4D[][] newRoadData = new Point4D[NY][NX-1];



		for(int j=0;j<NY;j++)

			for(int i=0;i<NX-1;i++){

				newRoadData[j][i]=new Point4D();

				newRoadData[j][i].x=roadData[j][i].x;
				newRoadData[j][i].y=roadData[j][i].y;
				newRoadData[j][i].z=roadData[j][i].z;
				newRoadData[j][i].setHexColor(roadData[j][i].getHexColor());
				newRoadData[j][i].setIndex(roadData[j][i].getIndex());
			}


		roadData=newRoadData;
		NX=NX-1;

	}


	private void deleteObject() {

		prepareUndoObjects();
		
		for(int i=0;i<drawObjects.size();i++){
			
			DrawObject dro=(DrawObject) drawObjects.elementAt(i);
			if(dro.isSelected())
				drawObjects.remove(dro);
		}

	}

	private void addObject() {


		int index=0;
		int x=invertX(100);
		int y=invertY(400);
		int z=0;
		int dx=deltax;
		int dy=deltay;
		int dz=100;

		addObject(x,y,z,dx,dy,dz,index);

	}


	private void addObject(MouseEvent arg0) {
		Point p=arg0.getPoint();

		int x=invertX((int)p.getX());
		int y=invertY((int)p.getY());
		cleanObjects();
		int index=0;
		ValuePair vp=(ValuePair) chooseObject.getSelectedItem();
		if(vp!=null && !vp.getValue().equals(""))
			index=Integer.parseInt(vp.getId());
		addObject(x,y,0,deltax,deltay,100,index);

	}

	private void addObject(int x, int y, int z, int dx, int dy, int dz,int index) {

		prepareUndoObjects();

		DrawObject dro=new DrawObject();
		dro.setIndex(index);
		dro.x=x;
		dro.y=y;
		dro.z=z;
		dro.dx=dx;
		dro.dy=dy;
		dro.dz=dz;
		dro.setHexColor("FFFFFF");


		if(!"".equals(objcoordinatesx.getText()))
			dro.x=Double.parseDouble(objcoordinatesx.getText());
		if(!"".equals(objcoordinatesy.getText()))
			dro.y=Double.parseDouble(objcoordinatesy.getText());
		if(!"".equals(objcoordinatesz.getText()))
			dro.z=Double.parseDouble(objcoordinatesz.getText());
		if(!"".equals(objcoordinatesdx.getText()))
			dro.dx=Double.parseDouble(objcoordinatesdx.getText());
		if(!"".equals(objcoordinatesdy.getText()))
			dro.dy=Double.parseDouble(objcoordinatesdy.getText());
		if(!"".equals(objcoordinatesdz.getText()))
			dro.dz=Double.parseDouble(objcoordinatesdz.getText());

		ValuePair vp=(ValuePair) chooseObject.getSelectedItem();
		if(vp!=null && !vp.getValue().equals("")){
			
			
			dro.index=Integer.parseInt(vp.getId());
		
		
		}	
		if(DrawObject.IS_3D){
			
			
			CubicMesh mesh=objectMeshes[dro.index].clone();
			dro.setMesh(mesh);
		}
		dro.setHexColor(ZBuffer.fromColorToHex(colorObjChoice.getBackground()));

		drawObjects.add(dro);


	}










	private void saveRoad() {

		fc = new JFileChooser();
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setDialogTitle("Save road");
		if(currentDirectory!=null)
			fc.setCurrentDirectory(currentDirectory);
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			currentDirectory=fc.getCurrentDirectory();
			File file = fc.getSelectedFile();
			saveRoad(file);

		} 


	}

	public void saveRoad(File file) {



		PrintWriter pr;
		try {
			pr = new PrintWriter(new FileOutputStream(file));
			pr.println("#NX="+NX);
			pr.println("#NY="+NY);

			for(int j=0;j<NY;j++){



				pr.print(decomposeRow(roadData[j]));


				pr.println();
			}	
			pr.close(); 	

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}

	private String decomposeRow(Point4D[] is) {
		String str="";

		for(int i=0;i<is.length;i++){

			if(i>0) str+="_";
			str+=is[i].x+","+is[i].y+","+is[i].z+","+is[i].getHexColor()+","+is[i].getIndex();
		}

		return str;
	}

	public void saveObjects(){
		fc = new JFileChooser();
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setDialogTitle("Save objects");
		if(currentDirectory!=null)
			fc.setCurrentDirectory(currentDirectory);
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			currentDirectory=fc.getCurrentDirectory();
			File file = fc.getSelectedFile();
			saveObjects(file);

		} 
	}

	private void saveObjects(File file) {
		PrintWriter pr;
		try {
			pr = new PrintWriter(new FileOutputStream(file));

			for(int i=0;i<drawObjects.size();i++){

				DrawObject dro=(DrawObject) drawObjects.elementAt(i);
				String str=dro.getX()+"_"+dro.getY()+"_"+dro.getZ()+"_"+
				dro.getDx()+"_"+dro.getDy()+"_"+dro.getDz()+"_"+dro.getIndex()+"_"+dro.getHexColor();
				pr.println(str);
			}

			pr.close(); 	

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}





	public void loadRoadFromFile(){	

		fc=new JFileChooser();
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setDialogTitle("Load road");
		if(currentDirectory!=null)
			fc.setCurrentDirectory(currentDirectory);

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			currentDirectory=fc.getCurrentDirectory();
			File file = fc.getSelectedFile();
			loadRoadFromFile(file);


		}

	}

	public void loadRoadFromFile(File file){

		oldRoads=new Stack();

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
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void loadObjectsFromFile(){	

		fc=new JFileChooser();
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setDialogTitle("Load objects ");
		if(currentDirectory!=null)
			fc.setCurrentDirectory(currentDirectory);

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			currentDirectory=fc.getCurrentDirectory();
			File file = fc.getSelectedFile();
			loadObjectsFromFile(file);


		}
	}

	public void loadObjectsFromFile(File file){

		oldRoads=new Stack();
		drawObjects=new Vector();

		try {
			BufferedReader br=new BufferedReader(new FileReader(file));


			String str=null;
			int rows=0;
			while((str=br.readLine())!=null){
				if(str.indexOf("#")>=0 || str.length()==0)
					continue;
				DrawObject dro=buildDrawObject(str);
				drawObjects.add(dro);

				if(DrawObject.IS_3D){
					CubicMesh mesh=objectMeshes[dro.getIndex()].clone();
					dro.setMesh(mesh);
				}
			}

			br.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
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



	public void actionPerformed(ActionEvent arg0) {
		Object o=arg0.getSource();

		if(o==jmt11){

			if(ISDEBUG)
				loadRoadFromFile(new File("lib/road_default"));
			else
				loadRoadFromFile();
			displayAll();
		}
		if(o==jmt12){
			if(ISDEBUG)
				loadObjectsFromFile(new File("lib/objects_default"));
			else
				loadObjectsFromFile();
			displayAll();
		}
		else if(o==jmt21){
			saveRoad();
		}
		else if(o==jmt22){
			saveObjects();
		}
		else if(o==jmt31){

			isUseTextures=jmt31.isSelected();
			displayAll();

		}
		else if(o==jmt41){
			undoObjects();
		}
		else if(o==jmt42){
			undoRoad();
		}
		else if(o==jmt51){
			showPreview();
		}
		else if(o==jmt52){
			showAltimetry();
		}
		else if(o==addRow){
			addRow();
			displayAll();
		}
		else if(o==deleteRow){
			deleteRow();
			displayAll();
		}
		else if(o==addLeftColumn){
			addColumn(-1);
			displayAll();
		}
		else if(o==addRightColumn){
			addColumn(+1);
			displayAll();
		}
		else if(o==deleteColumn){
			deleteColumn();
			displayAll();
		}
		else if(o==addObject){
			addObject();
			displayAll();
		}
		else if(o==delObject){
			deleteObject();
			displayAll();
		}
		else if(o==changePoint){
			changeSelectedRoadPoint();
			displayAll();
		}
		else if(o==changeObject){
			changeSelectedObject();
			displayAll();
		}
		else if(o==deselectAll){
			deselectAll();
		}
		else if(o==chooseNextTexture){
			int indx=chooseTexture.getSelectedIndex();
			if(indx<chooseTexture.getItemCount()-1)
				chooseTexture.setSelectedIndex(indx+1);
		}
		else if(o==choosePrevTexture){
			int indx=chooseTexture.getSelectedIndex();
			if(indx>0)
				chooseTexture.setSelectedIndex(indx-1);
		}
		else if(o==chooseNextObject){
			int indx=chooseObject.getSelectedIndex();
			if(indx<chooseObject.getItemCount()-1)
				chooseObject.setSelectedIndex(indx+1);
		}
		else if(o==choosePrevObject){
			int indx=chooseObject.getSelectedIndex();
			if(indx>0)
				chooseObject.setSelectedIndex(indx-1);
		}
		else if(o==moveRoadUp){

			moveSelectedRoadPoints(0,1,0);

		}
		else if(o==moveRoadDown){

			moveSelectedRoadPoints(0,-1,0);

		}
		else if(o==moveRoadLeft){

			moveSelectedRoadPoints(-1,0,0);

		}
		else if(o==moveRoadRight){

			moveSelectedRoadPoints(+1,0,0);

		}
		else if(o==moveRoadTop){

			moveSelectedRoadPoints(0,0,1);

		}
		else if(o==moveRoadBottom){

			moveSelectedRoadPoints(0,0,-1);

		}
		else if(o==moveObjUp){

			moveSelectedObject(0,1,0);

		}
		else if(o==moveObjDown){

			moveSelectedObject(0,-1,0);

		}
		else if(o==moveObjLeft){

			moveSelectedObject(-1,0,0);

		}
		else if(o==moveObjRight){

			moveSelectedObject(+1,0,0);

		}
		else if(o==moveObjTop){

			moveSelectedObject(0,0,1);

		}
		else if(o==moveObjBottom){

			moveSelectedObject(0,0,-1);

		}
	}

















	public void menuCanceled(MenuEvent e) {
		// TODO Auto-generated method stub

	}

	public void menuDeselected(MenuEvent e) {
		redrawAfterMenu=true;

	}

	public void menuSelected(MenuEvent e) {
		redrawAfterMenu=false;	

	}

	public static void main(String[] args) {

		RoadEditor re=new RoadEditor();
		re.initialize();
	}
	
	private void showPreview() {
		if(roadData.length==0)
			return;
		RoadEditorPreviewPanel preview=new RoadEditorPreviewPanel(roadData,this);
		
	}
	
	private void showAltimetry() {
		
		if(roadData.length==0)
			return;
		RoadAltimetryPanel altimetry=new RoadAltimetryPanel(roadData,this);
	}


	public void mouseClicked(MouseEvent arg0) {

		int buttonNum=arg0.getButton();
		//right button click
		if(buttonNum==MouseEvent.BUTTON3)
			addObject(arg0);
		else{
			selectPoint(arg0.getX(),arg0.getY());			
		}	
		displayAll();
	}

	private void deselectAllPoints(){

		for(int j=0;j<NY;j++){

			for(int i=0;i<NX;i++){
				roadData[j][i].setSelected(false);
			}

		}	

	}


	private void selectPoint(int x, int y) {

		//select point from road
		boolean found=false;
		
		for(int j=0;j<NY;j++){

			for(int i=0;i<NX;i++){

				int xo=convertX(roadData[j][i].x);
				int yo=convertY(roadData[j][i].y);

				Rectangle rect=new Rectangle(xo-5,yo-5,10,10);
				if(rect.contains(x,y)){

					if(!checkCoordinatesx.isSelected())
						coordinatesx.setText(""+roadData[j][i].x);
					if(!checkCoordinatesy.isSelected())
						coordinatesy.setText(""+roadData[j][i].y);
					if(!checkCoordinatesz.isSelected())
						coordinatesz.setText(""+roadData[j][i].z);
					colorRoadChoice.setBackground(ZBuffer.fromHexToColor(roadData[j][i].hexColor));

					for(int k=0;k<chooseTexture.getItemCount();k++){

						ValuePair vp=(ValuePair) chooseTexture.getItemAt(k);
						if(vp.getId().equals(""+roadData[j][i].getIndex())) 
							chooseTexture.setSelectedItem(vp);
					}

					roadData[j][i].setSelected(true);

					found=true;

				}
				else if(!checkMultiplePointsSelection.isSelected())
					roadData[j][i].setSelected(false);
			}

		}
		if(found)
			deselectAllObjects();
		//select object

		for(int i=0;i<drawObjects.size();i++){

			DrawObject dro=(DrawObject) drawObjects.elementAt(i);
			if(!checkMultipleObjectsSelection.isSelected())
				dro.setSelected(false);
			
			int yo=convertY(dro.y)+5;
			int xo=convertX(dro.x)-5;
			Rectangle rect=new Rectangle(xo,yo-10,10,10);
			if(rect.contains(x,y))
			{
				dro.setSelected(true);
				if(!objcheckCoordinatesx.isSelected())
					objcoordinatesx.setText(""+dro.x);
				if(!objcheckCoordinatesy.isSelected())
					objcoordinatesy.setText(""+dro.y);
				if(!objcheckCoordinatesz.isSelected())
					objcoordinatesz.setText(""+dro.z);
				if(!objcheckCoordinatesdx.isSelected())
					objcoordinatesdx.setText(""+dro.dx);
				if(!objcheckCoordinatesdy.isSelected())
					objcoordinatesdy.setText(""+dro.dy);
				if(!objcheckCoordinatesdz.isSelected())
					objcoordinatesdz.setText(""+dro.dz);
				for(int k=0;k<chooseObject.getItemCount();k++){

					ValuePair vp=(ValuePair) chooseObject.getItemAt(k);
					if(vp.getId().equals(""+dro.index) )
						chooseObject.setSelectedItem(vp);
				}
				colorObjChoice.setBackground(ZBuffer.fromHexToColor(dro.hexColor));
				if(checkMultipleObjectsSelection.isSelected())
					break;
			}

		}

	}
	
	public void deselectAllObjects(){
		
		for(int i=0;i<drawObjects.size();i++){
			
			DrawObject dro=(DrawObject) drawObjects.elementAt(i);
			dro.setSelected(false);
		}
	}

	public void cleanObjects(){
		
		if(!objcheckCoordinatesx.isSelected())	objcoordinatesx.setText("");
		if(!objcheckCoordinatesy.isSelected())objcoordinatesy.setText("");
		if(!objcheckCoordinatesz.isSelected())objcoordinatesz.setText("");
		
		if(!objcheckCoordinatesdx.isSelected())objcoordinatesdx.setText("");
		if(!objcheckCoordinatesdy.isSelected())objcoordinatesdy.setText("");
		if(!objcheckCoordinatesdz.isSelected())objcoordinatesdz.setText("");
		
		
		if(!checkObjColor.isSelected())checkObjColor.setBackground(ZBuffer.fromHexToColor("FFFFFF"));
		
		deselectAllObjects();
	}
	
	public void cleanPoints(){
		
		if(!checkCoordinatesx.isSelected())	coordinatesx.setText("");
		if(!checkCoordinatesy.isSelected())coordinatesy.setText("");
		if(!checkCoordinatesz.isSelected())coordinatesz.setText("");
		
		if(!checkRoadColor.isSelected())checkRoadColor.setBackground(ZBuffer.fromHexToColor("FFFFFF"));
	}
	
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	public void mousePressed(MouseEvent arg0) {
	
		  int x = arg0.getX();
	      int y = arg0.getY();
	      currentRect = new Rectangle(x, y, 0, 0);


	}








	private void selectPointsWithRectangle() {
		
		int x0=Math.min(currentRect.x,currentRect.x+currentRect.width);
		int x1=Math.max(currentRect.x,currentRect.x+currentRect.width);
		int y0=Math.min(currentRect.y,currentRect.y+currentRect.height);
		int y1=Math.max(currentRect.y,currentRect.y+currentRect.height);
			
		//select point from road
        boolean found=false;
        
		for(int j=0;j<NY;j++){

			for(int i=0;i<NX;i++){

				int xo=convertX(roadData[j][i].x);
				int yo=convertY(roadData[j][i].y);

				
				if(xo>=x0 && xo<=x1 && yo>=y0 && yo<=y1  ){

					roadData[j][i].setSelected(true);
					found=true;
					

				}
				else if(!checkMultiplePointsSelection.isSelected())
					roadData[j][i].setSelected(false);
			}

		}
		if(found)
			deselectAllObjects();
		
	}



	public void keyPressed(KeyEvent arg0) {

		int code =arg0.getKeyCode();
		if(code==KeyEvent.VK_DOWN )
			down();
		else if(code==KeyEvent.VK_UP  )
			up();
		else if(code==KeyEvent.VK_LEFT )
		{	MOVX=MOVX-10; 
		displayAll();
		}
		else if(code==KeyEvent.VK_RIGHT  )
		{	MOVX=MOVX+10;   
		displayAll();
		}
		else if(code==KeyEvent.VK_P  )
		{	changeSelectedRoadPoint();
		displayAll();
		}
		else if(code==KeyEvent.VK_B  )
		{	changeSelectedObject();
		displayAll();
		}
		else if(code==KeyEvent.VK_A  )
		{	addRow();    
		displayAll();
		}
		else if(code==KeyEvent.VK_D  )
		{	deleteRow();    
		displayAll();
		}
		else if(code==KeyEvent.VK_I  )
		{ addObject();
		displayAll();
		}
		else if(code==KeyEvent.VK_E  )
		{ deselectAll();
		}
		else if(code==KeyEvent.VK_F1  )
		{ 
		  zoom(+1);
		  displayAll();
		}
		else if(code==KeyEvent.VK_F2  )
		{  zoom(-1);
		   displayAll();
		}

	}




	public void up(){
		MOVY=MOVY-10;
		displayAll();

	}

	public void down(){
		MOVY=MOVY+10;
		displayAll();

	}


	private void zoom(int i) {
		
		double alfa=1.0;
		if(i>0){
			alfa=0.5;
		}
		else {
			alfa=2.0;
			
		}
		
		dx=(int) (dx*alfa);
		dy=(int) (dy*alfa);
		
		MOVX+=(int) ((WIDTH/2+MOVX)*(1.0/alfa-1.0));
		MOVY+=(int) ((-HEIGHT/2+MOVY)*(1.0/alfa-1.0));
	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}





	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}





	public void mouseWheelMoved(MouseWheelEvent arg0) {
		int pix=arg0.getUnitsToScroll();
		if(pix>0) up();
		else down();

	}





	public void mouseDragged(MouseEvent e) {
		updateSize(e);
		
		int x0=Math.min(currentRect.x,currentRect.x+currentRect.width);
		int x1=Math.max(currentRect.x,currentRect.x+currentRect.width);
		int y0=Math.min(currentRect.y,currentRect.y+currentRect.height);
		int y1=Math.max(currentRect.y,currentRect.y+currentRect.height);
	 	g2Alias.drawRect(x0,y0,x1-x0,y1-y0);


	}
	
	public void mouseReleased(MouseEvent arg0) {
		updateSize(arg0);
        selectPointsWithRectangle();
        displayAll();
       
	}

    void updateSize(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        currentRect.setSize(x - currentRect.x,
                            y - currentRect.y);
        displayAll();
   	   
   
    }




	public void mouseMoved(MouseEvent e) {
		Point p=e.getPoint();
		screenPoint.setText(invertX((int)p.getX())+","+invertY((int)p.getY()));

	}

	public void itemStateChanged(ItemEvent arg0) {

		Object o=arg0.getSource();
		if(o==chooseTexture){

			ValuePair val=(ValuePair) chooseTexture.getSelectedItem();
			if(!val.getId().equals("")){

				int num=Integer.parseInt(val.getId());
				ImageIcon ii=new ImageIcon(worldImages[num]);
				textureLabel.setIcon(ii);


			}
			else
				textureLabel.setIcon(null);

		}
		else if(o==chooseObject){

			ValuePair val=(ValuePair) chooseObject.getSelectedItem();
			if(!val.getId().equals("")){

				int num=Integer.parseInt(val.getId());
			 
				BufferedImage icon=new BufferedImage(100,100,BufferedImage.TYPE_3BYTE_BGR);
				icon.getGraphics().drawImage(objectImages[num],0,0,objectLabel.getWidth(),objectLabel.getHeight(),null);
				ImageIcon ii=new ImageIcon(icon);
				objectLabel.setIcon(ii);	
				

			}
			else
				{
				objectLabel.setIcon(null);
			     
				}
		}

	}
	
	public void propertyChange(PropertyChangeEvent arg0) {
		
		//System.out.println(arg0.getSource().getClass());
		if("paintDirtyRegions".equals(arg0.getPropertyName()) && redrawAfterMenu)
		{
			 displayAll();
			 redrawAfterMenu=false;
		}
		else if("roadUpdate".equals(arg0.getPropertyName()))
		{
			 displayAll();
		}
		
	}

	public class ValuePair{


		String id;
		String value;

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}

		public ValuePair(String id, String value) {
			super();
			this.id = id;
			this.value = value;
		}


		public String toString() {

			return this.value;
		}


	}

	public void setRoadData(String action ,Point4D[][] roadData2) {
		
		this.roadData=roadData2;
		firePropertyChange(action, false, true);
		
	}


}
