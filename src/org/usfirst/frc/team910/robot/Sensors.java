package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Sensors {

	private static final double ENCODER_RESOLUTION = 36.0 / 500.0;

	private Encoder lEncoder;
	private Encoder rEncoder;
	private AHRS navx;

	public double leftEncoder;
	public double rightEncoder;
	public double robotAngle;
	Sensors() {
		lEncoder = new Encoder(ElectroPaul.LEFT_ENCODER_PORT_1, ElectroPaul.LEFT_ENCODER_PORT_2, false);
		rEncoder = new Encoder(ElectroPaul.RIGHT_ENCODER_PORT_1, ElectroPaul.RIGHT_ENCODER_PORT_2, true);
		lEncoder.setDistancePerPulse(ENCODER_RESOLUTION);
		rEncoder.setDistancePerPulse(ENCODER_RESOLUTION);
		navx = new AHRS(SPI.Port.kMXP);
	}

	public void read() {
		leftEncoder = lEncoder.getDistance();
		rightEncoder = rEncoder.getDistance();
		robotAngle = navx.getYaw();
		SmartDashboard.putNumber("leftdistance", leftEncoder);
		SmartDashboard.putNumber("rightdistance", rightEncoder);
		SmartDashboard.putNumber("navxYaw", robotAngle);
		
		
	}

}
