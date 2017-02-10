package org.usfirst.frc.team910.robot;

public class TargetLocaterHigh extends TargetLocater implements Runnable {
	
	
	private static final int SIG_BOILER_1 = 1;
	private static final int SIG_BOILER_2 = 2;
	private static final int SIG_BOILER_3 = 3;
	private static final int SIG_ROPE_1 = 4;
	private static final int SIG_ROPE_2 = 5;
	private static final int SIG_ROPE_3 = 6;
	
	private static final Limit top_boiler_area_limit = new TargetLocater.Limit(0,0);
	private static final Limit bottom_boiler_area_limit = new TargetLocater.Limit(0,0);
	private static final Limit rope_area_Limit = new TargetLocater.Limit(0,0);
	private static final Limit top_boiler_aspectratio_limit = new TargetLocater.Limit(0,0);
	private static final Limit bottom_boiler_aspectratio_limit = new TargetLocater.Limit(0,0);
	private static final Limit rope_aspectratio_Limit = new TargetLocater.Limit(0,0);
	public TargetLocaterHigh(Camera.CameraData camData) {
		super(camData);  
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
				for(int i = 0; i < f.currentBlock; i++){
					
					switch( f.blocks[i].signature){
						
					case SIG_BOILER_1:						
					case SIG_BOILER_2:
					case SIG_BOILER_3:
						checkBoiler(f.blocks[i]);
					break;	
					
					case SIG_ROPE_1:
					case SIG_ROPE_2:
					case SIG_ROPE_3:
						checkRope(f.blocks[i]);
					break;
					}
					
				}
				
			}
			
		}
	}
	
	public void checkBoiler(Camera.Block currentblock){
		
		if(checkLimit(top_boiler_area_limit, currentblock.width * currentblock.height) && 
				checkLimit(top_boiler_aspectratio_limit, currentblock.width/currentblock.height)){
			
		}
		
		if(checkLimit(bottom_boiler_area_limit, currentblock.height * currentblock.width) && 
		checkLimit(bottom_boiler_aspectratio_limit, currentblock.width/currentblock.height)){
		
			
		}
	}
	public void checkRope(Camera.Block currentblock){
		
		
		
	}
	public Camera.Frame getMostRecentFrame() { 
		return data.frames[data.currentFrame]; 

	}
	
	
	
}
