package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;

public class AutonWait extends AutonStep{
	
	private double waitTime;
	private double endTime;
	
	public AutonWait(double time){
		waitTime = time;
	}
	
	public void setup(boolean blueAlliance){
		endTime = Timer.getFPGATimestamp() + waitTime;
	}
	
	public void run(){
		drive.tankDrive(0, 0, 1);
	}
	
	public boolean isDone(){
		return Timer.getFPGATimestamp() > endTime;
	}
	
}
