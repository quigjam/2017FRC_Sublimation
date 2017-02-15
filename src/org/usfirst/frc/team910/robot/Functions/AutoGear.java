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
		CAM_CHECK, CALCULATE, DRIVE_STRAIGHT1, ARC, DRIVE_STRAIGHT2, DELIVER_GEAR, REVERS;
	};

	private GearState gearState = GearState.values()[0];

	private double botStart;

	public void run() {
		if (in.autoGear) {

			// find goal
			switch (gearState) {
			case CAM_CHECK:

				if (sense.camera.gearGoalSearch()) {
					gearState = GearState.CALCULATE;
				}
				break;
			// find the arc, set the angles
			case CALCULATE:
				PathPlanning.calculateArcPoints(sense.robotAngle, in.targetGearPost, sense.cameraAngle,
						sense.cameraDistance);

				gearState = GearState.DRIVE_STRAIGHT1;

				botStart = (out.leftDriveEncoder + out.rightDriveEncoder) / 2;

				drive.originangle = sense.robotAngle;
				break;
			// drive up to arc
			case DRIVE_STRAIGHT1:

				drive.driveStraightNavX(false);
				if (((out.leftDriveEncoder + out.rightDriveEncoder) / 2) > (botStart + PathPlanning.distance)) {
					gearState = GearState.ARC;
					botStart = (out.leftDriveEncoder + out.rightDriveEncoder) / 2;
				}
				break;
			// drive in the arc
			case ARC:
				double distance = (out.leftDriveEncoder + out.rightDriveEncoder) / 2 - botStart;
				drive.driveCircle(drive.originangle, distance, PathPlanning.CIRCLE_RADIUS, 0, 0);
				// TODO finish circle drive

				if (((out.leftDriveEncoder + out.rightDriveEncoder) / 2) > (botStart + PathPlanning.arcdistance)) {
					gearState = GearState.DRIVE_STRAIGHT2;
					drive.originangle = sense.robotAngle;
				}
				break;
			// exit arc and drive into wall
			case DRIVE_STRAIGHT2:

				drive.driveStraightNavX(false);
				if (sense.accelX > WALL_ACCEL) {
					gearState = GearState.DELIVER_GEAR;
				}
				break;
			// deliver
			case DELIVER_GEAR:
				drive.tankDrive(0, 0);
				// TODO Add delivery function
				break;
			// reverse out of gear peg
			case REVERS:
				// TODO Add reverse function
				break;
			}
		} else {
			gearState = GearState.values()[0]; // reset
		}
	}
}
