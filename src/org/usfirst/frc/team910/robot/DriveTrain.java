package org.usfirst.frc.team910.robot;

public class DriveTrain {

	private static final double DRIVE_STRAIGHT_ENC_PWR = 0.1;
	private static final double DYN_BRAKE_PWR = 0.1; // full power in 10 inches
	private static final double DRIVE_STRAIGHT_NAVX_PWR = 0;
	private static final double AUTO_DRIVE_PWR = 0.2;
	private static final double SWERVE_FACTOR_ENC = 0.01;
	private static final double SWERVE_FACTOR_ANGLE = 0.01;

	private Inputs in;
	private Outputs out;
	private Sensors sense;

	public DriveTrain(Inputs in, Outputs out, Sensors sense) {
		this.in = in;
		this.out = out;
		this.sense = sense;
	}
																// sample text
	// this is the main function called from robot

	private enum DriveFunction {
		DYNAMIC_BRAKING, TANK_DRIVE, DRIVE_STRAIGHT
	}; 	
	private DriveFunction prevTask = DriveFunction.TANK_DRIVE;
	
	public void drive() {
		if (in.dynamicBrake) {
			dynamicBrake(prevTask!= DriveFunction.DYNAMIC_BRAKING);
			prevTask = DriveFunction.DYNAMIC_BRAKING;
		} 
		else if (in.driveStraight){
			driveStraightNavX(prevTask!= DriveFunction.DRIVE_STRAIGHT);
				prevTask = DriveFunction.DRIVE_STRAIGHT;
		}
		else {
			tankDrive(in.leftJoyStickY, in.rightJoyStickY);
			prevTask = DriveFunction.TANK_DRIVE;
		}
		
	}

	public void tankDrive(double leftPower, double rightPower) {
		out.setLeftDrive(leftPower);
		out.setRightDrive(rightPower);
	}

	private double leftEncPrev;
	private double rightEncPrev;

	private void dynamicBrake(boolean firstTime) {
		double leftEncoder = sense.leftEncoder;// Sense is short for sensors
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

	private double initialEncDiff;

	private void driveStraightEnc(boolean firstTime) {

		if (firstTime) {
			initialEncDiff = sense.leftEncoder - sense.rightEncoder;
		} else {
			initialEncDiff += in.leftJoyStickX * SWERVE_FACTOR_ENC;
			double currentEncDiff = sense.leftEncoder - sense.rightEncoder;
			double diffDiff = (initialEncDiff - currentEncDiff) * DRIVE_STRAIGHT_ENC_PWR;

			tankDrive(in.rightJoyStickY + diffDiff, in.rightJoyStickY - diffDiff);
		}

	}

	// Drive Straight With NavX
	double originangle = 0;

	public void driveStraightNavX(boolean firstTime) {
		double navxangle = sense.robotAngle;

		if (firstTime) {
			originangle = navxangle;
		} else {
			originangle += in.leftJoyStickX * SWERVE_FACTOR_ANGLE;
			double currentangle = navxangle;
			double angledifference = originangle - currentangle;
			double refineddiff = angledifference * DRIVE_STRAIGHT_NAVX_PWR;

			tankDrive(in.rightJoyStickY - refineddiff, in.rightJoyStickY + refineddiff);
		}
	}

	// Drive in a circle using NavX
	public void driveCircle(double startAngle, double distance, double radius, double velocity, double direction) {
		double K = 360 / 2 * Math.PI * radius;
		double targetAngle = startAngle + direction * K * distance;
		double angleError = targetAngle - sense.robotAngle;
		double correctionPwr = angleError * DRIVE_STRAIGHT_NAVX_PWR;
		tankDrive(AUTO_DRIVE_PWR - correctionPwr, AUTO_DRIVE_PWR + correctionPwr);
	}
}
