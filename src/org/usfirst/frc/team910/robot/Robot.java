package org.usfirst.frc.team910.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends IterativeRobot {

	Joystick leftJoy;
	Joystick rightJoy;
	CANTalon motorLF;
	CANTalon motorRF;
	CANTalon motorLR;
	CANTalon motorRR;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		leftJoy = new Joystick(0);
		rightJoy = new Joystick(1);
		motorLF = new CANTalon(0);
		motorRF = new CANTalon(1);
		motorLR = new CANTalon(2);
		motorRR = new CANTalon(3);
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
		double leftPwr = leftJoy.getY();
		double rightPwr = rightJoy.getY();

		motorLF.set(leftPwr);
		motorRF.set(-rightPwr);
		motorLR.set(leftPwr);
		motorRR.set(-rightPwr);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {

	}
}
