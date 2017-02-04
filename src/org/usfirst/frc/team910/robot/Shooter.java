package org.usfirst.frc.team910.robot;

public class Shooter {
	private static final double SHOOTER_SPEED = 4000;
	private static final double JOG_AMOUNT = 10;

	private Outputs out;
	private Inputs in;

	private double jogoffset = 0;

	public Shooter(Outputs out, Inputs in) {
		this.out = out;
		this.in = in;
	}

	public void run() {
		jog();
		if (in.autoShoot) {

		} else {
			shooterPrime();
			shooterFire();
		}
	}

	public void shooterPrime() {
		if (in.primeButton) {
			out.setShooterSpeed(SHOOTER_SPEED + jogoffset);
			// out.setAgitatorPower(0.8);
		} else {
			out.setShooterPower(0);
			out.setAgitatorPower(0);
		}

	}

	public void shooterFire() {
		if (in.fireButton) {
			out.setTransportPower(1);
		} else {
			out.setTransportPower(0); // TODO get actual powers for everything here
		}
	}

	boolean prevjogup = false;
	boolean prevjogdown = false;

	public void jog() {

		if (in.jogShooterUp && !prevjogup) {
			jogoffset += JOG_AMOUNT;
		} else if (in.jogShooterDown && !prevjogdown) {

			jogoffset -= JOG_AMOUNT;
		}

		prevjogup = in.jogShooterUp;
		prevjogdown = in.jogShooterDown;
	}
}