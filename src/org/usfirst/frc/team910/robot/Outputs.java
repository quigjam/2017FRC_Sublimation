package org.usfirst.frc.team910.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Talon;

public class Outputs {
	// private CANTalon leftMotor1;
	// private CANTalon leftMotor2;
	// private CANTalon rightMotor1;
	// private CANTalon rightMotor2;
	private CANTalon leftDriveCan;
	private CANTalon rightDriveCan;
	private Talon leftDrive;
	private Talon rightDrive;
	private CANTalon shooterMotor;
	private CANTalon transporterMotor;
	private CANTalon agitatorMotor;
	private CANTalon climbMotor1;
	private CANTalon climbMotor2;
	private CANTalon gearRollerMotor;
	private CANTalon gearPanelMotor1;
	private CANTalon gearPanelMotor2;

	Outputs() {
		// leftMotor1 = new CANTalon(ElectroPaul.LEFT_MOTOR_PORT_1);
		// leftMotor2 = new CANTalon(ElectroPaul.LEFT_MOTOR_PORT_2);
		// rightMotor1 = new CANTalon(ElectroPaul.RIGHT_MOTOR_PORT_1);
		// rightMotor2 = new CANTalon(ElectroPaul.RIGHT_MOTOR_PORT_2);
		leftDriveCan = new CANTalon(ElectroPaul.LEFT_DRIVE_CAN);
		rightDriveCan = new CANTalon(ElectroPaul.RIGHT_DRIVE_CAN);
		leftDrive = new Talon(ElectroPaul.LEFT_DRIVE);
		rightDrive = new Talon(ElectroPaul.RIGHT_DRIVE);
		shooterMotor = new CANTalon(ElectroPaul.SHOOTER_MOTOR);
		transporterMotor =  new CANTalon(ElectroPaul.TRANSPORTER_MOTOR);
		agitatorMotor = new CANTalon(ElectroPaul.AGITATOR_MOTOR);
		climbMotor1 = new CANTalon(ElectroPaul.CLIMB_MOTOR_1);
		climbMotor2 = new CANTalon(ElectroPaul.CLIMB_MOTOR_2);
		gearRollerMotor = new CANTalon(ElectroPaul.GEAR_ROLLER_MOTOR);
		gearPanelMotor1 = new CANTalon(ElectroPaul.GEAR_PANEL_MOTOR_1);
		gearPanelMotor2 = new CANTalon(ElectroPaul.GEAR_PANEL_MOTOR_2);
		
	}

	public void setLeftDrive(double power) {
		// leftMotor1.set(-power);
		// leftMotor2.set(-power);
		leftDriveCan.set(power);
		leftDrive.set(power);
	}

	public void setRightDrive(double power) {
		// rightMotor1.set(power);
		// rightMotor2.set(power);
		rightDriveCan.set(power);
		rightDrive.set(power);
	}

	public void setShooterPower(double power) {
		shooterMotor.set(power);
	}

	public void setFeedPower(double power) {
		transporterMotor.set(power);
	}

	public void setAgitatorPower(double power) {
		agitatorMotor.set(power);
	}

	public void setClimbPower(double power) {
		climbMotor1.set(power);
		climbMotor2.set(power);
	}

	public void setGearArm(double power) {
		gearPanelMotor1.set(power);
		gearPanelMotor2.set(power);

	}

	public void setGearRoller(double power) {
		gearRollerMotor.set(power);

	}

	public void setCameraLED(double power) {

	}

}
