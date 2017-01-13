package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Inputs {

	private Joystick leftStick;
	private Joystick rightStick;
	
	public double leftJoyStickY;
	public double rightJoyStickY;

	Inputs() {
		leftStick = new Joystick(ElectroPaul.LEFT_JOYSTICK_PORT);
		rightStick = new Joystick(ElectroPaul.RIGHT_JOYSTICK_PORT);
	}

	public void read() {
		leftJoyStickY = leftStick.getY();
		rightJoyStickY = rightStick.getY();

	}

}
