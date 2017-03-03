package org.usfirst.frc.team910.robot.Auton;

public class AutonResetAngle extends AutonStep{

	public void run(){
		sense.init();
	}
	public boolean isDone(){
		return true;
	}
}
