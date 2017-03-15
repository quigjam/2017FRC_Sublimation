package org.usfirst.frc.team910.robot.Subsystems;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;

public class GearSystem {

	// if parameter is met which we are using to tell we're close enough to the peg to initiate the procedure
	// set gear motor to some constant power to eject gear
	// when the gear is fully ejected from the robot, back the robot up from the peg
	// set flapmoter to a negative power and close it

	private Inputs in;
	private Outputs out;
	private Sensors sense;

	private static final double GROUND_POSITION = 0;
	private static final double SCORE_POSITION = 0;
	private static final double SAFE_POSITION = 0;
	private static final double ROLLER_POWER = 0.75;

	public GearSystem(Inputs in, Outputs out, Sensors sense) {

		this.in = in;
		this.out = out;
		this.sense = sense;
	}

	/*public void gearposition(int gearPanelPosition) { // Shows where a gear is on the panel

		switch (gearPanelPosition) {

		case 0: // startposition
			out.setGearPanelPower(START_POSITION);
			break;
		case 1: // hopperposition
			// if(ultrasonicDistance >= 0){
			out.setGearPanelPower(HOPPER_POSITION);
			// }
			break;

		case 2: // gearposition
			// if(ultrasonicDistance <= 0){
			out.setGearPanelPower(NATURAL_GEAR_POSITION);
			// }
			// if(ultrasonicDistance >= 0){
			out.setGearPanelPower(EXTENDED_GEAR_POSITION);
			// }
			break;
		}

	}*/

	public void run() {

		if (in.autoClimb || in.autoGear || in.autoShoot || in.autoHopper) { // if doing any of these
		} // functions dont do gearintake
		else {
			if (in.gearIntake) {
				gearRoller(ROLLER_POWER); // if we intake spin the motor to intake
			} else if (in.gearOuttake) {
				gearRoller(-ROLLER_POWER); // if we outtake spin the motor the oppostie way to outtake
			} else {
				gearRoller(0);

			}

			if (in.manualMode) {
				// TODO: Replace with position code
				if (in.fireOverride && in.gearPanelPosition == 1) {
					out.setGearPanelPower(0.20); // open gear panel
				} else if (in.fireOverride && in.gearPanelPosition == 3) {
					out.setGearPanelPower(-0.20); // close gear panel
				} else {
					out.setGearPanelPower(0); // stop moving gear panel
				}
			} else {// position mode
				// 1 2 3 switch position
				// 1 = ground
				// 2 = score
				// 3 = safe
				switch (in.gearPanelPosition) {
				case 1:
					out.setGearPanelPosition(GROUND_POSITION);
					break;
				case 2:
					out.setGearPanelPosition(SCORE_POSITION);
					break;
				case 3:
					out.setGearPanelPosition(SAFE_POSITION);
					break;
				default:
					out.setGearPanelPower(0);
					break;
				}
			}
		}
	}

	public void gearRoller(double power) { // when called set the GearRoller to some power
		out.setGearRoller(power);

	}
}
