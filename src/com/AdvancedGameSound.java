package com;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;



public class AdvancedGameSound extends Thread{

	SourceDataLine sound=null;
	SoundFilter sf=null;
	boolean play=true;
	boolean run=false;
	boolean once=false;
	int LOOP=0;
	int NEVER_ENDING_LOOP=-1;
	double INVERSE_TIME=0.1;
	private AudioInputStream audioStream;
	private byte[] audioData=null;
	private byte[] audioDataToPlay;



	private byte[] getdata(AudioInputStream audioStream)throws Exception{
		long length = audioStream.getFrameLength();
		byte[] data=new byte[(int) length];

		int bytesToRead =data.length;
		int bytesRead = 0;

		int cnt = 0;
		while (bytesToRead > 0 && cnt >= 0) {
			cnt = audioStream.read(data, bytesRead, bytesToRead);
			if (cnt > 0) {
				bytesRead += cnt;
				bytesToRead -= cnt;
			}
			else if (cnt == 0) {
				break;
			}
		}
      	return data;
	}
	
	public AdvancedGameSound(File file,int loop) throws Exception {


		this(file,loop,false);

	}

	public AdvancedGameSound(File file,boolean once) throws Exception {


         this(file,0,once); 

	}

	public AdvancedGameSound(File file,int loop,boolean once) throws Exception {

		audioStream = loadStream(file);
		audioData=getdata(audioStream);
		sound=getSourcedataLine(audioStream);
		sf=new SoundFilter(audioStream.getFormat().getFrameSize());
		this.LOOP=loop;
		this.once=once;

	}


	/**
	 * @param args
	 */
	 public static void main(String[] args) {

		try{

			AdvancedGameSound gs=new AdvancedGameSound(new File("lib/diesel.wav"),2,false);
			//AdvancedGameSound gs=new AdvancedGameSound(new File("lib/basicEngine.wav"),0,false);
			gs.filter(2);
			gs.start();


		}catch (Exception e) {
			e.printStackTrace();
		}
	 }


	 public void filter(double factor) {

		 audioDataToPlay=sf.filter(audioData,factor);
		
	}

	public void run() {

		 run=true; 

		 int count=0;
		 
		 sound.start();
		 while(count<=LOOP || LOOP==NEVER_ENDING_LOOP){
			 
			 
			 int bytesToRead=1000;

			 int cnt=bytesToRead;
			 int offset=0;
			 try{
				 while(cnt>0 && offset<audioDataToPlay.length && play){
					
					 if(bytesToRead+offset>audioDataToPlay.length)
						 bytesToRead=audioDataToPlay.length-offset;
					 //System.out.println(cnt+" "+offset+" "+bytesToRead+" "+audioDataToPlay.length );				 
					 cnt=sound.write(audioDataToPlay, offset, bytesToRead);
					 offset+=cnt;
                     
				 }
                 
				 if(once)
					 break;

				
			 }
			 catch (Exception e) {
				 //e.printStackTrace();
			 }
			 if(LOOP>0)
			  count++;
		
		 }
		
		 sound.close();

	 }

	 public void stopPlay(){

		 play=false;
		 if(sound.isRunning()){
			 sound.stop();
		 }

	 }

	 public void startPlay() {

		 play=true;

	 }


	 private SourceDataLine getSourcedataLine(AudioInputStream stream)throws Exception {
		 AudioFormat format = stream.getFormat();

		 DataLine.Info dataLineInfo =
			 new DataLine.Info(
					 SourceDataLine.class,
					 format);

		 SourceDataLine sourceDataLine = (SourceDataLine)
		 AudioSystem.getLine(
				 dataLineInfo);

		 sourceDataLine.open(format);
		
		 return sourceDataLine;
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
