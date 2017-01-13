package org.usfirst.frc.team910.robot;

public class DriveTrain {

	private Inputs in;
	private Outputs out;

	public DriveTrain(Inputs in, Outputs out) {
		this.in = in;
		this.out = out;
	}

	public void drive() {
		tankDrive();
	}

	public void tankDrive() {
		out.setLeftDrive(in.leftJoyStickY);
		out.setRightDrive(in.rightJoyStickY);
	}
}