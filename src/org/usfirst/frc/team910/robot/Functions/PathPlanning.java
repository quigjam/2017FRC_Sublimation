package org.usfirst.frc.team910.robot.Functions;

public class PathPlanning {

	public static final double CIRCLE_RADIUS = 48;
	private static final double CIRCLE_CIRCUMFERENCE = (2 * Math.PI * CIRCLE_RADIUS);

	private static class Line {
		public double slope = 0;
		public double yIntercept = 0;
	}

	private static class Point {
		public double x;
		public double y;
	}

	private static Line robotLine = new Line();
	private static Line goalLine = new Line();
	private static Line parallelrobotLine = new Line(); // previously X
	private static Line parallelgoalLine = new Line(); // previously Y
	private static Line perpendicularrobotLine = new Line();
	private static Line perpendiculargoalLine = new Line();

	private static Point centerofCircle = new Point();
	private static Point goalIntersection = new Point();
	private static Point robotIntersection = new Point();

	public static double distance;
	public static double arcdistance;

	public static void calculateArcPoints(double sensorsNavXangle, int inputsTargetGearPost, double inputsCameraAngle,
			double inputsCameraDistance) {
		double signGoal = 1;
		double signRobot = 1;
		// Step 1 Line from Start in current Direction
		// 0,0 = Robot Origin, Origin direction is based on NavX
		robotLine.yIntercept = 0;
		if (sensorsNavXangle == 90) { // undefined check
			robotLine.slope = 999999;
		} else if (sensorsNavXangle == 270) {
			robotLine.slope = -999999;
		} else {
			robotLine.slope = Math.tan(Math.toRadians(sensorsNavXangle));
		}
		// Step 2 Line from End Parallel to Spring Hook thing

		// which gear post are we going after?
		switch (inputsTargetGearPost) {
		case 1:// top
			goalLine.slope = Math.tan(Math.toRadians(120));
			if (sensorsNavXangle > 120 && sensorsNavXangle < 300) {
				signGoal = 1;
			} else {
				signGoal = -1;
			}
			if ((sensorsNavXangle < 90 || sensorsNavXangle > 120)
					&& (sensorsNavXangle < 270 || sensorsNavXangle > 300)) {
				signRobot = 1;
			} else {
				signRobot = -1;
			}
			break;
		case 2:// center
			goalLine.slope = Math.tan(Math.toRadians(179.999));
			if (sensorsNavXangle > 180) {
				signGoal = 1;
			} else {
				signGoal = -1;
			}
			if (sensorsNavXangle < 90 || sensorsNavXangle > 180 && sensorsNavXangle < 270) { // &&
																								// performs
																								// first
				signRobot = -1;
			} else {
				signRobot = 1;
			}
			break;
		case 3:// bottom
			goalLine.slope = Math.tan(Math.toRadians(240));
			if (sensorsNavXangle < 60 || sensorsNavXangle > 240) {
				signGoal = 1;
			} else {
				signGoal = -1;
			}

			if (sensorsNavXangle > 60 && sensorsNavXangle < 90 || sensorsNavXangle > 270 && sensorsNavXangle < 300) {
				signGoal = 1;
			} else {
				signGoal = -1;
			}
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
		double tempCos = Math.cos(Math.toRadians(sensorsNavXangle));
		if (tempCos == 0)
			tempCos = 0.000001; // undefined check
		parallelrobotLine.yIntercept = Math.abs(CIRCLE_RADIUS / tempCos) * signRobot;
		tempCos = Math.cos(Math.atan(goalLine.slope));
		if (tempCos == 0)
			tempCos = 0.000001; // undefined check
		parallelgoalLine.yIntercept = goalLine.yIntercept + Math.abs(CIRCLE_RADIUS / tempCos) * signGoal;

		// Step 4, find center of circle
		// set the parallelrobotline and parallelgoalline equal to each other to
		// find the 1 point they have in common
		centerofCircle.x = (parallelgoalLine.yIntercept - parallelrobotLine.yIntercept)
				/ (parallelrobotLine.slope - parallelgoalLine.slope);
		// slopes will never be equal, no need for undefined check
		centerofCircle.y = parallelrobotLine.slope * centerofCircle.x + parallelrobotLine.yIntercept;
		// Step 5, perpendicular lines from the center of the circle to the
		// original lines
		// perpendicular slopes are the negative inverse (-1/m)
		if (parallelrobotLine.slope == 0)
			parallelrobotLine.slope = 0.000001;
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

		distance = Math.sqrt((robotIntersection.x * robotIntersection.x) + (robotIntersection.y * robotIntersection.y));

		double arcangle = (Math.atan(perpendiculargoalLine.slope) - Math.atan(perpendicularrobotLine.slope));

		if (arcangle > 180)
			arcangle = 360 - arcangle;

		arcdistance = (arcangle / 360) * CIRCLE_CIRCUMFERENCE;
	}
}
