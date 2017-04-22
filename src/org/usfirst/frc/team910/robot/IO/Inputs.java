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
	public double leftJoyStickY = 0;
	public double rightJoyStickY = 0;
	public double leftJoyStickX = 0;
	public double rightJoyStickX = 0;
	public boolean dynamicBrake = false;
	public boolean driveStraight = false;
	public boolean autoGear = false;
	public boolean autoShoot = false;
	public boolean autoShootNoCam = false;
	public boolean autoClimb = false;
	public boolean autoHopper = false;//4 left Work in progress
	public boolean autoStraight = false;
	public boolean autoDeliverer = false;
	public boolean driveJogRight2 = false;
	public boolean driveJogLeft4 = false; //added 4/17
	public boolean driveJogLeft2 = false;
	public boolean driveJogRight4 = false;
	public boolean driveJogRunning = false; //added 4/17, not used on Joystick
	public boolean driveJogLeft8  = false; //added 4/20
	public boolean driveJogRight8 = false; //added 4/20
	
	// operator functions
	public boolean primeButton = false;
	public boolean fireButton = false;
	public boolean fireOverride = false;
	public boolean climbButton = false;
	public boolean cameraEnable = false;
	public boolean reverseButton = false;
	public int gearPanelPosition = 0;
	public boolean jogShooterUp = false;
	public boolean jogShooterDown = false;
	public int targetGearPost = 0;
	public boolean gearIntake = false;
	public boolean gearOuttake = false;
	public boolean autoMode = false;
	public boolean manualMode = false;
	public boolean shift = false;
	public int autonSelection = 0;

	public Inputs() { //creates joysticks that are plugged into the port specified in ElectroPaul
		leftStick = new Joystick(ElectroPaul.LEFT_JOYSTICK_PORT);
		rightStick = new Joystick(ElectroPaul.RIGHT_JOYSTICK_PORT);
		gamepad = new Joystick(ElectroPaul.GAME_PAD_JOYSTICK_PORT);
		controlBoard = new Joystick(ElectroPaul.CONTROL_BOARD_JOYSTICK_PORT);
		//driverGamepad = new Joystick(ElectroPaul.DRIVER_GAME_PAD_PORT);
	}
 
	public void read() {
		// driver functions
			
		//double xboxLY = deadband(DEADBAND_XBOX, driverGamepad.getRawAxis(1));
		//double xboxRX = deadband(DEADBAND_XBOX, driverGamepad.getRawAxis(4));
		driveJogLeft4 = leftStick.getRawButton(3);
		driveJogRight4 = leftStick.getRawButton(4);
		driveJogLeft2 = leftStick.getRawButton(5);
		driveJogRight2 = leftStick.getRawButton(6);
		
		int tmpPOV = leftStick.getPOV(); 						//Get value of left joystick POV (hat) button
		driveJogRight8 = false;									//Default left8 to false (note: actual POV button value is -1 when not pressed.
		if (tmpPOV > 0 && tmpPOV < 180) driveJogRight8 = true;	//It'll be 45, 90 or 135 if anywhere to the RIGHT 
		driveJogLeft8 = false;									//Default right8 to false (actual POV button value is -1 when not pressed.
		if (tmpPOV > 180 && tmpPOV < 360) driveJogLeft8 = true;	//It'll be 225, 270 or 315 if anywhere to the LEFT
		
		//SmartDashboard.putBoolean("driveJogLeft8",  driveJogLeft8);
		//SmartDashboard.putBoolean("driveJogRight8", driveJogRight8);
		
		leftJoyStickY = deadband(DEADBAND, -leftStick.getY()); //joystick with deadband taken into account
		rightJoyStickY = deadband(DEADBAND, -rightStick.getY());
		leftJoyStickX = deadband(DEADBAND, leftStick.getX());
		rightJoyStickX = deadband(DEADBAND, rightStick.getX());
		dynamicBrake = false; //leftStick.getTrigger();
		driveStraight = rightStick.getTrigger();
		//autoGear = rightStick.getRawButton(4);
		autoShoot = rightStick.getRawButton(5) || rightStick.getRawButton(6);
		autoShootNoCam = rightStick.getRawButton(6);
		//autoClimb = leftStick.getRawButton(5);
		//autoStraight = rightStick.getRawButton(3); //TODO Make autoStraight toggle
		autoDeliverer= leftStick.getRawButton(1);
		
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
		
		if(controlBoard.getRawButton(15) || gamepad.getRawButton(1)){
			gearPanelPosition = 1;
		} else if (controlBoard.getRawButton(16) || gamepad.getRawButton(4)){
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
