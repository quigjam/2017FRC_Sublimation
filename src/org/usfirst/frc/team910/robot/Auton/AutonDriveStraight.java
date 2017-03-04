package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonDriveStraight extends AutonStep {

	double distance;
	double stopDistance;
	double startDistance; 
	double halfDistance; 
	double power;
	double angle;
	double startTime;
	double rampEndTime;
	double rampEndDist; 
	double flatEnd; 
	double currentPower; 
	double rampDownStart; 
	
	private static final double RAMP_RATE = 0.5; //power per second 
	private static final double RAMP_DOWN_OFFSET = 0.1; 
	
	public AutonDriveStraight(double distance, double speed, double angle) {
		this.distance = distance;
		this.power = speed;
		this.angle = angle;
	}

	@Override
	public void setup() {
		startDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		stopDistance = startDistance + distance; 
		halfDistance = distance / 2 + startDistance; 
		startTime = Timer.getFPGATimestamp(); 
		currentPower = 0; 
		rampEndTime = 0;
		flatEnd = 0; 
		rampEndDist = 0; 
		rampDownStart = 0; 
	}

	@Override
	public void run() { 
		double currentDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		double currentTime = Timer.getFPGATimestamp();
		double elapsedTime = currentTime - startTime;
		if(currentDistance >= halfDistance) { // hold then ramp down
			if(flatEnd == 0) { // first time second half reached
				flatEnd = currentTime - startTime; 
			}
			if(currentDistance >= halfDistance + distance/2 - rampEndDist || elapsedTime >= flatEnd * 2 - rampEndTime) { // ramp down
				if(rampDownStart == 0) {
					rampDownStart = currentTime; 
				} 
				currentPower = power - (currentTime - rampDownStart) * RAMP_RATE - RAMP_DOWN_OFFSET; 
			} else { 
				currentPower = power; 
			}
		} else { // ramp up and hold 
			currentPower = elapsedTime * RAMP_RATE; 
			if(currentPower >= power) { // holding peak power
				currentPower = power; 
				if(rampEndTime == 0) { // first time peak power reached
					rampEndTime = elapsedTime;
					rampEndDist = currentDistance - startDistance; 
				}
			}
		}
		drive.originAngle.set(angle);
		drive.driveStraightNavX(false, currentPower, 0);
		
	}

	@Override
	public boolean isDone() {
		double avgEncDist = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		return avgEncDist >= distance;
	}
	
}
