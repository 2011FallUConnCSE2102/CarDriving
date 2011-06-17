package com;


import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * @author Piazza Francesco Giovanni ,Tecnes Milano http://www.tecnes.com
 *
 */

public class ZBuffer{

		int rgbColor;
		double z=0;

		
		public int getRgbColor() {
			return rgbColor;
		}
		public void setRgbColor(int rgbColor) {
			this.rgbColor = rgbColor;
		}
		public double getZ() {
			return z;
		}
		public void setZ(double z) {
			this.z = z;
		}
	
		public ZBuffer(int rgbColor, double z) {
			super();
			this.rgbColor = rgbColor;
			this.z = z;
		}
		public ZBuffer() {
			super();
		}
		
		public static Color  fromHexToColor(String col){



			int r=Integer.parseInt(col.substring(0,2),16);
			int g=Integer.parseInt(col.substring(2,4),16);
			int b=Integer.parseInt(col.substring(4,6),16);

			Color color=new Color(r,g,b);

			return color;
		}

		public static String fromColorToHex(Color col){

			String exe="";

			exe+=addZeros(Integer.toHexString(col.getRed()))
					+addZeros(Integer.toHexString(col.getGreen()))
					+addZeros(Integer.toHexString(col.getBlue()));

			return exe;

		}


		public static String addZeros(String hexString) {
			
			if(hexString.length()==1)
				return "0"+hexString;
			else 
				return hexString;
		}
		
		public static int  pickRGBColorFromTexture(Texture texture,Point3D p,Point3D xDirection,Point3D yDirection){
			
			int x=(int) Point3D.calculateDotProduct(p,xDirection);
			int y=(int) Point3D.calculateDotProduct(p,yDirection);
			
			return pickRGBColorFromTexture(texture,x,y);
		}
		
		public static int  pickRGBColorFromTexture(Texture texture,Point3D p,Point3D xDirection,Point3D yDirection, Point3D origin,int deltaX,int deltaY){
			
			int x=0;
			int y=0;
			
			if(origin!=null){
				
				 Point3D translate=p.substract(origin);
				 x=(int) Point3D.calculateDotProduct(translate,xDirection)+deltaX;
				 y=(int) Point3D.calculateDotProduct(translate,yDirection)+deltaY;
			}
			else
			{
				
				 x=(int) Point3D.calculateDotProduct(p,xDirection)+deltaX;
				 y=(int) Point3D.calculateDotProduct(p,yDirection)+deltaY;
				
			}	
			
			return pickRGBColorFromTexture(texture,x,y);
		}
		
		public static int pickRGBColorFromTexture(Texture texture,int i,int j){
			
			int w=texture.getWidth();
			int h=texture.getHeight();
			
			if(i<0) i=i%w+w;
			if(j<0) j=j%h+h;
			int argb = texture.getRGB(i%w, h-j%h-1);
			
			return argb;
			
		}
		public void update(double ys, int rgbColor) {
			
			if(getZ()==0 ||  getZ()>ys ){

				setZ(ys);
				setRgbColor(rgbColor);
                
			}
			
		}

	}
