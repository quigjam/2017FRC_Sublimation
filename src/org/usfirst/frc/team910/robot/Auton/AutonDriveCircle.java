package org.usfirst.frc.team910.robot.Auton;

import org.usfirst.frc.team910.robot.IO.Angle;

public class AutonDriveCircle extends AutonStep {

	double endDistance;
	double startDistance;
	double power;
	Angle startAngle;
	double radius;
	double direction;

	public AutonDriveCircle(double distance, double power, double startAngle, double radius, boolean clockwise) {
		this.endDistance = distance;
		this.power = power;
		this.startAngle = new Angle(startAngle);
		this.radius = radius;
		if (clockwise)
			direction = -1;
		else
			direction = 1;

	}

	@Override
	public void setup() {
		startDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
	}

	double prevDist = 0;

	@Override
	public void run() {
		double currDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2 - startDistance;
		double velocity = currDistance - prevDist / sense.deltaTime;
		drive.driveCircle(power, startAngle, currDistance, radius, velocity, direction);
		prevDist = currDistance;
	}

	@Override
	public boolean isDone() {
		return ((drive.leftDriveEncoder + drive.rightDriveEncoder) / 2) > (startDistance + endDistance);
	}
}
