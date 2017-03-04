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
		CAM_CHECK, DRIVE, DRIVE2, CLIMB               //constructs different cases for the case machine
	};

	private ClimbState climbState = ClimbState.values()[0];     //starts case machine for climbing
	private Target currentTarget;
	private double endTime = 0;  

	public void run() {
		if (in.autoClimb) {                                       //if we push the auto climb button
			currentTarget = sense.camera.rope.getCurrentTarget();   //set the rope as our current target
			switch (climbState) {                                   
			case CAM_CHECK:

				if (Timer.getFPGATimestamp() - currentTarget.time < LAG_TIME) { //if we see it within an acceptable period time
					climbState = ClimbState.DRIVE;									//move to the next state

				}
				break;

			case DRIVE:
				drive.originAngle.set(currentTarget.totalAngle.get()); //set target angle to where the target is plus where we are
				drive.driveStraightNavX(false, DRIVE_POWER, 0);							//drive straight to the target
				if ((currentTarget.distance) < ALLOWABLE_DISTANCE_ERROR) {				//if our distance from the target falls within an allowable error
					climbState = ClimbState.DRIVE2;										//advance to the next state
					endTime = Timer.getFPGATimestamp() + DRIVE_TIME;  					//set our end time to the current value of our time + our drive time
				}
				break;
			case DRIVE2:
				drive.originAngle.set(currentTarget.totalAngle.get());//set target angle to where the target is plus where we are
				drive.driveStraightNavX(false, DRIVE_POWER, 0);							//drive straight toward target
				climb.climb(CLIMB_POWER);												//start climbing
				if(Timer.getFPGATimestamp() > endTime) { 								// when the time gets greater than our end time
				climbState = climbState.CLIMB;											//go to CLIMB state
				}
				break;

			case CLIMB:

				climb.climb(CLIMB_POWER);												//climb
			}

		}
	}
}
