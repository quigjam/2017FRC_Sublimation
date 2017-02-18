package org.usfirst.frc.team910.robot.Vision;

import org.usfirst.frc.team910.robot.IO.Sensors;
import org.usfirst.frc.team910.robot.IO.Util;
import org.usfirst.frc.team910.robot.Vision.TargetLocater.Limit;

import edu.wpi.first.wpilibj.Timer;

public class TargetLocaterLow extends TargetLocater implements Runnable {

	private static final int SIG_GEAR_1 = 1;
	private static final int SIG_GEAR_2 = 2;
	private static final int SIG_GEAR_3 = 3;
	private static final int SIG_FUEL_1 = 4;
	private static final int SIG_FUEL_2 = 5;
	private static final int SIG_FUEL_3 = 6;

	private static final Limit GEAR_PEG_ASPECT_RATIO_LIMIT = new TargetLocater.Limit(0, 0);
	private static final Limit GEAR_PEG_AREA_LIMIT = new TargetLocater.Limit(0, 0);
	private static final Limit PEG_PAIR_LIMIT_X = new TargetLocater.Limit(0, 0);
	private static final Limit PEG_PAIR_LIMIT_Y = new TargetLocater.Limit(0, 0);
	private static final Limit PEG_PAIR_LIMIT_WIDTH = new TargetLocater.Limit(0, 0);
	private static final Limit PEG_PAIR_LIMIT_HEIGHT = new TargetLocater.Limit(0, 0);

	private static final Limit HOPPER_ASPECT_RATIO_LIMIT = new TargetLocater.Limit(0, 0);
	private static final Limit HOPPER_AREA_LIMIT = new TargetLocater.Limit(0, 0);

	public TargetArray gearGoal;
	public TargetArray hopper;
	private Sensors sense;

	public TargetLocaterLow(CameraData camData, TargetArray gearGoal, TargetArray hopper, double physcamangle,
			Sensors sense) {
		super(camData, physcamangle);
		this.gearGoal = gearGoal;
		this.hopper = hopper;
		this.sense = sense;
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
				for (int i = 0; i < f.currentBlock; i++) {

					switch (f.blocks[i].signature) {

					case SIG_GEAR_1:
					case SIG_GEAR_2:
					case SIG_GEAR_3:
						checkGear(f.blocks[i]);
						break;

					case SIG_FUEL_1:
					case SIG_FUEL_2:
					case SIG_FUEL_3:
						checkFuel(f.blocks[i]);
						break;
					}

				}

			}

			// call function to set the current gear target
			setGearTarget();
			// reset the tracking for detecting both sides of the gear peg
			currentPeg = 0;
			for (int i = 0; i < multipleGearPegs.length; i++) {
				multipleGearPegs[i] = null;
			}

		}
	}

	private Block[] multipleGearPegs = new Block[5];
	private int currentPeg = 0;

	public void checkGear(Block currentblock) {
		// is this a gear peg?
		if (checkLimit(GEAR_PEG_ASPECT_RATIO_LIMIT, currentblock.width / currentblock.height)
				&& checkLimit(GEAR_PEG_AREA_LIMIT, currentblock.width * currentblock.height)) {

			boolean match = false;
			// does this peg match with any of the ones we have seen so far?
			for (int i = 0; multipleGearPegs[i] != null && i < multipleGearPegs.length; i++) {
				if (checkLimit(PEG_PAIR_LIMIT_X, Math.abs(currentblock.xcord - multipleGearPegs[i].xcord))
						&& checkLimit(PEG_PAIR_LIMIT_Y, Math.abs(currentblock.ycord - multipleGearPegs[i].ycord))
						&& checkLimit(PEG_PAIR_LIMIT_WIDTH, Math.abs(currentblock.width - multipleGearPegs[i].width))
						&& checkLimit(PEG_PAIR_LIMIT_HEIGHT,
								Math.abs(currentblock.height - multipleGearPegs[i].height))) {
					match = true;
					// combine the blocks
					if (currentblock.xcord > multipleGearPegs[i].xcord) {
						multipleGearPegs[i].width = (currentblock.xcord + currentblock.width / 2)
								- (multipleGearPegs[i].xcord - multipleGearPegs[i].width / 2);
					} else {
						multipleGearPegs[i].width = (multipleGearPegs[i].xcord - multipleGearPegs[i].width / 2)
								- (currentblock.xcord + currentblock.width / 2);
					}
					multipleGearPegs[i].xcord = (currentblock.xcord + multipleGearPegs[i].xcord) / 2;
					multipleGearPegs[i].ycord = (currentblock.ycord + multipleGearPegs[i].ycord) / 2;
					break;
				}
			}

			// if no pegs matched, add this one for the future
			if (!match && currentPeg < multipleGearPegs.length) {
				multipleGearPegs[currentPeg] = currentblock;
				currentPeg++;
			}

		}
	}

	// function to set the current gear peg target from the best candidate in the multipleGearPegs array
	public void setGearTarget() {
		Block bestBlock = null;
		for (int i = 0; i < currentPeg; i++) {
			if (bestBlock == null || multipleGearPegs[i].width > bestBlock.width) {
				bestBlock = multipleGearPegs[i];
			}
		}

		if (bestBlock != null) {
			Target thisGearGoal = gearGoal.getNextTarget();
			thisGearGoal.cameraAngle = (bestBlock.xcord - (X_RES / 2)) * X_DEG_PER_PIXEL + physcamangle;
			thisGearGoal.robotAngle = sense.robotAngle.get();
			thisGearGoal.distance = Util.interpolate(GEAR_DIST_AXIS, BOILER_DIST_TABLE, bestBlock.width);
			thisGearGoal.time = Timer.getFPGATimestamp();
			thisGearGoal.totalAngle.set(thisGearGoal.cameraAngle + thisGearGoal.robotAngle);
			gearGoal.setNextTargetAsCurrent();
		}
	}

	public void checkFuel(Block currentblock) {
		if (checkLimit(HOPPER_ASPECT_RATIO_LIMIT, currentblock.width / currentblock.height)
				&& checkLimit(HOPPER_AREA_LIMIT, currentblock.width * currentblock.height)) {
			Target thisHopper = hopper.getCurrentTarget();
			thisHopper.cameraAngle = (currentblock.xcord - (X_RES / 2)) * X_DEG_PER_PIXEL + physcamangle;
			thisHopper.robotAngle = sense.robotAngle.get();
			thisHopper.distance = Util.interpolate(HOPPER_DIST_AXIS, HOPPER_DIST_TABLE, currentblock.width);
			thisHopper.time = Timer.getFPGATimestamp();
			thisHopper.totalAngle.set(thisHopper.cameraAngle + thisHopper.robotAngle);
			hopper.setNextTargetAsCurrent();
		}
	}

	public Frame getMostRecentFrame() {
		return data.frames[data.currentFrame];

	}

}
