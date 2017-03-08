package org.usfirst.frc.team910.robot.Auton;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonDriveStraight extends AutonStep {

	private static final double MAX_ACCEL = 60;
	private static final double V_MAX = 100.0;
	private static final double PCONST = 0.15 * 0.7;
	private static final double ALLOWABLE_ERROR = 1;
	private static final double V_CONST = 1 / 200;

	double distance;
	double stopDistance;
	double startDistance;
	double startTime;
	double halfDistance;
	double startDeccelTime;
	double maxPower;
	double angle;
	double v;
	double x;
	double a;
	double jerk;

	public AutonDriveStraight(double distance, double maxPower, double angle) {
		this.distance = distance;
		this.maxPower = maxPower;
		this.angle = angle;
	}

	@Override
	public void setup() {
		startDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		stopDistance = startDistance + distance;
		halfDistance = distance / 2 + startDistance;
		x = 0;
		v = 0;
		a = MAX_ACCEL;
		startTime = Timer.getFPGATimestamp();

		double t = Math.sqrt((6 * distance) / (5 * MAX_ACCEL));
		jerk = -(2 * MAX_ACCEL) / t;
		double tv = Math.sqrt((2 * V_MAX / MAX_ACCEL) / jerk);
		if (t > 2 * tv) {
			jerk = -2 * MAX_ACCEL / tv;
			t = 2 * tv;
			x = (1 / 6) * jerk * t * t * t + 0.5 * MAX_ACCEL * t * t;

			double constVelDist = distance - x;
			double constVelTime = constVelDist / V_MAX;

			startDeccelTime = tv + constVelTime + startTime;
		} else {
			startDeccelTime = t / 2 + startTime;
		}

	}

	@Override
	public void run() {
		double currentDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;

		if (currentDistance < stopDistance - ALLOWABLE_ERROR) {
			double j;
			if (Timer.getFPGATimestamp() < startDeccelTime && a <= 0) {
				a = 0;
				j = 0;
			} else {
				j = jerk;
			}
			double t = sense.deltaTime;
			x += (1 / 6) * j * t * t * t + 0.5 * a * t * t + v * t;
			v += 0.5 * j * t * t + a * t;
			a += j * t;
		} else {
			a = 0;
			v = 0;
		}
		double distError = startDistance + x - currentDistance;
		double power = PCONST * distError + v * V_CONST;

		SmartDashboard.putNumber("autonPower", power);
		drive.driveStraightNavX(false, power, 0);

	}

	@Override
	public boolean isDone() {
		double avgEncDist = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		return avgEncDist >= stopDistance - ALLOWABLE_ERROR;
	}

}
