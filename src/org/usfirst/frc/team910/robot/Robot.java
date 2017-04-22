package org.usfirst.frc.team910.robot;

import org.usfirst.frc.team910.robot.Auton.AutonMain;
import org.usfirst.frc.team910.robot.Functions.AutoClimb;
import org.usfirst.frc.team910.robot.Functions.AutoDelivererer;
import org.usfirst.frc.team910.robot.Functions.AutoGear;
import org.usfirst.frc.team910.robot.Functions.AutoShoot;
import org.usfirst.frc.team910.robot.Functions.DriveJog;
import org.usfirst.frc.team910.robot.IO.ElectroPaul;
import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.Climber;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.GearSystem;
import org.usfirst.frc.team910.robot.Subsystems.Shooter;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	Inputs in;
	Outputs out;
	Sensors sense;

	DriveTrain drive;
	Shooter shoot;
	GearSystem gear;
	Climber climb;

	AutoClimb autoClimb;
	AutoGear autoGear;
	AutoShoot autoShoot;
	AutoDelivererer autoDelivererer;
	DriveJog driveJog;
	
	AutonMain autonmain;
	Solenoid light;
	Solenoid light2;
	
	AnalogInput ai;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		ElectroPaul ep = new ElectroPaul();
		in = new Inputs();
		out = new Outputs();
		sense = new Sensors();

		drive = new DriveTrain(in, out, sense);
		shoot = new Shooter(out, in);
		gear = new GearSystem(in, out, sense);
		climb = new Climber(out, in);

		autoClimb = new AutoClimb(in, sense, drive, climb);
		autoGear = new AutoGear(in, sense, drive, gear);
		autoShoot = new AutoShoot(in, shoot, sense, drive);
		autoDelivererer = new AutoDelivererer(in, gear, sense, drive);
		driveJog = new DriveJog(in, out, drive);

		autonmain = new AutonMain();
		light = new Solenoid(0);
		light2 = new Solenoid(1);
		
		sense.init();
		
		ai = new AnalogInput(0);
		
		autonmain.init(in, sense, drive, gear, shoot, climb, autoShoot);
		
		CameraServer.getInstance().startAutomaticCapture();
	}

	@Override
	public void autonomousInit() {
		sense.init();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		sense.read();
		out.readEncoders();
		drive.run(true);
		autonmain.run();
		
		light.set(true);
		light2.set(true);
	}

	@Override
	public void teleopInit() {

	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		autonmain.currentStep = 0;
		in.read();
		sense.read();
		out.readEncoders();
		drive.run(false);
	
		autoShoot.run();
		autoDelivererer.run();
		driveJog.run();
		
		shoot.run();
		gear.run();
		climb.run();
		
		light.set(in.cameraEnable);
		light2.set(in.cameraEnable);
		
		SmartDashboard.putNumber("RemainingMatchTime", DriverStation.getInstance().getMatchTime());
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {

	}

	@Override
	public void disabledPeriodic() {
		in.read();
		sense.read();
		out.readEncoders();
		
		autonmain.setAutonProfile();
		
		SmartDashboard.putNumber("ai0", ai.getVoltage());
	}

}
