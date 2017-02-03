package org.usfirst.frc.team910.robot;

public class Climber {
	private Outputs out;
	private Inputs in;

	double climbReversePower = -1;
	double climbPower= 1;
	public Climber(Outputs out, Inputs in) {
		this.out = out;
		this.in = in;
	}

	
	public void reverse(double climbPower){
		
		if(in.reverseButton && in.climbButton){
			
			out.setClimbPower(climbReversePower);
		}
			else{
				
				out.setClimbPower(0);
				
			}
			
		
		
	}
	
	public void forward(double climbPower) {
		if (in.climbButton) {
			out.setClimbPower(climbPower);
		} else {
			out.setClimbPower(0);
		}

	}
}
