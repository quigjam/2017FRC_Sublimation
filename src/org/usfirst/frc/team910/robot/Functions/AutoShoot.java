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

	private Inputs in;
	private Shooter shoot;
	private Sensors sense;
	private DriveTrain drive;

	public AutoShoot( Inputs in, Shooter shoot) {

		this.in = in;
		this.shoot = shoot;
	}

	private enum ShootState {
		CAM_CHECK, DRIVE, ALIGN, PRIME, FIRE; // construct all the states we need
	};

	private ShootState shootState = ShootState.values()[0]; // put them in an array
	private Target currentTarget;

	public void run() {
		if (in.autoShoot) { // when we hit the auto shoot button
			currentTarget = sense.camera.boiler.getCurrentTarget(); // set our current target to the boiler
			switch (shootState) {
			case CAM_CHECK:

				if (Timer.getFPGATimestamp() - currentTarget.time < LAG_TIME) { // check to see if we see the target within an allowable time
					shootState = ShootState.DRIVE; // go to next state
				}

				break;
			case DRIVE:
				drive.originAngle.set(currentTarget.totalAngle.get()); // set our origin angel toward the target
				drive.driveStraightNavX(false, DRIVE_POWER, 0); // drive toward it
				if (Math.abs(currentTarget.distance - SHOOT_DISTANCE) < ALLOWABLE_DISTANCE_ERROR) { // when we get close enough to the target // when camera
																									// class is done we will get the distance of the camera to
																									// be < 0.05
					shootState = ShootState.ALIGN; // go to next state
				}
				break;
			case ALIGN:
				drive.rotate(currentTarget.totalAngle); // face the target
				shoot.shooterPrime(true);
				if (Math.abs(currentTarget.cameraAngle) < ALLOWABLE_ANGLE_ERROR) { // when we are lined up
					shootState = ShootState.PRIME; // go to the next step
				}
				break;

			case PRIME:
				shoot.shooterPrime(true); // prime
				if (shoot.upToSpeed()) { // when we get up to speed
					shootState = ShootState.FIRE; // go to next state

				}
				break;
			case FIRE:
				shoot.shooterPrime(true);
				shoot.shooterFire(true); // fire
			}

		} else {
			shootState = ShootState.values()[0]; // reset state machine
		}
	}
}