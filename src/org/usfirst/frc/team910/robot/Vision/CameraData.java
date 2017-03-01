package org.usfirst.frc.team910.robot.Vision;

public class CameraData {
	Frame[] frames;
	int currentFrame; // points to the location at which the next frame will be stored
	
	private static int newestCamera = 0; // notes the camera which sent the most recently received frame
	private static int newestFrameIndex = 0; // points to the location at which the most recently received frame is stored
	
	public CameraData() {
		currentFrame = 0;
		frames = new Frame[Camera.FRAMES_PER_CAMERA];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = new Frame();
		}
	}
	
	int getNewestCamera() { return CameraData.newestCamera; }
	int getNewestFrameIndex() { return CameraData.newestFrameIndex; }
	void setNewestCamera(int c) { CameraData.newestCamera = c; }
	void setNewestFrameIndex(int f) { CameraData.newestFrameIndex = f; }
}
