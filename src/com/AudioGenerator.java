/*
 * Thanks to Richard Baldwin from www.developer.com !
 * 
 */

package com;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;



public class AudioGenerator {

	public static void main(String[] args) {
		AudioGenerator ag=new AudioGenerator();
		try {
			ag.generate();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void generate() throws IOException {

		int LENGHT=16000;
		byte[] audioData=new byte[LENGHT*4];


		ByteBuffer byteBuffer;
		ShortBuffer shortBuffer;

		byteBuffer = ByteBuffer.wrap(audioData);
		shortBuffer = byteBuffer.asShortBuffer();
		
		int bytesPerSamp = 2;
		int byteLength = audioData.length/bytesPerSamp;



		float sampleRate=16000;
		int sampleSizeInBits=16;
		//createWave(byteLength,sampleRate,shortBuffer);
		createTriangleWave(byteLength,sampleRate,shortBuffer);
		
		int channels=2;
		boolean bigEndian=true;
		boolean signed=true;


		AudioFormat audioFormat=new AudioFormat(sampleRate,  sampleSizeInBits,  channels,  signed,  bigEndian);

		ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(audioData);

		AudioInputStream audioInputStream = new AudioInputStream(
				byteArrayInputStream,
				audioFormat,
				audioData.length/audioFormat.
				getFrameSize());

		String fileName="sound";


		AudioSystem.write(
				audioInputStream,
				AudioFileFormat.Type.AU,
				new File(fileName +".au"));


	}


	public void createWave(int sampLength,float sampleRate, ShortBuffer shortBuffer){

		for(int cnt = 0; cnt < sampLength; cnt++){
			double time = cnt/sampleRate;
			double freq = 950.0;//arbitrary frequency
			double sinValue =
				(Math.sin(2*Math.PI*freq*time) +
						Math.sin(2*Math.PI*(freq/1.8)*time) +
						Math.sin(2*Math.PI*(freq/1.5)*time)
						)/3.0;
			shortBuffer.put((short)(sampleRate*sinValue));
		}
	}
	
	public void createPianoWave(int sampLength,float sampleRate, ShortBuffer shortBuffer){

		double end=sampLength/sampleRate;
		double period=end/200;
		
		for(int cnt = 0; cnt < sampLength; cnt++){
			
			
			double time = cnt/sampleRate;
			
			double pitch=time-period*Math.floor(time/period);
			double val=Math.exp(-(pitch-period)/period)/Math.exp(1);
				
			shortBuffer.put((short)(sampleRate*val));
		}
	}
	
	public void createDrumWave(int sampLength,float sampleRate, ShortBuffer shortBuffer){

		double end=sampLength/sampleRate;
		double period=end/100;
		
		for(int cnt = 0; cnt < sampLength; cnt++){
			
			
			double time = cnt/sampleRate;
			
			double pitch=time-period*Math.floor(time/period);
			double val=0;
			if(pitch*2>period)
				val=1;
			else
				val=-1;
				
			shortBuffer.put((short)(sampleRate*val));
		}
	}
	
	public void createTriangleWave(int sampLength,float sampleRate, ShortBuffer shortBuffer){

		double end=sampLength/sampleRate;
		double period=end/100;
		System.out.println("period:"+period);
		System.out.println("frequency:"+1.0/period);
		
		for(int cnt = 0; cnt < sampLength; cnt++){
			
			
			double time = cnt/sampleRate;
			
			double pitch=time-period*Math.floor(time/period);
			double val=2*Math.abs(pitch-period/2)/period;
		
				
			shortBuffer.put((short)(sampleRate*val));
		}
	}


}
