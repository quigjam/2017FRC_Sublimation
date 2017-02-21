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
		CAM_ALIGN, DRIVE, DONE; 							//construct states for 
	};
	
	private HopperState hopperState = HopperState.values()[0]; //put them in an array
	
	public void run(){											
		if (in.autoHopper){											//if we hit the auto hopper button

			switch(hopperState){		
			case CAM_ALIGN:											
				if(sense.camera.hopperSearch()){					//find a hopper
					hopperState = HopperState.DRIVE;				//go to next state
				}
				break;

			case DRIVE:
				drive.originAngle.set(sense.cameraAngle.get());		//set the origin angel to where the target is
//				drive.driveStraightNavX(false,POWER,0);				//drive at it
//				if (sense.accelX > SOMENUMBER){						//when we hit the wall
					hopperState = HopperState.DONE;					//go to the next state
//				}
				break;

			case DONE:
//				if(){
					drive.tankDrive(0, 0);							//stop
//				}
			}
		}
	}
}