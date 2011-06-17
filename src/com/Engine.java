package com;
/**
 * @author Piazza Francesco Giovanni ,Tecnes Milano http://www.tecnes.com
 *
 */

public class Engine extends Thread{
	
	CarFrame2D carFrame;
	public static int dt=50;
	public static double ddt=dt/1000.0;
	
	public Engine(CarFrame2D carFrame) {
		super();
		this.carFrame = carFrame;
	}

	public void run() {
	
		
		while(true){
			
			
			carFrame.up();
			try {
				if(CarFrame2D.CAR_SPEED==0)
					break;
				sleep(dt);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		
		
				
		}
		
	}
	

	

}
