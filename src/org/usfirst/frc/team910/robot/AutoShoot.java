package org.usfirst.frc.team910.robot;


public class AutoShoot {
	private Outputs out;
	private Inputs in;
	private Shooter shoot;
	private Sensors sense;

	public AutoShoot(Outputs out, Inputs in, Shooter shoot) {
		this.out = out;
		this.in = in;
		this.shoot = shoot;
	}
	
	private enum ShootState{
		CAM_CHECK, ALIGN, PRIME, FIRE;
	};
	
	private ShootState shootState = ShootState.values()[0];
	
	public void run (){
		if(in.autoShoot){
			switch (shootState){
			case CAM_CHECK:

				if (sense.camera.fuelGoalSearch()) {
					shootState = ShootState.ALIGN; 
				}
			
				break;
			case ALIGN:
				//align to goal 
				//if camera is centered
				shootState = ShootState.PRIME;
				break;
				
			case PRIME:
					shoot.shooterPrime(); //TODO make so you don't need a button to press
					shootState = ShootState.FIRE;
					break;
			case FIRE:
				shoot.shooterFire();//TODO see comment above.
			}
			
		}
	}
}