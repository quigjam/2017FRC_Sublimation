package org.usfirst.frc.team910.robot;

public class Shooter {
	private Outputs out;
	private Inputs in;

	double jogconstant = 1;
	double jogoffset = 1;
	public Shooter(Outputs out, Inputs in) {
		this.out = out;
		this.in = in;
	}

	public void shooterPrime() {
		if (in.primeButton) {
			out.setShooterPower(1 + jogoffset);
			out.setAgitatorPower(1);
		} else {
			out.setShooterPower(0);
			out.setAgitatorPower(0);
		}

	}

	public void shooterFire() {
		if (in.fireButton) {
			out.setFeedPower(1);
		} else {
			out.setFeedPower(0); //TODO get actual powers for everything here 
		}
	boolean prevjogup = false;
	boolean prefjogdown = false;
		
	}
	boolean prevjogup = false;
	boolean prevjogdown = false;
		
	public void jog(boolean jogup, boolean jogdown) {
	
		if (jogup && !prevjogup) {

			jogoffset += jogconstant;
		} else if (jogdown && !prevjogdown) {

			jogoffset -= jogconstant;
		}

		prevjogup = jogup;
		prevjogdown = jogdown;
}}