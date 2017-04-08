package org.usfirst.frc.team910.robot.Auton;

public class AutonPrime extends AutonStep{

	public AutonPrime(){
		
	}
	
	public void setup(){
		
	}
	
	public void run(){
		//in.primeButton = true;
		shoot.justPrime(true);
	}
	
	public boolean isDone(){
		return true;
	}
	
}
