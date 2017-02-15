package org.usfirst.frc.team910.robot.Vision;

import edu.wpi.first.wpilibj.Timer;

public class Camera implements PixyEvent {

	private static final int PIXY_CAM_ID_1 = 122557792;
	private static final int PIXY_CAM_ID_2 = -324215471;
	private static final int PIXY_CAM_ID_3 = -250941110;
	private static final int PIXY_CAM_ID_4 = 10081618;
	private static final int PIXY_CAM_ID_5 = -657885874;
	private static final int PIXY_CAM_ID_6 = -1028254399;
	private static final int PIXY_CAM_ID_7 = -893772473;
	private static final int PIXY_CAM_ID_8 = 0;
	private static final int PIXY_CAM_ID_9 = 0;
	private static final int PIXY_CAM_ID_10 = 0;
	
	private static final int LEFT_PIXY = PIXY_CAM_ID_1;
	private static final int RIGHT_PIXY = PIXY_CAM_ID_2;
	private static final int FRONT_LOW_PIXY = PIXY_CAM_ID_3;
	private static final int FRONT_HIGH_PIXY = PIXY_CAM_ID_4;
	
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
		switch(cameraNumber){
		case LEFT_PIXY: 
			cameraNumber = 0;
			break;
		case RIGHT_PIXY:
			cameraNumber = 1;
			break;
		case FRONT_LOW_PIXY:
			cameraNumber = 2;
			break;
		case FRONT_HIGH_PIXY:
			cameraNumber = 3;
			break;
		default:
			System.out.println("Wrong pixy cam is plugged in. ID:" + cameraNumber);
		}
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
