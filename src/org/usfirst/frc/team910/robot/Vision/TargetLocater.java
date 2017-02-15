package org.usfirst.frc.team910.robot.Vision;

public class TargetLocater implements Runnable {
	
	protected CameraData data;
	
	
	
	
	
	public TargetLocater(CameraData camData) {
		data = camData;  
	}
	
	public void run() {
		while(true) {
			Frame f = getMostRecentFrame();
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
	
	public Frame getMostRecentFrame() { 
		return data.frames[data.currentFrame]; 

	}
	
	public static class Limit{
		
		public double upper;
		public double lower;
		
		public Limit(double upper, double lower){
			
		this.upper = upper;
		this.lower = lower;
		
		}
	}
	public boolean checkLimit(Limit limit, double var ){
		return var > limit.lower && var < limit.upper;
			
			
			
	
		
	}
}