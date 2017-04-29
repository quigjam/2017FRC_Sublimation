package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;

public class AutonAutoShoot extends AutonStep {

	private double time;
	private double endTime;
	private boolean slowAgi;
	
	AutonAutoShoot(double shootTime, boolean slowAgi){
		time = shootTime;
		endTime = 0;
		this.slowAgi = slowAgi;
	}
	
	public void setup(boolean blueAlliance){
		endTime = 0;	//added by Mr C 3/30/17		
	}
	
	public void run(){
		in.autoShoot = true;
		autoShoot.run(slowAgi);
		
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
