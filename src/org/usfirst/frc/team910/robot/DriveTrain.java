package org.usfirst.frc.team910.robot;

public class DriveTrain {

	private static final double DRIVE_STRAIGHT_ENC_PWR = 0.1;
	private static final double DYN_BRAKE_PWR = 0.1; // full power in 10 inches
	private static final double DRIVE_STRAIGHT_NAVX_PWR = 0;
	private static final double WALL_ACCEL = 0;
	private static final double AUTO_DRIVE_PWR = 0.2;

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
		if (in.leftTrigger) {
			dynamicBrake(!braking);
			braking = true;
		} else {
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
			double currentEncDiff = sense.leftEncoder - sense.rightEncoder;
			double DiffDiff = (initialEncDiff - currentEncDiff) * DRIVE_STRAIGHT_ENC_PWR;

			tankDrive(in.rightJoyStickY + DiffDiff, in.rightJoyStickY - DiffDiff);
		}

	}

	// Drive Straight With NavX
	double originangle = 0;

	private void driveStraightNavX(boolean firstTime) {
		double navxangle = sense.robotAngle;

		if (firstTime) {
			originangle = navxangle;
		} else {
			double currentangle = navxangle;
			double angledifference = originangle - currentangle;
			double refineddiff = angledifference * DRIVE_STRAIGHT_NAVX_PWR;

			tankDrive(in.rightJoyStickY - refineddiff, in.rightJoyStickY + refineddiff);
		}
	}

	private void driveCircle(double startAngle, double distance, double radius, double velocity, double direction) {
		double K = 360 / 2 * Math.PI * radius;
		double targetAngle = startAngle + direction * K * distance;
		double angleError = targetAngle - sense.robotAngle;
		double correctionPwr = angleError * DRIVE_STRAIGHT_NAVX_PWR;
		tankDrive(AUTO_DRIVE_PWR - correctionPwr, AUTO_DRIVE_PWR + correctionPwr);
	}

	private enum GearState {
		CAM_CHECK, CALCULATE, DRIVE_STRAIGHT1, ARC, DRIVE_STRAIGHT2, DELIVER_GEAR, REVERS;
	};

	private GearState gearState = GearState.values()[0];

	private double botStart;

	public void autogear(boolean first) {
		if (first) {
			gearState = GearState.values()[0];
		}

		switch (gearState) {
		case CAM_CHECK:

			if (sense.camera.gearGoalSearch()) {
				gearState = GearState.CALCULATE;
			}

			break;
		case CALCULATE:
			PathPlanning.calculateArcPoints(sense.robotAngle, in.targetGearPost, sense.cameraAngle,
					sense.cameraDistance);

			gearState = GearState.DRIVE_STRAIGHT1;

			botStart = (sense.leftEncoder + sense.rightEncoder) / 2;

			originangle = sense.robotAngle;
			break;
		case DRIVE_STRAIGHT1:

			driveStraightNavX(false);
			if (((sense.leftEncoder + sense.rightEncoder) / 2) > (botStart + PathPlanning.distance)) {
				gearState = GearState.ARC;
				botStart = (sense.leftEncoder + sense.rightEncoder) / 2;
			}
			break;
		case ARC:
			driveCircle(originangle, 0, 0, 0, 0);

			if (((sense.leftEncoder + sense.rightEncoder) / 2) > (botStart + PathPlanning.arcdistance)) {
				gearState = GearState.DRIVE_STRAIGHT2;
				originangle = sense.robotAngle;
			}
			break;
		case DRIVE_STRAIGHT2:

			driveStraightNavX(false);
			if (sense.accelX > WALL_ACCEL) {
				gearState = GearState.DELIVER_GEAR;
			}
			break;
		case DELIVER_GEAR:
			tankDrive(0, 0);
			// TODO Add delivery function
			break;
		case REVERS:
			// TODO Add reverse function
			break;
		}
	}

}
