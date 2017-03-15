package org.usfirst.frc.team910.robot.Functions;

import org.usfirst.frc.team910.robot.IO.Inputs;
import org.usfirst.frc.team910.robot.IO.Outputs;
import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.Subsystems.DriveTrain;
import org.usfirst.frc.team910.robot.Subsystems.Shooter;
import org.usfirst.frc.team910.robot.Vision.Target;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoShoot {

	private static final double DRIVE_POWER = 0.7;
	private static final double ALLOWABLE_ANGLE_ERROR = 1;
	private static final double LAG_TIME = 0.1;
	private static final double SETTLE_TIME = 0.75;
	private static final double SHOOT_DISTANCE = 48;
	private static final double ALLOWABLE_DISTANCE_ERROR = 6;
	
	private static double P_CONST = 0.075;
	private static double V_CONST = 0.5;
	private static double SPEED_FILT = 0.3;

	private Inputs in;
	private Shooter shoot;
	private Sensors sense;
	private DriveTrain drive;

	public AutoShoot( Inputs in, Shooter shoot, Sensors sense, DriveTrain drive) {
		this.sense = sense;
		this.in = in;
		this.shoot = shoot;
		this.drive = drive;
	}

	private enum ShootState {
		CAM_CHECK, DRIVE, ALIGN, PRIME, FIRE; // construct all the states we need
	};

	private ShootState shootState = ShootState.values()[0]; // put them in an array
	private Target currentTarget;
	
	private double prevDist = 0;
	private double prevVel = 0;
	private double timeSpentClose = 0;

	public void run() {
		
		SmartDashboard.getNumber("P_CONST", P_CONST);
		SmartDashboard.getNumber("V_CONST", V_CONST);
		SmartDashboard.getNumber("SPEED_FILT", SPEED_FILT);
		
		if (in.autoShoot) { // when we hit the auto shoot button
			currentTarget = sense.camera.boiler.getCurrentTarget(); // set our current target to the boiler
			switch (shootState) {
			case CAM_CHECK:
				shoot.shooterPrime(false,false);
				timeSpentClose = 0;
				prevDist = (drive.leftDriveEncoder + drive.rightDriveEncoder) / 2;
				drive.setBrakes(false);
				if (Timer.getFPGATimestamp() - currentTarget.time < LAG_TIME) { // check to see if we see the target within an allowable time
					shootState = ShootState.DRIVE; // go to next state
				}

				break;
			case DRIVE:
				drive.originAngle.set(currentTarget.totalAngle.get()); // set our origin angel toward the target
				
				double vel = ((drive.leftDriveEncoder + drive.rightDriveEncoder) / 2 - prevDist) * sense.deltaTime;
				prevVel += SPEED_FILT * (vel - prevVel);
				
				double distPower = P_CONST * (currentTarget.distance - SHOOT_DISTANCE);
				double velPower = - prevVel* V_CONST;
				double power =  distPower + velPower;
				SmartDashboard.putNumber("distPower", distPower);
				SmartDashboard.putNumber("velPower", velPower);
				if(Math.abs(power) > DRIVE_POWER){
					power = power / Math.abs(power) * DRIVE_POWER;
				}
				
				drive.driveStraightNavX(false, power, 0); // drive toward it
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
				//shoot.shooterPrime(true,false);
				if (Math.abs(currentTarget.cameraAngle) < ALLOWABLE_ANGLE_ERROR) { // when we are lined up
					shootState = ShootState.PRIME; // go to the next step
					drive.tankDrive(0, 0, 1);
				}
				break;

			case PRIME:
				//shoot.shooterPrime(true,false); // prime
				if (shoot.upToSpeed()) { // when we get up to speed
					shootState = ShootState.FIRE; // go to next state

				}
				break;
			case FIRE:
				//shoot.shooterPrime(true,true);
			}

		} else {
			shootState = ShootState.values()[0]; // reset state machine
		}
		
		SmartDashboard.putString("ShootState", shootState.toString());
	}
}