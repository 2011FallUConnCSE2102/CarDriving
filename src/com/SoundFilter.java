/**
 * 
 * WORKING FOR 16 BIT  PER FRAME SAMPLES
 * 
 */
package com;

public class SoundFilter {
	
	int frameSize=0;
	
	public SoundFilter(int frameSize) {
		this.frameSize=frameSize;
	}

	public byte[] filter(byte[] data,double modulation){
		
		int length=data.length;
		int dim=(int) (length*modulation);
		dim-=dim%frameSize;
		byte[] newData=new byte[dim];
		
		for(int i=0;i<dim;i+=frameSize){
		
			int index=(i*length)/(dim-1);
			index=index-index%frameSize;
			short sample = getSample(data,index);
			setSample(newData,i,sample);
			
		}
		return newData;
	}
	//16 bit sample
	public static short getSample(byte[] buffer, int position) {
		return (short)(
				((buffer[position+1] & 0xff) << 8) |
				(buffer[position] & 0xff));
	}
	//16 bit sample
	public static void setSample(byte[] buffer, int position,
			short sample)
	{
		buffer[position] = (byte)(sample & 0xff);
		buffer[position+1] = (byte)((sample >> 8) & 0xff);
	}

		
}
