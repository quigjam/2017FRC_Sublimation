package org.usfirst.frc.team910.robot.Vision;

import edu.wpi.first.wpilibj.Timer;

public class Camera implements PixyEvent {

	public static final int BLOCKS_PER_FRAME = 64;
	public static final int FRAMES_PER_CAMERA = 10;
	public static final int NUMBER_OF_TARGETS = 3;
	private static final int MAX_CAMERAS = 4;

	public CameraData[] cameraData;

	public Camera() {
		PixyListener pl = new PixyListener(this, 10075);
		Thread t = new Thread(null, pl, "PixyListener");
		t.start();
		cameraData = new CameraData[MAX_CAMERAS];
		for (int i = 0; i < cameraData.length; i++) {
			cameraData[i] = new CameraData();
		}
		highestFrame = new int[MAX_CAMERAS];
	}

	public boolean gearGoalSearch() { // TODO Placeholder, write camera class
		return true;

	}

	public boolean climbSearch() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean fuelGoalSearch() {
		// TODO more camera to write
		return true;
	}

	private int[] highestFrame;

	// String order: cam#, frame#, sig, x, y, width, height
	public void eventGet(String s) {
		String[] parts = s.split(",");
		int cameraNumber = Integer.parseInt(parts[0]);
		int frameNumber = Integer.parseInt(parts[1]);
		int frameIndex = frameNumber % FRAMES_PER_CAMERA;
		if (frameNumber > highestFrame[cameraNumber]) {
			cameraData[cameraNumber].frames[frameIndex].reset();
			highestFrame[cameraNumber] = frameNumber;
			cameraData[cameraNumber].frames[frameIndex].time = Timer.getFPGATimestamp();
		}
		cameraData[cameraNumber].currentFrame = frameIndex;
		Frame currentFrame = cameraData[cameraNumber].frames[frameIndex];
		if (currentFrame.currentBlock < BLOCKS_PER_FRAME) {
			Block currentBlock = currentFrame.blocks[currentFrame.currentBlock];
			currentBlock.signature = Integer.parseInt(parts[2]);
			currentBlock.xcord = Integer.parseInt(parts[3]);
			currentBlock.ycord = Integer.parseInt(parts[4]);
			currentBlock.width = Integer.parseInt(parts[5]);
			currentBlock.height = Integer.parseInt(parts[6]);
			currentFrame.currentBlock++;
		}
	}

}
