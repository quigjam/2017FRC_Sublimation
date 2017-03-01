package org.usfirst.frc.team910.robot.Auton;

import java.util.ArrayList;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.GearSystem;
import org.usfirst.frc.team910.robot.Subsystems.Shooter;

public class AutonMain {

	ArrayList<AutonStep> steps;
	int currentStep = 0;

	public AutonMain() {
		steps = new ArrayList<AutonStep>();
		steps.add(new AutonDriveStraight(10, 0.2, 0));
		steps.add(new AutonEndStep());
	}

	public void init(Inputs in, Sensors sense, DriveTrain drive, GearSystem gear, Shooter shoot) {
		AutonStep.setRobotReferences(in, sense, drive, gear, shoot);
	}

	public void run() {
		steps.get(currentStep).run(); // institutes a state machine as an array of autons, works similarly to last year, but cleaner
		if (steps.get(currentStep).isDone()) {
			currentStep++;
			steps.get(currentStep).setup();
		}
	}
}
