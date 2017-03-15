package org.usfirst.frc.team910.robot.Vision;

import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.IO.Util;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TargetLocaterHigh extends TargetLocater implements Runnable {

	private static final int SIG_BOILER_1 = 1;
	private static final int SIG_BOILER_2 = 2;
	private static final int SIG_BOILER_3 = 3;
	private static final int SIG_ROPE_1 = 4;
	private static final int SIG_ROPE_2 = 5;
	private static final int SIG_ROPE_3 = 6;

	private static final Limit top_boiler_area_limit = new TargetLocater.Limit(1500, 200);
	private static final Limit bottom_boiler_area_limit = new TargetLocater.Limit(1500, 200);
	private static final Limit top_boiler_aspectratio_limit = new TargetLocater.Limit(1.5, 1);
	private static final Limit bottom_boiler_aspectratio_limit = new TargetLocater.Limit(1.5, 1);
	private static final int TOP_BOILER_X_ALIGN = 15;

	private static final Limit rope_area_limit = new TargetLocater.Limit(0, 0);
	private static final Limit rope_aspectratio_limit = new TargetLocater.Limit(0, 0);

	private TargetArray boiler;
	private TargetArray rope;
	private Sensors sense;

	public TargetLocaterHigh(CameraData camData, TargetArray boiler, TargetArray rope, Sensors sense,
			double physcamangle) {
		super(camData, physcamangle);
		this.boiler = boiler;
		this.rope = rope;
		this.sense = sense;

	}

	private double oldTime = 0;
	
	public synchronized void run() {
		while (true) {
			double time = Timer.getFPGATimestamp();
			SmartDashboard.putNumber("TLocTime", time - oldTime);
			oldTime = time;
			
			Frame f = getMostRecentFrame();
			if (f.currentBlock < 1) {
				try {
					//this.wait(200);
					Thread.sleep(20);
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
			} else {
				for (int i = 0; i < f.currentBlock; i++) {

					switch (f.blocks[i].signature) {

					case SIG_BOILER_1:
					case SIG_BOILER_2:
					case SIG_BOILER_3:
						checkBoiler(f.blocks[i]);
						break;

					case SIG_ROPE_1:
					case SIG_ROPE_2:
					case SIG_ROPE_3:
						checkRope(f.blocks[i]);
						break;
					}

				}
				if (!foundBot && topBoiler != null)
					useTopBoiler(); // if we still havent found a low boiler, use only the top as the target
				topBoiler = null;
				foundBot = false;
				f.currentBlock = 0;
			}

		}
	}

	private Block topBoiler = null;
	private boolean foundBot = false;

	// if we didnt find a low boiler block, just use the top one
	private void useTopBoiler(){
		SmartDashboard.putNumber("settingboiler", Timer.getFPGATimestamp());
		Target thisBoiler = boiler.getNextTarget();
		thisBoiler.cameraAngle = ((X_RES / 2.0) - topBoiler.xcord) * X_DEG_PER_PIXEL + physcamangle;
		thisBoiler.distance = Util.interpolate(BOILER_DIST_AXIS, BOILER_DIST_TABLE, topBoiler.ycord);
		thisBoiler.robotAngle = topBoiler.robotAngle;
		thisBoiler.time = Timer.getFPGATimestamp();
		thisBoiler.totalAngle.set(thisBoiler.robotAngle + thisBoiler.cameraAngle);
		boiler.setNextTargetAsCurrent();
		return;
	}
	
	public void checkBoiler(Block currentblock) {

		SmartDashboard.putNumber("BoilerArea", (double) currentblock.height * (double) currentblock.width);
		SmartDashboard.putNumber("BoilerAspect", (double) currentblock.width / (double) currentblock.height);
		SmartDashboard.putNumber("boiler xval", currentblock.xcord);
		
		if (topBoiler == null && checkLimit(top_boiler_area_limit, (double) currentblock.width * (double) currentblock.height)
				&& checkLimit(top_boiler_aspectratio_limit, (double) currentblock.width / (double) currentblock.height)) {
			topBoiler = currentblock;
			SmartDashboard.putNumber("settingtopboiler", Timer.getFPGATimestamp());
		}

		else if (topBoiler != null && checkLimit(bottom_boiler_area_limit, (double) currentblock.height * (double) currentblock.width)
				&& checkLimit(bottom_boiler_aspectratio_limit, (double) currentblock.width / (double) currentblock.height)
				&& topBoiler.xcord - TOP_BOILER_X_ALIGN < currentblock.xcord
				&& topBoiler.xcord + TOP_BOILER_X_ALIGN > currentblock.xcord) {
			Target thisBoiler = boiler.getNextTarget();
			thisBoiler.cameraAngle = (currentblock.xcord - (X_RES / 2)) * X_DEG_PER_PIXEL + physcamangle;
			thisBoiler.distance = Util.interpolate(BOILER_DIST_AXIS, BOILER_DIST_TABLE, currentblock.width);
			thisBoiler.robotAngle = currentblock.robotAngle;
			thisBoiler.time = Timer.getFPGATimestamp();
			thisBoiler.totalAngle.set(thisBoiler.cameraAngle + thisBoiler.robotAngle);
			boiler.setNextTargetAsCurrent();
			foundBot = true;
			SmartDashboard.putNumber("settingboiler", Timer.getFPGATimestamp());
		}
	}

	public void checkRope(Block currentblock) {

		if (checkLimit(rope_aspectratio_limit, currentblock.width / currentblock.height)
				&& checkLimit(rope_area_limit, currentblock.width * currentblock.height)) {
			Target thisRope = rope.getNextTarget();
			thisRope.cameraAngle = (currentblock.xcord - (X_RES / 2)) * X_DEG_PER_PIXEL + physcamangle;
			thisRope.distance = Util.interpolate(ROPE_DIST_AXIS, ROPE_DIST_TABLE, currentblock.width);
			thisRope.robotAngle = sense.robotAngle.get();
			thisRope.time = Timer.getFPGATimestamp();
			thisRope.totalAngle.set(thisRope.cameraAngle + thisRope.robotAngle);
			rope.setNextTargetAsCurrent();
		}
	}

	public Frame getMostRecentFrame() {
		return data.frames[data.currentFrame];

	}

}
