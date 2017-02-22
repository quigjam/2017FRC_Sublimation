package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.GearSystem;
import org.usfirst.frc.team910.robot.Vision.Target;

import edu.wpi.first.wpilibj.Timer;

public class AutoGear {

	private static final double WALL_ACCEL = 0;
	private static final double LAG_TIME = 0.1;
	private static final double MIN_ARC_ANGLE = 30;
	private static final double DRIVE_POWER = 0.2;

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
		CAM_CHECK, CALCULATE, DRIVE_STRAIGHT1, ARC, DRIVE_STRAIGHT2, DELIVER_GEAR, REVERSE, DONE; // constructing all the states for auto gear
	};

	private GearState gearState = GearState.values()[0]; // put all the states in an array

	private double botStart;
	private double lastDistance = 0;

	public void run() {
		if (in.autoGear) { // when we hit the auto gear button start up
			Target currentTarget = getBestTarget();

			switch (gearState) {
			case CAM_CHECK:

				if (Timer.getFPGATimestamp() - currentTarget.time < LAG_TIME) { // check to see if we see the target within an allowable time
					gearState = GearState.CALCULATE; // go to next state
				}
				break;

			case CALCULATE:
				PathPlanning.calculateArcPoints(sense.robotAngle, in.targetGearPost, currentTarget.totalAngle, // calculate the arc points
						currentTarget.distance);

				gearState = GearState.DRIVE_STRAIGHT1; // advance to next state

				botStart = (out.leftDriveEncoder + out.rightDriveEncoder) / 2; // set bot start to the average of the encoders

				drive.originAngle = sense.robotAngle; // set robot angle to our origin angle
				break;

			case DRIVE_STRAIGHT1:

				// drive.driveStraightNavX(false);
				if (((out.leftDriveEncoder + out.rightDriveEncoder) / 2) > (botStart + PathPlanning.distance)) { // if the average of the encoders is greater
																													// than the starting average plus the
																													// distance traveled in the pathplanning
					gearState = GearState.ARC; // go to the next state
					botStart = (out.leftDriveEncoder + out.rightDriveEncoder) / 2; // take a new average for the bot start
					lastDistance = botStart;
				}
				break;

			case ARC:
				double distance = (out.leftDriveEncoder + out.rightDriveEncoder) / 2 - botStart; // set out distance to difference of our current average and
																									// our past average
				drive.driveCircle(drive.originAngle, distance, PathPlanning.CIRCLE_RADIUS, (distance - lastDistance),
						PathPlanning.direction); // start driving on the arc

				if (((out.leftDriveEncoder + out.rightDriveEncoder) / 2) > (botStart + PathPlanning.arcdistance)) { // if our encoder average becomes greater
																													// than out old average + how far we've gone
																													// on the arc
					gearState = GearState.DRIVE_STRAIGHT2; // go to next state
					drive.originAngle = sense.robotAngle; // set the origin angle as our current robot position
				}
				break;
			case DRIVE_STRAIGHT2:
				if (Math.abs(currentTarget.cameraAngle) > MIN_ARC_ANGLE) {
					gearState = GearState.DONE;
				}
				drive.driveStraightNavX(false, DRIVE_POWER, 0);
				if (sense.accelX > WALL_ACCEL) { // when we hit the wall and acceleration breaks
					gearState = GearState.DELIVER_GEAR; // go to next step
				}
				break;

			case DELIVER_GEAR:
				drive.tankDrive(0, 0); // stop
				// TODO Add delivery function //drop gear
				break;
			// reverse out of gear peg
			case REVERSE:
				// TODO Add reverse function //get out of there
				break;
			case DONE:
				drive.tankDrive(0, 0);
				break;
			}
		} else {
			gearState = GearState.values()[0]; // reset
		}
	}

	public Target getBestTarget() {
		Target gearGoalLeft = sense.camera.gearGoalLeft.getCurrentTarget(); //target seen by left camera
		Target gearGoalMid = sense.camera.gearGoalMid.getCurrentTarget(); //target seen by front camera
		Target gearGoalRight = sense.camera.gearGoalRight.getCurrentTarget(); //target seen by right camera

		if (Timer.getFPGATimestamp() - gearGoalMid.time < LAG_TIME) { //if the most recent target is the middle
			return gearGoalMid;
		} else {
			boolean goodLeft = Math.abs(gearGoalLeft.cameraAngle) < 90; //left is within optimal angle
			boolean goodRight = Math.abs(gearGoalRight.cameraAngle) < 90; //right is within optimal angle
			if (goodLeft && goodRight) { //if both are good
				if (gearGoalLeft.distance < gearGoalRight.distance) { //if left is closer
					return gearGoalLeft;
				} else { //right is closer
					return gearGoalRight;
				}
			} else if (goodLeft) { //if left is good
				return gearGoalLeft;
			} else if (goodRight) { //if right is good
				return gearGoalRight;
			} else { //if mid was not most recent, but the other two are bad
				return gearGoalMid;
			}
		}

	}

}
