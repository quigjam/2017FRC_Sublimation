package org.usfirst.frc.team910.robot.Auton;

import org.usfirst.frc.team910.robot.IO.Util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonFastArc extends AutonStep {

	private double x;
	private double y;
	private double prevRight;
	private double prevLeft;
	private static final double POWER_PER_DEGREE = 0.1;
	private static final double[] turnPower = { 1, 0, 1 };
	private static final double[] turnAngle = { 0, 45, 90 };
	private static final double[] xDistAxis = { 0, 36, 60 };

	public AutonFastArc() {
		x = 0;
		y = 0;
		prevRight = 0;
		prevLeft = 0;
	}

	public void setup() {
		x = 0;
		y = 0;
		prevRight = drive.rightDriveEncoder;
		prevLeft = drive.leftDriveEncoder;
	}

	public void run() {
		double leftEnc = drive.leftDriveEncoder;
		double rightEnc = drive.rightDriveEncoder;

		double deltaL = leftEnc - prevLeft;
		double deltaR = rightEnc - prevRight;

		double dist = (deltaL + deltaR) / 2;

		x += dist * Math.cos(sense.robotAngle.get());
		y += dist * Math.sin(sense.robotAngle.get());

		double lPower = Util.interpolate(xDistAxis, turnPower, x);
		double targetAngle = Util.interpolate(xDistAxis, turnAngle, x);

		lPower += (targetAngle - sense.robotAngle.get()) * POWER_PER_DEGREE;
		drive.tankDrive(lPower, 1, 0.5); // Power limit is 0.5 for testing

		prevLeft = leftEnc;
		prevRight = rightEnc;
		
		SmartDashboard.putNumber("x", x);
		SmartDashboard.putNumber("y", y);
		SmartDashboard.putNumber("LeftPower", lPower);
		SmartDashboard.putNumber("TargetAngle", targetAngle);
	}

	public boolean isDone() {
		return (y > 48);

	}
}
