package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.GearSystem;

public class AutoGear {

	private static final double WALL_ACCEL = 0;

	private Inputs in;
	private Outputs out;
	private Sensors sense;
	private DriveTrain drive;
	private GearSystem gear;

	public AutoGear(Inputs in, Sensors sense, DriveTrain drive, GearSystem gear) {
		this.in = in;
		this.sense = sense;
		this.drive = drive;
		this.gear = gear;
	}

	private enum GearState {
		CAM_CHECK, CALCULATE, DRIVE_STRAIGHT1, ARC, DRIVE_STRAIGHT2, DELIVER_GEAR, REVERS; //constructing all the states for auto gear
	};

	private GearState gearState = GearState.values()[0];      //put all the states in an array

	private double botStart;

	public void run() {
		if (in.autoGear) {				   					//when we hit the auto gear button start up

		
			switch (gearState) {
			case CAM_CHECK:

				if (sense.camera.gearGoalSearch()) {         // find goal
					gearState = GearState.CALCULATE;		//go to next state									
				}
				break;
		
			case CALCULATE:
				PathPlanning.calculateArcPoints(sense.robotAngle, in.targetGearPost, sense.cameraAngle, //calculate the arc points 
						sense.cameraDistance);

				gearState = GearState.DRIVE_STRAIGHT1;								//advance to next state

				botStart = (out.leftDriveEncoder + out.rightDriveEncoder) / 2;		//set bot start to the average of the encoders

				drive.originAngle = sense.robotAngle;								//set robot angle to our orgin angle
				break;
		
			case DRIVE_STRAIGHT1:

				//drive.driveStraightNavX(false);
				if (((out.leftDriveEncoder + out.rightDriveEncoder) / 2) > (botStart + PathPlanning.distance)) { //if the average of the encoders is greater than the starting average plus the distance traveled in the pathplanning
					gearState = GearState.ARC;										//go to the next state
					botStart = (out.leftDriveEncoder + out.rightDriveEncoder) / 2;	//take a new average for the bot start
				}
				break;
		
			case ARC:
				double distance = (out.leftDriveEncoder + out.rightDriveEncoder) / 2 - botStart; //set out distance to difference of our current average and our past average
				drive.driveCircle(drive.originAngle, distance, PathPlanning.CIRCLE_RADIUS, 0, 0); //start driving on the arc
				// TODO finish circle drive

				if (((out.leftDriveEncoder + out.rightDriveEncoder) / 2) > (botStart + PathPlanning.arcdistance)) { //if our encoder average becomes greater than out old average + how far we've gone on the arc
					gearState = GearState.DRIVE_STRAIGHT2;            //go to next state
					drive.originAngle = sense.robotAngle;			//set the orgin angle as our curret robot position
				}
				break;
			case DRIVE_STRAIGHT2:

				//drive.driveStraightNavX(false);
				if (sense.accelX > WALL_ACCEL) {					//when we hit the wall and acceleration breaks	
					gearState = GearState.DELIVER_GEAR;				//go to next step
				}
				break;
	
			case DELIVER_GEAR:
				drive.tankDrive(0, 0);								//stop
				// TODO Add delivery function						//drop gear
				break;
			// reverse out of gear peg
			case REVERS:								
				// TODO Add reverse function						//get out of there
				break;
			}
		} else {
			gearState = GearState.values()[0]; 						// reset			
		}
	}
}
