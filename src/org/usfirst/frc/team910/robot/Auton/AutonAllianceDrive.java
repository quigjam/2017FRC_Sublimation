package org.usfirst.frc.team910.robot.Auton;

public class AutonAllianceDrive extends AutonStep {

	private AutonStep red;
	private AutonStep blue;
	private boolean blueAlliance;
	
	public AutonAllianceDrive(AutonStep blue, AutonStep red) {
		this.red = red;
		this.blue = blue;
	}
	
	public void setup(boolean blueAlliance) {
		this.blueAlliance = blueAlliance;
		
		if(blueAlliance) {
			blue.setup(blueAlliance);
		} else {
			red.setup(blueAlliance);
		}
	}
	
	public void run() {
		if(blueAlliance){
			blue.run();
		} else {
			red.run();
		}
	}
	
	public boolean isDone() {
		if(blueAlliance){
			return blue.isDone();
		} else {
			return red.isDone();
		}
	}
}
