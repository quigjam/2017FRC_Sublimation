package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;

public class AutonGearDeploy extends AutonStep {

	private double startTime;
	
	public AutonGearDeploy() {
		
	}
	
	public void setup(boolean blueAlliance){
		startTime = Timer.getFPGATimestamp();
	}
	
	public void run(){
		if(startTime + 1 < Timer.getFPGATimestamp()){
			in.gearOuttake = true;
			in.gearPanelPosition = 1;
			gear.run();
			drive.driveStraightNavX(true, 0, 0); //capture the current angle
		} else if (startTime + 1.5 < Timer.getFPGATimestamp()){
			drive.driveStraightNavX(false, -0.35, 0); //backup
		} else if(startTime + 2 < Timer.getFPGATimestamp()){
			drive.driveStraightNavX(false, 0.5, 0); //drive forward to put the gear on
			in.gearPanelPosition = 2;
			gear.run();
		} else if(startTime + 2.5 < Timer.getFPGATimestamp()){
			in.gearOuttake = false; //turn off the gear roller
			gear.run();
		}
	}
	
	public boolean isDone(){
		return Timer.getFPGATimestamp() < startTime + 2.75;
	}
}
