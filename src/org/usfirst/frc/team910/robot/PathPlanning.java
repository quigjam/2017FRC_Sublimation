package org.usfirst.frc.team910.robot;

public class PathPlanning {

	private static final double CIRCLE_RADIUS = 0;

	private class Line {
		public double slope = 0;
		public double yIntercept = 0;
	}

	
	
	
	
	double sensorsNavXangle = 0;
	int inputsTargetGearPost = 0;
	double inputsCameraAngle = 0;
	double inputsCameraDistance = 0;

	Line robotLine = new Line();
	Line goalLine = new Line();
	Line parallelrobotLine = new Line(); //previously X
	Line parallelgoalLine = new Line();  //previously Y

	private void calculateArcPoints() {
		// Step 1 Line from Start in current Direction
		// 0,0 = Robot Origin, Origin direction is based on NavX
		robotLine.yIntercept = 0;
		robotLine.slope = Math.tan(Math.toRadians(sensorsNavXangle));
		// Step 2 Line from End Parallel to Spring Hook thing

		// which gear post are we going after?
		switch (inputsTargetGearPost) {
		case 1:// top
			goalLine.slope = Math.tan(Math.toRadians(120));
			break;
		case 2:// center
			goalLine.slope = Math.tan(Math.toRadians(180));
			break;
		case 3:// bottom
			goalLine.slope = Math.tan(Math.toRadians(240));
			break;
		}

		// find x cord based on camera angle using sine
		double xCord = inputsCameraDistance * Math.sin(Math.toRadians(inputsCameraAngle));
		// find y cord based on camera angle using cosine
		double yCord = inputsCameraDistance * Math.cos(Math.toRadians(inputsCameraAngle));
		// find y intercept
		goalLine.yIntercept = yCord - (goalLine.slope * xCord);

		// Step 3 Generate parallel inboard lines
		parallelrobotLine.slope = robotLine.slope;
		parallelgoalLine.slope = goalLine.slope;
		parallelrobotLine.yIntercept = CIRCLE_RADIUS / Math.cos(Math.toRadians(sensorsNavXangle));
		parallelgoalLine.yIntercept = CIRCLE_RADIUS / Math.cos(Math.toRadians(inputsCameraAngle));

		//Step 4, find center of circle
		//set the parrallelrobotline and parrallelgoalline equal to each other to find the x intercept they have in common
		
		//Step 5, perpandicular lines from the center of the circle to the orginal lines
		//add the y-interecept of of the parrallel robot line to the Circle_Radius to get to the orginal robot line
		//add the y-intercept of the parrallel goal line to the Circle_Radius to get to the orginal goal line
		
		//Step 6 find the coordinates of the intercetion of the perpandicular lines from the center of the circle to the orginal lines.
		//
	}

}
