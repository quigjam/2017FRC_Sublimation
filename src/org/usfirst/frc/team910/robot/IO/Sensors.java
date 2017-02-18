package org.usfirst.frc.team910.robot.IO;

import org.usfirst.frc.team910.robot.Vision.Camera;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Sensors {

	private AHRS navx;

	public Camera camera;

	public Angle robotAngle;
	public double accelX;

	public Angle cameraAngle; // TODO Placeholder, get from camera and make
								// field relative
	public double cameraDistance;// TODO Placeholder, get from camera

	public double ultrasonicDistance;//Placeholder
	
	public Sensors() {

		navx = new AHRS(SPI.Port.kMXP);
		camera = new Camera();
		robotAngle = new Angle(0);
		cameraAngle = new Angle(0);
	}

	public void read() {
		robotAngle.set(navx.getYaw());
		accelX = navx.getRawAccelX();
		SmartDashboard.putNumber("navxYaw", robotAngle.get());

	}

}
