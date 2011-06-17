package com;

import java.text.DecimalFormat;


/**
 * 
 * Reference book:Dinamica del veicolo (Vehicle Dynamics),by Massimo Guiggiani  
 * 
 * @author Francesco Piazza
 *
 */

public class CarDynamics {
	

	//STRUCTURAL VARIABLES
	double a1=0;
	double a2=0;	
	double tau=1;
	double chi=0;
	//wheelbase
	double l=0;
	//front and rear track
	double t1=0;
	double t2=0;

	//moment of inerzia
	double Jz=0;
	double i_Jz=0;
	
	//vehicle mass
	double m=0;
	double i_m=0;

	//DYNAMIC VARIABLES
	
	//front steering angles 1=front,2=rear
	double delta1=0;
	double delta2=0;
	
	double delta=0;
	
	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		
		this.delta = delta;
		delta1=tau*delta;
		delta2=chi*tau*delta;
	}

	// longitudinal  velocity
	double u=0;
	// yaw  velocity
	double r=0;
	// lateral  velocity
	double nu=0;
	
	//tire slip angles
	double alfa1=0;
	double alfa2=0;
	
	//side slip angles
	double beta1=0;
	double beta2=0;
	
	//cornering stiffness
	double C1=0;
	double C2=0;
	
	//relaxation lenght
	double d=0.25;
	
	//tractive forces
	double Fx1=0;
	double Fx2=0;
	//lateral forces
	private double Fy1;
	private double Fy2;
	//torque force
	double torque_force=0;
	//braking force
	double brakingForce=0;
		
    //DRAG PROPERTIES
	//air density
	double ro=0;
	double S=0;
	double Cx=0;
	
	//Centripetal Force
	double ay=0;
	
	//COORDINATES INCREMENTS
	double dx=0;
	double dy=0;
	double dpsi=0;
    double psi=0;
	
	static DecimalFormat df=null;
	
	private byte Fx2Signum=0;
	private boolean isBraking=false;
	
	
	//MATH CONSTANTS
	
	double PI_2=Math.PI*0.5;

	public static void main(String[] args) {
		
	    double dt=0.01;
	    df=new DecimalFormat("##.###");	
	   
		CarDynamics cd=new CarDynamics(1000,1.2,1.4,1,1,1,0,1680,100000,100000);
		cd.setAerodynamics(1.3, 1.8, 0.35);
		cd.setForces(3000, 3000);
		cd.setInitvalues(0, 0, 0, 0);
		cd.setFx2Versus(1);
		
		//System.out.println("\t"+df.format(0)+"\t "+cd);
		
		long time=System.currentTimeMillis();
		for(double t=dt;t<=200000;t+=dt){
			
		
			cd.move(dt);
			//System.out.println("\t"+df.format(t)+"\t "+cd);
			//System.out.println("\t"+df.format(t)+"\t "+cd.printForces());
			
		}
		
		System.out.println(System.currentTimeMillis()-time);
	}
	
	void move(double dt) {
		
		int sgn=(int) Math.signum(u);
		
		//calculate lateral forces:
		calculateFy1(dt);
		calculateFy2(dt);
        calculateAutomaticTorque(); 	
       		
		//formula for constant speed:
		//Fx2=m*(0-nu*r)+Fy1*delta1+0.5*ro*S*Cx*u*u;
              
        
		double du=i_m*(Fx1+Fx2-sgn*(Fy1*delta1+Fy2*delta2+0.5*ro*S*Cx*u*u))+nu*r;
        double dnu=i_m*(sgn*(Fy1+Fy2)+Fx1*delta1+Fx2*delta2)-u*r;
		double dr=i_Jz*(sgn*(Fy1*a1-Fy2*a2)+Fx1*a1*delta1-Fx2*a2*delta2);
		
	
		//System.out.println(printForces());
		//System.out.println(toString());
		//System.out.println(du+" "+dnu+" "+dr);
		//System.out.println(du*dt+" "+dnu*dt+" "+dr*dt);
		
		u+=du*dt;
		nu+=dnu*dt;
		r+=dr*dt;
		
		calculateCoordinatesIncrements(dt);
		//System.out.println(dx+" "+dy+" "+psi);
		
		//ay=dnu/dt+u*r;
		
	
	}
	
	

              
       

	private void calculateAutomaticTorque() {
		
		
	   if(!isBraking)	
		    Fx2=Fx2Signum*torque_force*Math.exp(-0.15*Math.abs(u));
	   else if(isBraking)
		    Fx2=-brakingForce*Math.signum(u);
		
	}
	
	
	/**
	 * REAR LATERAL FORCE
	 * 
	 * @param dt
	 */

	private void calculateFy2(double dt) {
		
		if(Math.abs(u-r*t1*0.5)==0)
			alfa2=delta2-PI_2*Math.signum((nu-r*a2));
		else
			alfa2=delta2-Math.atan((nu-r*a2)/(u-r*t2*0.5));
		//double dFy2= (C2*alfa2-Fy2)*u/d;
		//Fy2+=dt*dFy2;
		Fy2= C2*alfa2*(1.0-Math.exp(-Math.abs(u)*dt/d));
		
	}
	
	
	
	/**
	 * FRONT LATERAL FORCE
	 * 
	 * @param dt
	 */
	private void calculateFy1(double dt) {
	
		if(Math.abs(u-r*t1*0.5)==0)
			alfa1=delta1-PI_2*Math.signum((nu+r*a1));
		else
			alfa1=delta1-Math.atan((nu+r*a1)/(u-r*t1*0.5));	
		
		//double dFy1= (C1*alfa1-Fy1)*u/d;
		//Fy1+=dt*dFy1;
		Fy1= C1*alfa1*(1.0-Math.exp(-Math.abs(u)*dt/d));
	}
	
	public String toString() {
		
		String str=" nu= "+nu+ " ,r= "+r+ " ,u= "+u+" ,alfa1= "+alfa1+" ,alfa2= "+alfa2;
		
		return str;
	}
	
	
	public String printForces() {
		
		String str=" Fy1= "+Fy1+ " ,Fy2= "+Fy2+ " ,Fx2= "+Fx2;
		
		return str;
	}
	
	public CarDynamics(){}
	
	public CarDynamics(double m,double a1, double a2,double t1,double t2, double tau, double chi ,double jz, double c1,
			double c2) {
		
		this.m=m;
		i_m=1.0/m;
		
		Jz = jz;
		i_Jz=1.0/Jz;
		
		this.a1 = a1;
		this.a2 = a2;
		l=a1+a2;
		this.tau = tau;
		this.chi = chi;
		C1 = c1;
		C2 = c2;
		this.t1=t1;
		this.t2=t2;
	}

	public void setInitPosition(double x0,double y0,double psi0){
	
		dx=x0;
		dy=y0;
		dpsi=psi0;
		psi=0;
		
	}
	
	public void setAerodynamics(double ro,double S,double Cx){
		
		this.ro=ro;
		this.S=S;
		this.Cx=Cx;
	}
	
	public void calculateCoordinatesIncrements(double dt){
		
		dpsi=r*dt;
		psi+=dpsi;
		double cosPsi=Math.cos(psi);
		double sinPsi=Math.sin(psi);
		
		dx=dt*(u*cosPsi-nu*sinPsi);
		dy=dt*(u*sinPsi+nu*cosPsi);
	}
	
	
	public void setInitvalues(double delta, double u, double r, double nu) {
		
		this.delta = delta;
		this.u = u;
		this.r = r;
		this.nu = nu;
		
		delta1=tau*delta;
		delta2=chi*tau*delta;
		
	}
	
	/**
	 * FRONT TRACTIVE FORCE
	 * 
	 * @return
	 */
	public double getFx2() {
		return Fx2;
	}

	/**
	 * REAR TRACTIVE FORCE
	 * 
	 * @return
	 */
	public void setFx2(double fx2) {
		Fx2 = fx2;
	}

	public void setFx2Versus(int i) {
		
		Fx2Signum=(byte) i;
		
	}
	
	public boolean isIsbreaking() {
		return isBraking;
	}

	public void setIsbraking(boolean isbreaking) {
		this.isBraking = isbreaking;
	}
	
	public double getTorque_force() {
		return torque_force;
	}
	
	public void setForces(double torque_force,double brakingForce){
		
		
		this.torque_force=torque_force;
		this.brakingForce=brakingForce;
		
	}

	public void setTorque_force(double torqueForce) {
		torque_force = torqueForce;
	}

	public double getBrakingForce() {
		return brakingForce;
	}

	public void setBrakingForce(double brakingForce) {
		this.brakingForce = brakingForce;
	}


}
