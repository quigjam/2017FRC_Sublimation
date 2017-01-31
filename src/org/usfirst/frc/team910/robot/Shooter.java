package org.usfirst.frc.team910.robot;

public class Shooter {
	private Outputs out;
	private Inputs in;

	public Shooter(Outputs out, Inputs in) {
		this.out = out;
		this.in = in;
	}

	public void shooterPrime(double primePower) {
		if (in.primeButton) {
			out.setPrimePower(1);
		}

	}

	public void reverse() {
		if (in.reverseButton && in.primeButton) {
			out.setPrimePower(-1);
		}
	}

	public void shoot() {
		if (in.shootButton) {
			out.setShooterPower(1);
		} else {
			out.setShooterPower(0); // TODO find shooting and prime powers
			out.setPrimePower(0);
		}
	}
}