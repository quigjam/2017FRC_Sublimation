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
	private double[] turnPowerL;
	private double[] turnPowerR;
	private double[] turnAngle;
	private double[] xDistAxis;
	private double prevPwr = 0;
	private double PWR_FILT = 0.1;
	private double MAX_PWR = 0.9;
	private DriveComplete dc;

	public AutonFastArc(double[] turnPowerL, double[] turnPowerR, double[] turnAngle, double[] xDistAxis, DriveComplete dc) {
		this.turnPowerL = turnPowerL;
		this.turnPowerR = turnPowerR;
		this.turnAngle = turnAngle;
		this.xDistAxis = xDistAxis; 
		this.dc = dc;
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
		//return (Math.abs(y) > 28);
		//return (Math.abs(x) < 28);
		return dc.isDone(x, y);
	}
}
