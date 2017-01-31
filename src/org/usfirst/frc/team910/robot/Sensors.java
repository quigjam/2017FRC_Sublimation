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

	public Camera camera;

	public double leftEncoder;
	public double rightEncoder;

	public Encoder gearEncoder;

	public double robotAngle;
	public double accelX;

	public double cameraAngle; // TODO Placeholder, get from camera and make
								// field relative
	public double cameraDistance;// TODO Placeholder, get from camera

	Sensors() {
		lEncoder = new Encoder(ElectroPaul.LEFT_ENCODER_PORT_1, ElectroPaul.LEFT_ENCODER_PORT_2, false);
		rEncoder = new Encoder(ElectroPaul.RIGHT_ENCODER_PORT_1, ElectroPaul.RIGHT_ENCODER_PORT_2, true);
		lEncoder.setDistancePerPulse(ENCODER_RESOLUTION);
		rEncoder.setDistancePerPulse(ENCODER_RESOLUTION);

		navx = new AHRS(SPI.Port.kMXP);
		camera = new Camera();

		gearEncoder = new Encoder(1, 2, false);
	}

	public void read() {
		leftEncoder = lEncoder.getDistance();
		rightEncoder = rEncoder.getDistance();
		robotAngle = navx.getYaw();
		accelX = navx.getRawAccelX();
		SmartDashboard.putNumber("leftdistance", leftEncoder);
		SmartDashboard.putNumber("rightdistance", rightEncoder);
		SmartDashboard.putNumber("navxYaw", robotAngle);

	}

}
