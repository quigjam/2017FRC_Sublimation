package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.Shooter;
import org.usfirst.frc.team910.robot.Vision.Target;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoShoot {

	private static double DRIVE_POWER = 0.5;
	private static final double ALLOWABLE_ANGLE_ERROR = 1;
	private static final double LAG_TIME = 0.1;
	private static final double SETTLE_TIME = 0.5;
	private static final double SHOOT_DISTANCE = 36;
	private static final double ALLOWABLE_DISTANCE_ERROR = 8;
	private static final double REVERSE_DIST = 36;
	private static final double CAM_DIST_FILT = 1; //SHEP Was  0.25;	
	
	private static double P_CONST = 0.035;
	private static double V_CONST = 0.018;
	private static double SPEED_FILT = 0.4;
	private static double RAMP_FILT = 0.1;

	private Inputs in;
	private Shooter shoot;
	private Sensors sense;
	private DriveTrain drive;

	public AutoShoot( Inputs in, Shooter shoot, Sensors sense, DriveTrain drive) {
		this.sense = sense;
		this.in = in;
		this.shoot = shoot;
		this.drive = drive;
		
		prefs = Preferences.getInstance();
	}

	private enum ShootState {
		CAM_CHECK, DRIVE, ALIGN, PRIME, FIRE, REVERSE; // construct all the states we need
	};

	private ShootState shootState = ShootState.values()[0]; // put them in an array
	private Target currentTarget;
	
	private double prevDist = 0;
	private double prevVel = 0;
	private double timeSpentClose = 0;
	
	private double rampFactor = 0;
	
	Preferences prefs;
	
	private double stopDist = 0;
	
	private double filteredDist = 0;
	
	public void run() {
		
		//P_CONST = prefs.getDouble("P_CONST", P_CONST);
		//V_CONST = prefs.getDouble("V_CONST", V_CONST);
		//SPEED_FILT = prefs.getDouble("SPEED_FILT", SPEED_FILT);
		//DRIVE_POWER = prefs.getDouble("DRIVE_POWER", DRIVE_POWER);
		//RAMP_FILT = prefs.getDouble("RAMP_FILT", RAMP_FILT);
		
		//SmartDashboard.putNumber("drivepower", DRIVE_POWER);
		
		if (in.autoShoot) { // when we hit the auto shoot button
			currentTarget = sense.camera.boiler.getCurrentTarget(); // set our current target to the boiler
			switch (shootState) {
			case CAM_CHECK:
				shoot.shooterPrime(false,false,0,false);
				timeSpentClose = 0;
				prevDist = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
				drive.setBrakes(false);
				rampFactor = currentTarget.distance;
				filteredDist = currentTarget.distance;
				if (Timer.getFPGATimestamp() - currentTarget.time < LAG_TIME && !in.autoShootNoCam) { // check to see if we see the target within an allowable time
					shootState = ShootState.DRIVE; // go to next state
				} else if (in.autoShootNoCam){
					shootState = ShootState.REVERSE;
					drive.driveStraightNavX(true, 0, 0);
					currentTarget.distance = REVERSE_DIST;
					stopDist = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2 + REVERSE_DIST;
				}

				break;
				
			case DRIVE:
				drive.originAngle.set(currentTarget.totalAngle.get()); // set our origin angel toward the target
				
				double dist = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
				double vel = (dist - prevDist) / sense.deltaTime;
				prevDist = dist;
				prevVel += SPEED_FILT * (vel - prevVel);
				if(vel < 0) prevVel = 0;
				
				//filter dist
				filteredDist += (currentTarget.distance - filteredDist) * CAM_DIST_FILT;
				
				double distPower = P_CONST * (currentTarget.distance - SHOOT_DISTANCE);  //CurrentTargetDistance WAS prevDist
				//double velPower = -vel * V_CONST;
				double velPower = -prevVel * V_CONST;   //Changed by Mr C.
				double power =  distPower + velPower;
				rampFactor += RAMP_FILT * (DRIVE_POWER - rampFactor);
				SmartDashboard.putNumber("distPower", distPower);
				SmartDashboard.putNumber("ASvelPower", velPower);
				SmartDashboard.putNumber("ASdist", dist);
				SmartDashboard.putNumber("ASvel", vel);
				if(Math.abs(power) > rampFactor){
					power = power / Math.abs(power) * rampFactor;
				}
				
				drive.driveStraightNavX(false, power, 0); // drive toward it
				
				shoot.shooterPrime(true,false,SHOOT_DISTANCE, false);
				
				//when close to target
				if (Math.abs(currentTarget.distance - SHOOT_DISTANCE) < ALLOWABLE_DISTANCE_ERROR) { 
					timeSpentClose += sense.deltaTime;
					if(timeSpentClose > SETTLE_TIME){
						shootState = ShootState.ALIGN; // go to next state
					}
				} else {
					timeSpentClose = 0;
				}
				break;
				
			case ALIGN:
				drive.setBrakes(true);
				drive.rotate(currentTarget.totalAngle); // face the target
				filteredDist += (currentTarget.distance - filteredDist) * CAM_DIST_FILT;
				shoot.shooterPrime(true,false,filteredDist, false);
				if (Math.abs(currentTarget.cameraAngle) < ALLOWABLE_ANGLE_ERROR) { // when we are lined up
					shootState = ShootState.PRIME; // go to the next step
					drive.tankDrive(0, 0, 1);
				}
				break;

			case PRIME:
				filteredDist += (currentTarget.distance - filteredDist) * CAM_DIST_FILT;
				shoot.shooterPrime(true,false,filteredDist, false); // prime
				if (shoot.upToSpeed() || in.fireButton) { // when we get up to speed
					shootState = ShootState.FIRE; // go to next state

				}
				break;
				
			case FIRE:
				filteredDist += (currentTarget.distance - filteredDist) * CAM_DIST_FILT;
				shoot.shooterPrime(true,true,filteredDist,false);
				break;
			
			case REVERSE:
				shoot.shooterPrime(true,false,REVERSE_DIST,false);
				drive.driveStraightNavX(false, -0.3, 0);
				if((drive.leftDriveEncoder + drive.rightDriveEncoder) / 2 > stopDist){
					drive.tankDrive(0, 0, 1);
					drive.setBrakes(true);
					shootState = ShootState.PRIME;
				}
				break;
			}
			
		} else {
			shootState = ShootState.values()[0]; // reset state machine
		}
		
		SmartDashboard.putString("ShootState", shootState.toString());
	}
	
	public boolean isShooting() {
		return shootState == ShootState.FIRE;
	}
	
	public boolean isCamAlign() {
		return shootState == ShootState.CAM_CHECK;
	}
}