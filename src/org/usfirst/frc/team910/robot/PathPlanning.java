package org.usfirst.frc.team910.robot;

public class PathPlanning {

	private static final double CIRCLE_RADIUS = 0;

	private class Line {
		public double slope = 0;
		public double yIntercept = 0;
	}

	private class Point {
		public double x;
		public double y;
	}

	double sensorsNavXangle = 0;
	int inputsTargetGearPost = 0;
	double inputsCameraAngle = 0;
	double inputsCameraDistance = 0;

	Line robotLine = new Line();
	Line goalLine = new Line();
	Line parallelrobotLine = new Line(); // previously X
	Line parallelgoalLine = new Line(); // previously Y
	Line perpendicularrobotLine = new Line();
	Line perpendiculargoalLine = new Line();

	Point centerofCircle = new Point();
	Point goalIntersection = new Point();
	Point robotIntersection = new Point();

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

		// Step 4, find center of circle
		// set the parallelrobotline and parallelgoalline equal to each other to
		// find the 1 point they have in common
		centerofCircle.x = (parallelgoalLine.yIntercept - parallelrobotLine.yIntercept)
				/ (parallelrobotLine.slope - parallelgoalLine.slope);
		centerofCircle.y = parallelrobotLine.slope * centerofCircle.x + parallelrobotLine.yIntercept;
		// Step 5, perpendicular lines from the center of the circle to the
		// original lines
		// perpendicular slopes are the negative inverse (-1/m)
		perpendicularrobotLine.slope = -1 / parallelrobotLine.slope;
		perpendiculargoalLine.slope = -1 / parallelgoalLine.slope;
		// we know the line passes through the center of the circle and has a
		// slope of (-1/m)
		// solve for y-intercept
		perpendicularrobotLine.yIntercept = centerofCircle.y - (perpendicularrobotLine.slope * centerofCircle.x);
		perpendiculargoalLine.yIntercept = centerofCircle.y - (perpendiculargoalLine.slope * centerofCircle.x);
		// Step 6 find the coordinates of the intersection of the perpendicular
		// lines from the center of the circle to the original lines.
		// these intersections are the start and end points of our arc
		goalIntersection.x = (perpendiculargoalLine.yIntercept - goalLine.yIntercept)
				/ (goalLine.slope - perpendiculargoalLine.slope);
		goalIntersection.y = (perpendiculargoalLine.slope * goalIntersection.x) + perpendiculargoalLine.yIntercept;

		robotIntersection.x = (perpendicularrobotLine.yIntercept - robotLine.yIntercept)
				/ (robotLine.slope - perpendicularrobotLine.slope);
		robotIntersection.y = (perpendicularrobotLine.slope * robotIntersection.x) + perpendicularrobotLine.yIntercept;
	}

}
