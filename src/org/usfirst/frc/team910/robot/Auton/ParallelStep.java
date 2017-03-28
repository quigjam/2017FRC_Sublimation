package org.usfirst.frc.team910.robot.Auton;

import java.util.ArrayList;

public class ParallelStep extends AutonStep {
	
	ArrayList<AutonStep> steps;
	
	public ParallelStep(ArrayList<AutonStep> steps) {
		this.steps = steps;
	}
	
	public void setup(boolean blueAlliance){
		for(AutonStep s : steps){
			s.setup(blueAlliance);
		}
	}
	
	public void run(){
		for(AutonStep s : steps){
			s.run();
		}
	}
	
	public boolean isDone() {
		boolean done = true;
		for(AutonStep s : steps){
			done = done && s.isDone();
		}
		return done;
	}
	
}
