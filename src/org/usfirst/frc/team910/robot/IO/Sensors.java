package org.usfirst.frc.team910.robot.IO;

import org.usfirst.frc.team910.robot.Vision.Camera;
import java.util.concurrent.locks.ReentrantLock;

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
	
	private ReentrantLock rl;
	
	public Sensors() {

		navx = new AHRS(SPI.Port.kMXP);
		robotAngle = new Angle(0);
		rl = new ReentrantLock();
		camera = new Camera(rl, this);

	}
	
	
	double zeroYaw = 0;
	public void init(){
		//navx.zeroYaw();
		zeroYaw = navx.getYaw();
	}

	public void read() {

		robotAngle.set(navx.getYaw() - zeroYaw); //Yaw of NavX is the robot angle
		accelX = navx.getRawAccelX(); //Accleration of robot
		SmartDashboard.putNumber("navxAngle", robotAngle.get());
		SmartDashboard.putBoolean("NavXWorking", navx.isConnected());

		SmartDashboard.putNumber("navxYaw", navx.getYaw() - zeroYaw);
		SmartDashboard.putNumber("navxRoll", navx.getRoll());
		SmartDashboard.putNumber("navxPitch", navx.getPitch());
		double time = Timer.getFPGATimestamp();
		deltaTime = time - lastTime;
		lastTime = time;
		SmartDashboard.putNumber("TimeDelta", deltaTime);
	}

}
