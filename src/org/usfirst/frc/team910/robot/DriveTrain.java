package org.usfirst.frc.team910.robot;

public class DriveTrain {

	private static final double DYN_BRAKE_PWR = 0.1; //full power in 10 inches 
	private static final double DRIVE_STAIGHT_NAVX_PWR = 0;
	
	private Inputs in;
	private Outputs out;
	private Sensors sense;
	
	public DriveTrain(Inputs in, Outputs out, Sensors sense) {
		this.in = in;
		this.out = out;
		this.sense = sense;
	}

	// this is the main function called from robot
	boolean braking;  
	public void drive() {	
		if(in.leftTrigger) {
			dynamicBrake(!braking);
			braking = true;
		}
		else { 
			tankDrive(in.leftJoyStickY, in.rightJoyStickY);
			braking = false;
		}
	}

	private void tankDrive(double leftPower, double rightPower) {
		out.setLeftDrive(leftPower);
		out.setRightDrive(rightPower);
	}

	private double leftEncPrev;
	private double rightEncPrev;

	private void dynamicBrake(boolean firstTime) {
		double leftEncoder = sense.leftEncoder;
		double rightEncoder = sense.rightEncoder;

		if (firstTime) {
			leftEncPrev = leftEncoder;
			rightEncPrev = rightEncoder;
		} else {
			double leftEncDiff = leftEncPrev - leftEncoder;
			double rightEncDiff = rightEncPrev - rightEncoder;

			tankDrive(leftEncDiff * DYN_BRAKE_PWR, rightEncDiff * DYN_BRAKE_PWR);
		}

	}
	
	double originangle = 0;
	
	private void driveStraigthNavx(boolean firstTime) {
		double navxangle= sense.robotAngle;
		
		if(firstTime) {
			originangle = navxangle;
		} else {
			double currentangle = navxangle;
			double angledifference = originangle - currentangle;
			double refineddiff = angledifference * DRIVE_STAIGHT_NAVX_PWR;
			
			tankDrive(in.rightJoyStickY - refineddiff,in.rightJoyStickY + refineddiff);
		}
	}
}