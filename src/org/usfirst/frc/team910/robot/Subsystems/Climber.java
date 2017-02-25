package org.usfirst.frc.team910.robot.Subsystems;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;


public class Climber {
	private Outputs out;
	private Inputs in;

	private static final double CLIMB_POWER = 1;
	private static final double REVERSE_CLIMB_POWER = .50;

	public Climber(Outputs out, Inputs in) {
		this.out = out;
		this.in = in;
	}

	public void run() {
		if (in.autoClimb || in.autoGear || in.autoShoot || in.autoHopper) {

		} else if (in.reverseButton && in.climbButton) {
			climb(-CLIMB_POWER / 5);
		} else if (in.climbButton) {
			climb(CLIMB_POWER);
		} else {
			climb(0);

		}
	}

	public void climb(double climbPower) { // TODO: stop when we hit the top
		out.setClimbPower(climbPower);
	}

}
