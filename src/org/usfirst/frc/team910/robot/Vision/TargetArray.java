package org.usfirst.frc.team910.robot.Vision;

public class TargetArray {
	public Target[] targets;
	public int currentTarget;

	public TargetArray() {
		currentTarget = 0;
		targets = new Target[Camera.NUMBER_OF_TARGETS];
		for (int i = 0; i < targets.length; i++) {
			targets[i] = new Target();

		}
	}
}


