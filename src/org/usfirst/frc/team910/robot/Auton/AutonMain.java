package org.usfirst.frc.team910.robot.Auton;

import java.util.ArrayList;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.GearSystem;
import org.usfirst.frc.team910.robot.Subsystems.Shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonMain {

	ArrayList<AutonStep> steps;
	int currentStep = 0;

	public AutonMain() {
		steps = new ArrayList<AutonStep>();
		steps.add(new AutonDriveStraight(10, 0.2, 90));
		steps.add(new AutonEndStep());
	}

	public void init(Inputs in, Sensors sense, DriveTrain drive, GearSystem gear, Shooter shoot) {
		AutonStep.setRobotReferences(in, sense, drive, gear, shoot);
	}

	public void run() {
		SmartDashboard.putNumber("AutonStep", currentStep);
		steps.get(currentStep).run(); // institutes a state machine as an array of autons, works similarly to a "switch"
		if (steps.get(currentStep).isDone()) {
			currentStep++;
			steps.get(currentStep).setup();
		}
	}
}
