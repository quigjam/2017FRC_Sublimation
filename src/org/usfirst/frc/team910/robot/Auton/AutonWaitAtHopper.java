package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;

public class AutonWaitAtHopper extends AutonStep{

	private double time;
	private double endTime;
	private double AGI_PWR = -0.3;
	
	AutonWaitAtHopper(double time){
		this.time = time;
		endTime = 0;
	}
	
	public void setup(boolean blueAlliance){
		endTime = Timer.getFPGATimestamp() + time;
	}
	
	public void run(){
		boolean applyPower = Math.floor(Timer.getFPGATimestamp() * 1.5) % 2 == 0;
		if(applyPower) { 
			//drive.tankDrive(0.7, 0.7, 0.7);
		} else {
			drive.tankDrive(0, 0, 1);
		}
		
		if(Timer.getFPGATimestamp() < endTime - time / 2) {
			shoot.agitate(AGI_PWR);
		} else {
			shoot.agitate(0);
		}
	}
	
	public boolean isDone(){
		if(Timer.getFPGATimestamp() > endTime){
			drive.tankDrive(0, 0, 1);
			return true;
		} else {
			return false;
		}
	}
	
}
