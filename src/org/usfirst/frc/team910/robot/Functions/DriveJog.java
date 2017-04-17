package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;

public class DriveJog {

	private double forwardInside;
	private double forwardOutside;
	private static final double REVERSE_INSIDE = 0;
	private static final double REVERSE_OUTSIDE = 15;
	private static final double TWO_INCH_FORWARD_INSIDE =  7;
	private static final double TWO_INCH_FORWARD_OUTSIDE=  0;
	private static final double FOUR_INCH_FORWARD_INSIDE = 7;
	private static final double FOUR_INCH_FORWARD_OUTSIDE= 0;


	private Inputs in;
	private Outputs out;
	private DriveTrain drive;

	private boolean previousButtonState; // whether or not jog was pressed the previous loop
	private boolean goingRight; // shows if driving right, false = drive left
	private int buttonCount; // number of times jog has been pressed
	private double startleftencoder;
	private double startrightencoder;

	public DriveJog(Inputs in, Outputs out, DriveTrain drive) {
		this.in = in;
		this.out = out;
		this.drive = drive;
	}

	private enum JogState { // constructs states
		REVERSE_FIRST_CURVE, REVERSE_SECOND_CURVE, FORWARD_FIRST_CURVE, FORWARD_SECOND_CURVE
	};

	private JogState jogState = JogState.values()[0]; // puts state in array

	public void run() {

		if (in.driveStraight || !in.driveJogRunning) { // waiting or function to be active

			if (in.driveJogLeft || in.driveJogRight || in.driveJogLeft2 || in.driveJogRight2) { // start the case machine
				in.driveJogRunning = true;
				if(in.driveJogLeft || in.driveJogRight){
					forwardInside = FOUR_INCH_FORWARD_INSIDE;
					forwardOutside = FOUR_INCH_FORWARD_OUTSIDE;
				}
				else {
					forwardInside = TWO_INCH_FORWARD_INSIDE;
					forwardOutside = TWO_INCH_FORWARD_OUTSIDE;
				}
				jogState = JogState.REVERSE_FIRST_CURVE;
				goingRight = in.driveJogRight;
				startleftencoder = drive.leftDriveEncoder;
				startrightencoder = drive.rightDriveEncoder;
			} else {
				in.driveJogRunning = false;
			}

		} else {
			switch (jogState) {

			case REVERSE_FIRST_CURVE: // reverse backward into position
				boolean isdone;
				if (goingRight) {
					isdone = drivecurve(REVERSE_OUTSIDE, REVERSE_INSIDE, true);

				} else {
					isdone = drivecurve(REVERSE_INSIDE, REVERSE_OUTSIDE, true);
				}
				if (isdone) {
					jogState = JogState.REVERSE_SECOND_CURVE;
					startleftencoder = drive.leftDriveEncoder;
					startrightencoder = drive.rightDriveEncoder;
				}
				break;

			case REVERSE_SECOND_CURVE:
				boolean isAlsoDone;
				if (goingRight) {
					isAlsoDone = drivecurve(REVERSE_INSIDE, REVERSE_OUTSIDE, true);
				} else {
					isAlsoDone = drivecurve(REVERSE_OUTSIDE, REVERSE_INSIDE, true);
				}
				if (isAlsoDone) {
					jogState = JogState.FORWARD_FIRST_CURVE;
					startleftencoder = drive.leftDriveEncoder;
					startrightencoder = drive.rightDriveEncoder;
				}
				break;

			case FORWARD_FIRST_CURVE:
				boolean isdone1;
				
				if (goingRight ) {
					isdone1 = drivecurve(forwardInside, TWO_INCH_FORWARD_INSIDE,
							false);
				} else {
					isdone1 = drivecurve(TWO_INCH_FORWARD_INSIDE,forwardInside,
							false);
				}
				if (isdone1) {
					jogState = JogState.FORWARD_FIRST_CURVE;
					startleftencoder = drive.leftDriveEncoder;
					startrightencoder = drive.rightDriveEncoder;
				}
				break;

			case FORWARD_SECOND_CURVE:
				if (goingRight) {
					isdone1 = drivecurve(forwardInside, forwardOutside,
							false);
				} else {
					isdone1 = drivecurve(forwardOutside, forwardInside,
							false);
				}
				if (isdone1) {
					jogState = JogState.FORWARD_FIRST_CURVE;
					startleftencoder = drive.leftDriveEncoder;
					startrightencoder = drive.rightDriveEncoder;
				}
				in.driveJogRunning = false;
			}

		}

	}





	public boolean drivecurve(double left, double right, boolean reverse) {

		double leftTarget;
		double rightTarget;

		if (reverse) {

			// the encoder values of where we want to end up
			leftTarget = startleftencoder - left;
			rightTarget = startrightencoder - right;

			// PID to the target values
			drive.tankDrive(leftTarget - drive.leftDriveEncoder, rightTarget - drive.rightDriveEncoder, 0.5);

			// return true when we have made it
			return leftTarget > drive.leftDriveEncoder && rightTarget > drive.rightDriveEncoder;

		} else {
			leftTarget = startleftencoder + left;
			rightTarget = startrightencoder + right;

			drive.tankDrive(leftTarget - drive.leftDriveEncoder, rightTarget - drive.rightDriveEncoder, 0.5);
			return leftTarget < drive.leftDriveEncoder && rightTarget < drive.rightDriveEncoder;
		}
	}
}
