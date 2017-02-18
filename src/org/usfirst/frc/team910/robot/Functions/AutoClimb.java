package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.Climber;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Vision.Target;

import edu.wpi.first.wpilibj.Timer;

public class AutoClimb {

	private static final double DRIVE_POWER = 0.2;
	private static final double ALLOWABLE_ANGLE_ERROR = .5;
	private static final double LAG_TIME = 0.1;
	private static final double ALLOWABLE_DISTANCE_ERROR = 1;
	private static final double CLIMB_POWER = 0.9;
	private static final double DRIVE_TIME = 0.5; 

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
		CAM_CHECK, DRIVE, DRIVE2, CLIMB
	};

	private ClimbState climbState = ClimbState.values()[0];
	private Target currentTarget;
	private double endTime = 0;  

	public void run() {
		if (in.autoClimb) {
			currentTarget = sense.camera.rope.getCurrentTarget();
			switch (climbState) {
			case CAM_CHECK:

				if (Timer.getFPGATimestamp() - currentTarget.time < LAG_TIME) {
					climbState = ClimbState.DRIVE;

				}
				break;

			case DRIVE:
				drive.originAngle.set(sense.robotAngle.get() + sense.cameraAngle.get());
				drive.driveStraightNavX(false, DRIVE_POWER, 0);
				if ((currentTarget.distance) < ALLOWABLE_DISTANCE_ERROR) {
					climbState = ClimbState.DRIVE2;
					endTime = Timer.getFPGATimestamp() + DRIVE_TIME;  
				}
				break;
			case DRIVE2:
				drive.originAngle.set(sense.robotAngle.get() + sense.cameraAngle.get());
				drive.driveStraightNavX(false, DRIVE_POWER, 0);
				climb.forward(CLIMB_POWER);
				if(Timer.getFPGATimestamp() > endTime) { 
				climbState = climbState.CLIMB;
				}
				break;

			case CLIMB:

				climb.forward(CLIMB_POWER);
			}

		}
	}
}
