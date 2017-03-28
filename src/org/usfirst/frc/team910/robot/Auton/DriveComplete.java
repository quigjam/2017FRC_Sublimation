package org.usfirst.frc.team910.robot.Auton;

public interface DriveComplete {
	public boolean isDone(double x, double y, boolean blueAlliance);
	public boolean flipLR();
}
