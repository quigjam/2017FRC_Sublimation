package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.Shooter;
import org.usfirst.frc.team910.robot.Vision.Target;

import edu.wpi.first.wpilibj.Timer;

public class AutoShoot {

	private static final double DRIVE_POWER = 0.2;
	private static final double ALLOWABLE_ANGLE_ERROR = 1;
	private static final double LAG_TIME = 0.1;
	private static final double SHOOT_DISTANCE = 40;
	private static final double ALLOWABLE_DISTANCE_ERROR = 3;

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
		CAM_CHECK, DRIVE, ALIGN, PRIME, FIRE; // construct all the states we need
	};

	private ShootState shootState = ShootState.values()[0]; // put them in an array
	private Target currentTarget;

	public void run() {
		if (in.autoShoot) { // when we hi
			currentTarget = sense.camera.boiler.getCurrentTarget();
			switch (shootState) {
			case CAM_CHECK:

				if (Timer.getFPGATimestamp() - currentTarget.time < LAG_TIME) {
					shootState = ShootState.DRIVE;
				}

				break;
			case DRIVE:
				drive.originAngle.set(sense.camera.boiler.getCurrentTarget().totalAngle.get());
				drive.driveStraightNavX(false, DRIVE_POWER, 0);
				if (Math.abs(currentTarget.distance - SHOOT_DISTANCE) < ALLOWABLE_DISTANCE_ERROR) { // when camera class is done we will get the distance of the
																									// camera to be < 0.05
					shootState = ShootState.ALIGN;
				}
				break;
			case ALIGN:
				drive.rotate(currentTarget.totalAngle);
				shoot.shooterPrime(true);
				if (Math.abs(currentTarget.cameraAngle) < ALLOWABLE_ANGLE_ERROR) {
					shootState = ShootState.PRIME;
				}
				break;

			case PRIME:
				shoot.shooterPrime(true);
				if (shoot.upToSpeed(out.shooterSpeedEncoder)) {
					shootState = ShootState.FIRE;

				}
				break;
			case FIRE:
				shoot.shooterFire(true);
				shoot.shooterPrime(true);
			}

		} else {
			shootState = ShootState.values()[0];
		}
	}
}