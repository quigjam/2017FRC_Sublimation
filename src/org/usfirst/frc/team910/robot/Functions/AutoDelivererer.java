package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.GearSystem;

import edu.wpi.first.wpilibj.Timer;

public class AutoDelivererer {

	private GearSystem gear;
	private Inputs in;
	private Sensors sense;
	private DriveTrain drive;

	public AutoDelivererer(Inputs in, GearSystem gear, Sensors sense, DriveTrain drive) {
		this.in = in;
		this.gear = gear;
		this.sense = sense;
		this.drive = drive;
	}

	private enum DeliverererState {
		START, RELEASE, DROP, REVERSE, RESET;
	};

	private DeliverererState deliverState = DeliverererState.values()[0];

	private double startTime;

	public void run() {
		if (!in.autoDeliverer) { //reset when not pressed
			deliverState = DeliverererState.values()[0];
		} else {
			// when we hit the auto gear button start up
			switch (deliverState) {

			case START:
				startTime = Timer.getFPGATimestamp();
				deliverState = DeliverererState.RELEASE;
				break;

			case RELEASE:
				in.gearOuttake = true;
				//gear.run();
				if (Timer.getFPGATimestamp() >= startTime + 0.1) {
					deliverState = DeliverererState.DROP;
					startTime = Timer.getFPGATimestamp();
				}
				break;
				
			case DROP:
				in.gearPanelPosition = 1;
				in.gearOuttake = true;
				//gear.run();
				if(Timer.getFPGATimestamp() >= startTime + 0.1) {
					deliverState = DeliverererState.RESET;
					drive.driveStraightNavX(true, 0, 0);
					startTime = Timer.getFPGATimestamp();
				}
				break;
				
			/*case REVERSE:
				in.gearPanelPosition = 1;
				in.gearOuttake = false;
				//gear.run();
				drive.driveStraightNavX(false, -0.5, 0);
				if(Timer.getFPGATimestamp() >= startTime + 0.15) {
					deliverState = DeliverererState.RESET;
				}
				break;
			*/
				
			case RESET:
				in.gearPanelPosition = 1;
				//gear.run();
				//drive.tankDrive(0, 0, 1);
			}

		}
	}
}