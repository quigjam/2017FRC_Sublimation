package org.usfirst.frc.team910.robot.Auton;

public class AutonMain {
	
	AutonStep steps[];
	int currentStep = 0;
	
	public void init(){
		
	}
	public void run(){
		steps[currentStep].run();
		if(steps[currentStep].isDone()){
			currentStep ++;
			steps[currentStep].setup();
		}
	}
}