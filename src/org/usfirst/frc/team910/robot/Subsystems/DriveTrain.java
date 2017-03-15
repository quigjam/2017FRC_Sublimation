package org.usfirst.frc.team910.robot.Subsystems;

import org.usfirst.frc.team910.robot.IO.Angle;
import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.IO.Util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {

	private static final double DRIVE_STRAIGHT_ENC_PWR = 0.1;
	private static final double DYN_BRAKE_PWR = 0.5; // full power in 10 inches
	private static final double DRIVE_STRAIGHT_NAVX_PWR = 0.04;
	private static final double DRIVE_CIRCLE_PWR = 0.05;
	private static final double[] SWERVE_FACTOR_ENC = {2, 2, 0.5, 0.5};// in/0.1sec
	private static final double[] SWERVE_DRIVE_SPEED_AXIS = {11, 12, 100, 101};// in/sec
	private static final double SWERVE_FACTOR_ANGLE = 0.15;
	private static final double ROTATE_MAX_PWR = 0.4;
	private static final double ROTATE_PWR_FACTOR = 0.1;
	private static final double CIRCLE_DRIVE_KV = 0.002; // Feed-Forward term for circleDrive

	private Inputs in;
	private Outputs out;
	private Sensors sense;

	public double leftDriveEncoder;
	public double rightDriveEncoder;

	public DriveTrain(Inputs in, Outputs out, Sensors sense) {
		this.in = in;
		this.out = out;
		this.sense = sense;
	}
	// sample text
	// this is the main function called from robot

	private enum DriveFunction {
		DYNAMIC_BRAKING, TANK_DRIVE, DRIVE_STRAIGHT, AUTO_STRAIGHT
	};

	private DriveFunction prevTask = DriveFunction.TANK_DRIVE;

	public void run(boolean auton) {
		leftDriveEncoder = out.leftDriveEncoder;
		rightDriveEncoder = out.rightDriveEncoder;

		if (in.autoClimb || in.autoGear || in.autoShoot || in.autoHopper || auton) {
			
		} else if (in.dynamicBrake) {	
			setBrakes(false);
			dynamicBrake(prevTask != DriveFunction.DYNAMIC_BRAKING);
			prevTask = DriveFunction.DYNAMIC_BRAKING;
		} else if (in.driveStraight) {
			setBrakes(false);
			driveStraightEnc(prevTask != DriveFunction.DRIVE_STRAIGHT, in.rightJoyStickY, in.leftJoyStickX);
			prevTask = DriveFunction.DRIVE_STRAIGHT;
		} else if (in.autoStraight) {
			setBrakes(false);
			autoStraight(prevTask != DriveFunction.AUTO_STRAIGHT, in.leftJoyStickY, in.rightJoyStickY);
			prevTask = DriveFunction.AUTO_STRAIGHT;
		} else {
			setBrakes(false);
			tankDrive(in.leftJoyStickY, in.rightJoyStickY, 1);
			prevTask = DriveFunction.TANK_DRIVE;
		}
	}

	public void tankDrive(double leftPower, double rightPower, double powerLimit) {
		double pwrAdj = Math.max(Math.abs(leftPower), Math.abs(rightPower));
		SmartDashboard.putNumber("lDrivePower", leftPower);
		SmartDashboard.putNumber("rDrivePower", rightPower);
		if (pwrAdj > powerLimit) {
			leftPower /= pwrAdj;
			leftPower *= powerLimit;
			rightPower /= pwrAdj;
			rightPower *= powerLimit;
		}
		SmartDashboard.putNumber("leftDrivePower", leftPower);
		SmartDashboard.putNumber("rightDrivePower", rightPower);
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

			tankDrive(leftEncDiff * DYN_BRAKE_PWR, rightEncDiff * DYN_BRAKE_PWR, 0.8);
		}

	}

	private double initialEncDiff;
	private double prevEncVal = 0;
	
	// TODO: use new inputs in driveStraightEnc
	private void driveStraightEnc(boolean firstTime, double power, double swerve) {

		if (firstTime) {
			initialEncDiff = out.leftDriveEncoder - out.rightDriveEncoder;
			prevEncVal = (Math.abs(out.leftDriveEncoder) + Math.abs(out.rightDriveEncoder)) / 2;
		} else {
			double encAvg = (Math.abs(out.leftDriveEncoder) + Math.abs(out.rightDriveEncoder)) / 2;
			double speed = (encAvg - prevEncVal) / sense.deltaTime;
			double prevEncVal = encAvg;
			initialEncDiff += in.leftJoyStickX * Util.interpolate(SWERVE_DRIVE_SPEED_AXIS,SWERVE_FACTOR_ENC,speed);
			double currentEncDiff = out.leftDriveEncoder - out.rightDriveEncoder;
			double diffDiff = (initialEncDiff - currentEncDiff) * DRIVE_STRAIGHT_ENC_PWR;

			tankDrive(in.rightJoyStickY + diffDiff, in.rightJoyStickY - diffDiff, Math.max(0.3, Math.abs(in.rightJoyStickY)));
		}

	}

	// Drive Straight With NavX
	public Angle originAngle = new Angle(0);

	public void driveStraightNavX(boolean firstTime, double power, double swerve) {
		Angle navxangle = sense.robotAngle;

		if (firstTime) {
			originAngle.set(navxangle.get());
		} else {
			originAngle.add(swerve * -SWERVE_FACTOR_ANGLE);
			double angledifference = originAngle.subtract(navxangle);
			double powerDiff = angledifference * DRIVE_STRAIGHT_NAVX_PWR;

			double driveStraightPower = Math.max(0.3, Math.abs(power));
			SmartDashboard.putNumber("dsPower", driveStraightPower);
			tankDrive(power - powerDiff, power + powerDiff, driveStraightPower);
		}
		SmartDashboard.putNumber("DriveStraightOriginAngle", originAngle.get());
	}

	// Drive in a circle using NavX
	Angle circleTargetAngle = new Angle(0);

	public void driveCircle(double power, Angle startAngle, double distance, double radius, double velocity,
			double direction) {
		double K = 360 / (2 * Math.PI * radius);
		circleTargetAngle.set(startAngle.get() + direction * K * distance + CIRCLE_DRIVE_KV * K * velocity * direction);
		SmartDashboard.putNumber("Circle Target Angle", circleTargetAngle.get());
		double angleError = circleTargetAngle.subtract(sense.robotAngle);
		double correctionPwr = angleError * DRIVE_CIRCLE_PWR;
		tankDrive(power - correctionPwr, power + correctionPwr, power);
	}

	public void rotate(Angle target) { // allows robot to rotate to a desired angle
		double adjAngle = target.subtract(sense.robotAngle);
		//double adjAngle = sense.robotAngle.subtract(target);
		SmartDashboard.putNumber("rotateAngle", adjAngle);
		double power = ROTATE_PWR_FACTOR * adjAngle;
		
		if(Math.abs(power) > ROTATE_MAX_PWR){
			power = power / Math.abs(power) * ROTATE_MAX_PWR;
		}
		SmartDashboard.putNumber("rotatePower", power);
		tankDrive(-power, power, Math.abs(power));

	}

	boolean autoStraightFirstTime = true;

	// If both joysticks are pushed really far up, activate driveStraight
	public void autoStraight(boolean firstTime, double leftStickY, double rightStickY) {
		autoStraightFirstTime = autoStraightFirstTime || firstTime;

		if (leftStickY > 0.95 && rightStickY > 0.95) {
			driveStraightNavX(autoStraightFirstTime, 1, 0);
			autoStraightFirstTime = false;
		} else {
			autoStraightFirstTime = true;
		}
	}
	
	public void setBrakes(boolean brakes){
		out.setDriveBrake(brakes);
	}
}
