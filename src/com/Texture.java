package com;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



public class Texture {

	
	int WIDTH;
	int HEIGHT;
	static double pi_2=Math.PI/2;
	
	public int[][] rgb;
	
	public Texture(BufferedImage bi){
		
		HEIGHT=bi.getHeight();
		WIDTH=bi.getWidth();
		rgb=new int[WIDTH][HEIGHT];
	
		
		for(int i=0;i<WIDTH;i++)
			for(int j=0;j<HEIGHT;j++)
				rgb [i][j]=bi.getRGB(i,j);
	}
	
	
	public int getRGB(int i,int j){
		
		return rgb[i][j];
	}


	public int getWidth() {
		return WIDTH;
	}


	public int getHeight() {
		
		return HEIGHT;
	}
	
	public static void main(String[] args) {
		String fileOrig="C:\\Programmi\\eclipse-SDK-3.3.2-win32\\eclipse\\workspace\\Driving2D\\lib\\road4.jpg";
		transformTexture(fileOrig);
	}
	
	public static void transformTexture(String fileOrig){
		
		File fileO=new File(fileOrig);
		String fileOut=fileO.getParent()+"\\tran_"+fileO.getName();
		
		BufferedImage bout;
		try {
			bout = transform(fileOrig);
			saveImage(bout,fileOut);
		
		System.out.println("END");						
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	private static BufferedImage transform(String fileOrig) throws IOException {
	
		BufferedImage imageOrig=ImageIO.read(new File(fileOrig));
			
		
		int h=imageOrig.getHeight();
		int w=imageOrig.getWidth();
		
		BufferedImage bout=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		
		int lenght=w*h;
		int[] rgb=new int[lenght];
		
		
		for(int i=0;i<w;i++)
			for(int j=0;j<h;j++){
				
				
				int iNew=calculateNewX(i,j,w,h);
				int jNew=calculateNewY(i,j,w,h);
				
				int argbs = 0;
				
				if(iNew<0 || jNew<0)
					argbs = Color.WHITE.getRGB();
				else
					argbs = imageOrig.getRGB(iNew, jNew);
				int alphas=0xff & (argbs>>24);
				int rs = 0xff & (argbs>>16);
				int gs = 0xff & (argbs >>8);
				int bs = 0xff & argbs;
								
				Color color=new Color(rs,gs,bs,alphas);
				rgb[i+j*w]=color.getRGB();
				
		}		
		
		
		bout.setRGB(0,0,w,h,rgb,0,w);
		return bout;
		
	}


	private static int calculateNewY(int i, int j, int w, int h) {
	
		
		int dy=h-1;
		int newJ=dy-(int) Math.round(h/pi_2*Math.atan((dy-j)*1.0/(w-i)));
		//System.out.println(Math.atan((dy-j)/(w-i)));
		return newJ;
	}


	private static int calculateNewX(int i, int j, int w, int h) {
		
		int dy=h-1;
		int newI=(int) Math.round(w-Math.sqrt((dy-j)*(dy-j)+(w-i)*(w-i)));
		if(newI<0)
			//return -1;
			newI+=w;
		return newI;
	}


	private static void saveImage(BufferedImage buf,String fileName) throws IOException {
		
		File file=new File(fileName);
	
	
	
		
			ImageIO.write(buf,"jpg",file);
			
		
	}
}
