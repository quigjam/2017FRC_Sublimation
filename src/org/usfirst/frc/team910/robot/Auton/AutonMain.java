package org.usfirst.frc.team910.robot.Auton;

import java.util.ArrayList;

import org.usfirst.frc.team910.robot.Functions.AutoDelivererer;
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
	private static boolean OVERRIDE_ALLIANCE = false;
	private boolean blueAlliance = false;
	private static double CLIMBER_RUN_TIME = 0.35;
	
	//drive to hopper
	private static final double r = 21;//red start turn distance
	private static final double b = 23;//blue start turn distance
  //private static final double[] turnPowerL_2Hopper = 	   { 1, 1, 1, -0.5,  -0.2,    1,    1,  0.5 }; //Inside
	private static final double[] turnPowerL_2Hopper = 	   { 1, 1, 1, -0.53, -0.2,    1,    1,  0.5 }; //Inside
  //private static final double[] turnPowerR_2Hopper = 	   { 1, 1, 1,    1,    1,    1,    1, -0.65 }; //Outside
	private static final double[] turnPowerR_2Hopper = 	   { 1, 1, 1,    1,    1,    1,    1, -0.45 }; //Outside
	private static final double[] xDistAxis_2Hopper_Red =  { 0, 0, r, r+23, r+44, r+58, r+71,  r+95 }; //end dist is ~121
	private static final double[] xDistAxis_2Hopper_Blue = { 0, 0, b, b+23, b+44, b+58, b+71,  b+95 }; //switch to this when blue
		
	//Next line NOT USED
	private static final double[] turnAngle_2Hopper =  	   { 0, 0, 0,    0,   45,   80,   90,    90 };
	
	//drive from hopper to boiler
	private static final double[] turnPowerL_From_Hopper = { -1, -1,   -1, 0.75,   1,  0.55, -1 }; //Outside of backup then Inside of Forward
	private static final double[] turnPowerR_From_Hopper = { -1, -1, 0.1,    1,   1,  0.55, -1 }; //Inside of backup  then Outside of Forward
	private static final double[] turnAngle_From_Hopper =  { 90, 90,   90,  160, 160,  160, 160 };
	private static final double[] xDistAxis_From_Hopper =  {  0,  0,   20,   30,  40,   55,  64 };//end dist is ~63
	
	//drive to gear peg left 
	private static final double[] turnPowerL_gearL = { -1, -1, -1,    -1,   -1,  -1,   1 };
	private static final double[] turnPowerR_gearL = { -1, -1, -1, -0.15,   -1,  -1,   1 };
	private static final double[] turnAngle_gearL =  {  0,  0,  0,   -45,  -60, -60, -60 };
	private static final double[] xDistAxis_gearL =  {  0,  0, 90,   115,  140, 150, 160 };
	
	//drive to gear peg left
	private static final double[] turnPowerL_gearR = { -1, -1, -1,    -1,   -1,  -1,   1 };
	private static final double[] turnPowerR_gearR = { -1, -1, -1, -0.15,   -1,  -1,   1 };
	private static final double[] turnAngle_gearR =  {  0,  0,  0,   -45,  -60, -60, -60 };
	private static final double[] xDistAxis_gearR =  {  0,  0, 90,   115,  140, 150, 160 };
	
	//drive to center gear
	private static final double[] turnPowerL_gearC = { -1, -1, -1,  1,    1 }; //use these if you want to drive straight
	private static final double[] turnPowerR_gearC = { -1, -1, -1,  1,    1 };
	//private static final double[] turnPowerL_gearC = { -1, -1, -1,  1,   1,  0.5 }; //use these to turn on the way out
	//private static final double[] turnPowerR_gearC = { -1, -1, -1,  1, 0.1,  0.5 };
	private static final double[] turnAngle_gearC =  {  0,  0,  0,  0,   0,    0 };
	private static final double[] xDistAxis_gearC =  {  0,  0, 56, 76,  90,  165 };
	
	
	//hopper end points
	private static final double End_Point_To_Hopper = 36; //how far on the y axis to travel into the hopper
	private static final double End_Point_From_Hopper = -22; //was26 //how far on the x axis to drive to the boiler
	private static final double End_Point_To_Center_Gear = 80; //use if only driving straight
	//private static final double End_Point_To_Center_Gear = 160; //use for the turning part
	private static final double End_Point_To_Side_Gear_L = 154;
	private static final double End_Point_To_Side_Gear_R = 154;
	
	
	
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
	private static final int PIVIT_SHOT_AUTO = 5;
	ArrayList<AutonStep> pivitShot;
	public int currentStep = 0;

	ArrayList<AutonStep> gearAuto;
	
	public AutonMain() {
	// MOTION PROFILE INPUTS ----------------------------------------------------------------------------------------------------------//
		MotionProfileInputs mi = new MotionProfileInputs();
		
		mi.leftSegments = new double[2]; mi.rightSegments = new double[2];
		mi.leftSegments[0] = 60; mi.rightSegments[0] = 20;
		mi.leftSegments[1] = 10; mi.rightSegments[1] = 10.1;
		//mi.leftSegments[2] = 12; mi.rightSegments[2] = 12;
		mi.leftBrakeDist = mi.leftSegments[0] + mi.leftSegments[1] /*+ mi.rightSegments[2]*/ - 28; //start brake 4in into last segment
		mi.rightBrakeDist = mi.rightSegments[0] + mi.rightSegments[1] /*+ mi.rightSegments[2]*/ - 0; //start brake 4in into last segment
		mi.endL = 75;
		mi.endR = 35;
		mi.endTime = 1.5;
		mi.endAccel = 999;
		mi.powerLimit = 0.9;
		
		
	// JUST DRIVE AUTO -----------------------------------------------------------------------------------------------------------------//
		justDrive = new ArrayList<AutonStep>();
		justDrive.add(new AutonResetAngle());
		//justDrive.add(new AutonUnlatchClimber(CLIMBER_RUN_TIME)); //remove later
		justDrive.add(new AutonDriveTime(0.5, 1.5, 0, false)); //make positive power again
		//justDrive.add(new AutonMotionProfiler(mi));
		
		//justDrive.add(new AutonWait(1.5)); //remove later
		//justDrive.add(new AutonAllianceDrive(new AutonDriveTime(0.3,1.75,-30,false), new AutonDriveTime(0.3,1.75,30,false)));//remove later
		//justDrive.add(new AutonWait(1));//remove later
		//justDrive.add(new AutonAutoShoot(10)); //remove later
		/*justDrive.add(new AutonFastArc(false, true, 0.9, turnPowerL_2Hopper, turnPowerR_2Hopper, turnAngle_2Hopper, xDistAxis_2Hopper_Blue, new DriveComplete(){
			public boolean isDone(double l, double r, double x, double y, boolean blueAlliance){
				return Math.abs(y) > End_Point_To_Side_Gear_R
						|| l > xDistAxis_2Hopper_Red[xDistAxis_2Hopper_Red.length-1] && blueAlliance
						|| r > xDistAxis_2Hopper_Blue[xDistAxis_2Hopper_Blue.length-1] && !blueAlliance;
			}
		}));*/
		justDrive.add(new AutonEndStep());
		
	// PIVIT SHOT AUTO ----------------------------------------------------------------------------------------------------------------//
		pivitShot = new ArrayList<AutonStep>();
		pivitShot.add(new AutonResetAngle());
		pivitShot.add(new AutonUnlatchClimber(CLIMBER_RUN_TIME)); //remove later
		pivitShot.add(new AutonDriveTime(-0.5, 1.5, 0, false)); //make positive power again
		pivitShot.add(new AutonWait(1.5)); //remove later
		pivitShot.add(new AutonAllianceDrive(new AutonDriveTime(0.3,1.75,-30,false), new AutonDriveTime(0.3,1.75,30,false)));//remove later
		pivitShot.add(new AutonWait(1));//remove later
		pivitShot.add(new AutonAutoShoot(10)); //remove later
		/*justDrive.add(new AutonFastArc(false, true, 0.9, turnPowerL_2Hopper, turnPowerR_2Hopper, turnAngle_2Hopper, xDistAxis_2Hopper_Blue, new DriveComplete(){
			public boolean isDone(double l, double r, double x, double y, boolean blueAlliance){
				return Math.abs(y) > End_Point_To_Side_Gear_R
						|| l > xDistAxis_2Hopper_Red[xDistAxis_2Hopper_Red.length-1] && blueAlliance
						|| r > xDistAxis_2Hopper_Blue[xDistAxis_2Hopper_Blue.length-1] && !blueAlliance;
			}
		}));*/
		pivitShot.add(new AutonEndStep());
		
		//old pivot shot 
		/*
		pivitShot = new ArrayList<AutonStep>();
		pivitShot.add(new AutonResetAngle());
		pivitShot.add(new AutonAllianceDrive(new AutonDriveTime(-0.5, 1.5, -35, true), new AutonDriveTime(-0.5, 1.5, 35, true)));
		pivitShot.add(new AutonAllianceDrive(new AutonDriveTime(0.5, 1.0, -60, true), new AutonDriveTime(0.5, 1.0, 60, true)));
		//pivitShot.add(new AutonPrime());
		pivitShot.add(new AutonAutoShoot(13));
		pivitShot.add(new AutonEndStep());
		*/
		
		
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
		double gearDrivePwr = 0.45; //was .75 with other drive cycle

		/*
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
		*/
		
		
		//Center Gear Auto
		//start
		centerGearAuto = new ArrayList<AutonStep>();
		centerGearAuto.add(new AutonResetAngle());
		
		//drive and relase climber/gear
		ArrayList<AutonStep> list = new ArrayList<AutonStep>();
		//list.add(new AutonUnlatchClimber(CLIMBER_RUN_TIME));
		list.add(new AutonDriveTime(-gearDrivePwr, 1.5, 0, false));
		/*list.add( new AutonFastArc(false,true,gearDrivePwr,turnPowerL_gearC,turnPowerR_gearC,turnAngle_gearC,xDistAxis_gearC,new DriveComplete(){
			public boolean isDone(double l, double r, double x, double y, boolean blueAlliance){
				return Math.abs(l) > End_Point_To_Center_Gear;
			}
		}));*/
		
		ParallelStep ps = new ParallelStep(list);
		centerGearAuto.add(ps);
		
		
		/*
		// hold on to something this is where trevor takes over
		
		//run auto gear delivery 
		centerGearAuto.add(new AutoDelivererer());
		*/
		
		//deliver gear (no longer needed with passive deployer)
		//centerGearAuto.add(new AutonGearDeploy());
		
		//reverse
		centerGearAuto.add(new AutonDriveTime(gearDrivePwr, 0.75, 0, false));
		
		//drive towards boiler (not needed when we are using the table with the turn at the end -- 4/7)
		centerGearAuto.add(new AutonAllianceDrive(new AutonDriveTime(gearDrivePwr, 1.5, -60, false), new AutonDriveTime(gearDrivePwr, 1.5, 60, false)));
		
		//shoot
		centerGearAuto.add(new AutonAutoShoot(10));
		centerGearAuto.add(new AutonEndStep());
		
		
		
		
		
		//HOPPER AUTO ----------------------------------------------------------------------------------------------//
		
		//new auto
		double drivePwr = 0.9;
		hopperShootAuto = new ArrayList<AutonStep>();
		
		//step 1: reset navX
		hopperShootAuto.add(new AutonResetAngle());
		
		//step 2: briefly run the climber and drive to the hopper
		list = new ArrayList<AutonStep>();
		list.add(new AutonUnlatchClimber(CLIMBER_RUN_TIME));//was.75
		AutonFastArc afaBlue = new AutonFastArc(false, true, drivePwr, turnPowerL_2Hopper, turnPowerR_2Hopper, turnAngle_2Hopper, xDistAxis_2Hopper_Blue, new DriveComplete(){
			public boolean isDone(double l, double r, double x, double y, boolean blueAlliance){
				return Math.abs(y) > End_Point_To_Hopper
						|| r > xDistAxis_2Hopper_Red[xDistAxis_2Hopper_Red.length-1] && blueAlliance
						|| l > xDistAxis_2Hopper_Blue[xDistAxis_2Hopper_Blue.length-1] && !blueAlliance;
			}
		});
		AutonFastArc afaRed = new AutonFastArc(false, true, drivePwr, turnPowerL_2Hopper, turnPowerR_2Hopper, turnAngle_2Hopper, xDistAxis_2Hopper_Red, new DriveComplete(){
			public boolean isDone(double l, double r, double x, double y, boolean blueAlliance){
				return Math.abs(y) > End_Point_To_Hopper
						|| r > xDistAxis_2Hopper_Red[xDistAxis_2Hopper_Red.length-1] && blueAlliance
						|| l > xDistAxis_2Hopper_Blue[xDistAxis_2Hopper_Blue.length-1] && !blueAlliance;
			}
		}); 
		list.add(new AutonAllianceDrive(afaBlue, afaRed));
		ps = new ParallelStep(list);
		hopperShootAuto.add(ps);
		
		//step 3: start priming and crashing into the hopper to get all the balls into the robot
		list = new ArrayList<AutonStep>();
		list.add(new AutonWaitAtHopper(3));
		//list.add(new AutonWait(3)); //replace this for competition
		list.add(new AutonPrime());
		ps = new ParallelStep(list);
		hopperShootAuto.add(ps);
		
		//step 4: drive away from the hopper and keep priming
		list = new ArrayList<AutonStep>();
		list.add(new AutonFastArc(true, true, drivePwr, turnPowerL_From_Hopper, turnPowerR_From_Hopper, turnAngle_From_Hopper, xDistAxis_From_Hopper, new DriveComplete(){
			public boolean isDone(double l, double r, double x, double y, boolean blueAlliance){
				return x < End_Point_From_Hopper
						|| r > xDistAxis_From_Hopper[xDistAxis_From_Hopper.length - 1] && blueAlliance
						|| l > xDistAxis_From_Hopper[xDistAxis_From_Hopper.length - 1] && !blueAlliance;
			}
		}));
		list.add(new AutonPrime());
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
		SmartDashboard.putString("AutonKey", "Auton Profiles:\n0) Just Drive Forward\n1) Hopper Shoot Drive\n2) Left Gear\n3) Center Gear\n4) Right Gear\n5) Pivot Shot");
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
		OVERRIDE_ALLIANCE = Preferences.getInstance().getBoolean("OVERRIDE_ALLIANCE", OVERRIDE_ALLIANCE);
		
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
		
		//allow overriding of the alliance if the field is not cooperating
		if(OVERRIDE_ALLIANCE){
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
			//steps = leftGearAuto;
			steps = justDrive;
			break;
		case CENTER_GEAR_AUTO:
			steps = centerGearAuto;
			//steps = justDrive;
			break;
		case RIGHT_GEAR_AUTO:
			//steps = rightGearAuto;
			steps = justDrive;
			break;
		case PIVIT_SHOT_AUTO:
			steps = pivitShot;
			break;
		default:
			steps = doNothing;
			break;
		}

		SmartDashboard.putString("ChosenAuton", steps.getClass().getName());
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
