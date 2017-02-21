package org.usfirst.frc.team910.robot.IO;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Outputs {

	private static final double CURRENT_LIMIT = 40; // in amps
	private static final double AMP_SECOND_LIMIT = 70;
	private static final double MIN_REST_TIME = 5;// in seconds
	private static final double DRIVE_INCH_PER_REV = 4*Math.PI;

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
	private PowerDistributionPanel pdp;

	public double leftDriveEncoder;
	public double rightDriveEncoder;
	public double shooterSpeedEncoder;
	public double transporterSpeedEncoder;
	public double agitatorSpeedEncoder;

	public double gearPanelPositionEncoder;

	public Outputs() {
		// leftMotor1 = new CANTalon(ElectroPaul.LEFT_MOTOR_PORT_1);
		// leftMotor2 = new CANTalon(ElectroPaul.LEFT_MOTOR_PORT_2);
		// rightMotor1 = new CANTalon(ElectroPaul.RIGHT_MOTOR_PORT_1);
		// rightMotor2 = new CANTalon(ElectroPaul.RIGHT_MOTOR_PORT_2);
		leftDriveCan = new CANTalon(ElectroPaul.LEFT_DRIVE_CAN);
		leftDriveCan.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		leftDriveCan.setEncPosition(0);
		leftDrive = new Talon(ElectroPaul.LEFT_DRIVE);
		rightDriveCan = new CANTalon(ElectroPaul.RIGHT_DRIVE_CAN);
		rightDriveCan.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		rightDriveCan.setEncPosition(0);
		rightDriveCan.setInverted(true);
		rightDrive = new Talon(ElectroPaul.RIGHT_DRIVE);
		rightDrive.setInverted(true);

		shooterMotor = new CANTalon(ElectroPaul.SHOOTER_MOTOR);
		shooterMotor.enableBrakeMode(false);
		shooterMotor.configPeakOutputVoltage(0, -12);
		shooterMotor.setCloseLoopRampRate(7);//7V per sec
		shooterMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		shooterMotor.reverseOutput(true);
		shooterMotor.reverseSensor(true);

		transporterMotor = new CANTalon(ElectroPaul.TRANSPORTER_MOTOR);
		agitatorMotor = new CANTalon(ElectroPaul.AGITATOR_MOTOR);
		climbMotor1 = new CANTalon(ElectroPaul.CLIMB_MOTOR_1);
		climbMotor2 = new CANTalon(ElectroPaul.CLIMB_MOTOR_2);
		gearRollerMotor = new CANTalon(ElectroPaul.GEAR_ROLLER_MOTOR);
		gearPanelMotor1 = new CANTalon(ElectroPaul.GEAR_PANEL_MOTOR_1);
		gearPanelMotor2 = new CANTalon(ElectroPaul.GEAR_PANEL_MOTOR_2);
		pdp = new PowerDistributionPanel(0);

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
		shooterMotor.changeControlMode(TalonControlMode.PercentVbus); //Lets shooter be set by power
		if (currentMonitor(ElectroPaul.SHOOTER_MOTOR)) { //Prevent fire hazard by monitoring current
			shooterMotor.set(0);
		} else {
			shooterMotor.set(power);
		}
	}

	public void setShooterSpeed(double speed) { //Lets shooter be powered by speed

		if (currentMonitor(ElectroPaul.SHOOTER_MOTOR)) { //Fire hazard prevention	
			shooterMotor.changeControlMode(TalonControlMode.PercentVbus);
			shooterMotor.set(0);
		} else {
			shooterMotor.changeControlMode(TalonControlMode.Speed);
			//shooterMotor.changeControlMode(TalonControlMode.PercentVbus);
			shooterMotor.set(speed);
		}
	}

	public void setTransportPower(double power) { //Lets transporter be set by power
		transporterMotor.changeControlMode(TalonControlMode.PercentVbus);
		if (currentMonitor(ElectroPaul.TRANSPORTER_MOTOR)) { //Fire hazard prevention
			transporterMotor.set(0);
		} else {
			transporterMotor.set(power);
		}
	}

	public void setTransportSpeed(double speed) { //Lets transporter be set by power
		if (currentMonitor(ElectroPaul.TRANSPORTER_MOTOR)) { //Fire hazard prevention
			transporterMotor.changeControlMode(TalonControlMode.PercentVbus); 
			transporterMotor.set(0);
		} else {
			transporterMotor.changeControlMode(TalonControlMode.Speed);
			transporterMotor.set(speed);
		}
	}

	public void setAgitatorPower(double power) { //Lets agitator be set by power
		agitatorMotor.changeControlMode(TalonControlMode.PercentVbus);
		agitatorMotor.set(power);
	}

	public void setAgitatorSpeed(double speed) { //Lets agitator be set by speed
		agitatorMotor.changeControlMode(TalonControlMode.Speed);
		agitatorMotor.set(speed);
	}

	public void setClimbPower(double power) { ////Lets climb be set by power
		if (currentMonitor(ElectroPaul.CLIMB_MOTOR_1) || currentMonitor(ElectroPaul.CLIMB_MOTOR_2)) { //Fire hazard prevention
			climbMotor1.set(0);
			climbMotor2.set(0);
		} else {
			climbMotor1.set(power);
			climbMotor2.set(power);
		}
	}

	public void setGearPanelPower(double power) {
		gearPanelMotor1.changeControlMode(TalonControlMode.PercentVbus);
		gearPanelMotor2.changeControlMode(TalonControlMode.PercentVbus);
		gearPanelMotor1.set(power);
		gearPanelMotor2.set(power);

	}

	public void setGearPanelSpeed(double speed) {
		gearPanelMotor1.changeControlMode(TalonControlMode.Speed);
		gearPanelMotor2.changeControlMode(TalonControlMode.Speed);
		gearPanelMotor1.set(speed);
		gearPanelMotor2.set(speed);
	}

	public void setGearRoller(double power) {
		gearRollerMotor.set(power);

	}

	public void setCameraLED(double power) {

	}

	private double[] currentSum = new double[16]; // sum of all previous current
	private double[] restEndTime = new double[16];

	public double maxCurr=0;
	private boolean currentMonitor(int motor) {
		double current = pdp.getCurrent(motor);
		if(current > maxCurr){
			maxCurr=current;
			SmartDashboard.putNumber("maxCurr", maxCurr);
		}
		if (current > CURRENT_LIMIT) {
			currentSum[motor] += current * 0.02;
			SmartDashboard.putNumber("Motor"+ motor + "current", current);
		} else {
			currentSum[motor] = 0;
		}
		if (currentSum[motor] > AMP_SECOND_LIMIT) {
			restEndTime[motor] = Timer.getFPGATimestamp() + MIN_REST_TIME;
			SmartDashboard.putNumber("Resting motor" + motor + "until: ", restEndTime[motor]);
		}
		return (restEndTime[motor] > Timer.getFPGATimestamp());
	}

	public void readEncoders() {
		leftDriveEncoder = leftDriveCan.getPosition() * DRIVE_INCH_PER_REV;
		rightDriveEncoder = rightDriveCan.getPosition()*DRIVE_INCH_PER_REV;
		shooterSpeedEncoder = shooterMotor.getSpeed();
		transporterSpeedEncoder = transporterMotor.getSpeed();
		agitatorSpeedEncoder = agitatorMotor.getSpeed();
		gearPanelPositionEncoder = gearPanelMotor1.getPosition();

		SmartDashboard.putNumber("leftDriveEncoder", leftDriveEncoder);
		SmartDashboard.putNumber("rightDriveEncoder", rightDriveEncoder);
		SmartDashboard.putNumber("shooterSpeedEncoder", shooterSpeedEncoder);
		SmartDashboard.putNumber("transporterSpeedEncoder", transporterSpeedEncoder);
		SmartDashboard.putNumber("agitatorSpeedEncoder", agitatorSpeedEncoder);
		SmartDashboard.putNumber("gearPanelPositionEncoder", gearPanelPositionEncoder);

	}
}
