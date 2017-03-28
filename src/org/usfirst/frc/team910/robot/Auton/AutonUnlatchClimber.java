package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;

public class AutonUnlatchClimber extends AutonStep {

	double runTime;
	double endTime;
	
	public AutonUnlatchClimber(double runTime){
		this.runTime = runTime;
	}
	
	public void setup(){
		endTime = Timer.getFPGATimestamp() + runTime;
	}
	
	public void run(){
		if(Timer.getFPGATimestamp() > endTime){
			in.climbButton = false;
		} else {
			in.climbButton = true;
		}
		climb.run();
	}
	
	public boolean isDone(){
		return Timer.getFPGATimestamp() > endTime;
	}
	
}
