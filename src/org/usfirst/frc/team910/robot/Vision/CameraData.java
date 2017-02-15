package org.usfirst.frc.team910.robot.Vision;

public class CameraData {
	Frame[] frames;
	int currentFrame;

	public CameraData() {
		currentFrame = 0;
		frames = new Frame[Camera.FRAMES_PER_CAMERA];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = new Frame();
		}
	}
}
