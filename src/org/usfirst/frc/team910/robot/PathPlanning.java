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
	Line parallelX = new Line();
	Line parallelY = new Line();

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
		parallelX.slope = robotLine.slope;
		parallelY.slope = goalLine.slope;
		parallelX.yIntercept = CIRCLE_RADIUS / Math.cos(Math.toRadians(sensorsNavXangle));
		parallelY.yIntercept = CIRCLE_RADIUS / Math.cos(Math.toRadians(inputsCameraAngle));

	}

}
