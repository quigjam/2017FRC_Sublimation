package org.usfirst.frc.team910.robot.Vision;

import edu.wpi.first.wpilibj.Timer;
import java.util.concurrent.locks.ReentrantLock;

public class Camera implements PixyEvent {

	private static final int PIXY_MESSAGE_EVENT_ALIVE = 1;
	private static final int PIXY_MESSAGE_EVENT_OBJECT_DETECTED = 2;
	
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
	
	private static final int BLOCK_MSG_OFFSET_SIG = 0;
	private static final int BLOCK_MSG_OFFSET_X = 1;
	private static final int BLOCK_MSG_OFFSET_Y = 2;
	private static final int BLOCK_MSG_OFFSET_WIDTH = 3;
	private static final int BLOCK_MSG_OFFSET_HEIGHT = 4;
	private static final int BLOCK_NUM_ELEMENTS = 5;
	
	private static final int PI_NETWORK_PORT_NUMBER = 10075 ;
   
	public CameraData[] cameraData;
	
	public TargetArray boiler;
	public TargetArray rope;
	public TargetArray hopper;
	public TargetArray gearGoalLeft;
	public TargetArray gearGoalMid;
	public TargetArray gearGoalRight;

	private ReentrantLock mutex; // Synchronize access to cameraData
	
	public Camera(ReentrantLock rl) {
		
		// Save the ReentrantLock required for the synchronization / mutual exclusion 
		this.mutex = rl;

		boiler = new TargetArray();
		rope = new TargetArray();
		hopper = new TargetArray();
		gearGoalLeft = new TargetArray();
		gearGoalMid = new TargetArray();
		gearGoalRight = new TargetArray();

		mutex.lock(); // Enter critical section
		// Create camera data structures 
		try {
			cameraData = new CameraData[MAX_CAMERAS];
			for (int i = 0; i < cameraData.length; i++) {
				cameraData[i] = new CameraData();
			}	
		} finally {
			mutex.unlock(); // Exit critical section
		}

		// Start listening for messages from the Pi
		PixyListener pl = new PixyListener(this, PI_NETWORK_PORT_NUMBER );
		Thread t = new Thread(null, pl, "PixyListener");
		t.start();
		
	}

	// Messages received by the Pi that this code knows about:
	//
	// PIXY_MESSAGE_EVENT_ALIVE message format is:
	// PIXY_MESSAGE_EVENT_ALIVE,number of cameras detected,camera ID,firmware major version, firmware minor version, firmware build number
	//
	// PIXY_MESSAGE_EVENT_OBJECT_DETECTED message format is:
	// PIXY_MESSAGE_EVENT_OBJECT_DETECTED, camera number, frame number, number of blocks, signature, x, y, width, height
	public void eventGet(String s) {
		String[] parts = s.split(",");
		int messageType = Integer.parseInt(parts[0]);
		switch(messageType){ // Check the message type 
		
		case PIXY_MESSAGE_EVENT_ALIVE: 
			System.out.println("Alive message received:" + s);
			return; // End of PIXY_MESSAGE_EVENT_ALIVE processing

		case PIXY_MESSAGE_EVENT_OBJECT_DETECTED:
			int cameraNumber = Integer.parseInt(parts[1]);
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
				System.out.println("Wrong pixy cam is plugged in. ID:" + Integer.parseInt(parts[1]));
				return;
			}
			
			mutex.lock(); // Enter critical section
			// Access camera data structures 
			try {
				// Remember this camera to get the most recent frame to callers when requested
				cameraData[cameraNumber].setNewestCamera(cameraNumber);

				// Add the frame just received to the end of the list of frames up to the limit of the number of frames
				// Otherwise, add this frame by overwriting / inserting this frame at the beginning of the list for this camera
				// Each message contains exactly one frame of data
				int newestFrame;
				if (cameraData[cameraNumber].currentFrame < FRAMES_PER_CAMERA) {
					newestFrame = cameraData[cameraNumber].currentFrame;
				} else {
					newestFrame = cameraData[cameraNumber].currentFrame = 0;				
				}
				// Remember this frame to get it to callers when requested
				cameraData[cameraNumber].setNewestFrameIndex(newestFrame);

				// Save the frame number received for this frame; maybe this will be useful for debugging, but it isn't used for anything functional
				cameraData[cameraNumber].frames[newestFrame].frameNumber = Integer.parseInt(parts[2]);

				// Apply a time stamp to this frame which is the system clock time in seconds as counted by the roboRIO FPGA
				cameraData[cameraNumber].frames[newestFrame].time = Timer.getFPGATimestamp();

				// Copy the number of blocks in this frame for this camera from the received message
				int numBlocks = Integer.parseInt(parts[3]);

				// Copy the blocks received for this frame
				for(int i=0; i < numBlocks; i++ ) {
					// Extract the block data from the message received from the Pi as follows:
					// 4 (offset within the original / complete message where the first block appears) + i (current block within the message) * number of things within a block + offset within a block where the individual piece of data is located				
					cameraData[cameraNumber].frames[newestFrame].blocks[cameraData[cameraNumber].frames[newestFrame].currentBlock].signature = Integer.parseInt(parts[4+(i*BLOCK_NUM_ELEMENTS)+BLOCK_MSG_OFFSET_SIG]);
					cameraData[cameraNumber].frames[newestFrame].blocks[cameraData[cameraNumber].frames[newestFrame].currentBlock].xcord = Integer.parseInt(parts[4+(i*BLOCK_NUM_ELEMENTS)+BLOCK_MSG_OFFSET_X]);
					cameraData[cameraNumber].frames[newestFrame].blocks[cameraData[cameraNumber].frames[newestFrame].currentBlock].ycord = Integer.parseInt(parts[4+(i*BLOCK_NUM_ELEMENTS)+BLOCK_MSG_OFFSET_Y]);
					cameraData[cameraNumber].frames[newestFrame].blocks[cameraData[cameraNumber].frames[newestFrame].currentBlock].width = Integer.parseInt(parts[4+(i*BLOCK_NUM_ELEMENTS)+BLOCK_MSG_OFFSET_WIDTH]);
					cameraData[cameraNumber].frames[newestFrame].blocks[cameraData[cameraNumber].frames[newestFrame].currentBlock].height = Integer.parseInt(parts[4+(i*BLOCK_NUM_ELEMENTS)+BLOCK_MSG_OFFSET_HEIGHT]);
					// Add the blocks just received to the end of the list of blocks for this frame up to the limit of the number of blocks
					// Otherwise, add these blocks by overwriting / inserting these blocks at the beginning of the list
					if (cameraData[cameraNumber].frames[newestFrame].currentBlock < BLOCKS_PER_FRAME) {
						cameraData[cameraNumber].frames[newestFrame].currentBlock++;
					} else {
						cameraData[cameraNumber].frames[newestFrame].currentBlock = 0;				
					}
					// Add a block to the block counter for this frame
					cameraData[cameraNumber].frames[newestFrame].numBlocks++;
				}
			
				// Update the location of the next frame for this camera
				// Each message has exactly one frame of data 
				cameraData[cameraNumber].currentFrame++;
				
			} finally {
				mutex.unlock(); // Exit critical section
			}

			break; // End of PIXY_MESSAGE_EVENT_OBJECT_DETECTED processing
	    
		default:
		    System.out.println("Raspberry Pi sent a message we don't recognize" + s);
		    return; // End of invalid message processing
		}
	}
}	
