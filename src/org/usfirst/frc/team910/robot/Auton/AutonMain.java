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
	private static final double[] turnPowerL_2Hopper = { 1, 1,  1, 0.15,  1,  1 };
	private static final double[] turnPowerR_2Hopper = { 1, 1,  1,    1,  1,  1 };
	private static final double[] turnAngle_2Hopper =  { 0, 0,  0,   45, 90, 90 };
	private static final double[] xDistAxis_2Hopper =  { 0, 0, 28,   53, 77, 87 };
	
	//drive from hopper to boiler
	private static final double[] turnPowerL_From_Hopper = { -1, -1, 0.5,   1,   1,   1 };
	private static final double[] turnPowerR_From_Hopper = {  0,  0,   1,   1,   1,   1 };
	private static final double[] turnAngle_From_Hopper =  { 90, 90, 135, 180, 135, 135 };
	private static final double[] xDistAxis_From_Hopper =  {  0,  0,  12,  24,  36,  48 };
	
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
	private static final double END_POINT = 28;
	private static final double END_POINT_THE_SECOND = 45;
	
	
	ArrayList<AutonStep> steps;
	private static final int HOPPER_SHOOT_AUTO = 1;
	ArrayList<AutonStep> hopperShootAutonBlue;
	ArrayList<AutonStep> hopperShootAutonRed;
	ArrayList<AutonStep> testingAuto;
	private static final int JUST_DRIVE_AUTO = 0;
	ArrayList<AutonStep> justDrive;
	int currentStep = 0;

	ArrayList<AutonStep> gearAuto;
	public AutonMain() {
		justDrive = new ArrayList<AutonStep>();
		justDrive.add(new AutonResetAngle());
		justDrive.add(new AutonDriveTime(0.7, 3, 0, false));
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
		

		gearAuto = new ArrayList<AutonStep>();
		gearAuto.add(new AutonResetAngle());
		gearAuto.add(new AutonDriveStraight(5*12 , 0.4, 0));
		gearAuto.add(new AutonDriveTime(0.5, 2.5, -45, false));
		gearAuto.add(new AutonGearDeploy());
		gearAuto.add(new AutonEndStep());
		
		
		
		//new auto
		double drivePwr = 0.5;
		testingAuto = new ArrayList<AutonStep>();
		
		//step 1: reset navX
		testingAuto.add(new AutonResetAngle());
		
		//step 2: briefly run the climber and drive to the hopper
		ArrayList<AutonStep> list = new ArrayList<AutonStep>();
		//list.add(new AutonUnlatchClimber(0.75));
		list.add(new AutonFastArc(false, true, drivePwr, turnPowerL_2Hopper, turnPowerR_2Hopper, turnAngle_2Hopper, xDistAxis_2Hopper, new DriveComplete(){
			public boolean isDone(double x, double y, boolean blueAlliance){
				return Math.abs(y) > END_POINT;
			}
		}));
		ParallelStep ps = new ParallelStep(list);
		testingAuto.add(ps);
		
		//step 3: start priming and crashing into the hopper to get all the balls into the robot
		list = new ArrayList<AutonStep>();
		list.add(new AutonWaitAtHopper(3));
		//list.add(new AutonPrime());
		ps = new ParallelStep(list);
		testingAuto.add(ps);
		
		//step 4: drive away from the hopper and keep priming
		list = new ArrayList<AutonStep>();
		list.add(new AutonFastArc(true, true, drivePwr, turnPowerL_From_Hopper, turnPowerR_From_Hopper, turnAngle_From_Hopper, xDistAxis_From_Hopper, new DriveComplete(){
			public boolean isDone(double x, double y, boolean blueAlliance){
				return Math.abs(x) > END_POINT_THE_SECOND;
			}
		}));
		//list.add(new AutonPrime());
		ps = new ParallelStep(list);
		testingAuto.add(ps);
		
		//step 5: run the auto shoot function for whatever time is left
		testingAuto.add(new AutonAutoShoot(10));
		
		//step 6: call the end step to make sure everything stops
		testingAuto.add(new AutonEndStep());
		
		
		
		
		steps = new ArrayList<AutonStep>();
		//steps.add(new AutonResetAngle());
		//steps.add(new AutonDriveStraight(192, 0.5, 0));
		//steps.add(new AutonDriveCircle(9.5*12, 0.4, 0, 6*12, true));
		steps.add(new AutonEndStep());
		
		//steps = hopperShootAutonRed;
		steps = testingAuto;
	}

	public void init(Inputs in, Sensors sense, DriveTrain drive, GearSystem gear, Shooter shoot, Climber climb, AutoShoot as) {
		AutonStep.setRobotReferences(in, sense, drive, gear, shoot, climb, as);
		//SmartDashboard.putString("AutonKey", "Auton Profiles:\n0) Just Drive Forward\n1) Hopper Shoot Drive");
	}
	
	public void setAutonProfile(){
		
		blueAlliance = false;
		
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
		
		
		/*
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
		
		if(alliance == DriverStation.Alliance.Blue){
			switch(AUTON_PROFILE){
			case HOPPER_SHOOT_AUTO:
				steps = hopperShootAutonBlue;
				break;
			case JUST_DRIVE_AUTO:
				steps = justDrive;
				break;
			}
		} else if (alliance == DriverStation.Alliance.Red){
			switch(AUTON_PROFILE){
			case HOPPER_SHOOT_AUTO:
				steps = hopperShootAutonRed;
				break;
			case JUST_DRIVE_AUTO:
				steps = justDrive;
				break;
			}
		} else {
			//be sad because no auton
		}
		*/
		
		
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
