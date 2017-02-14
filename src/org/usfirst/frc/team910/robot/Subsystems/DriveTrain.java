package org.usfirst.frc.team910.robot.Subsystems;

import org.usfirst.frc.team910.robot.IO.Angle;
import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;

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
		if (in.autoClimb || in.autoGear || in.autoShoot) {

		} else if (in.dynamicBrake) {
			dynamicBrake(prevTask != DriveFunction.DYNAMIC_BRAKING);
		} else if (in.driveStraight) {
			driveStraightNavX(prevTask != DriveFunction.DRIVE_STRAIGHT);
		} else {
			tankDrive(in.leftJoyStickY, in.rightJoyStickY);
		}
	}

	public void tankDrive(double leftPower, double rightPower) {
		out.setLeftDrive(leftPower);
		out.setRightDrive(rightPower);
		prevTask = DriveFunction.TANK_DRIVE;
	}

	private double leftEncPrev;
	private double rightEncPrev;

	private void dynamicBrake(boolean firstTime) {
		double leftEncoder = out.leftDriveEncoder;
		double rightEncoder = out.rightDriveEncoder;
		prevTask = DriveFunction.DYNAMIC_BRAKING;

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
			initialEncDiff = out.leftDriveEncoder - out.rightDriveEncoder;
		} else {
			initialEncDiff += in.leftJoyStickX * SWERVE_FACTOR_ENC;
			double currentEncDiff = out.leftDriveEncoder - out.rightDriveEncoder;
			double diffDiff = (initialEncDiff - currentEncDiff) * DRIVE_STRAIGHT_ENC_PWR;

			tankDrive(in.rightJoyStickY + diffDiff, in.rightJoyStickY - diffDiff);
		}

	}

	// Drive Straight With NavX
	public Angle originangle = new Angle(0);

	public void driveStraightNavX(boolean firstTime) {
		Angle navxangle = sense.robotAngle;
		prevTask = DriveFunction.DRIVE_STRAIGHT;

		if (firstTime) {
			originangle.set(navxangle.get());
		} else {
			originangle.add(in.leftJoyStickX * SWERVE_FACTOR_ANGLE);
			double angledifference = originangle.subtract(navxangle);
			double powerDiff = angledifference * DRIVE_STRAIGHT_NAVX_PWR;

			tankDrive(in.rightJoyStickY - powerDiff, in.rightJoyStickY + powerDiff);
		}
	}

	// Drive in a circle using NavX
	Angle circleTargetAngle = new Angle(0);
	public void driveCircle(Angle startAngle, double distance, double radius, double velocity, double direction) {
		double K = 360 / 2 * Math.PI * radius;
		circleTargetAngle.set(startAngle.get() + direction * K * distance);
		double angleError = circleTargetAngle.subtract(sense.robotAngle); 
		double correctionPwr = angleError * DRIVE_STRAIGHT_NAVX_PWR;
		tankDrive(AUTO_DRIVE_PWR - correctionPwr, AUTO_DRIVE_PWR + correctionPwr);
	}
}
