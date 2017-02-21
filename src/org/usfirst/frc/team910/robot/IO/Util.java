package org.usfirst.frc.team910.robot.IO;

public class Util {
	public static double interpolate(double[] axis, double[] table, double value) { //Interpolation function
		int i;
		for (i = 1; i < axis.length-1; i++) {
			if(axis[i] > value){
				break;
			}
		}
		double index = (value- axis[i-1])/(axis[i]-axis[i-1]);
		return index*(table[i]-table[i-1])+table[i-1];
	}
}
