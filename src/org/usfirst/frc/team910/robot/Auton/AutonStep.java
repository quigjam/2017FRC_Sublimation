package org.usfirst.frc.team910.robot.Auton;

import org.usfirst.frc.team910.robot.Functions.AutoShoot;
import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.Climber;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.GearSystem;
import org.usfirst.frc.team910.robot.Subsystems.Shooter;

public class AutonStep {

	protected static Inputs in;
	protected static Sensors sense;
	protected static DriveTrain drive;
	protected static GearSystem gear;
	protected static Shooter shoot;
	protected static AutoShoot autoShoot;
	protected static Climber climb;
	
	public static void setRobotReferences(Inputs in, Sensors sense, DriveTrain drive, GearSystem gear, Shooter shoot, Climber climb, AutoShoot as){
		AutonStep.in = in;
		AutonStep.sense = sense;
		AutonStep.drive = drive;
		AutonStep.gear = gear;
		AutonStep.shoot = shoot;
		AutonStep.autoShoot = as;
		AutonStep.climb = climb;
	}
	
	//Placeholder functions
	public void setup(boolean blueAlliance){
		
	}
	
	public void run(){
		
	}
	
	public boolean isDone(){
		return true;
	}
}
