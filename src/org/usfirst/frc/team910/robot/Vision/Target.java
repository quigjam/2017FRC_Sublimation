package org.usfirst.frc.team910.robot.Vision;

import org.usfirst.frc.team910.robot.IO.Angle;

public class Target {
	public double time;
	public double distance;
	public double cameraAngle;
	public double robotAngle;
	public Angle totalAngle; 

	public Target() {
		time = 0;
		distance = 0;
		cameraAngle = 0;
		robotAngle = 0;
		totalAngle = new Angle(0);
	}
}

