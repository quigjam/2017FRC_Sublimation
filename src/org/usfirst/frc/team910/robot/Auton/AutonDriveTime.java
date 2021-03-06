package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;

public class AutonDriveTime extends AutonStep {
	
	private static final double POWER_FILT = 0.15;
	
	private double power;
	private double time;
	private double angle;
	private boolean prime;
	
	private double endTime;
	private double prevPower;
	
	public AutonDriveTime(double power, double time, double angle, boolean prime){
		this.power = power;
		this.time = time;
		this.angle = angle;
		this.prime = prime;
		prevPower = 0;
	}
	
	public void setup(boolean blueAlliance) {
		endTime = Timer.getFPGATimestamp() + time;
	}
	
	public void run() {
		prevPower += POWER_FILT * (power - prevPower);
		drive.originAngle.set(angle);
		drive.driveStraightNavX(false, prevPower, 0);
		
		if(prime){
			shoot.shooterPrime(true, false, 36, false, false);
		} else {
			shoot.shooterPrime(false, false, 36, false, false);
		}
	}
	
	public boolean isDone() {
		return Timer.getFPGATimestamp() > endTime;
	}
}
