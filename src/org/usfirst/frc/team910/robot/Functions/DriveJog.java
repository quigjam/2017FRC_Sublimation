package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;

import edu.wpi.first.wpilibj.Timer;

public class DriveJog {

	private double forwardInside;
	private double forwardOutside;
	private static final double REVERSE_INSIDE = 0; 
	private static final double REVERSE_OUTSIDE_DEFAULT = 9;
	private static       double REVERSE_OUTSIDE = 9;
	private static final double REVERSE_OUTSIDE_EIGHT = 13;  //2" and 4" jogs both use the same REVERSE_OUTSIDE value, need a larger value for 8" jog
	
	private static final double TWO_INCH_FORWARD_INSIDE = 2; // drive straight
	private static final double TWO_INCH_FORWARD_OUTSIDE = 4;
	
	private static final double FOUR_INCH_FORWARD_INSIDE = 0;
	private static       double FOUR_INCH_FORWARD_OUTSIDE = 8; // S curve the same as reverse but in other direction
	
	private static final double EIGHT_INCH_FORWARD_INSIDE = 1;
	private static       double EIGHT_INCH_FORWARD_OUTSIDE = 10; // S curve the same as reverse but in other direction
	
	private static final double DRIVE_PWR_FILT = 0.1; //how fast we ramp from current power to max power, 0.1 is min to max in 0.5 seconds
	private static final double MAX_PWR = 0.85; //power is capped at this
	private static final double MIN_PWR = 0.3; //power filter is reset to this in between stages
	private static final double P_CONST = 0.12; //PID constant 0.2 power per inch of error
	private static final double STOP_TIME = 0.3; //extra time to PID to the final distance at the end to ensure we stop
	private static final double MAX_STAGE_TIME = 0.5; //4 stages at this amount of time, if any of them exceed this we stop

	private Inputs in;
	private Outputs out;
	private DriveTrain drive;

	private boolean previousButtonState; // whether or not jog was pressed the
											// previous loop
	private boolean goingRight; // shows if driving right, false = drive left
	private int buttonCount; // number of times jog has been pressed
	private double startleftencoder;
	private double startrightencoder;
	private double prevDrivePwr;
	private double endTime;
	private double startTime;

	public DriveJog(Inputs in, Outputs out, DriveTrain drive) {
		this.in = in;
		this.out = out;
		this.drive = drive;
	}

	private enum JogState { // constructs states
		REVERSE_FIRST_CURVE, REVERSE_SECOND_CURVE, FORWARD_FIRST_CURVE, FORWARD_SECOND_CURVE, STOP
	};

	private JogState jogState = JogState.values()[0]; // puts state in array

