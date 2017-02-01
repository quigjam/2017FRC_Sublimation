package org.usfirst.frc.team910.robot;

public class Shooter {
	private Outputs out;
	private Inputs in;

	public Shooter(Outputs out, Inputs in) {
		this.out = out;
		this.in = in;
	}

	public void shooterPrime(double primePower) { // this actually spins the hopper feeder thingy  
		if (in.hopperFeedButton) {
			out.setHopperFeedPower(1);
		}

	}

	public void reverse() {
		if (in.reverseButton && in.hopperFeedButton) { // hopper feeder thingy spins in reverse to unjam things 
			out.setHopperFeedPower(-1);
		}
	}

	public void shoot() {
		if (in.shootButton) {
			out.setShooterPower(1); //the shooter starts to spin 
		} else {
			out.setShooterPower(0); // TODO find shooting and prime powers
			out.setHopperFeedPower(0);
		} 
	}
} 