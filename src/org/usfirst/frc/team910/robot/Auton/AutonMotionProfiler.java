package org.usfirst.frc.team910.robot.Auton;

import org.usfirst.frc.team910.robot.IO.Util;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonMotionProfiler extends AutonStep {
	
	private static final double[] ACCEL_TABLE = { 170,  100,   30,    0}; //in in/sec/sec
	private static final double[] DECEL_TABLE = {-180, -130,  -80,  -60};
	private static final double[] ACCEL_AXIS =  {   0,  120,  200,  210}; //in in/sec
	
	private static final double Ka_MAX_PCT = 0.6;  
	private static final double Kv = 0; 	  // power / in/sec
	private static final double KdistP = 0.45; // power/in of error
	private static final double dt = 0.02; //time delta between runs
	
	private static final double MAX_ERROR = 3; //max error before slowing down
	
	private static int profileCount = 1;
	private int driveNum = 0;
	
	
	MotionProfileInputs inputs;
	
	private int segmentIndex;
	
	private double targetL;
	private double targetR;
	private double targetVelL;
	private double targetVelR;
	
	private double prevDistL;
	private double prevDistR;
	private double startL;
	private double startR;
	
	private double totalL;
	private double totalR;
	
	private double leftSum;
	private double rightSum;
	
	private double endTime;
	
	private double Ka_MAX;  // max allowed accel power
	
	private boolean forwardsL;
	private boolean forwardsR;
	
	public AutonMotionProfiler(MotionProfileInputs inputs){
		this.inputs = inputs;
		Ka_MAX = inputs.powerLimit * Ka_MAX_PCT;
		
		forwardsL = inputs.leftSegments[0] > 0;
		forwardsR = inputs.rightSegments[0] > 0;
	}
	
	public void setup(boolean blueAlliance){
		driveNum = profileCount;
		profileCount++;
		
		segmentIndex = 0;
		
		startL = drive.leftDriveEncoder;
		startR = drive.rightDriveEncoder;
		prevDistL = 0;
		prevDistR = 0;
		
		targetVelL = 0;
		targetVelR = 0;
		targetL = 0;
		targetR = 0;
		
		totalL = 0;
		totalR = 0;
		
		endTime = inputs.endTime + Timer.getFPGATimestamp();
		
		leftSum = 0;
		rightSum = 0;
		for(int i=0;i<inputs.leftSegments.length;i++){
			leftSum += inputs.leftSegments[i];
			rightSum += inputs.rightSegments[i];
		}
	}
	
	public void run(){
		
		double currL = drive.leftDriveEncoder - startL;
		double currR = drive.rightDriveEncoder - startR;
		
		double deltaL = currL - prevDistL;
		double deltaR = currR - prevDistR;
		
		totalL += deltaL;
		totalR += deltaR;
		
		//is this the first part of a new segment?
		boolean newSegment = !(segmentIndex == inputs.leftSegments.length - 1);
		if(newSegment){
			newSegment = newSegment && (Math.abs(targetL) > Math.abs(inputs.leftSegments[segmentIndex + 1]) || Math.abs(targetR) > Math.abs(inputs.rightSegments[segmentIndex +1]));
		}
		if(newSegment){
			//setup new segment
			targetL -= inputs.leftSegments[segmentIndex];
			targetR -= inputs.rightSegments[segmentIndex];
			currL -= inputs.leftSegments[segmentIndex];
			currR -= inputs.rightSegments[segmentIndex];
			startL += inputs.leftSegments[segmentIndex];
			startR += inputs.rightSegments[segmentIndex];
			
			segmentIndex++;
		}
		
		prevDistL = currL;
		prevDistR = currR;
			
		// run this segment
		
		//check if we should be slowing down
		boolean brake = Math.abs(totalL) > Math.abs(inputs.leftBrakeDist) || Math.abs(totalR) > Math.abs(inputs.rightBrakeDist);
		
		//check which side is the lead side for this segment
		boolean leftLeader = Math.abs(inputs.leftSegments[segmentIndex]) > Math.abs(inputs.rightSegments[segmentIndex]);
		
		
		//calc the leader distance setpoint
		double accelL;
		double accelR;
		double KaL = Ka_MAX;
		double KaR = Ka_MAX;
		
		if(leftLeader){
			//calc the leader
			//calc left side
			if(brake){
				accelL = Util.interpolate(ACCEL_AXIS, DECEL_TABLE, Math.abs(targetVelL)) * inputs.powerLimit;
				KaL = -KaL;
			} else {
				accelL = Util.interpolate(ACCEL_AXIS, ACCEL_TABLE, Math.abs(targetVelL)) * inputs.powerLimit;
			}	
			
			//handle segments that require backing up
			if(inputs.leftSegments[segmentIndex] < 0){
				accelL = -accelL;
				KaL = -KaL;
			}
			
			targetL += 0.5 * accelL * dt * dt + targetVelL * dt;
			targetVelL += accelL * dt;
			
			//slow down if target is too far ahead of the current position
			if(Math.abs(targetL) > Math.abs(currL + MAX_ERROR)){
				targetL = currL + MAX_ERROR * Math.signum(targetL);
				targetVelL = deltaL / dt;
			}
			
			//calc the follower distance setpoint
			//calc the right side
			double newTargetR = targetL / inputs.leftSegments[segmentIndex] * inputs.rightSegments[segmentIndex]; 
			
			double requiredVelR = 2 * (newTargetR - targetR) / dt - targetVelR; 
			
			double requiredAccelR = (requiredVelR - targetVelR) / dt;
			
			//check if we are using the ACCEL or DECEL limits
			//if the signs match, we are accelerating
			double maxAccel;
			if(Math.signum(requiredAccelR) == Math.signum(requiredVelR)){
				maxAccel = Util.interpolate(ACCEL_AXIS, ACCEL_TABLE, Math.abs(targetVelR)) * inputs.powerLimit;
			} else {
				maxAccel = Util.interpolate(ACCEL_AXIS, DECEL_TABLE, Math.abs(targetVelR)) * inputs.powerLimit;
			}
			
			//if accel is larger than the max, than we need to limit it
			//if we are limiting accel, recalculate distance and vel based on the limited accel
			//else use the ones we already calculated
			if(Math.abs(requiredAccelR) > Math.abs(maxAccel)){
				accelR = Math.abs(maxAccel) * Math.signum(requiredAccelR);
				KaR = KaR * Math.signum(requiredAccelR);
				targetR += 0.5 * accelR * dt * dt + targetVelR * dt;
				targetVelR += accelR * dt;
			} else {
				accelR = requiredAccelR;
				targetVelR = requiredVelR;
				targetR = newTargetR;
				KaR = requiredAccelR / Math.abs(maxAccel) * Ka_MAX_PCT;
			}
			
			
			
		} else { //right side is the leader
			//calc the leader
			//calc right side
			if(brake){
				accelR = Util.interpolate(ACCEL_AXIS, DECEL_TABLE, Math.abs(targetVelR)) * inputs.powerLimit;
				KaR = -KaR;
			} else {
				accelR = Util.interpolate(ACCEL_AXIS, ACCEL_TABLE, Math.abs(targetVelR)) * inputs.powerLimit;
			}	
			
			//handle segments that require backing up
			if(inputs.rightSegments[segmentIndex] < 0){
				accelR = -accelR;
				KaR = -KaR;
			}
			
			targetR += 0.5 * accelR * dt * dt + targetVelR * dt;
			targetVelR += accelR * dt;
			
			//slow down if target is too far ahead of the current position
			if(Math.abs(targetR) > Math.abs(currR + MAX_ERROR)){
				targetR = currR + MAX_ERROR * Math.signum(targetR);
				targetVelR = deltaR / dt;
			}
			
			//calc the follower distance setpoint
			//calc the left side
			double newTargetL = targetR / inputs.rightSegments[segmentIndex] * inputs.leftSegments[segmentIndex]; 
			
			double requiredVelL = 2 * (newTargetL - targetL) / dt - targetVelL; 
			
			double requiredAccelL = (requiredVelL - targetVelL) / dt;
			
			//check if we are using the ACCEL or DECEL limits
			//if the signs match, we are accelerating
			double maxAccel;
			if(Math.signum(requiredAccelL) == Math.signum(requiredVelL)){
				maxAccel = Util.interpolate(ACCEL_AXIS, ACCEL_TABLE, Math.abs(targetVelL)) * inputs.powerLimit;
			} else {
				maxAccel = Util.interpolate(ACCEL_AXIS, DECEL_TABLE, Math.abs(targetVelL)) * inputs.powerLimit;
			}
			
			//if accel is larger than the max, than we need to limit it
			//if we are limiting accel, recalculate distance and vel based on the limited accel
			//else use the ones we already calculated
			if(Math.abs(requiredAccelL) > Math.abs(maxAccel)){
				accelL = Math.abs(maxAccel) * Math.signum(requiredAccelL);
				KaL = KaL * Math.signum(requiredAccelL);
				targetL += 0.5 * accelL * dt * dt + targetVelL * dt;
				targetVelL += accelL * dt;
			} else {
				accelL = requiredAccelL;
				targetVelL = requiredVelL;
				targetL = newTargetL;
				KaL = requiredAccelL / Math.abs(maxAccel) * Ka_MAX_PCT;
			}
		}

		
		//PID to this profile point
		//leftPower = P * (setpointL - currL) + P*angleError + Kv * vl + Ka * al;
		double leftPower = KdistP * (targetL - currL) + Kv * targetVelL + KaL;
		double rightPower = KdistP * (targetR - currR) + Kv * targetVelR + KaR;
		
		drive.tankDrive(leftPower, rightPower, inputs.powerLimit);
		
		SmartDashboard.putNumber("autonPowerL", leftPower);
		SmartDashboard.putNumber("autonPowerR", rightPower);
		SmartDashboard.putNumber("autonTargetVelL", targetVelL);
		SmartDashboard.putNumber("autonTargetVelR", targetVelR);
		SmartDashboard.putNumber("targetL", targetL);
		SmartDashboard.putNumber("targetR", targetR);
		SmartDashboard.putNumber("totalL", totalL);
		SmartDashboard.putNumber("totalR", totalR);
		SmartDashboard.putNumber("KaL", KaL);
		SmartDashboard.putNumber("KaR", KaR);
		SmartDashboard.putNumber("lErr", targetL - currL);
		SmartDashboard.putNumber("rErr", targetR - currR);
		
		
	}
	//segments 
	public boolean isDone(){
		
		if(Math.abs(totalL) > Math.abs(inputs.endL)){
			SmartDashboard.putString("MotionProfileEnd" + driveNum, "totalL");
			return true;
		} else if(Math.abs(totalR) > Math.abs(inputs.endR)){
			SmartDashboard.putString("MotionProfileEnd" + driveNum, "totalR");
			return true;
		} else if(Timer.getFPGATimestamp() > endTime){
			SmartDashboard.putString("MotionProfileEnd" + driveNum, "time");
			return true;
		} else if(Math.abs(sense.getAccel()) > Math.abs(inputs.endAccel)){
			SmartDashboard.putString("MotionProfileEnd" + driveNum, "accel");
			return true;
		} else if(Math.abs(totalL) > Math.abs(leftSum) && Math.abs(totalR) > Math.abs(rightSum)){
			SmartDashboard.putString("MotionProfileEnd" + driveNum, "pathComplete");
			return true;
		} else if((forwardsL && targetVelL < 0 || !forwardsL && targetVelR > 0) || 
				  (forwardsR && targetVelR < 0 || !forwardsR && targetVelR > 0)) {
  		    SmartDashboard.putString("MotionProfileEnd" + driveNum, "speedZero");
			return true;	 
		} else {
			SmartDashboard.putString("MotionProfileEnd" + driveNum, "");
			return false;
		}
				
	}
	
}