	public void run() {

		if (in.driveStraight || !in.driveJogRunning) { // waiting or function to
														// be active

			if (in.driveJogLeft4 || in.driveJogRight4 || in.driveJogLeft2 || in.driveJogRight2 || in.driveJogLeft8 || in.driveJogRight8) { // start
																																		   // the
																																		   // case
																																		   // machine
				in.driveJogRunning = true;
				if (in.driveJogLeft4 || in.driveJogRight4) {			//4" jog?
					forwardInside = FOUR_INCH_FORWARD_INSIDE;
					forwardOutside = FOUR_INCH_FORWARD_OUTSIDE;
					REVERSE_OUTSIDE = REVERSE_OUTSIDE_DEFAULT;			//reset to default if not 8" jog
				} else if (in.driveJogLeft2 || in.driveJogRight2) {		//2" jog?
					forwardInside = TWO_INCH_FORWARD_INSIDE;
					forwardOutside = TWO_INCH_FORWARD_OUTSIDE;
					REVERSE_OUTSIDE = REVERSE_OUTSIDE_DEFAULT;			//reset to default if not 8" jog
				} else {												//new 8" jog?
					forwardInside = EIGHT_INCH_FORWARD_INSIDE;
					forwardOutside = EIGHT_INCH_FORWARD_OUTSIDE;					
					REVERSE_OUTSIDE = REVERSE_OUTSIDE_EIGHT;			//override for 8" only
				}
				jogState = JogState.REVERSE_FIRST_CURVE;
				goingRight = in.driveJogRight4 || in.driveJogRight2 || in.driveJogRight8;
				startleftencoder = drive.leftDriveEncoder;
				startrightencoder = drive.rightDriveEncoder;
				prevDrivePwr = MIN_PWR;
				startTime = Timer.getFPGATimestamp();
			} else {
				in.driveJogRunning = false;
			}

		} else {
			
			boolean timeExpired = Timer.getFPGATimestamp() - startTime > MAX_STAGE_TIME;
			
			switch (jogState) {
			case REVERSE_FIRST_CURVE: // reverse backward into position
				if (goingRight) {
					//if this stage is done
					if(drivecurve(REVERSE_OUTSIDE, REVERSE_INSIDE, true, P_CONST) || timeExpired){
						jogState = JogState.REVERSE_SECOND_CURVE;
						startleftencoder -= REVERSE_OUTSIDE;
						startrightencoder -= REVERSE_INSIDE;
						startTime = Timer.getFPGATimestamp();
					}
					
				} else {
					//if this stage is done
					if(drivecurve(REVERSE_INSIDE, REVERSE_OUTSIDE, true, P_CONST) || timeExpired){
						jogState = JogState.REVERSE_SECOND_CURVE;
						startleftencoder -= REVERSE_INSIDE;
						startrightencoder -= REVERSE_OUTSIDE;
						startTime = Timer.getFPGATimestamp();
					}
				}
				break;

			case REVERSE_SECOND_CURVE:
				if (goingRight) {
					if(drivecurve(REVERSE_INSIDE, REVERSE_OUTSIDE, true, P_CONST) || timeExpired){
						jogState = JogState.FORWARD_FIRST_CURVE;
						startleftencoder -= REVERSE_INSIDE;
						startrightencoder -= REVERSE_OUTSIDE;
						prevDrivePwr = MIN_PWR;
						startTime = Timer.getFPGATimestamp();
					}
				} else {
					if(drivecurve(REVERSE_OUTSIDE, REVERSE_INSIDE, true, P_CONST) || timeExpired){
						jogState = JogState.FORWARD_FIRST_CURVE;
						startleftencoder -= REVERSE_OUTSIDE;
						startrightencoder -= REVERSE_INSIDE;
						prevDrivePwr = MIN_PWR;
						startTime = Timer.getFPGATimestamp();
					}
				}
				break;

			case FORWARD_FIRST_CURVE:
				if (goingRight) {
					if(drivecurve(forwardOutside, forwardInside, false, P_CONST) || timeExpired){
						jogState = JogState.FORWARD_SECOND_CURVE;
						startleftencoder += forwardOutside;
						startrightencoder += forwardInside;
						startTime = Timer.getFPGATimestamp();
					}
				} else {
					if(drivecurve(forwardInside, forwardOutside, false, P_CONST) || timeExpired){
						jogState = JogState.FORWARD_SECOND_CURVE;
						startleftencoder += forwardInside;
						startrightencoder += forwardOutside;
						startTime = Timer.getFPGATimestamp();
					}
				}
				break;

			case FORWARD_SECOND_CURVE:
				if (goingRight) {
					if(drivecurve(forwardInside, forwardOutside, false, P_CONST) || timeExpired){
						jogState = JogState.STOP;
						startleftencoder += forwardInside;
						startrightencoder += forwardOutside;
						//in.driveJogRunning = false;
						prevDrivePwr = MIN_PWR;
						endTime = Timer.getFPGATimestamp();
					}
				} else {
					if(drivecurve(forwardOutside, forwardInside, false, P_CONST) || timeExpired){
						jogState = JogState.STOP;
						startleftencoder += forwardOutside;
						startrightencoder += forwardInside;
						//in.driveJogRunning = false;
						prevDrivePwr = MIN_PWR;
						endTime = Timer.getFPGATimestamp();
					}
				}
				break;
				
			case STOP:
				if(endTime + STOP_TIME < Timer.getFPGATimestamp()){
					in.driveJogRunning = false;
				}
				drivecurve(0,0,false, P_CONST/2);
			}

		}

	}

	public boolean drivecurve(double left, double right, boolean reverse, double P) {

		double leftTarget;
		double rightTarget;
		
		//filter drive power
		prevDrivePwr += (MAX_PWR - prevDrivePwr) * DRIVE_PWR_FILT;

		if (reverse) {

			// the encoder values of where we want to end up
			leftTarget = startleftencoder - left;
			rightTarget = startrightencoder - right;

			// PID to the target values
			drive.tankDrive((leftTarget - drive.leftDriveEncoder) * P, (rightTarget - drive.rightDriveEncoder) * P, prevDrivePwr);

			// return true when we have made it
			return leftTarget > drive.leftDriveEncoder && rightTarget > drive.rightDriveEncoder;

		} else {
			leftTarget = startleftencoder + left;
			rightTarget = startrightencoder + right;

			drive.tankDrive((leftTarget - drive.leftDriveEncoder) * P, (rightTarget - drive.rightDriveEncoder) * P, prevDrivePwr);
			return leftTarget < drive.leftDriveEncoder && rightTarget < drive.rightDriveEncoder;
		}
	}
}
