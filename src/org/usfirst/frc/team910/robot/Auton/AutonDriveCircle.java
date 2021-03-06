package org.usfirst.frc.team910.robot.Auton;

import org.usfirst.frc.team910.robot.IO.Angle;

public class AutonDriveCircle extends AutonStep {

	double endDistance;
	double startDistance;
	double distance;
	double power;
	Angle startAngle;
	double endAngle;
	double radius;
	double direction;

	public AutonDriveCircle(double distance, double power, double startAngle, double radius, double endAngle, boolean clockwise) {
		this.distance = distance;
		this.power = power;
		this.startAngle = new Angle(startAngle);
		this.radius = radius;
		this.endAngle = endAngle;
		if (clockwise)
			direction = -1;
		else
			direction = 1;

	}

	@Override
	public void setup(boolean blueAlliance) {
		startDistance = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
		endDistance = startDistance + distance;
		prevDist = startDistance;
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
		return ((drive.leftDriveEncoder + drive.rightDriveEncoder) / 2) > (endDistance);
	}
}
