package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Inputs {

	private Joystick leftStick;
	private Joystick rightStick;
	private Joystick gamepad; 
	private Joystick controlBoard;

	//driver functions
	public double leftJoyStickY;
	public double rightJoyStickY;
	public double leftJoyStickX;
	public double rightJoyStickX;
	public boolean dynamicBrake;
	public boolean driveStraight;
	public boolean autoGear;
	public boolean autoShoot;
	public boolean autoClimb;
	
	
	//operator functions
	public boolean primeButton; 
	public boolean fireButton;
	public boolean fireOveride;
	public boolean climbButton;
	public boolean cameraEnable;
	public boolean reverseButton;
	public int gearPanelPostition;
	public boolean jogShooterPower;
	public double jogShooterValue;
	public int targetGearPost;
	public boolean gearIntake;
	public boolean gearOuttake;
	
	
	Inputs() {
		leftStick = new Joystick(ElectroPaul.LEFT_JOYSTICK_PORT);
		rightStick = new Joystick(ElectroPaul.RIGHT_JOYSTICK_PORT);
	}

	public void read() {
		leftJoyStickY = -leftStick.getY();
		rightJoyStickY = -rightStick.getY();
		dynamicBrake = leftStick.getTrigger();
		SmartDashboard.putNumber("leftStick", leftJoyStickY);
		SmartDashboard.putNumber("rightStick", rightJoyStickY);
		
		reverseButton = rightStick.getRawButton(2);
		climbButton = rightStick.getRawButton(1);
	}

}
