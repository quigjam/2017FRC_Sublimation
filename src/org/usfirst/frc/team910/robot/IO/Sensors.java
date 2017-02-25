package org.usfirst.frc.team910.robot.IO;

import org.usfirst.frc.team910.robot.Vision.Camera;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Sensors {

	private AHRS navx;

	public Camera camera;

	public Angle robotAngle;
	public double accelX;

	public double ultrasonicDistance;//Placeholder
	
	public double deltaTime;
	
	private double lastTime = 0;
	
	public Sensors() {

		navx = new AHRS(SPI.Port.kMXP);
		camera = new Camera();
		robotAngle = new Angle(0);
	}

	public void read() {
		robotAngle.set(navx.getYaw()); //Yaw of NavX is the robot angle
		accelX = navx.getRawAccelX(); //Accleration of robot
		SmartDashboard.putNumber("navxYaw", robotAngle.get());

		double time = Timer.getFPGATimestamp();
		deltaTime = time - lastTime;
		lastTime = time;
		
	}

}
