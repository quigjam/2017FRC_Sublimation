package org.usfirst.frc.team910.robot.Auton;

import org.usfirst.frc.team910.robot.IO.Util;

import edu.wpi.first.wpilibj.Timer;

public class AutonMotionProfiler extends AutonStep {
	
	private static final double[] ACCEL_TABLE = { 170,  170,  170}; //in in/sec/sec
	private static final double[] DECEL_TABLE = {-170, -170, -170};
	private static final double[] ACCEL_AXIS =  {   0,   60,  180}; //in in/sec
	
	
	
	private static final double Ka_MAX_PCT = 0.9;  
	private static final double Kv = 0; 	  // power / in/sec
	private static final double KdistP = 0.1; // power/in of error
	private static final double dt = 0.02; //time delta between runs
	
	
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
	
	private double startTime;
	
	private double Ka_MAX;  // max allowed accel power
	
	public AutonMotionProfiler(MotionProfileInputs inputs){
		this.inputs = inputs;
		Ka_MAX = inputs.powerLimit * Ka_MAX_PCT;
	}
	
	public void setup(boolean blueAlliance){
		segmentIndex = 0;
		
		prevDistL = drive.leftDriveEncoder;
		prevDistR = drive.rightDriveEncoder;
		startL = prevDistL;
		startR = prevDistR;
		
		targetVelL = 0;
		targetVelR = 0;
		targetL = 0;
		targetR = 0;
		
		startTime = 0;
		
		leftSum = 0;
		rightSum = 0;
		for(int i=0;i<inputs.leftSegments.length;i++){
			leftSum += inputs.leftSegments[i];
			rightSum += inputs.rightSegments[i];
		}
	}
	
	public void run(){
		
		double currL = drive.leftDriveEncoder;
		double currR = drive.rightDriveEncoder;
		
		double deltaL = currL - prevDistL;
		double deltaR = currR - prevDistR;
		
		totalL = currL - startL;
		totalR = currR - startR;
		
		//is this the first part of a new segment?
		boolean newSegment = !(segmentIndex == inputs.leftSegments.length - 1);
		if(newSegment){
			newSegment = newSegment && (Math.abs(targetL) > Math.abs(inputs.leftSegments[segmentIndex + 1]) || Math.abs(targetR) > Math.abs(inputs.rightSegments[segmentIndex +1]));
		}
		if(newSegment){
			//setup new segment
			targetL -= inputs.leftSegments[segmentIndex];
			targetR -= inputs.rightSegments[segmentIndex];
			
			
			segmentIndex++;
		}
		
			
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
			} else {
				accelL = Util.interpolate(ACCEL_AXIS, ACCEL_TABLE, Math.abs(targetVelL)) * inputs.powerLimit;
			}	
			
			//handle segments that require backing up
			if(inputs.leftSegments[segmentIndex] < 0){
				accelL = -accelL;
			}
			
			targetL += 0.5 * accelL * dt * dt + targetVelL * dt;
			targetVelL += accelL * dt;
			
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
			} else {
				accelR = Util.interpolate(ACCEL_AXIS, ACCEL_TABLE, Math.abs(targetVelR)) * inputs.powerLimit;
			}	
			
			//handle segments that require backing up
			if(inputs.rightSegments[segmentIndex] < 0){
				accelR = -accelR;
			}
			
			targetR += 0.5 * accelR * dt * dt + targetVelR * dt;
			targetVelR += accelR * dt;
			
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
		
	}
	//segments 
	public boolean isDone(){
		
		return totalL > inputs.endL 
			|| totalR > inputs.endR
			|| Timer.getFPGATimestamp() > startTime + inputs.endTime
			|| sense.getAccel() > inputs.endAccel
			|| (totalL > leftSum && totalR > rightSum);		
	}
	
}
