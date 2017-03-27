package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;

public class AutonDriveTimeClimb extends AutonStep {
	
	private static final double POWER_FILT = 0.1;
	
	private double power;
	private double time;
	private double angle;
	
	private double endTime;
	private double prevPower;
	
	public AutonDriveTimeClimb(double power, double time, double angle){
		this.power = power;
		this.time = time;
		this.angle = angle;
		prevPower = 0;
	}
	
	public void setup(boolean blueAlliance) {
		endTime = Timer.getFPGATimestamp() + time;
	}
	
	public void run() {
		prevPower += POWER_FILT * (power - prevPower);
		drive.originAngle.set(angle);
		drive.driveStraightNavX(false, prevPower, 0);
		in.climbButton = true;
		climb.run();
	}
	
	public boolean isDone() {
		if(Timer.getFPGATimestamp() > endTime){
			in.climbButton = false;
			climb.run();
			return true;
		}
		return false;
	}
}
