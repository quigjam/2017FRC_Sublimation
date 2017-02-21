package org.usfirst.frc.team910.robot.IO;

public class Angle { //math to create angles throughout NavX-related and Auto functions
	private double angle;
	
	public Angle(double angle) {
		this.angle = mod(angle);
		
	}
	
	public double get() {
		return angle;
	}
	
	public void set(double newAngle) {
		angle = mod(angle);
	}
	
	public void add(double value) {
		angle = mod(angle + value);
	}
	
	//returns the shortest distance around the circle
	public double subtract(Angle other) {
		double difference = angle - other.get(); 
		return difference % 180; 
	}
	
	// this function ensures that our angle is positive when a negative number is passed in.
	private double mod(double value){
		return (value % 360 + 360) % 360;
	}
}
