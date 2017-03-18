package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;

public class AutonGearDeploy extends AutonStep {

	private double startTime;
	
	public AutonGearDeploy() {
		
	}
	
	public void setup(){
		startTime = Timer.getFPGATimestamp();
	}
	
	public void run(){
		if(startTime + 1 < Timer.getFPGATimestamp()){
			in.gearOuttake = true;
			in.gearPanelPosition = 1;
			gear.run();
			drive.driveStraightNavX(true, 0, 0);
		} else if (startTime + 1.5 < Timer.getFPGATimestamp()){
			drive.driveStraightNavX(false, -0.35, 0);
		} else if(startTime + 2 < Timer.getFPGATimestamp()){
			drive.driveStraightNavX(false, 0.5, 0);
			in.gearPanelPosition = 2;
			gear.run();
		} else if(startTime + 2.75 < Timer.getFPGATimestamp()){
			drive.driveStraightNavX(false, -0.7, 0);
		} else {
			drive.tankDrive(0, 0, 1);
		}
	}
	
	public boolean isDone(){
		return false;
	}
}