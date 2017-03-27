package org.usfirst.frc.team910.robot.Auton;

import org.usfirst.frc.team910.robot.IO.Util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonFastArc extends AutonStep {

	private boolean blueAlliance;
	private double x;
	private double y;
	private double prevRight;
	private double prevLeft;
	private static final double POWER_PER_DEGREE = 0.1;
	private static final double[] turnPowerL = { 1, 1,    1, 0.15,  1.2,    1 };
	private static final double[] turnPowerR = { 1, 1,    1,    1,  0.8,    1 };
	private static final double[] turnAngle =  { 0, 0,    0,   45,   90,   90 };
	private static final double[] xDistAxis =  { 0, 0, 31.5, 52.5, 71.5, 73.5 };
	private double prevPwr = 0;
	private double PWR_FILT = 0.1;
	private double MAX_PWR = 0.9;
	

	public AutonFastArc() {
		x = 0;
		y = 0;
		prevRight = 0;
		prevLeft = 0;
	}

	public void setup(boolean blueAlliance) {
		x = 0;
		y = 0;
		prevRight = drive.rightDriveEncoder;
		prevLeft = drive.leftDriveEncoder;
		this.blueAlliance = blueAlliance;
		prevPwr = 0;
	}

	public void run() {
		double leftEnc = drive.leftDriveEncoder;
		double rightEnc = drive.rightDriveEncoder;

		double deltaL = leftEnc - prevLeft;
		double deltaR = rightEnc - prevRight;

		double dist = (deltaL + deltaR) / 2;

		x += dist * Math.cos(Math.toRadians(sense.robotAngle.get()));
		y += dist * Math.sin(Math.toRadians(sense.robotAngle.get()));

		double lPower = Util.interpolate(xDistAxis, turnPowerL, x);
		double rPower = Util.interpolate(xDistAxis, turnPowerR, x);
		double targetAngle = Util.interpolate(xDistAxis, turnAngle, x);

		if (!blueAlliance)
			targetAngle = -targetAngle;

		double anglePower = Math.max(Math.min(sense.robotAngle.subtract(targetAngle) * -POWER_PER_DEGREE, 0.5), -0.5);
		lPower += anglePower;
		
		prevPwr += (MAX_PWR - prevPwr) * PWR_FILT; 
		if (blueAlliance) {
			drive.tankDrive(lPower, rPower, prevPwr);
		} else {
			drive.tankDrive(rPower, lPower, prevPwr);
		}
		prevLeft = leftEnc;
		prevRight = rightEnc;

		SmartDashboard.putNumber("x", x);
		SmartDashboard.putNumber("y", y);
		SmartDashboard.putNumber("LeftPower", lPower);
		SmartDashboard.putNumber("TargetAngle", targetAngle);
	}

	public boolean isDone() {
		return (Math.abs(y) > 28);

	}
}
