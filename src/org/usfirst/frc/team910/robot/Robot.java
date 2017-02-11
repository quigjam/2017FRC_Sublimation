package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

	Inputs in;
	Outputs out;
	Sensors sense;

	DriveTrain drive;
	Shooter shoot;
	GearSystem gear;
	Climber climb;

	AutoClimb autoClimb;
	AutoGear autoGear;
	AutoShoot autoShoot;

	/**
	 * This function is run when the robot is first started up and should be used for any initialization code.
	 */
	@Override
	public void robotInit() {
		in = new Inputs();
		out = new Outputs();
		sense = new Sensors();

		drive = new DriveTrain(in, out, sense);
		shoot = new Shooter(out, in);
		gear = new GearSystem();
		climb = new Climber(out, in);

		autoClimb = new AutoClimb(in, sense, drive, climb);
		autoGear = new AutoGear(in, sense, drive, gear);
		autoShoot = new AutoShoot(out, in, shoot);
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
		in.read();
		sense.read();
		out.readEncoders();
		drive.drive();
		shoot.run();
		// gear.run();
		// climb.run();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {

	}

	@Override
	public void disabledPeriodic() {
		in.read();
		sense.read();
		out.readEncoders();
	}

}
