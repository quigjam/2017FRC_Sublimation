package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;

public class AutonUnlatchClimber extends AutonStep {

	double runTime;
	double endTime;
	
	public AutonUnlatchClimber(double runTime){
		this.runTime = runTime;
	}
	
	public void setup(boolean blueAlliance){
		endTime = Timer.getFPGATimestamp() + runTime;
		//climb.climb(0.6);
	}
	
	public void run(){
		//gear.setPanel();
		in.gearPanelPosition = 4;
		in.manualMode = false;
		gear.run();
		//gear.setPanel();
		if(Timer.getFPGATimestamp() > endTime){
			//in.climbButton = false;
			//climb.climb(0);
		} else {
			//in.climbButton = true;
			//climb.climb(0.6);
		}
		//climb.climb(0.6);
		//climb.run();
	}
	
	
	public boolean isDone(){
		if (Timer.getFPGATimestamp() > endTime) {
		    climb.climb(0);//climb.run();
		    return true;
		} else {
			climb.climb(0.6);
			return false;
		}
		///return Timer.getFPGATimestamp() > endTime;
	}
	
}
