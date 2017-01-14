package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.Encoder;

public class Sensors {

	private Encoder lEncoder;
	private Encoder rEncoder;

	public double leftEncoder;
	public double rightEncoder;

	Sensors() {
		lEncoder = new Encoder(ElectroPaul.LEFT_ENCODER_PORT_1, ElectroPaul.LEFT_ENCODER_PORT_2, false);
		rEncoder = new Encoder(ElectroPaul.RIGHT_ENCODER_PORT_1, ElectroPaul.RIGHT_ENCODER_PORT_2, false);
		lEncoder.setDistancePerPulse(1);
		rEncoder.setDistancePerPulse(1);
	}

	public void read() {
		leftEncoder = lEncoder.get();
		rightEncoder = rEncoder.get();
	}
}
