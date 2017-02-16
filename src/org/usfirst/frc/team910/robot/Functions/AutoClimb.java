package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.Climber;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;



public class AutoClimb {
	
	private static final double DRIVE_POWER = 0.2;
	private static final double ALLOWABLE_ANGLE_ERROR = .5;
	
	private Inputs in;
	private Outputs out;
	private Sensors sense;
	private DriveTrain drive;
	private Climber climb;

	public AutoClimb(Inputs in, Sensors sense, DriveTrain drive, Climber climb) {
		this.in = in;
		this.sense = sense;
		this.drive = drive;
		this.climb = climb;
	}

	private enum ClimbState {
		CAM_CHECK, DRIVE, ALIGN, CLIMB
	};

	private ClimbState climbState = ClimbState.values()[0];

	public void run() {
		if (in.autoClimb) {
			switch (climbState) {
			case CAM_CHECK:

				if (sense.camera.climbSearch()) { // fix later
					climbState = ClimbState.DRIVE;

				}
				break;
				
			case DRIVE:
				drive.originAngle.set(sense.robotAngle.get() + sense.cameraAngle.get());
				drive.driveStraightNavX(false, DRIVE_POWER, 0);
				if (true) { // when camera class is done we will get the distance of the camera to be = 0					
					climbState = ClimbState.ALIGN;
				}
			
			case ALIGN:
				drive.rotate(sense.cameraAngle);
				if (Math.abs(sense.cameraAngle.get()) < ALLOWABLE_ANGLE_ERROR) {
				climbState = ClimbState.CLIMB;
				}
				break;

			case CLIMB:
			// climb.climbNow(0);// TODO see if number is needed here
			}

		}
	}
}
