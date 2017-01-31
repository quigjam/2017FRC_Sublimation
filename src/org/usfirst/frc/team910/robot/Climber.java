package org.usfirst.frc.team910.robot;

public class Climber {
	private Outputs out;
	private Inputs in;

	public Climber(Outputs out, Inputs in) {
		this.out = out;
		this.in = in;
	}

	public void climbNow(double climbPower) {
		if (in.climbButton) {
			out.setClimbPower(1);
		} else {
			out.setClimbPower(0);
		}

	}
}
