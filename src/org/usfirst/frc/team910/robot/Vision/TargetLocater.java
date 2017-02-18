package org.usfirst.frc.team910.robot.Vision;

public class TargetLocater implements Runnable {

	protected static final int X_FOV = 75;
	protected static final int Y_FOV = 47;
	protected static final int X_RES = 640;
	protected static final int Y_RES = 400;
	protected static final int X_DEG_PER_PIXEL = 75 / 640;

	protected static final double[] BOILER_DIST_AXIS = { 1, 1 };
	protected static final double[] BOILER_DIST_TABLE = { 1, 1 };

	protected static final double[] ROPE_DIST_AXIS = { 1, 1 };
	protected static final double[] ROPE_DIST_TABLE = { 1, 1 };

	protected static final double[] GEAR_DIST_AXIS = { 1, 1 };
	protected static final double[] GEAR_DIST_TABLE = { 1, 1 };
	
	protected static final double[] HOPPER_DIST_AXIS = { 1, 1 };
	protected static final double[] HOPPER_DIST_TABLE = { 1, 1 };

	protected double physcamangle;

	protected CameraData data;

	public TargetLocater(CameraData camData, double physcamangle) {
		data = camData;
		this.physcamangle = physcamangle;
	}

	public void run() {
		while (true) {
			Frame f = getMostRecentFrame();
			if (f.currentBlock < 2) {
				try {
					this.wait(20);
				} catch (InterruptedException e) {
				}
			} else {

			}
		}
	}

	public Frame getMostRecentFrame() {
		return data.frames[data.currentFrame];

	}

	public static class Limit {

		public double upper;
		public double lower;

		public Limit(double upper, double lower) {

			this.upper = upper;
			this.lower = lower;

		}
	}

	public boolean checkLimit(Limit limit, double var) {
		return var > limit.lower && var < limit.upper;

	}
}
