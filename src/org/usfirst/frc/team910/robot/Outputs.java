package org.usfirst.frc.team910.robot;

import com.ctre.CANTalon;

public class Outputs {
	private CANTalon leftMotor1;
	private CANTalon leftMotor2;
	private CANTalon rightMotor1;
	private CANTalon rightMotor2;

	Outputs() {
		leftMotor1 = new CANTalon(ElectroPaul.LEFT_MOTOR_PORT_1);
		leftMotor2 = new CANTalon(ElectroPaul.LEFT_MOTOR_PORT_2);
		rightMotor1 = new CANTalon(ElectroPaul.RIGHT_MOTOR_PORT_1);
		rightMotor2 = new CANTalon(ElectroPaul.RIGHT_MOTOR_PORT_2);
	}

	public void setLeftDrive(double power) {
		leftMotor1.set(power);
		leftMotor2.set(power);
	}

	public void setRightDrive(double power) {
		rightMotor1.set(power);
		rightMotor2.set(power);
	}
}
