package org.usfirst.frc.team910.robot;

public class TargetLocater implements Runnable {
	
	private Camera.CameraData data;
	
	public TargetLocater(Camera.CameraData camData) {
		data = camData;  
	}
	
	public void run() {
		while(true) {
			Camera.Frame f = getMostRecentFrame();
			if(f.currentBlock < 2) { 
				try {
					this.wait(20);
				} catch (InterruptedException e) {
				}
			}
			else { 
				
			}
		}
	}
	
	public Camera.Frame getMostRecentFrame() { 
		return data.frames[data.currentFrame]; 
	}
}
