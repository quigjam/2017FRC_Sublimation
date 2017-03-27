package org.usfirst.frc.team910.robot.IO;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Angle { //math to create angles throughout NavX-related and Auto functions
	private double angle;
	
	public Angle(double angle) {
		this.angle = mod(angle);
		
	}
	
	public double get() {
		return angle;
	}
	
	public void set(double newAngle) {
		angle = mod(newAngle);
	}
	
	public void add(double value) {
		angle = mod(angle + value);
	}
	
	//returns the shortest distance around the circle
	public double subtract(Angle other) {
		double diff = angle - other.get(); //target minus actual
		
		if(diff > 180) {
			diff = 360 - diff;
		} else if(diff < -180) {
			diff = 360 + diff;
		}
		
		SmartDashboard.putNumber("Angle.sub", diff);
		
		return diff; 
	}
	
	public double subtract(double other) {
		double diff = angle - mod(other); //target minus actual
		
		if(diff > 180) {
			diff = 360 - diff;
		} else if(diff < -180) {
			diff = 360 + diff;
		}
		
		SmartDashboard.putNumber("Angle.sub", diff);
		
		return diff; 
	}
	
	// this function ensures that our angle is positive when a negative number is passed in.
	private double mod(double value){
		return (value % 360 + 360) % 360;
	}
}
