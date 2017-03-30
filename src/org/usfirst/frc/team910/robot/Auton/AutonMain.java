package org.usfirst.frc.team910.robot.Auton;

import java.util.ArrayList;

import org.usfirst.frc.team910.robot.Functions.AutoShoot;
import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.Climber;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.GearSystem;
import org.usfirst.frc.team910.robot.Subsystems.Shooter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonMain {
	
	private static int AUTON_PROFILE = 0;
	private static final int RED = 0;
	private static final int BLUE = 1;
	private static int DEFAULT_ALLIANCE = RED;
	private boolean blueAlliance = false;
	
	//drive to hopper
	private static final double[] turnPowerL_2Hopper = { 1, 1,  1,    0,  1, 1.2,  1 };
	private static final double[] turnPowerR_2Hopper = { 1, 1,  1,    1,  1,   1, -0.65 };
	private static final double[] turnAngle_2Hopper =  { 0, 0,  0,   45, 80,  90,   90 };
	private static final double[] xDistAxis_2Hopper =  { 0, 0, 32,   76, 90, 103,  123 }; //end dist is ~121
	
	//drive from hopper to boiler
	private static final double[] turnPowerL_From_Hopper = { -1, -1, -1, 0.5,   1,  0.25,   0 };
	private static final double[] turnPowerR_From_Hopper = { -1, -1,  0,   1,   1,  0.25,   0 };
	private static final double[] turnAngle_From_Hopper =  { 90, 90, 90, 160, 160,  160, 160 };
	private static final double[] xDistAxis_From_Hopper =  {  0,  0, 20,  35,  45,   60,  65 };//end dist is ~58
	
	//drive to gear peg left (flip for right)
	private static final double[] turnPowerL_gearL = { 1, 1,  1,    1,   1,   1 };
	private static final double[] turnPowerR_gearL = { 1, 1,  1, 0.15,   1,   1 };
	private static final double[] turnAngle_gearL =  { 0, 0,  0,  -45, -60, -60 };
	private static final double[] xDistAxis_gearL =  { 0, 0, 48,   60,  72,  80 };
	
	//drive to center gear
	private static final double[] turnPowerL_gearC = { 1, 1,  1 };
	private static final double[] turnPowerR_gearC = { 1, 1,  1 };
	private static final double[] turnAngle_gearC =  { 0, 0,  0 };
	private static final double[] xDistAxis_gearC =  { 0, 0, 48 };
	
	
	//hopper end points
	private static final double End_Point_To_Hopper = 30;
	private static final double End_Point_From_Hopper = -4;
	
	
	ArrayList<AutonStep> steps;
	private static final int HOPPER_SHOOT_AUTO = 1;
	//ArrayList<AutonStep> hopperShootAutonBlue;
	//ArrayList<AutonStep> hopperShootAutonRed;
	ArrayList<AutonStep> hopperShootAuto;
	
	private static final int LEFT_GEAR_AUTO = 2;
	private static final int RIGHT_GEAR_AUTO = 4;
	private static final int CENTER_GEAR_AUTO = 3;
	ArrayList<AutonStep> leftGearAuto;
	ArrayList<AutonStep> centerGearAuto;
	ArrayList<AutonStep> rightGearAuto;
	private static final int JUST_DRIVE_AUTO = 0;
	ArrayList<AutonStep> justDrive;
	ArrayList<AutonStep> doNothing;
	public int currentStep = 0;

	ArrayList<AutonStep> gearAuto;
	public AutonMain() {
		justDrive = new ArrayList<AutonStep>();
		justDrive.add(new AutonResetAngle());
		justDrive.add(new AutonDriveTime(0.5, 1, 0, false));
		justDrive.add(new AutonEndStep());
		
		//old autons
	  /*hopperShootAutonBlue = new ArrayList<AutonStep>();
		hopperShootAutonBlue.add(new AutonResetAngle());
		hopperShootAutonBlue.add(new AutonDriveStraight(4*12, 0.4, 0));
		hopperShootAutonBlue.add(new AutonDriveCircle(2*Math.PI*2.5*12/4, 0.7, 0, 2.5*12, 90, false));
		hopperShootAutonBlue.add(new AutonDriveTimeClimb(0.7, 0.75, 90));
		hopperShootAutonBlue.add(new AutonWaitAtHopper(3));
		hopperShootAutonBlue.add(new AutonDriveTime(-0.9, 0.5, 90, true));
		hopperShootAutonBlue.add(new AutonDriveTime(0.9, 0.5, 142, true));
		hopperShootAutonBlue.add(new AutonAutoShoot(6));
		hopperShootAutonBlue.add(new AutonEndStep());
		
		hopperShootAutonRed = new ArrayList<AutonStep>();
		hopperShootAutonRed.add(new AutonResetAngle());
		hopperShootAutonRed.add(new AutonDriveStraight(4*12, 0.4, 0));
		hopperShootAutonRed.add(new AutonDriveCircle(2*Math.PI*2.5*12/4, 0.7, 0, 2.5*12, -90, true));
		hopperShootAutonRed.add(new AutonDriveTimeClimb(0.7, 0.75, -90));
		hopperShootAutonRed.add(new AutonWaitAtHopper(3));
		hopperShootAutonRed.add(new AutonDriveTime(-0.9, 0.5, -90, true));
		hopperShootAutonRed.add(new AutonDriveTime(0.9, 0.45, -135, true));
		hopperShootAutonRed.add(new AutonAutoShoot(6));
		hopperShootAutonRed.add(new AutonEndStep());*/
		
		
		/*
		gearAuto = new ArrayList<AutonStep>();
		gearAuto.add(new AutonResetAngle());
		gearAuto.add(new AutonDriveStraight(5*12 , 0.4, 0));
		gearAuto.add(new AutonDriveTime(0.5, 2.5, -45, false));
		gearAuto.add(new AutonGearDeploy());
		gearAuto.add(new AutonEndStep());
		*/
		
		
		//GEAR AUTOS -----------------------------------------------------------------------------------------------//
		double gearDrivePwr = 0.5;

		leftGearAuto = new ArrayList<AutonStep>();
		leftGearAuto.add(new AutonResetAngle());
		leftGearAuto.add(new AutonDriveStraight(5*12 , gearDrivePwr, 0));
		leftGearAuto.add(new AutonDriveTime(gearDrivePwr, 2, -60, false));
		leftGearAuto.add(new AutonGearDeploy());
		leftGearAuto.add(new AutonDriveTime(-gearDrivePwr, 2, -60, false));
		leftGearAuto.add(new AutonAllianceDrive(new AutonDriveTime(gearDrivePwr, 2, 150, true), new AutonDriveCircle(Math.PI*6*12*4/6, 1, 30, 6*12, 180, true)));
		leftGearAuto.add(new AutonAutoShoot(6));
		leftGearAuto.add(new AutonEndStep());
		
		rightGearAuto = new ArrayList<AutonStep>();
		rightGearAuto.add(new AutonResetAngle());
		rightGearAuto.add(new AutonDriveStraight(5*12 , gearDrivePwr, 0));
		rightGearAuto.add(new AutonDriveTime(gearDrivePwr, 2, 60, false));
		rightGearAuto.add(new AutonGearDeploy());
		rightGearAuto.add(new AutonDriveTime(-gearDrivePwr, 2, 60, false));
		rightGearAuto.add(new AutonAllianceDrive(new AutonDriveCircle(Math.PI*6*12*4/6, 1, -30, 6*12, 180, false), new AutonDriveTime(gearDrivePwr, 2, -150, true)));
		rightGearAuto.add(new AutonAutoShoot(6));
		rightGearAuto.add(new AutonEndStep());
		
		centerGearAuto = new ArrayList<AutonStep>();
		centerGearAuto.add(new AutonResetAngle());
		centerGearAuto.add(new AutonDriveTime(gearDrivePwr, 3, 0, false));
		centerGearAuto.add(new AutonGearDeploy());
		centerGearAuto.add(new AutonDriveTime(-gearDrivePwr, 2, 60, false));
		centerGearAuto.add(new AutonAllianceDrive(new AutonDriveTime(gearDrivePwr, 2, 90, true), new AutonDriveTime(gearDrivePwr, 2, -90, true)));
		centerGearAuto.add(new AutonAutoShoot(6));
		centerGearAuto.add(new AutonEndStep());
		
		
		
		
		
		//HOPPER AUTO ----------------------------------------------------------------------------------------------//
		
		//new auto
		double drivePwr = 0.9;
		hopperShootAuto = new ArrayList<AutonStep>();
		
		//step 1: reset navX
		hopperShootAuto.add(new AutonResetAngle());
		
		//step 2: briefly run the climber and drive to the hopper
		ArrayList<AutonStep> list = new ArrayList<AutonStep>();
		//list.add(new AutonUnlatchClimber(0.75));
		list.add(new AutonFastArc(false, true, drivePwr, turnPowerL_2Hopper, turnPowerR_2Hopper, turnAngle_2Hopper, xDistAxis_2Hopper, new DriveComplete(){
			public boolean isDone(double x, double y, boolean blueAlliance){
				return Math.abs(y) > End_Point_To_Hopper;
			}
		}));
		ParallelStep ps = new ParallelStep(list);
		hopperShootAuto.add(ps);
		
		//step 3: start priming and crashing into the hopper to get all the balls into the robot
		list = new ArrayList<AutonStep>();
		//list.add(new AutonWaitAtHopper(3));
		list.add(new AutonWait(3)); //replace this for competition
		//list.add(new AutonPrime());
		ps = new ParallelStep(list);
		hopperShootAuto.add(ps);
		
		//step 4: drive away from the hopper and keep priming
		list = new ArrayList<AutonStep>();
		list.add(new AutonFastArc(true, true, drivePwr, turnPowerL_From_Hopper, turnPowerR_From_Hopper, turnAngle_From_Hopper, xDistAxis_From_Hopper, new DriveComplete(){
			public boolean isDone(double x, double y, boolean blueAlliance){
				return x < End_Point_From_Hopper;
			}
		}));
		//list.add(new AutonPrime());
		ps = new ParallelStep(list);
		hopperShootAuto.add(ps);
		
		//step 5: run the auto shoot function for whatever time is left
		hopperShootAuto.add(new AutonAutoShoot(10));
		
		//step 6: call the end step to make sure everything stops
		hopperShootAuto.add(new AutonEndStep());
		
		
		
		
		doNothing = new ArrayList<AutonStep>();
		//steps.add(new AutonResetAngle());
		//steps.add(new AutonDriveStraight(192, 0.5, 0));
		//steps.add(new AutonDriveCircle(9.5*12, 0.4, 0, 6*12, true));
		doNothing.add(new AutonEndStep());
		
		steps = doNothing;
		//steps = hopperShootAutonRed;
		//steps = hopperShootAuto;
	}

	public void init(Inputs in, Sensors sense, DriveTrain drive, GearSystem gear, Shooter shoot, Climber climb, AutoShoot as) {
		AutonStep.setRobotReferences(in, sense, drive, gear, shoot, climb, as);
		SmartDashboard.putString("AutonKey", "Auton Profiles:\n0) Just Drive Forward\n1) Hopper Shoot Drive\n2) Left Gear\n3) Center Gear\n4) Right Gear");
	}
	
	public void setAutonProfile(){
		
		//blueAlliance = false;
		
		/*switch(AutonStep.in.autonSelection){
		case 1:
			steps = hopperShootAutonBlue;
			break;
		case 2:
			steps = justDrive;
			break;
		case 3:
			steps = hopperShootAutonRed;
			break;
			default:
				
		}
		*/
		
		
		
		DriverStation ds = DriverStation.getInstance();
		AUTON_PROFILE = Preferences.getInstance().getInt("AUTON_PROFILE", AUTON_PROFILE);
		SmartDashboard.putNumber("AutonProfile", AUTON_PROFILE);
		DEFAULT_ALLIANCE = Preferences.getInstance().getInt("DEFAULT_ALLIANCE", DEFAULT_ALLIANCE);
		SmartDashboard.putNumber("DefaultAlliance", DEFAULT_ALLIANCE);
		
		DriverStation.Alliance alliance = ds.getAlliance();
		if(alliance == DriverStation.Alliance.Invalid){
			switch(DEFAULT_ALLIANCE){
			case RED:
				alliance = DriverStation.Alliance.Red;
				break;
			case BLUE:
				alliance = DriverStation.Alliance.Blue;
				break;
			}
		}
		
		
		//blue alliance will be true when we are blue, otherwise false
		blueAlliance = alliance == DriverStation.Alliance.Blue;
		SmartDashboard.putBoolean("blueAlliance", blueAlliance);
		
		switch(AUTON_PROFILE){
		case HOPPER_SHOOT_AUTO:
			steps = hopperShootAuto;
			break;
		case JUST_DRIVE_AUTO:
			steps = justDrive;
			break;
		case LEFT_GEAR_AUTO:
			steps = leftGearAuto;
			break;
		case CENTER_GEAR_AUTO:
			steps = centerGearAuto;
			break;
		case RIGHT_GEAR_AUTO:
			steps = rightGearAuto;
			break;
		default:
			steps = doNothing;
			break;
		}

		
	}

	public void run() {
		SmartDashboard.putNumber("AutonStep", currentStep);
		steps.get(currentStep).run(); // institutes a state machine as an array of autons, works similarly to a "switch"
		if (steps.get(currentStep).isDone()) {
			currentStep++;
			steps.get(currentStep).setup(blueAlliance);
		}
	}
}
