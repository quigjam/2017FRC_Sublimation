package org.usfirst.frc.team910.robot;

public class DriveTrain {

	private static final double DRIVE_STRAIGHT_ENC_PWR = 0.1;
	private static final double DYN_BRAKE_PWR = 0.1; //full power in 10 inches 
	
	private Inputs in;
	private Outputs out;
	private Sensors sense;
	
	public DriveTrain(Inputs in, Outputs out, Sensors sense) {
		this.in = in;
		this.out = out;
		this.sense = sense;
	}

	// this is the main function called from robot
	boolean braking;  
	public void drive() {	
		if(in.leftTrigger) {
			dynamicBrake(!braking);
			braking = true;
		}
		else { 
			tankDrive(in.leftJoyStickY, in.rightJoyStickY);
			braking = false;
		}
	}

	private void tankDrive(double leftPower, double rightPower) {
		out.setLeftDrive(leftPower);
		out.setRightDrive(rightPower);
	}

	private double leftEncPrev;
	private double rightEncPrev;

	private void dynamicBrake(boolean firstTime) {
		double leftEncoder = sense.leftEncoder;
		double rightEncoder = sense.rightEncoder;

		if (firstTime) {
			leftEncPrev = leftEncoder;
			rightEncPrev = rightEncoder;
		} else {
			double leftEncDiff = leftEncPrev - leftEncoder;
			double rightEncDiff = rightEncPrev - rightEncoder;

			tankDrive(leftEncDiff * DYN_BRAKE_PWR, rightEncDiff * DYN_BRAKE_PWR);
		}
		
	}
	
	private double initialEncDiff; 
	
	private void driveStraightEnc(boolean firstTime) {
		
		if (firstTime) {
			initialEncDiff = sense.leftEncoder - sense.rightEncoder;
		}else{
			double currentEncDiff = sense.leftEncoder - sense.rightEncoder;
			double DiffDiff = (initialEncDiff - currentEncDiff) * DRIVE_STRAIGHT_ENC_PWR;
			
			tankDrive(in.rightJoyStickY + DiffDiff,in.rightJoyStickY - DiffDiff );
		}
		
	}
}	