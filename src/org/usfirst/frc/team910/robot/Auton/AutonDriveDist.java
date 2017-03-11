package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonDriveDist extends AutonStep{
	
	private static final double ALLOWABLE_ERROR = 0.5;
	private static final double RAMP_UP_CONST = 1;
	private static final double RAMP_DN_CONST = 0.02;
	private static final double PCONST = 0;
	private static final double MIN_RAMP = 0.5;
	private double distance;
	private double maxPower;
	private double angle;
	
	private double startDistance;
	private double stopDistance;
	private double halfDistance;
	private double startTime;
	
	public AutonDriveDist(double distance, double maxPower, double angle) {
		this.distance = distance;
		this.maxPower = maxPower;
		this.angle = angle;
	}

	@Override
	public void setup() {
		startDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		stopDistance = startDistance + distance;
		halfDistance = distance / 2 + startDistance;

	}

	@Override
	public void run() {
		double currentDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		double currentTime = Timer.getFPGATimestamp();
		double ramp;
		
		if (currentDistance < halfDistance) {
			ramp = (currentTime - startTime) * RAMP_UP_CONST;
		} else {
			ramp = (stopDistance - currentDistance) * RAMP_DN_CONST;
		}
		ramp = Math.min(Math.max(ramp, MIN_RAMP), maxPower);
		
		double distError = currentDistance - startDistance;
		double power = PCONST * distError;
		
		if(Math.abs(power) > ramp){
			power = power/Math.abs(power) * ramp;
		}

		drive.originAngle.set(angle);
		SmartDashboard.putNumber("autonPower", power);
		drive.driveStraightNavX(false, power, 0);

	}

	@Override
	public boolean isDone() {
		double avgEncDist = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		return avgEncDist >= stopDistance - ALLOWABLE_ERROR;
	}
}
