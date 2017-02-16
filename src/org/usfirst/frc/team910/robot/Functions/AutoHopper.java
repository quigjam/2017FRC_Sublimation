package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.GearSystem;

public class AutoHopper {

	private Inputs in;
	private Sensors sense;
	private DriveTrain drive;
	
	private double hopperTargetAngle = 0;
	private double botStart = 0;
	
	public AutoHopper(Inputs in, Sensors sense, DriveTrain drive, GearSystem gear) {
		this.in = in;
		this.sense = sense;
		this.drive = drive;
	}
	
	private enum HopperState{
		CAM_ALIGN, DRIVE, DONE; 
	};
	
	private HopperState hopperState = HopperState.values()[0];
	
	public void run(){
		if (in.autoHopper){
			//find hopper by looking at bunch of balls
			switch(hopperState){
			case CAM_ALIGN:
				if(sense.camera.hopperSearch()){
					hopperState = HopperState.DRIVE;
				}
				break;

			case DRIVE:
				drive.originAngle.set(sense.cameraAngle.get());
//				drive.driveStraightNavX(false,POWER,0);
//				if (sense.accelX > SOMENUMBER){
					hopperState = HopperState.DONE;
//				}
				break;

			case DONE:
//				if(){
					drive.tankDrive(0, 0);
//				}
			}
		}
	}
}