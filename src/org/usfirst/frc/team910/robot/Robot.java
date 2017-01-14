package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

	Inputs inputs;
	Outputs outputs;
	Sensors sense;

	DriveTrain drive;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		inputs = new Inputs();
		outputs = new Outputs();
		sense = new Sensors();

		drive = new DriveTrain(inputs, outputs, sense);
	}

	@Override
	public void autonomousInit() {

	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {

	}

	@Override
	public void teleopInit() {

	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		inputs.read();
		sense.read();
		drive.drive();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {

	}

	@Override
	public void disabledPeriodic() {
		inputs.read();
		sense.read();
	}

}
