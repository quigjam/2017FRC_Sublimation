package org.usfirst.frc.team910.robot.Vision;

import org.usfirst.frc.team910.robot.IO.Sensors;

import edu.wpi.first.wpilibj.Timer;

public class TargetLocaterHigh extends TargetLocater implements Runnable {

	private static final int SIG_BOILER_1 = 1;
	private static final int SIG_BOILER_2 = 2;
	private static final int SIG_BOILER_3 = 3;
	private static final int SIG_ROPE_1 = 4;
	private static final int SIG_ROPE_2 = 5;
	private static final int SIG_ROPE_3 = 6;

	private static final Limit top_boiler_area_limit = new TargetLocater.Limit(0, 0);
	private static final Limit bottom_boiler_area_limit = new TargetLocater.Limit(0, 0);
	private static final Limit top_boiler_aspectratio_limit = new TargetLocater.Limit(0, 0);
	private static final Limit bottom_boiler_aspectratio_limit = new TargetLocater.Limit(0, 0);
	private static final int TOP_BOILER_X_ALIGN = 5;

	private static final Limit rope_area_limit = new TargetLocater.Limit(0, 0);
	private static final Limit rope_aspectratio_limit = new TargetLocater.Limit(0, 0);

	private TargetArray boiler;
	private TargetArray rope;
	private Sensors sense;

	public TargetLocaterHigh(CameraData camData, TargetArray boiler, TargetArray rope, Sensors sense) {
		super(camData);
		this.boiler = boiler;
		this.rope = rope;
		this.sense = sense;
	}

	public void run() {
		while (true) {
			Frame f = getMostRecentFrame();
			if (f.currentBlock < 1) {
				try {
					this.wait(20);
				} catch (InterruptedException e) {
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
				topBoiler = null;
			}

		}
	}

	private Block topBoiler = null;

	public void checkBoiler(Block currentblock) {

		if (topBoiler == null && checkLimit(top_boiler_area_limit, currentblock.width * currentblock.height)
				&& checkLimit(top_boiler_aspectratio_limit, currentblock.width / currentblock.height)) {
			topBoiler = currentblock;
		}

		else if (topBoiler != null && checkLimit(bottom_boiler_area_limit, currentblock.height * currentblock.width)
				&& checkLimit(bottom_boiler_aspectratio_limit, currentblock.width / currentblock.height)
				&& topBoiler.xcord - TOP_BOILER_X_ALIGN < currentblock.xcord
				&& topBoiler.xcord + TOP_BOILER_X_ALIGN > currentblock.xcord) {
			Target thisBoiler = boiler.getNextTarget();
			thisBoiler.cameraAngle = 0;
			thisBoiler.distance = 0; // TODO math
			thisBoiler.robotAngle = sense.robotAngle.get();
			thisBoiler.time = Timer.getFPGATimestamp();
			boiler.setNextTargetAsCurrent();

		}
	}

	public void checkRope(Block currentblock) {

		if (checkLimit(rope_aspectratio_limit, currentblock.width / currentblock.height)
				&& checkLimit(rope_area_limit, currentblock.width * currentblock.height)) {
			Target thisrope = rope.getNextTarget();
			thisrope.cameraAngle = 0;
			thisrope.distance = 0;
			thisrope.robotAngle = sense.robotAngle.get();
			thisrope.time = Timer.getFPGATimestamp();
			rope.setNextTargetAsCurrent();
		}
	}

	public Frame getMostRecentFrame() {
		return data.frames[data.currentFrame];

	}

}
