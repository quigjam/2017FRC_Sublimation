package org.usfirst.frc.team910.robot;

public class Shooter {
	private Outputs out;
	private Inputs in;

	public Shooter(Outputs out, Inputs in) {
		this.out = out;
		this.in = in;
	}

	private void shootNow(double shootPower) {
		out.setShooterPower(shootPower);
	}
	public void shoot() {
		if (in.shootButton) {
			shootNow(1);
		}else {
			shootNow(0);	//TODO figure out actual powers for shooting
		}
	}
}
