package org.usfirst.frc.team910.robot;

public class PathPlanning {

	private class Line {
		public double slope = 0;
		public double yIntercept = 0;
	}

	double sensorsNavXangle = 0;
	int inputsTargetGearPost = 0;


	Line robotLine = new Line();
	Line goalLine = new Line();

	private void calculateArcPoints() {
		// Step 1 Line from Start in current Direction
		// 0,0 = Robot Origin, Origin direction is based on NavX
		robotLine.yIntercept = 0;
		robotLine.slope = Math.tan(Math.toRadians(sensorsNavXangle));
		// Step 2 Line from End Parallel to Spring Hook thing
		goalLine.yIntercept = 0;
		
		//which gear post are we going after?
		switch(inputsTargetGearPost){
		case 1://top
			goalLine.slope = Math.tan(Math.toRadians(120));
			break;
		case 2://center
			goalLine.slope = Math.tan(Math.toRadians(180));
			break;
		case 3://bottom
			goalLine.slope = Math.tan(Math.toRadians(240));
			break;
		} 
		

		
	}

}
