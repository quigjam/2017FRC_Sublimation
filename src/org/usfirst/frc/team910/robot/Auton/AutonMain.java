package org.usfirst.frc.team910.robot.Auton;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.GearSystem;
import org.usfirst.frc.team910.robot.Subsystems.Shooter;

public class AutonMain {
	
	AutonStep steps[];
	int currentStep = 0;
	
	public void init(Inputs in, Sensors sense, DriveTrain drive, GearSystem gear, Shooter shoot){
		AutonStep.setRobotReferences(in, sense, drive, gear, shoot);
	}
	
	public void run(){
		steps[currentStep].run();          //institutes a state machine as an array of autons, works similarly to a "switch" 
		if(steps[currentStep].isDone()){
			currentStep ++;
			steps[currentStep].setup();
		}
	}
}
