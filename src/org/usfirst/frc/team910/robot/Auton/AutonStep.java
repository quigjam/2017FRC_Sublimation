package org.usfirst.frc.team910.robot.Auton;

public abstract class AutonStep {

	public abstract void setup();
	public abstract void run();
	
	public abstract boolean  isDone();
}
