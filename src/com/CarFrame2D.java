package com;
/**
 * @author Piazza Francesco Giovanni ,Tecnes Milano http://www.tecnes.com
 *
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;




public class CarFrame2D extends JFrame implements KeyListener,ActionListener {

	String VERSION="CarDriving2D 1.1.0";
	
	JPanel center=null;
	private Graphics2D graphics2D;
	public static int HEIGHT=500;
	public static int WIDTH=800;
	
	int car_num=0;
	int back_num=0;
	int obj_num=0;
	
	public static double CAR_SPEED=0;
	public static int SPEED_SCALE=10;
	
	public static int BUTTOMBORDER=100;
	public static int UPBORDER=40;
	public static int LEFTBORDER=0;
	public static int RIGHTBORDER=0;
	
	public static Color BACKGROUND_COLOR=new Color(132,249,62);
	
	
	
	
	JButton resetCar=null;

	
	public static Image supercar=null;
	
	public static BufferedImage[] objects=null;
	String IMAGES_PATH="lib/";
	Engine engine=null;

	private JPanel bottom;
	private Road2D road;
	private JPanel up;
	private Transparency transparency;
	private JLabel speedometer;

	public Properties p;

	private JLabel forward;

	
	public static BufferedImage[] worldImages;
	public static boolean isUseTextures=true;
	private static JLabel steerAngle;
	
	
	String SOUNDS_FOLDER="lib/";
	File hornFile=null;
	File engineFile=null;
	AdvancedGameSound engineSound=null;
	DecimalFormat df=new DecimalFormat("####");

	private BufferedImage buf=null;

	//steering angle,positive anti-clockwise
    double delta=0.15;

	
	 public static void main(String[] args) {
		CarFrame2D ff=new CarFrame2D();
		//ff.initialize();
	
	}
	 
	 
	public CarFrame2D(){
	
	 super(); 
	 setTitle(VERSION);
	 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 setLayout(null);
	 center=new JPanel();
	 center.setBackground(BACKGROUND_COLOR);
	 center.setBounds(LEFTBORDER,UPBORDER,WIDTH,HEIGHT);
	 add(center);
	
	 
	 bottom=new JPanel();
	 resetCar= new JButton("Reset car");
	 resetCar.addActionListener(this);
	 resetCar.addKeyListener(this);
	 bottom.add(resetCar);
	 bottom.setBounds(0,UPBORDER+HEIGHT,LEFTBORDER+WIDTH+RIGHTBORDER,BUTTOMBORDER);
	 add(bottom);
	 setSize(LEFTBORDER+WIDTH+RIGHTBORDER,UPBORDER+HEIGHT+BUTTOMBORDER);
	 buildUpPannel();
	 
	 initialize();
	 setVisible(true);
	 
	} 

	private void buildUpPannel() {
		
		up=new JPanel();
		JLabel speed=new JLabel("Speed:");
		speedometer=new JLabel("");
		up.add(speed);
		up.add(speedometer);
		forward=new JLabel("(F)");
		up.add(forward);
		
		
		JLabel steer=new JLabel("Steer:");
		steerAngle=new JLabel("0");
		up.add(steer);
		up.add(steerAngle);
		
		up.setBounds(0,0,LEFTBORDER+WIDTH+RIGHTBORDER,UPBORDER);
		add(up);
	}

	
	

	/**
	 * 
	 */
	private void initialize() {
		
		loadProperties();
	


		try {

		
			
			
			
			supercar=ImageIO.read(new File("lib/supercar_"+car_num+".gif"));
			supercar=transparency.makeColorTransparent(supercar,Color.WHITE);

	        
		
			
			File directoryImg=new File("lib");
			File[] files=directoryImg.listFiles();
			
			Vector vObjects=new Vector();
			
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("object_")){
					
					vObjects.add(files[i]);
					
				}		
			}
			
			objects=new BufferedImage[vObjects.size()];
			
			for(int j=0;j<vObjects.size();j++){
				
				File fileObj=(File) vObjects.elementAt(j);
				
				objects[j]=ImageIO.read(fileObj);
				objects[j]=DrawObject.fromImageToBufferedImage(
						objects[j],Color.WHITE
						
				
				);
				
			}

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
					
				}

				
			}
			
			hornFile=new File(SOUNDS_FOLDER+"horn.wav");
			engineFile=new File(SOUNDS_FOLDER+"shortDiesel.wav");
			engineSound=new AdvancedGameSound(engineFile,-1,false);
			engineSound.filter(getEngineModulation());
			
			graphics2D=(Graphics2D) center.getGraphics();
			transparency=new Transparency();
			setCarSpeed(0);
			road=new Road2D(WIDTH,HEIGHT,this);
			
			buf=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		     
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}





	public void loadProperties(){
		
		p=new Properties();
		try {
			p.load(new FileInputStream("lib/driving.properties"));
			
			if("true".equals(p.getProperty("ISUSETEXTURE")))
					isUseTextures=true;
			else
				isUseTextures=false;	
			
			if( p.getProperty("SOUND_BANK_PATH")==null)
				p.setProperty("SOUND_BANK_PATH","lib\\soundbank.gm");
			if( p.getProperty("INSTRUMENT_CODE")==null)
				p.setProperty("INSTRUMENT_CODE",""+0);
			
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent arg0)  {
		char press=arg0.getKeyChar();
		
		int code=arg0.getKeyCode();
		
		if(code==KeyEvent.VK_R) 
			reset();
		else if(code==KeyEvent.VK_UP)
		{	
			if(getcarSpeed()==0){
				engine=new Engine(this);
				engine.start();				   
			}

			road.setAccelerationVersus(1);

		}
		else if(code==KeyEvent.VK_DOWN && engine!=null)
		{	
			road.setIsBraking(true);
		}
		else if(code==KeyEvent.VK_LEFT)
		{			
			//left();
			steer(delta);
		}
		else if(code==KeyEvent.VK_RIGHT)
		{			
			//right();
			steer(-delta);
		}
		else if(code==KeyEvent.VK_C)
		{

			car_num=car_num+1;
			try{
			
				boolean exists = (new File(IMAGES_PATH+"supercar_"+car_num+".gif")).exists();
				if(exists){
					supercar=ImageIO.read(new File(IMAGES_PATH+"supercar_"+car_num+".gif")); 
					supercar=transparency.makeColorTransparent(supercar,Color.WHITE);
				}
				else
				{
					car_num=0;
					supercar=ImageIO.read(new File(IMAGES_PATH+"supercar_"+car_num+".gif"));
					supercar=transparency.makeColorTransparent(supercar,Color.WHITE);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			reset();

		}
		else if(code==KeyEvent.VK_A)
		{			
		    Road2D.FORWARD=1;
		    forward.setText("(F)");
		}
		else if(code==KeyEvent.VK_S)
		{			
			Road2D.FORWARD=-1;
			forward.setText("(R)");
		}
		else if(code==KeyEvent.VK_H)
		{			
			playHorn();
		
		}
	
	

	}




	private void playHorn() {

		try {

			GameSound hornSound = new GameSound(hornFile,true);
			hornSound.start();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	



	public void setCarSpeed(double d) {

		CAR_SPEED=d;
		if(CAR_SPEED<0)
			CAR_SPEED=0;

		speedometer.setText(df.format(SPEED_SCALE*CAR_SPEED));

		try {

			if(CAR_SPEED>0){

				if(!engineSound.isRun()){

					engineSound.start();
				}
				engineSound.setPlay(true);
				engineSound.filter(getEngineModulation());

			}
			else engineSound.setPlay(false);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	
	private double getEngineModulation(){
		
		double ratio  = 20.0/CAR_SPEED;
		
		if(ratio>5)
			return 5;
		else if(ratio<0)
			return 1;
		else
			return ratio;
	}
	
	public double getcarSpeed(){
		
		return CAR_SPEED;
	}


	public void up() {
		road.up(graphics2D);
		drawRoad(); 
	}
	

	
	public void steer(double angle) {
		
		road.setSteerAngle(angle);
	
	
	}
	
	public static void setSteeringAngle() {
		
		DecimalFormat df=new DecimalFormat("##.##");
		steerAngle.setText(df.format(180*Road2D.turningAngle/Math.PI));
		
	}


	public void keyReleased(KeyEvent arg0) {
		
		
		int code=arg0.getKeyCode();
		
		
		if(code==KeyEvent.VK_LEFT ||code==KeyEvent.VK_RIGHT )
		{			
			
			steer(0);
			
		}
		
		if(code==KeyEvent.VK_UP)
		{	
	
			road.setAccelerationVersus(0);

		}
		else if(code==KeyEvent.VK_DOWN && engine!=null)
		{			
			road.setAccelerationVersus(0);
		}
	}


	public void actionPerformed(ActionEvent arg0) {
		Object o=arg0.getSource();
		if(o==resetCar)
			reset();
		
	}

	public void reset(){
		
		road.reset(graphics2D);
		setCarSpeed(0);
		if(engine!=null){
			road.setAccelerationVersus(0);
		}
		Road2D.turningAngle=0;
		Road2D.direction=new Point3D(0,1,0);
		Road2D.FORWARD=+1;
		steerAngle.setText(""+Road2D.turningAngle);

		drawRoad();
	}

	private void drawRoad()  {
		
		if(graphics2D==null)
			graphics2D=(Graphics2D) center.getGraphics();
		
		   
		Graphics2D bufGraphics=(Graphics2D)buf.getGraphics();
		
		road.drawRoad(bufGraphics);
		
		road.drawCar(bufGraphics);
		graphics2D.drawImage(buf,0,0,WIDTH,HEIGHT,null);
		
		
	}
	
	

	
	
	
	public void paint(Graphics g) {
		
		super.paint(g);
		drawRoad();
		
	}


	public Road2D getRoad() {
		return road;
	}



}
