package org.usfirst.frc.team910.robot.Subsystems;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;

public class Climber {
	private Outputs out;
	private Inputs in;

	double climbReversePower = -1;
	double climbPower= 1;
	public Climber(Outputs out, Inputs in) {
		this.out = out;
		this.in = in;
	}

	//also delete the below function for reasons
	public void run(){
		if(in.climbButton){
			if(in.reverseButton){
				out.setClimbPower(-0.2);
			} else {
				out.setClimbPower(0.2);
			}
		} else {
			out.setClimbPower(0);
		}
	}
	//seriously delete it
	
	public void reverse(double climbPower){ //climb down
		
		if(in.reverseButton && in.climbButton){
			
			out.setClimbPower(climbReversePower);
		}
			else{
				
				out.setClimbPower(0);
				
			}
			
		
		
	}
	
	public void forward(double climbPower) { //climb up
		if (in.climbButton) {
			out.setClimbPower(climbPower);
		} else {
			out.setClimbPower(0);
		}

		
	}
}
