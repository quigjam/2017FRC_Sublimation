package org.usfirst.frc.team910.robot.Subsystems;

import org.usfirst.frc.team910.robot.IO.Angle;
import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;

public class DriveTrain {

	private static final double DRIVE_STRAIGHT_ENC_PWR = 0.1;
	private static final double DYN_BRAKE_PWR = 0.5; // full power in 10 inches
	private static final double DRIVE_STRAIGHT_NAVX_PWR = 0;
	private static final double AUTO_DRIVE_PWR = 0.2;
	private static final double SWERVE_FACTOR_ENC = 0.5;
	private static final double SWERVE_FACTOR_ANGLE = 0.01;
	private static final double ROTATE_MAX_PWR = 0.2;
	private static final double ROTATE_PWR_FACTOR = 0.005;
	private static final double CIRCLE_DRIVE_KV = 0; // Feed-Forward term for circleDrive

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

	public void run() {
		if (in.autoClimb || in.autoGear || in.autoShoot || in.autoHopper) {

		} else if (in.dynamicBrake) {
			dynamicBrake(prevTask != DriveFunction.DYNAMIC_BRAKING);
			prevTask = DriveFunction.DYNAMIC_BRAKING;
		} else if (in.driveStraight) {
			driveStraightNavX(prevTask != DriveFunction.DRIVE_STRAIGHT, in.rightJoyStickY, in.leftJoyStickX);
			prevTask = DriveFunction.DRIVE_STRAIGHT;
		} else {
			tankDrive(in.leftJoyStickY, in.rightJoyStickY);
			prevTask = DriveFunction.TANK_DRIVE;
		}
	}

	public void tankDrive(double leftPower, double rightPower) {
		double pwrAdj = Math.max(Math.abs(leftPower), Math.abs(rightPower));
		if (pwrAdj > 1) {
			leftPower /= pwrAdj;
			rightPower /= pwrAdj;
		}
		out.setLeftDrive(leftPower);
		out.setRightDrive(rightPower);
	}

	private double leftEncPrev;
	private double rightEncPrev;

	private void dynamicBrake(boolean firstTime) {
		double leftEncoder = out.leftDriveEncoder;
		double rightEncoder = out.rightDriveEncoder;

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
	public Angle originAngle = new Angle(0);

	public void driveStraightNavX(boolean firstTime, double power, double swerve) {
		Angle navxangle = sense.robotAngle;

		if (firstTime) {
			originAngle.set(navxangle.get());
		} else {
			originAngle.add(swerve * SWERVE_FACTOR_ANGLE);
			double angledifference = originAngle.subtract(navxangle);
			double powerDiff = angledifference * DRIVE_STRAIGHT_NAVX_PWR;

			tankDrive(power - powerDiff, power + powerDiff);
		}
	}

	// Drive in a circle using NavX
	Angle circleTargetAngle = new Angle(0);

	public void driveCircle(Angle startAngle, double distance, double radius, double velocity, double direction) {
		double K = 360 / (2 * Math.PI * radius);
		circleTargetAngle.set(startAngle.get() + direction * K * distance + CIRCLE_DRIVE_KV * K * velocity);
		double angleError = circleTargetAngle.subtract(sense.robotAngle);
		double correctionPwr = angleError * DRIVE_STRAIGHT_NAVX_PWR;
		tankDrive(AUTO_DRIVE_PWR - correctionPwr, AUTO_DRIVE_PWR + correctionPwr);
	}

	public void rotate(Angle target) { // allows robot to rotate to a desired angle
		double adjAngle = target.subtract(sense.robotAngle);
		double power = ROTATE_PWR_FACTOR * adjAngle;
		power = Math.max(Math.min(power, ROTATE_MAX_PWR), -ROTATE_MAX_PWR);
		tankDrive(-power, power);

	}
}
