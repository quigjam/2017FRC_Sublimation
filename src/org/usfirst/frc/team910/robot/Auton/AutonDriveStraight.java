package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonDriveStraight extends AutonStep {

	private static final double V_MAX = 10.0;
	private static final double ACCEL = 3.0;
	private static final double PCONST = 0.15;
	
	
	
	double distance;
	double stopDistance;
	double startDistance;
	double startTime;
	double halfDistance;
	double startDeccelTime;
	double power;
	double angle;
	double currVel;
	double x;

	
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
		x = 0;
		currVel = 0;
		startTime = Timer.getFPGATimestamp();
		
		startDeccelTime = Math.sqrt(distance/2*ACCEL);
		if (ACCEL*startDeccelTime > V_MAX){
			double accelTime = V_MAX / ACCEL;
			double accelDist = (V_MAX/2)*accelTime;
			double vMaxDist = (halfDistance - accelDist);
			double vMaxTime = vMaxDist / V_MAX; 
			
			startDeccelTime = accelTime + vMaxTime*2;
		}
	}

	@Override
	public void run() { 
		double currentDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		double accel = ACCEL;
		if (Timer.getFPGATimestamp() > startTime+startDeccelTime){
			accel = -ACCEL;
		} else if(currVel == V_MAX) {
			accel = 0;
		}
		x = x + currVel*sense.deltaTime+0.5*accel*sense.deltaTime*sense.deltaTime;
		currVel = currVel + accel*sense.deltaTime;
		if(currVel > V_MAX) currVel = V_MAX;
		
		double distError = currentDistance - startDistance - x;
		power = PCONST * distError;
		SmartDashboard.putNumber("autonPower", power);
		drive.driveStraightNavX(false, power, 0);
		
		//TODO Maybe add jerk later
	}
		

	@Override
	public boolean isDone() {
		double avgEncDist = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		return avgEncDist >= stopDistance;
	}
	
}
