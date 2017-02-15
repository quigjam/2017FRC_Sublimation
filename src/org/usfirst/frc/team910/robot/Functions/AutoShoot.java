package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.Shooter;

public class AutoShoot {

	private static final double DRIVE_POWER = 0.2;
	private static final double ALLOWABLE_ANGLE_ERROR = 1;

	private Outputs out;
	private Inputs in;
	private Shooter shoot;
	private Sensors sense;
	private DriveTrain drive;

	public AutoShoot(Outputs out, Inputs in, Shooter shoot) {
		this.out = out;
		this.in = in;
		this.shoot = shoot;
	}

	private enum ShootState {
		CAM_CHECK, DRIVE, ALIGN, PRIME, FIRE;
	};

	private ShootState shootState = ShootState.values()[0];

	public void run() {
		if (in.autoShoot) {
			switch (shootState) {
			case CAM_CHECK:

				if (sense.camera.fuelGoalSearch()) {
					shootState = ShootState.DRIVE;
				}

				break;
			case DRIVE:
				drive.originAngle.set(sense.robotAngle.get() + sense.cameraAngle.get());
				drive.driveStraightNavX(false, DRIVE_POWER, 0);
				if (true) { // when camera class is done we will get the distance of the camera to be < 0.05
					shootState = ShootState.ALIGN;
				}
				break;
			case ALIGN:
				drive.rotate(sense.cameraAngle);
				if (Math.abs(sense.cameraAngle.get()) < ALLOWABLE_ANGLE_ERROR) {
					shootState = ShootState.PRIME;
				}
				break;

			case PRIME:
				shoot.shooterPrime();
				if (shoot.upToSpeed(out.shooterSpeedEncoder)) {
					shootState = ShootState.FIRE;

				}
				break;
			case FIRE:
				shoot.shooterFire();// TODO see comment above.
			}

		}
	}
}