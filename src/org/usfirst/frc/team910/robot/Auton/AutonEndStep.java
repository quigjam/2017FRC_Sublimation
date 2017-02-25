package org.usfirst.frc.team910.robot.Auton;

public class AutonEndStep extends AutonStep {

	@Override
	public void run(){
		drive.tankDrive(0, 0);
		shoot.shooterPrime(false);
		shoot.shooterFire(false);
		gear.gearRoller(0);
	}
	
	@Override
	public boolean isDone(){
		return false;
	}

}
