package org.usfirst.frc.team910.robot.IO;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Outputs {

	private static final double CURRENT_LIMIT = 90; // in amps
	private static final double AMP_SEC_LIMIT = 70; //in amp*sec 
	private static final double MIN_REST_TIME = 5;// in seconds
	private static final double DRIVE_INCH_PER_REV = 4*Math.PI;
	
	//private static final double SHOOTER_SPEED_LIMIT = 500; //when to ramp to full power

	// private CANTalon leftMotor1;
	// private CANTalon leftMotor2;
	// private CANTalon rightMotor1;
	// private CANTalon rightMotor2;
	private CANTalon leftDriveCan1;
	private CANTalon leftDriveCan2;
	private CANTalon leftDriveCan3;
	private CANTalon rightDriveCan1;
	private CANTalon rightDriveCan2;
	private CANTalon rightDriveCan3;
	//private Talon leftDrive;
	//private Talon rightDrive;
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
	public double leftDriveSpeed;
	public double rightDriveSpeed;
	public double shooterSpeedEncoder;
	public double transporterSpeedEncoder;
	public double agitatorSpeedEncoder;
	public double gearIntakeCurrent;

	public double gearPanelPositionPotL;
	public double gearPanelPositionPotR;
	
	public double gearPanel1Current;
	public double gearPanel2Current;
	public double climb1Current;
	public double climb2Current;

	public Outputs() {
		// leftMotor1 = new CANTalon(ElectroPaul.LEFT_MOTOR_PORT_1);
		// leftMotor2 = new CANTalon(ElectroPaul.LEFT_MOTOR_PORT_2);
		// rightMotor1 = new CANTalon(ElectroPaul.RIGHT_MOTOR_PORT_1);
		// rightMotor2 = new CANTalon(ElectroPaul.RIGHT_MOTOR_PORT_2);
		leftDriveCan1 = new CANTalon(ElectroPaul.LEFT_DRIVE_CAN1);		
		leftDriveCan1.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		leftDriveCan1.setEncPosition(0);
		leftDriveCan1.reverseSensor(true);
		leftDriveCan2 = new CANTalon(ElectroPaul.LEFT_DRIVE_CAN2);
		leftDriveCan3 = new CANTalon(ElectroPaul.LEFT_DRIVE_CAN3);
		//leftDrive = new Talon(ElectroPaul.LEFT_DRIVE);
		
		rightDriveCan1 = new CANTalon(ElectroPaul.RIGHT_DRIVE_CAN1);
		rightDriveCan1.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		rightDriveCan1.setEncPosition(0);
		rightDriveCan1.setInverted(true);
		rightDriveCan2 = new CANTalon(ElectroPaul.RIGHT_DRIVE_CAN2);
		rightDriveCan2.setInverted(true);
		rightDriveCan3 = new CANTalon(ElectroPaul.RIGHT_DRIVE_CAN3);
		rightDriveCan3.setInverted(true);
		//rightDrive = new Talon(ElectroPaul.RIGHT_DRIVE);
		//rightDrive.setInverted(true);

		shooterMotor = new CANTalon(ElectroPaul.SHOOTER_MOTOR);
		shooterMotor.enableBrakeMode(false);
		shooterMotor.configPeakOutputVoltage(0, -12);
		shooterMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		shooterMotor.setInverted(false);
		shooterMotor.reverseOutput(true);
		shooterMotor.reverseSensor(true); 
		//shooterMotor.setCloseLoopRampRate(7);//7V per sec done below
		//shooterMotor.setPID(0.65, 0, 1.15, 0.062, 0, 28, 0);
		//shooterMotor.setPID(1, 0, 1.15, 0.062, 0, 40, 0); //OLD PID with Shepherd with 9:1 Shooter Trans!  
		//shooterMotor.setPID(2.5, 0, 2, 0.0318, 0, 0, 0); //PID, FFwd set for 1800rpm, 4:1 Trans  
		shooterMotor.setPID(2.4, 0, 2.1, 0.0318, 0, 0, 0); //PID, FFwd set for 1740rpm, 4:1 Trans    1785 actual rpm at 37" hit 26/30
	
	//shooterMotor.setPID(99999, 0, 0, 0, 0, 0, 0); //bang bang PID settings
	
		shooterMotor.configPeakOutputVoltage(0, -6); //half power to start bang bang

		transporterMotor = new CANTalon(ElectroPaul.TRANSPORTER_MOTOR);
		transporterMotor.enableBrakeMode(true);
		transporterMotor.setInverted(true);
		transporterMotor.reverseOutput(false);
		transporterMotor.reverseSensor(true);
		transporterMotor.configPeakOutputVoltage(0, -12);
		transporterMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		transporterMotor.setPID(0.025, 0, 0.03, 0.04, 0, 0, 0);
		
		
		agitatorMotor = new CANTalon(ElectroPaul.AGITATOR_MOTOR);
		agitatorMotor.enableBrakeMode(false);
		agitatorMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		agitatorMotor.setInverted(true);
		agitatorMotor.reverseOutput(true);
		agitatorMotor.reverseSensor(false);
		agitatorMotor.setPID(0, 0.0001, 0, 0.038, 0, 0, 0);
		//agitatorMotor.setVoltageRampRate(12);
		
		climbMotor1 = new CANTalon(ElectroPaul.CLIMB_MOTOR_1);
		climbMotor2 = new CANTalon(ElectroPaul.CLIMB_MOTOR_2);
		climbMotor1.enableBrakeMode(true);
		climbMotor2.enableBrakeMode(true);
		gearRollerMotor = new CANTalon(ElectroPaul.GEAR_ROLLER_MOTOR);
		gearRollerMotor.reverseOutput(true);
		gearRollerMotor.setInverted(true);
		
		gearPanelMotor1 = new CANTalon(ElectroPaul.GEAR_PANEL_MOTOR_1);
		gearPanelMotor1.enableBrakeMode(false);
		gearPanelMotor1.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
		gearPanelMotor1.configPeakOutputVoltage(10, -10);
		gearPanelMotor1.setPID(7,0,30);//(7, 0, 30);
		//gearPanelMotor1.reverseOutput(true);
		//gearPanelMotor1.reverseSensor(true);
		gearPanelMotor2 = new CANTalon(ElectroPaul.GEAR_PANEL_MOTOR_2);
		gearPanelMotor2.enableBrakeMode(false);
		gearPanelMotor2.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
		gearPanelMotor2.configPeakOutputVoltage(10, -10);
		gearPanelMotor2.setPID(7,0,30);//(7, 0, 30);
		//gearPanelMotor2.reverseOutput(true);
		gearPanelMotor2.reverseSensor(true);
		
		
		
		pdp = new PowerDistributionPanel(0);

	}

	public void setLeftDrive(double power) {
		// leftMotor1.set(-power);
		// leftMotor2.set(-power);
		leftDriveCan1.set(power*limitValue);
		leftDriveCan2.set(power*limitValue);
		leftDriveCan3.set(power*limitValue);
		//leftDrive.set(power);
	}

	public void setRightDrive(double power) {
		// rightMotor1.set(power);
		// rightMotor2.set(power);
		rightDriveCan1.set(power*limitValue);
		rightDriveCan2.set(power*limitValue);
		rightDriveCan3.set(power*limitValue);
		//rightDrive.set(power);
	}
	
	public void setDriveBrake(boolean brake){
		//disabled brake mode
		//UNDID this for MSC brake = false;
		leftDriveCan1.enableBrakeMode(brake);
		leftDriveCan2.enableBrakeMode(brake);
		leftDriveCan3.enableBrakeMode(brake);
		rightDriveCan1.enableBrakeMode(brake);
		rightDriveCan2.enableBrakeMode(brake);
		rightDriveCan3.enableBrakeMode(brake);
	}

	public void setShooterPower(double power) {
		shooterMotor.changeControlMode(TalonControlMode.PercentVbus); //Lets shooter be set by power
		if (currentMonitor(ElectroPaul.SHOOTER_MOTOR) || limitActive) { //Prevent fire hazard by monitoring current
			shooterMotor.set(0);
		} else {
			shooterMotor.set(power);
		}
	}

	public void setShooterSpeed(double speed) { //Lets shooter be powered by speed

		if (currentMonitor(ElectroPaul.SHOOTER_MOTOR) || limitActive) { //Fire hazard prevention	
			shooterMotor.changeControlMode(TalonControlMode.PercentVbus);
			shooterMotor.set(0);
		} else {
			shooterMotor.changeControlMode(TalonControlMode.Speed);
			//shooterMotor.changeControlMode(TalonControlMode.PercentVbus);
			shooterMotor.set(speed);
			
			//bang bang
			if(shooterMotor.getSpeed() > 800){
				shooterMotor.configPeakOutputVoltage(0, -12);
			} else if(shooterMotor.getSpeed() > 600) {
				shooterMotor.configPeakOutputVoltage(0, -10); 
			} else if(shooterMotor.getSpeed() > 400) {
				shooterMotor.configPeakOutputVoltage(0, -8); 
			} else {
				shooterMotor.configPeakOutputVoltage(0, -6); 
			}
			
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
			//transporterMotor.set(speed);
			transporterMotor.setSetpoint(speed);
		}
	}

	public void setAgitatorPower(double power) { //Lets agitator be set by power
		agitatorMotor.changeControlMode(TalonControlMode.PercentVbus);
		agitatorMotor.set(power*limitValue);
	}

	public void setAgitatorSpeed(double speed) { //Lets agitator be set by speed
		
		if(limitActive){
			setAgitatorPower(0);
		} else {
			agitatorMotor.changeControlMode(TalonControlMode.Speed);
			if (speed <= 0){
				agitatorMotor.configPeakOutputVoltage(0, -12);
			} else {
				agitatorMotor.configPeakOutputVoltage(12, 0);
			}
			agitatorMotor.set(speed);	
		}
	}

	public void setClimbPower(double power) { ////Lets climb be set by power
		if(power != 0){
			SmartDashboard.putNumber("ClimberMotor1Current", pdp.getCurrent(ElectroPaul.CLIMB_MOTOR_1));//Just for testing 3/12/17
			SmartDashboard.putNumber("ClimberMotor2Current", pdp.getCurrent(ElectroPaul.CLIMB_MOTOR_2));//Just for testing 3/12/17
		}
		
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

	public void setGearPanelPosition(double posL, double posR) {
		gearPanelMotor1.changeControlMode(TalonControlMode.Position);
		gearPanelMotor2.changeControlMode(TalonControlMode.Position);
		gearPanelMotor1.set(posL);
		gearPanelMotor2.set(posR);
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
			SmartDashboard.putNumber("MotorSum" + motor, currentSum[motor]);
			SmartDashboard.putNumber("Motor"+ motor + "current", current);
		} else {
			currentSum[motor] = 0;
			//SmartDashboard.putNumber("Motor"+ motor + "current", current);
		}
		if (currentSum[motor] > AMP_SEC_LIMIT) {
			restEndTime[motor] = Timer.getFPGATimestamp() + MIN_REST_TIME;
			SmartDashboard.putNumber("Resting motor" + motor + "until: ", restEndTime[motor]);
		}
		return (restEndTime[motor] > Timer.getFPGATimestamp());
	}
	
	
	//commented out to continue later, maybe, kindof
	/*
	double[] prevCurr = new double[50];
	int prevCurrIdx = 0;
	double avgCurr1s = 0;
	double MAX_1s_CURR = 5*120;
	
	double[] prev1sCurrAvg = new double[5];
	int prev1sCurrIdx = 0;
	double avgCurr5s = 0;
	double MAX_5s_CURR = 3*120;
	
	double[] prev5sCurrAvg = new double[2];
	int prev5sCurrIdx = 0;
	double avgCurr10s = 0;
	double MAX_10s_CURR = 2*120;
	
	double[] prev10sCurrAvg = new double[5];
	int prev10sCurrIdx = 0;
	double avgCurr50s = 0;
	double MAX_50s_CURR = 1.5*120;
	
	double[] prev50sCurrAvg = new double[2];
	int prev50sCurrIdx = 0;
	double avgCurr100s = 0;
	double MAX_100s_CURR = 1.25*120;
	
	
	public void protectBreaker(){
		double totalCurr = pdp.getTotalCurrent() * (1/50);
		
		//handle instant current avg array
		avgCurr1s += totalCurr;
		avgCurr1s -= prevCurr[prev1sCurrIdx];
		prevCurr[prev1sCurrIdx] = totalCurr;
		prev1sCurrIdx++;
		if(prev1sCurrIdx >= prevCurr.length) {
			prev1sCurrIdx = 0;
			
			//handle 1s current avg array
			avgCurr5s += avgCurr1s;
			avgCurr5s -= prev1sCurrAvg[prev5sCurrIdx];
			prev1sCurrAvg[prev5sCurrIdx] = avgCurr1s;
			prev5sCurrIdx++;
			if(prev5sCurrIdx >= prev1sCurrAvg.length){
				prev5sCurrIdx = 0;
				
				//handle 5s current avg array
			}
		} else {
			double temp5sAvg = 0;
			for(int i=1; i < prev1sCurrAvg.length; i++){
				temp5sAvg = prev1sCurrAvg[i];
			}
			temp5sAvg += ((prevCurr.length - prev1sCurrIdx) / prevCurr.length) * prev1sCurrAvg[prev5sCurrIdx];
			temp5sAvg += (prev1sCurrIdx / prevCurr.length) * avgCurr1s;
		}
		
		
	}
	*/
	
	double avgCurr1s = 0;
	double avgCurr5s = 0;
	double avgCurr10s = 0;
	double avgCurr50s = 0;
	double avgCurr100s = 0;
	
	double FILT_1s = 0.04; //roughly a time constant of 2
	double FILT_5s = FILT_1s / 5;
	double FILT_10s = FILT_5s / 2;
	double FILT_50s = FILT_10s / 5;
	double FILT_100s = FILT_50s / 2;
	
	/*double CURR_LIM_1s = 5*120;
	double CURR_LIM_5s = 3*120;
	double CURR_LIM_10s = 2*120;
	double CURR_LIM_50s = 1.5*120;
	double CURR_LIM_100s = 1.25*120;*/
	double CURR_LIM_1s = 10*120;
	double CURR_LIM_5s = 10*120;
	double CURR_LIM_10s = 10*120;
	double CURR_LIM_50s = 10*120;
	double CURR_LIM_100s = 10*120;
	
	boolean limitActive;
	double limitValue;
	
	public void protectBreaker(boolean override){
		
		double totalCurr = pdp.getTotalCurrent();		
		
		avgCurr1s += (totalCurr - avgCurr1s) * FILT_1s;
		avgCurr5s += (totalCurr - avgCurr5s) * FILT_5s;
		avgCurr10s += (totalCurr - avgCurr10s) * FILT_10s;
		avgCurr50s += (totalCurr - avgCurr50s) * FILT_50s;
		avgCurr100s += (totalCurr - avgCurr100s) * FILT_100s;
		
		SmartDashboard.putNumber("avgCurr1s",avgCurr1s);
		SmartDashboard.putNumber("avgCurr5s",avgCurr5s);
		SmartDashboard.putNumber("avgCurr10s",avgCurr10s);
		SmartDashboard.putNumber("avgCurr50s",avgCurr50s);
		SmartDashboard.putNumber("avgCurr100s",avgCurr100s);
		
		if((avgCurr1s > CURR_LIM_1s ||
		   avgCurr5s > CURR_LIM_5s ||
		   avgCurr10s > CURR_LIM_10s ||
		   avgCurr50s > CURR_LIM_50s ||
		   avgCurr100s > CURR_LIM_100s) &&
		   !override){
			limitValue = 0.5;
			limitActive = true;
		} else {
			limitValue = 1;
			limitActive = false;
		}
		
		SmartDashboard.putBoolean("limitActive", limitActive);
		SmartDashboard.putNumber("LimitValue", limitValue);
	}
	

	double maxTransporterSpeed = 0;
	
	private double lastLeft;
	private double lastRight;
	private double lastTime;
	
	public void readEncoders() {
		double time = Timer.getFPGATimestamp();
		leftDriveEncoder = leftDriveCan1.getPosition() * DRIVE_INCH_PER_REV;
		rightDriveEncoder = rightDriveCan1.getPosition()*DRIVE_INCH_PER_REV;
		leftDriveSpeed = (leftDriveEncoder - lastLeft) / (time - lastTime);
		lastLeft = leftDriveEncoder;
		rightDriveSpeed = (rightDriveEncoder - lastRight) / (time - lastTime);
		lastRight = rightDriveEncoder;
		lastTime = time;
		
		shooterSpeedEncoder = shooterMotor.getSpeed();
		transporterSpeedEncoder = transporterMotor.getSpeed();
		agitatorSpeedEncoder = agitatorMotor.getSpeed();
		
		gearPanelPositionPotL = gearPanelMotor1.getPosition();
		gearPanelPositionPotR = gearPanelMotor2.getPosition();

		if(transporterSpeedEncoder == 0){
			maxTransporterSpeed = 0;
		} else{
			maxTransporterSpeed = Math.min(maxTransporterSpeed, transporterSpeedEncoder);
		}
		
		gearIntakeCurrent = pdp.getCurrent(ElectroPaul.GEAR_ROLLER_MOTOR);
		gearPanel1Current = pdp.getCurrent(ElectroPaul.GEAR_PANEL_MOTOR_1);
		gearPanel2Current = pdp.getCurrent(ElectroPaul.GEAR_PANEL_MOTOR_2);
		
		climb1Current = pdp.getCurrent(ElectroPaul.CLIMB_MOTOR_1);
		climb2Current = pdp.getCurrent(ElectroPaul.CLIMB_MOTOR_2);
		
		SmartDashboard.putNumber("GearPanel1Current", gearPanel1Current);
		SmartDashboard.putNumber("GearPanel2Current", gearPanel2Current);
		
		SmartDashboard.putNumber("MaxTransporterSpeed", maxTransporterSpeed);
		SmartDashboard.putNumber("leftDriveEncoder", leftDriveEncoder);
		SmartDashboard.putNumber("rightDriveEncoder", rightDriveEncoder);
		SmartDashboard.putNumber("leftDriveSpeed", leftDriveSpeed);
		SmartDashboard.putNumber("rightDriveSpeed", rightDriveSpeed);
		if(shooterSpeedEncoder > 1725)
		SmartDashboard.putNumber("shooterSpeedEncoder", shooterSpeedEncoder);
		SmartDashboard.putNumber("transporterSpeedEncoder", transporterSpeedEncoder);
		SmartDashboard.putNumber("agitatorSpeedEncoder", agitatorSpeedEncoder);
		SmartDashboard.putNumber("gearPanelPositionPotL", gearPanelPositionPotL);
		SmartDashboard.putNumber("gearPanelPositionPotR", gearPanelPositionPotR);
		SmartDashboard.putNumber("GearIntakeCurrent", gearIntakeCurrent);

		SmartDashboard.putDouble("shooterpower", shooterMotor.getOutputVoltage());
		
	}
}
