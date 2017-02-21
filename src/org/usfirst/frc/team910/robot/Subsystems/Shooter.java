package org.usfirst.frc.team910.robot.Subsystems;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {
	private static final double SHOOTER_SPEED = 1700;
	private static final double JOG_AMOUNT = 10;
	private static final double ALLOWABLE_SHOOTER_ERROR = 50;

	private Outputs out;
	private Inputs in;

	private double jogoffset = 0;

	public Shooter(Outputs out, Inputs in) {
		this.out = out;
		this.in = in;
	}

	public void run() {
		jog();
		if (in.autoClimb || in.autoGear || in.autoShoot || in.autoHopper) { // if we hit any of these do nothing

		} else { // if we do anything else

			shooterPrime(in.primeButton); // call prime with the prime button

			shooterFire(in.fireButton); // and shooter with the fire button

		}
	}

	public void shooterPrime(boolean primeButton) { // Moves the big roller
		if (primeButton) { // if we hit the prime button
			out.setShooterSpeed(SHOOTER_SPEED + jogoffset); // ready the shooter to fire (with our constant shooter speed + how much we have jogged)
			out.setAgitatorPower(0.8); // start spinning the agitator to get the fuel moving
			// out.setShooterPower (0.5);
		} else { // if anything else happens make sure the shooter motor doesn't move
			out.setShooterPower(0);
		}
	}

	public boolean upToSpeed(double currentSpd) { // lets us know when we get the shooter motor up to the speed we set it to
		return currentSpd > SHOOTER_SPEED + jogoffset - ALLOWABLE_SHOOTER_ERROR;
	}

	public void shooterFire(boolean fireButton) { // Moves the smaller roller, transports balls from hopper
		if (fireButton) { // when we hit that fire button
			out.setTransportPower(0.75); // start up the transporter motor and start moving balls into the shooter motor
		} else { // if anything else happens make sure the transport motor doesn't move
			out.setTransportPower(0);
		}
	}

	boolean prevjogup = false;
	boolean prevjogdown = false;

	public void jog() { // allows slight adjustment of positions

		if (in.jogShooterUp && !prevjogup) { // if we hit the jog up button and didn't just hit jog up a moment ago
			jogoffset += JOG_AMOUNT; // add the jog constant to the jog offset of the motor
		} else if (in.jogShooterDown && !prevjogdown) { // if we hit jog down and didn't just hit jog down a moment ago

			jogoffset -= JOG_AMOUNT; // subtract the jog constant to the jog offset of the motor
		}

		prevjogup = in.jogShooterUp; // make sure we don't add more than once for button push
		prevjogdown = in.jogShooterDown; // or subtract

		SmartDashboard.putNumber("JogValue", SHOOTER_SPEED + jogoffset); // put out the jog-adjusted value to the dashboardgit
	}
}