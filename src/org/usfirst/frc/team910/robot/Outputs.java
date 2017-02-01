package org.usfirst.frc.team910.robot;

import com.ctre.CANTalon;

public class Outputs {
	private CANTalon leftMotor1;
	private CANTalon leftMotor2;
	private CANTalon rightMotor1;
	private CANTalon rightMotor2;
	private CANTalon shooterMotor;
	private CANTalon hopperFeedMotor;
	private CANTalon flapMotor;
	private CANTalon climbMotor;

	Outputs() {
		leftMotor1 = new CANTalon(ElectroPaul.LEFT_MOTOR_PORT_1);
		leftMotor2 = new CANTalon(ElectroPaul.LEFT_MOTOR_PORT_2);
		rightMotor1 = new CANTalon(ElectroPaul.RIGHT_MOTOR_PORT_1);
		rightMotor2 = new CANTalon(ElectroPaul.RIGHT_MOTOR_PORT_2);
		shooterMotor = new CANTalon(ElectroPaul.SHOOTER_MOTOR_PORT);
	}

	public void setLeftDrive(double power) {
		leftMotor1.set(-power);
		leftMotor2.set(-power);
	}

	public void setRightDrive(double power) {
		rightMotor1.set(power);
		rightMotor2.set(power);
	}

	public void setShooterPower(double power) {
		shooterMotor.set(power);
	}

	public void setHopperFeedPower(double power) {
		hopperFeedMotor.set(power);
	}

	public void setFlapMotor(double power) {
		hopperFeedMotor.set(power);
	}

	public void setClimbPower(double power) {
		climbMotor.set(power);

	}
}
