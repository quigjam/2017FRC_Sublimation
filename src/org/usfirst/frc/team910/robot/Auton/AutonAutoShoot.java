package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;

public class AutonAutoShoot extends AutonStep {

	private double time;
	private double endTime;
	
	AutonAutoShoot(double shootTime){
		time = shootTime;
		endTime = 0;
	}
	
	public void setup(boolean blueAlliance){
		
	}
	
	public void run(){
		in.autoShoot = true;
		autoShoot.run();
		
		if(autoShoot.isCamAlign()){
			drive.tankDrive(0, 0, 1);
		}
	}
	
	public boolean isDone(){
		if(autoShoot.isShooting()){
			if (endTime == 0){
				endTime = Timer.getFPGATimestamp() + time;
			}
			return Timer.getFPGATimestamp() > endTime;
		} else {
			return false;
		}
	}
	
}
