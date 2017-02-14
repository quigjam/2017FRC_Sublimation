package org.usfirst.frc.team910.robot.IO;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Inputs {

	private static final double DEADBAND = 0.1;

	private Joystick leftStick;
	private Joystick rightStick;
	private Joystick gamepad;
	private Joystick controlBoard;

	// driver functions
	public double leftJoyStickY;
	public double rightJoyStickY;
	public double leftJoyStickX;
	public double rightJoyStickX;
	public boolean dynamicBrake;
	public boolean driveStraight;
	public boolean autoGear;
	public boolean autoShoot;
	public boolean autoClimb;// 5 left

	// operator functions
	public boolean primeButton;
	public boolean fireButton;
	public boolean fireOverride;
	public boolean climbButton;
	public boolean cameraEnable;
	public boolean reverseButton;
	public int gearPanelPostition;
	public boolean jogShooterUp;
	public boolean jogShooterDown;
	public int targetGearPost;
	public boolean gearIntake;
	public boolean gearOuttake;

	public Inputs() {
		leftStick = new Joystick(ElectroPaul.LEFT_JOYSTICK_PORT);
		rightStick = new Joystick(ElectroPaul.RIGHT_JOYSTICK_PORT);
		gamepad = new Joystick(ElectroPaul.GAME_PAD_JOYSTICK_PORT);
		controlBoard = new Joystick(ElectroPaul.CONTROL_BOARD_JOYSTICK_PORT);
	}

	public void read() {
		// driver functions
		leftJoyStickY = deadband(DEADBAND, -leftStick.getY());
		rightJoyStickY = deadband(DEADBAND, -rightStick.getY());
		leftJoyStickX = deadband(DEADBAND, leftStick.getX());
		rightJoyStickX = deadband(DEADBAND, rightStick.getX());
		dynamicBrake = leftStick.getTrigger();
		driveStraight = rightStick.getTrigger();
		autoGear = rightStick.getRawButton(4);
		autoShoot = rightStick.getRawButton(5);
		autoClimb = leftStick.getRawButton(5);

		// operator functions
		primeButton = controlBoard.getRawButton(1) || gamepad.getRawButton(5);

		fireButton = controlBoard.getRawButton(2) || gamepad.getRawButton(6);
		fireOverride = controlBoard.getRawButton(3) || gamepad.getRawButton(3);
		climbButton = controlBoard.getRawButton(4);
		cameraEnable = controlBoard.getRawButton(4) || gamepad.getRawButton(4);
		reverseButton = controlBoard.getRawButton(5) || gamepad.getRawButton(5);
		gearIntake = controlBoard.getRawButton(6) || gamepad.getRawButton(6);
		gearOuttake = controlBoard.getRawButton(7) || gamepad.getRawButton(7);
		jogShooterUp = controlBoard.getRawButton(8) || gamepad.getRawAxis(3) > 0.9;
		jogShooterDown = controlBoard.getRawButton(9) || gamepad.getRawAxis(2) > 0.9;

	}

	public double deadband(double deadband, double joyPos) {
		if (Math.abs(joyPos) < deadband) {
			joyPos = 0;
		}
		return (joyPos - deadband) / (1 - deadband);
	}

}
