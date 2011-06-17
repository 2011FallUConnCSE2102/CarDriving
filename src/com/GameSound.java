/**
 * @author Piazza Francesco Giovanni ,Tecnes Milano http://www.tecnes.com
 *
 */
package com;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;



public class GameSound extends Thread{
	
	Clip clip=null;
	boolean play=true;
	boolean run=false;
	boolean once=false;
	int LOOP=0;
	double INVERSE_TIME=0.1;
	
	public GameSound(File file,int loop) throws Exception {
		
		AudioInputStream stream = loadStream(file);
		clip=getClip(stream);
		this.LOOP=loop;
	     
	}
	
	public GameSound(File file,boolean once) throws Exception {
		
		AudioInputStream stream = loadStream(file);
		clip=getClip(stream);
		this.once=once;
		
	}
	
	public GameSound(File file,int loop,boolean once) throws Exception {
		
		AudioInputStream stream = loadStream(file);
		clip=getClip(stream);
		this.LOOP=loop;
		this.once=once;
		
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try{

			//GameSound gs=new GameSound(new File("lib/diesel.wav"),0,false);
			GameSound gs=new GameSound(new File("lib/basicEngine.wav"),0,false);
			gs.start();


		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void run() {

		run=true; 
		
		while(run){
			try {
				if(play){
					
					clip.loop(LOOP);

					Thread.sleep((long)(1000*INVERSE_TIME));


					clip.setFramePosition(0);
					if(once)
						break;

				}
				else
					Thread.sleep(100);
				
				

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		clip.stop();
		clip.close();

	}
	
	public void stopPlay(){
		
		play=false;
		if(clip.isRunning()){
			clip.stop();
			clip.setFramePosition(0);
		}
		
	}
	
	public void startPlay() {
		
		play=true;
		
	}


	private static Clip getClip(AudioInputStream stream)throws Exception {
		AudioFormat format = stream.getFormat();
		DataLine.Info info=new DataLine.Info(Clip.class,format);

		Clip clip = (Clip) AudioSystem.getLine(info);
		clip.open(stream);
		return clip;
	}

	private static AudioInputStream loadStream(File file)throws Exception  {

		AudioFileFormat aff=AudioSystem.getAudioFileFormat(file);
		AudioInputStream stream = AudioSystem.getAudioInputStream(file);
		return stream;
	}


	public boolean isPlay() {
		return play;
	}


	public void setPlay(boolean play) {
		this.play = play;
	}


	public boolean isRun() {
		return run;
	}


	public void setRun(boolean run) {
		this.run = run;
	}

	public double getINVERSE_TIME() {
		return INVERSE_TIME;
	}

	public void setINVERSE_TIME(double inverse_time) {
		INVERSE_TIME = inverse_time;
	}











	
}
