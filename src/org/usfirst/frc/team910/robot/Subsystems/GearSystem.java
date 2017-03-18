package org.usfirst.frc.team910.robot.Subsystems;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GearSystem {

	// if parameter is met which we are using to tell we're close enough to the peg to initiate the procedure
	// set gear motor to some constant power to eject gear
	// when the gear is fully ejected from the robot, back the robot up from the peg
	// set flapmoter to a negative power and close it

	private Inputs in;
	private Outputs out;
	private Sensors sense;

	private static final double GROUND_POSITION = 200;
	private static final double SCORE_POSITION_L = 0.624;
	private static final double SCORE_POSITION_R = -0.636;
	private static final double SAFE_POSITION = 800;
	private static final double ROLLER_POWER = 0.9;
	private static final double CURRENT_SPIKE_TIME = 0.5;
	private static final double CURRENT_MAX = 8;
	
	private static final double GEAR_PANEL_POWER_UP = 0.35;
	private static final double GEAR_PANEL_POWER_DN = 0.7;
	
	private static final double PANEL_CURRENT_PEAK = 8.5;
	private static final double PANEL_CURRENT_TIME = 0.5;
	
	private static final double GEAR_DN_TIME = 1.5;
	
	
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

	private boolean currentTripped = false;
	private double currentTime = 0;
	private boolean gearUpLimit = false;
	private boolean gearDnLimit = false;
	private double gearUpTime = 0;
	private double gearDnTime = 0;
	
	
	public void run() {

		SmartDashboard.putBoolean("HasGear", currentTripped);
		
		if (in.autoClimb || in.autoGear || in.autoShoot || in.autoHopper) { // if doing any of these
		} // functions dont do gearintake
		else {

			if (in.manualMode) { //if in manual mode
				//arm
				if (in.gearPanelPosition == 1) { //this is down
					gearUpLimit = false;
					gearUpTime = 0;
					if(gearDnLimit){
						out.setGearPanelPower(0);
					} else {
						out.setGearPanelPower(GEAR_PANEL_POWER_UP); // open gear panel
					}
					if(out.gearPanel1Current + out.gearPanel2Current > PANEL_CURRENT_PEAK){
						gearDnTime += sense.deltaTime;
						if(gearDnTime > PANEL_CURRENT_TIME){
							gearDnLimit = true;
						}
					} else {
						gearDnTime = 0;
					}
					
				} else if (in.gearPanelPosition == 3) { //this is up
					gearDnLimit = false;
					gearDnTime = 0;
					if(gearUpLimit){
						out.setGearPanelPower(0.2);
					} else {
						out.setGearPanelPower(-GEAR_PANEL_POWER_DN); // close gear panel
					}
					if(out.gearPanel1Current + out.gearPanel2Current > PANEL_CURRENT_PEAK){
						gearUpTime += sense.deltaTime;
						if(gearUpTime > PANEL_CURRENT_TIME){
							gearUpLimit = true;
						}
					} else {
						gearUpTime = 0;
					}
					
				} else {
					out.setGearPanelPower(0); // stop moving gear panel
					gearDnLimit = false;
					gearUpLimit = false;
					gearDnTime = 0;
					gearUpTime = 0;
				}
				
				//roller
				/*if (in.gearIntake) {
					gearRoller(ROLLER_POWER); // if we intake spin the motor to intake
				} else if (in.gearOuttake) {
					gearRoller(-ROLLER_POWER); // if we outtake spin the motor the oppostie way to outtake
				} else {
					gearRoller(0);
				}*/
				
				if(in.gearIntake){
					if(currentTripped){
						//drive at low power to hold the gear in
						gearRoller(ROLLER_POWER*0.15);
					} else {
						gearRoller(ROLLER_POWER);
						if(out.gearIntakeCurrent > CURRENT_MAX){
							currentTime += sense.deltaTime;
							if(currentTime > CURRENT_SPIKE_TIME){
								currentTripped = true;
							}
						} else {
							currentTime = 0;
						}
					}
				} else if (in.gearOuttake){
					gearRoller(-ROLLER_POWER);
					currentTime = 0;
					currentTripped = false;
				} else {
					gearRoller(ROLLER_POWER * 0.15);
				}
			} else {// position mode
				
				
				if (in.gearPanelPosition == 1) {
					gearUpLimit = false;
					gearUpTime = 0;
					if(gearDnLimit){
						out.setGearPanelPower(0);
					} else {
						out.setGearPanelPower(GEAR_PANEL_POWER_UP); // open gear panel
					}
					if(out.gearPanel1Current + out.gearPanel2Current > PANEL_CURRENT_PEAK){
						gearDnTime += sense.deltaTime;
						if(gearDnTime > PANEL_CURRENT_TIME){
							gearDnLimit = true;
						}
					} else {
						gearDnTime = 0;
					}
					
				} else if (in.gearPanelPosition == 3) {
					gearDnLimit = false;
					gearDnTime = 0;
					if(gearUpLimit){
						out.setGearPanelPower(-0.2);
					} else {
						out.setGearPanelPower(-GEAR_PANEL_POWER_DN); // close gear panel
					}
					if(out.gearPanel1Current + out.gearPanel2Current > PANEL_CURRENT_PEAK){
						gearUpTime += sense.deltaTime;
						if(gearUpTime > PANEL_CURRENT_TIME){
							gearUpLimit = true;
						}
					} else {
						gearUpTime = 0;
					}
					
				} else {
					//out.setGearPanelPower(0); // stop moving gear panel
					gearDnLimit = false;
					gearUpLimit = false;
					gearDnTime = 0;
					gearUpTime = 0;
					
					out.setGearPanelPosition(SCORE_POSITION_L,SCORE_POSITION_R);
				}
				
				//roller
				/*if (in.gearIntake) {
					gearRoller(ROLLER_POWER); // if we intake spin the motor to intake
				} else if (in.gearOuttake) {
					gearRoller(-ROLLER_POWER); // if we outtake spin the motor the oppostie way to outtake
				} else {
					gearRoller(0);
				}*/
				
				if(in.gearIntake){
					if(currentTripped){
						//drive at low power to hold the gear in
						gearRoller(ROLLER_POWER*0.15);
					} else {
						gearRoller(ROLLER_POWER);
						if(out.gearIntakeCurrent > CURRENT_MAX){
							currentTime += sense.deltaTime;
							if(currentTime > CURRENT_SPIKE_TIME){
								currentTripped = true;
							}
						} else {
							currentTime = 0;
						}
					}
				} else if (in.gearOuttake){
					gearRoller(-ROLLER_POWER);
					currentTime = 0;
					currentTripped = false;
				} else {
					gearRoller(ROLLER_POWER * 0.15);
				}
				
				/*
				// 1 2 3 switch position
				// 1 = ground
				// 2 = score
				// 3 = safe
				switch (in.gearPanelPosition) {
				case 1://ground
					out.setGearPanelPosition(GROUND_POSITION);
					if(currentTripped){
						//drive at low power to hold the gear in
						gearRoller(ROLLER_POWER*0.2);
					} else {
						gearRoller(ROLLER_POWER);
						if(out.gearIntakeCurrent > CURRENT_MAX){
							currentTime += sense.deltaTime;
							if(currentTime > CURRENT_SPIKE_TIME){
								currentTripped = true;
							}
						} else {
							currentTime = 0;
						}
					}
					break;
					
				case 2://score
					out.setGearPanelPosition(SCORE_POSITION);
					currentTripped = false;
					currentTime = 0;
					
					//roller
					if (in.gearIntake) {
						gearRoller(ROLLER_POWER); // if we intake spin the motor to intake
					} else if (in.gearOuttake) {
						gearRoller(-ROLLER_POWER); // if we outtake spin the motor the oppostie way to outtake
					} else {
						gearRoller(0);
					}
					break;
					
				case 3://safe
					out.setGearPanelPosition(SAFE_POSITION);
					currentTripped = false;
					currentTime = 0;
					
					//roller
					if (in.gearIntake) {
						gearRoller(ROLLER_POWER); // if we intake spin the motor to intake
					} else if (in.gearOuttake) {
						gearRoller(-ROLLER_POWER); // if we outtake spin the motor the oppostie way to outtake
					} else {
						gearRoller(0);
					}
					break;
					
				default:
					out.setGearPanelPower(0);
					currentTripped = false;
					currentTime = 0;
					break;
				}
				*/
			}
		}
	}

	public void gearRoller(double power) { // when called set the GearRoller to some power
		out.setGearRoller(power);

	}
}
