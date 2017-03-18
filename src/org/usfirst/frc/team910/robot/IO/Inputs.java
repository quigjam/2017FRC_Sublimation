package org.usfirst.frc.team910.robot.IO;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Inputs {

	private static final double DEADBAND = 0.1;
	private static final double DEADBAND_XBOX = 0.2;

	private Joystick leftStick;
	private  Joystick rightStick;
	private Joystick gamepad;
	private Joystick controlBoard;
	private Joystick driverGamepad;

	// driver functions
	public double leftJoyStickY;
	public double rightJoyStickY;
	public double leftJoyStickX;
	public double rightJoyStickX;
	public boolean dynamicBrake;
	public boolean driveStraight;
	public boolean autoGear;
	public boolean autoShoot;
	public boolean autoShootNoCam;
	public boolean autoClimb;
	public boolean autoHopper;//4 left Work in progress
	public boolean autoStraight;

	// operator functions
	public boolean primeButton;
	public boolean fireButton;
	public boolean fireOverride;
	public boolean climbButton;
	public boolean cameraEnable;
	public boolean reverseButton;
	public int gearPanelPosition;
	public boolean jogShooterUp;
	public boolean jogShooterDown;
	public int targetGearPost;
	public boolean gearIntake;
	public boolean gearOuttake;
	public boolean autoMode;
	public boolean manualMode;
	public boolean shift;
	public int autonSelection;

	public Inputs() { //creates joysticks that are plugged into the port specified in ElectroPaul
		leftStick = new Joystick(ElectroPaul.LEFT_JOYSTICK_PORT);
		rightStick = new Joystick(ElectroPaul.RIGHT_JOYSTICK_PORT);
		//gamepad = new Joystick(ElectroPaul.GAME_PAD_JOYSTICK_PORT);
		controlBoard = new Joystick(ElectroPaul.CONTROL_BOARD_JOYSTICK_PORT);
		//driverGamepad = new Joystick(ElectroPaul.DRIVER_GAME_PAD_PORT);
	}
 
	public void read() {
		// driver functions
			
		//double xboxLY = deadband(DEADBAND_XBOX, driverGamepad.getRawAxis(1));
		//double xboxRX = deadband(DEADBAND_XBOX, driverGamepad.getRawAxis(4));
		
		leftJoyStickY = deadband(DEADBAND, -leftStick.getY()); //joystick with deadband taken into account
		rightJoyStickY = deadband(DEADBAND, -rightStick.getY());
		leftJoyStickX = deadband(DEADBAND, leftStick.getX());
		rightJoyStickX = deadband(DEADBAND, rightStick.getX());
		dynamicBrake = leftStick.getTrigger();
		driveStraight = rightStick.getTrigger();
		autoGear = rightStick.getRawButton(4);
		autoShoot = rightStick.getRawButton(5) || rightStick.getRawButton(6);
		autoShootNoCam = rightStick.getRawButton(6);
		autoClimb = leftStick.getRawButton(5);
		autoStraight = rightStick.getRawButton(3); //TODO Make autoStraight toggle
		
		//if the xbox controller is active, override with drive straight and drive with it
		/*if(xboxLY > 0|| xboxRX > 0){
			leftJoyStickX = xboxRX;
			rightJoyStickY = xboxLY;
			driveStraight = true;
		}*/
		
		// operator functions
		//Change later for competition board. Check Steven's phone
		primeButton = controlBoard.getRawButton(6) ;//|| gamepad.getRawButton(5); 
		fireButton = controlBoard.getRawButton(5) ;//|| gamepad.getRawButton(6); 
		fireOverride = controlBoard.getRawButton(14) ;//|| gamepad.getRawButton(3);
		climbButton = controlBoard.getRawButton(1);
		cameraEnable = controlBoard.getRawButton(7) ;//|| gamepad.getRawButton(4);
		reverseButton = controlBoard.getRawButton(2) ;//|| gamepad.getRawButton(5);
		gearIntake = controlBoard.getRawButton(12) ;//|| gamepad.getRawButton(6);
		gearOuttake = controlBoard.getRawButton(13) ;//|| gamepad.getRawButton(7);
		jogShooterUp = controlBoard.getRawButton(17) ;//|| gamepad.getRawAxis(3) > 0.9;
		jogShooterDown = controlBoard.getRawButton(18) ;//|| gamepad.getRawAxis(2) > 0.9;
		manualMode = controlBoard.getRawButton(14);
		autoMode = !manualMode;
		shift = controlBoard.getRawButton(3);
		
		if(controlBoard.getRawButton(15)){
			gearPanelPosition = 1;
		} else if (controlBoard.getRawButton(16)){
			gearPanelPosition = 3;
		} else {
			gearPanelPosition = 2;
		}
		
		if(controlBoard.getRawButton(8)){
			//RIGHT
			autonSelection = 3;
		} else if(controlBoard.getRawButton(9)){
			//LEFT
			autonSelection = 1;
		} else {
			//CENTER
			autonSelection = 2;
		}

	}

	public double deadband(double deadband, double joyPos) { //Creates a deadzone that prevents overly sensitive inputs
		if (Math.abs(joyPos) < deadband) {
			return(0);
		}else if (joyPos > 0){
			return (joyPos - deadband) / (1 - deadband);
		} else {
			return (joyPos + deadband) / (1 - deadband);
		}
	}
}
