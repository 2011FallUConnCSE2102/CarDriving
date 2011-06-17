package com;


import java.util.StringTokenizer;
import java.util.Vector;

public class LineData implements Cloneable{

		Vector lineDatas=new Vector();
		boolean isSelected=false;

		public int size(){

			return lineDatas.size();
		}

		public void addIndex(int n){
			lineDatas.add(new Integer(n));
		}

		public int getIndex(int i){
			return ((Integer)lineDatas.elementAt(i)).intValue();
		}
		
		
		public String toString() {
			
			return decomposeLineData(this);
		}

		public LineData(){}
		
		public LineData (int i, int j, int k, int l) {
			
			this.addIndex(i);
			this.addIndex(j);
			this.addIndex(k);
			this.addIndex(l);

			
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		} 
		

		public LineData clone() {
		
			LineData ldnew=new LineData();
			
			for(int i=0;i<size();i++){
				
				ldnew.addIndex(getIndex(i));
			}
			
			return ldnew;
		}
		
		private String decomposeLineData(LineData ld) {

			String str="";

			for(int j=0;j<ld.size();j++){

				if(j>0)
					str+=",";
				str+=ld.getIndex(j);

			}

			return str;
		}

		public int positionOf(int i) {
			
			for(int j=0;j<size();j++){
				
				if(i==getIndex(j))
					return j;
			}
			
			return -1;
		}
		

	}