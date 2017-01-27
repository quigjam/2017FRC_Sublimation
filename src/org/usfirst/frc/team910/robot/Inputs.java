package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Inputs {

	private Joystick leftStick;
	private Joystick rightStick;

	public double leftJoyStickY;
	public double rightJoyStickY;
	public boolean leftTrigger;
	
	public int targetGearPost; //TODO Placeholder, figure out how operator controls this

	Inputs() {
		leftStick = new Joystick(ElectroPaul.LEFT_JOYSTICK_PORT);
		rightStick = new Joystick(ElectroPaul.RIGHT_JOYSTICK_PORT);
	}

	public void read() {
		leftJoyStickY = -leftStick.getY();
		rightJoyStickY = -rightStick.getY();
		leftTrigger = leftStick.getTrigger();
		SmartDashboard.putNumber("leftStick", leftJoyStickY);
		SmartDashboard.putNumber("rightStick", rightJoyStickY);
	}

}
