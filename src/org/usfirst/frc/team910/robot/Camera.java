package org.usfirst.frc.team910.robot;

public class Camera implements PixyEvent {

	private static final int BLOCKS_PER_FRAME = 64; 
	private static final int FRAMES_PER_CAMERA = 10;
	private static final int MAX_CAMERAS = 4; 
	
	public class Block {
		int signature;
		int xcord;
		int ycord;
		int width;
		int height;

		public Block() {
			signature = 0;
			xcord = 0;
			ycord = 0;
			width = 0;
			height = 0;
		}
	}

	public class Frame {
		Block[] blocks;
		int currentBlock;

		public Frame() {
			currentBlock = 0;
			blocks = new Block[BLOCKS_PER_FRAME];
			for(int i = 0; i < blocks.length; i++) {
				blocks[i] = new Block();
			}
		}
		public void reset() {
			currentBlock = 0;
		}
	}
	
	public class CameraData {
		Frame[] frames;
		int currentFrame;
		
		public CameraData() {
			currentFrame = 0;
			frames = new Frame[FRAMES_PER_CAMERA];
			for(int i = 0; i < frames.length; i++) {
				frames[i] = new Frame(); 
			}
		}
	}
	
	public CameraData[] cameraData;

	public Camera() {
		PixyListener pl = new PixyListener(this, 10075);
		Thread t = new Thread(null, pl, "PixyListener");
		t.start();
		cameraData = new CameraData[MAX_CAMERAS];
		for(int i = 0; i < cameraData.length; i++) {
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
	//String order: cam#, frame#, sig, x, y, width, height
	public void eventGet(String s) {
		String[] parts = s.split(","); 
		int cameraNumber = Integer.parseInt(parts[0]);
		int frameNumber = Integer.parseInt(parts[1]);
		int frameIndex = frameNumber% FRAMES_PER_CAMERA; 
		if(frameNumber > highestFrame[cameraNumber]) {
			cameraData[cameraNumber].frames[frameIndex].reset();
			highestFrame[cameraNumber] = frameNumber;
		}
		cameraData[cameraNumber].currentFrame = frameIndex;
		Frame currentFrame = cameraData[cameraNumber].frames[frameIndex];
		if(currentFrame.currentBlock < BLOCKS_PER_FRAME) {
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
