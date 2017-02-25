package org.usfirst.frc.team910.robot.Auton;

public class AutonDriveStraight extends AutonStep {

	double distance;
	double speed;
	double angle;

	public AutonDriveStraight(double distance, double speed, double angle) {
		distance = this.distance;
		speed = this.speed;
		angle = this.angle;
	}

	@Override
	public void setup() {
		distance += (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
	}

	@Override
	public void run() {
		drive.originAngle.set(angle);
		drive.driveStraightNavX(false, speed, 0);
	}

	@Override
	public boolean isDone() {
		double avgEncDist = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;

		return avgEncDist >= distance;
	}

}
