package org.usfirst.frc.team910.robot.Auton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.usfirst.frc.team910.robot.IO.Util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonFastArc extends AutonStep {

	private boolean blueAlliance;
	private double x;
	private double y;
	private double prevRight;
	private double prevLeft;
	private static final double POWER_PER_DEGREE = 0.0;
	private double[] turnPowerL;
	private double[] turnPowerR;
	private double[] turnAngle;
	private double[] xDistAxis;
	private double prevPwr = 0;
	private double PWR_FILT = 0.1;
	private double MAX_PWR = 0.5;
	private DriveComplete dc;
	
	private BufferedWriter fos; 
	private static int fileCount = 1;
	
	private double l;
	private double r;
	
	private boolean flipAxis;
	private boolean flipSides;

	public AutonFastArc(boolean flipAxis, boolean flipSides, double pwr, double[] turnPowerL, double[] turnPowerR, double[] turnAngle, double[] xDistAxis, DriveComplete dc) {
		this.turnPowerL = turnPowerL;
		this.turnPowerR = turnPowerR;
		this.turnAngle = turnAngle;
		this.xDistAxis = xDistAxis; 
		this.dc = dc;
		this.flipAxis = flipAxis;
		this.flipSides = flipSides;
		this.MAX_PWR = pwr;
		x = 0;
		y = 0;
		prevRight = 0;
		prevLeft = 0;
		l = 0;
		r = 0;
		
		/*
		try{
			fos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("/home/lvusr/out" + fileCount + ".txt"))));
			fileCount++;
		
			fos.write("l,r,x,y,inPwr,outPwr,setangle,lookup,botangle;");
		} catch(Exception e){
			e.getMessage();
		}
		*/
	}

	public void setup(boolean blueAlliance) {
		x = 0;
		y = 0;
		prevRight = drive.rightDriveEncoder;
		prevLeft = drive.leftDriveEncoder;
		this.blueAlliance = blueAlliance;
		prevPwr = 0;
		l = 0;
		r = 0;
	}

	public void run() {
		double leftEnc = drive.leftDriveEncoder;
		double rightEnc = drive.rightDriveEncoder;

		double deltaL = leftEnc - prevLeft;
		double deltaR = rightEnc - prevRight;

		double dist = (deltaL + deltaR) / 2;

		x += dist * Math.cos(Math.toRadians(sense.robotAngle.get()));
		y += dist * Math.sin(Math.toRadians(sense.robotAngle.get()));
		
		l += Math.abs(deltaL);
		r += Math.abs(deltaR);
		
		//flip lookup axis when we flip sides or switch directions
		double lookup;
		int flipCount = 0;
		if(blueAlliance && flipSides) flipCount++;
		//if(flipAxis) flipCount++;
		
		if(flipCount == 1) {
			lookup = r;
		} else {
			lookup = l;
		}
		SmartDashboard.putNumber("lookup", lookup);
		
		double insidePower = Util.interpolate(xDistAxis, turnPowerL, lookup);
		double outsidePower = Util.interpolate(xDistAxis, turnPowerR, lookup);
		double targetAngle = Util.interpolate(xDistAxis, turnAngle, lookup);
		
		//flip angle when we flip sides
		if(!blueAlliance && flipSides){
			targetAngle = -targetAngle;
		}
		
		//feedback
		double anglePower = Math.max(Math.min(sense.robotAngle.subtract(targetAngle) * -POWER_PER_DEGREE, 0.5), -0.5);
		//flip angle when we flip sides
		if(blueAlliance && flipSides) anglePower = -anglePower;
		SmartDashboard.putNumber("anglePower", anglePower);
		insidePower += anglePower;
		
		
		//low pass filter
		prevPwr += (MAX_PWR - prevPwr) * PWR_FILT; 
		
		//switch left and right powers when we are on the other side
		if (blueAlliance && flipSides) {
			drive.tankDrive(insidePower, outsidePower, prevPwr);
		} else {
			drive.tankDrive(outsidePower, insidePower, prevPwr);
		}
		
		prevLeft = leftEnc;
		prevRight = rightEnc;

		SmartDashboard.putNumber("l", l);
		SmartDashboard.putNumber("r", r);
		SmartDashboard.putNumber("x", x);
		SmartDashboard.putNumber("y", y);
		SmartDashboard.putNumber("botAngle", sense.robotAngle.get());
		SmartDashboard.putNumber("insidePower", insidePower);
		SmartDashboard.putNumber("outsidePower", outsidePower);
		SmartDashboard.putNumber("TargetAngle", targetAngle);
		
		/*
		//fos.write("l,r,x,y,lPwr,rPwr,setangle,lookup,botangle");
		try{
			fos.write(l + "," + r + "," + x + "," + y + "," + insidePower + "," + outsidePower + "," + targetAngle + "," + lookup + "," + sense.robotAngle.get() + ";");
		} catch(Exception e){
			e.getMessage();
		}
		*/
	}

	public boolean isDone() {
		//return (Math.abs(y) > 28);
		//return (Math.abs(x) < 28);
		//double l = drive.leftDriveEncoder - leftInit;
		//double r= drive.rightDriveEncoder - rightInit;
		if(dc.isDone(l,r,x, y, blueAlliance)){
			/*
			try{
				fos.flush();
				fos.close();
			} catch  (Exception e){
				e.getMessage();
			}
			*/
			return true;
		}
		return false;
	}
}
