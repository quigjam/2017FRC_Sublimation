package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonDriveStraight extends AutonStep {

	private static final double V_MAX = 100.0;
	private static final double ACCEL = 60.0;
	private static final double PCONST = 0.15*0.7;
	private static final double ALLOWABLE_ERROR = 0.5;
	private static final double V_CONST = 1/200;
	
	
	double distance;
	double stopDistance;
	double startDistance;
	double startTime;
	double halfDistance;
	double startDeccelTime;
	double maxPower;
	double angle;
	double currVel;
	double x;
	double stopTime;

	
	public AutonDriveStraight(double distance, double maxPower, double angle) {
		this.distance = distance;
		this.maxPower = maxPower;
		this.angle = angle;
	}

	@Override
	public void setup() {
		startDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		stopDistance = startDistance + distance; 
		halfDistance = distance / 2 + startDistance;
		x = 0;
		currVel = 0;
		startTime = Timer.getFPGATimestamp();
		
		startDeccelTime = Math.sqrt(distance/ACCEL);
		stopTime = startDeccelTime*2;
		if (ACCEL*startDeccelTime > V_MAX){
			double accelTime = V_MAX / ACCEL;
			double accelDist = (V_MAX/2)*accelTime;
			double vMaxDist = (halfDistance - accelDist);
			double vMaxTime = vMaxDist / V_MAX; 
			
			startDeccelTime = accelTime + vMaxTime*2;
			stopTime = startDeccelTime + accelTime;
		}
		
		stopTime += startTime;
	}

	@Override
	public void run() { 
		double currentDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		double accel = ACCEL;
		
		if (Timer.getFPGATimestamp() > startTime + startDeccelTime){
		//if (currentDistance > halfDistance) {
			accel = -ACCEL;
		} else if(currVel == V_MAX) {
			accel = 0;
		}
		
		//if we have finished the ramping dont drive backwards 
		if(Math.abs(x) >= Math.abs(distance) - ALLOWABLE_ERROR || Timer.getFPGATimestamp() > stopTime){
			//add to I term here if we need to
		} else {
			x = x + currVel*sense.deltaTime + 0.5*accel*sense.deltaTime*sense.deltaTime;
			currVel = currVel + accel*sense.deltaTime;
			if(currVel > V_MAX) currVel = V_MAX;
		}
		
		double distError = startDistance + x - currentDistance;
		double power = PCONST * distError + currVel * V_CONST;
		
		SmartDashboard.putNumber("autonPower", power);
		drive.driveStraightNavX(false, power, 0);
		
		//TODO Maybe add jerk later
	}
		

	@Override
	public boolean isDone() {
		double avgEncDist = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		return avgEncDist >= stopDistance - ALLOWABLE_ERROR;
	}
	
}
