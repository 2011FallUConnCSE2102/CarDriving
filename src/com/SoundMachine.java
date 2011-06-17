package com;
/**
 * @author Piazza Francesco Giovanni ,Tecnes Milano http://www.tecnes.com
 *
 */

import java.io.File;
import java.io.IOException;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;



public class SoundMachine extends Thread{
	
	CarFrame2D carFrame;
	Synthesizer synth=null; 
	MidiChannel mc[];
	//not too bad 134,324,364,410
	int INSTRUMENT_CODE=0;
	int numberSynth=INSTRUMENT_CODE; //30 guitar distortion!
    public boolean play=true; 
	
	private Instrument[] ins;
	public static Soundbank sb;
	
	public SoundMachine(CarFrame2D carFrame,int INSTRUMENT_CODE){
		this.INSTRUMENT_CODE=INSTRUMENT_CODE;
		this.carFrame = carFrame;
		
		try{ 
			
			
			//////////////////////////
			// load Synthesizer
			synth=MidiSystem.getSynthesizer();
			synth.open();
			MidiDevice.Info mdi=synth.getDeviceInfo(); 
			System.out.println("info synth:"+mdi.getDescription());
			
			
			////////////////
			// load soundbank   
			File fb=new File(carFrame.p.getProperty("SOUND_BANK_PATH"));
			sb=MidiSystem.getSoundbank(fb);
			
			
			////////////
			// load Instrument   
			ins=sb.getInstruments();
			System.out.println("Instruments number:"+ins.length); 
			//numberSynth=Integer.parseInt(carFrame.p.getProperty("INSTRUMENT_CODE"));
			
			//////////////
			// load Instrument on synth
			
			boolean b =synth.loadAllInstruments(sb);
			if(b) System.out.println(" Loaded Instrument :"+ins[INSTRUMENT_CODE].toString());
			
			//load channel (one of 16)
			Patch pat=ins[INSTRUMENT_CODE].getPatch();
			//MidiChannel
			mc=synth.getChannels();
			mc[0].programChange(pat.getBank(),pat.getProgram());
			mc[1].programChange(pat.getBank(),pat.getProgram());
			// sz.close();
			
		}// try end
		catch(MidiUnavailableException mue){mue.getMessage();}
		catch(InvalidMidiDataException imdfe){}
		catch(IOException ioe){}
		catch(Exception e){}
		
		
	}// creator end 
	
 
	public void run() {


		while(play){
			double inverse_time=CarFrame2D.CAR_SPEED;

			if(inverse_time!=0){

				inverse_time=1.0/inverse_time;

				int notefreq=70;
				mc[0].noteOn(notefreq,50);

				try{Thread.sleep(100);

				}catch(InterruptedException ie) {System.out.println(ie.getMessage());}	

				mc[0].noteOff(notefreq);
				try{

					Thread.sleep((long)(1000*inverse_time));		
				}
				catch(InterruptedException ie) {
					System.out.println(ie.getMessage());	
				}

			}
			else{
				try{
					Thread.sleep(100);
				}
				catch(InterruptedException ie) {
					System.out.println(ie.getMessage());

				}

			}
		}
	}
	


	public boolean isPlay() {
		return play;
	}


	public void setPlay(boolean play) {
		this.play = play;
	}





	
	
	
	
	
	
}