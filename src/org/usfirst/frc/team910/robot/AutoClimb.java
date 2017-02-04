package org.usfirst.frc.team910.robot;

public class AutoClimb {
	private Inputs in;
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
		CAM_CHECK, ALIGN, CLIMB
	};

	private ClimbState climbState = ClimbState.values()[0];

	public void run() {
		if (in.autoClimb) {
			switch (climbState) {
			case CAM_CHECK:

				if (sense.camera.climbSearch()) { // fix later
					climbState = ClimbState.ALIGN;

				}
				break;
			case ALIGN:
				// TODO Add Function here
				climbState = ClimbState.CLIMB;
				break;

			case CLIMB:
				// climb.climbNow(0);// TODO see if number is needed here
			}

		}
	}
}
