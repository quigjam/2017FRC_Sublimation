package org.usfirst.frc.team910.robot.Vision;

public class TargetLocaterLow extends TargetLocater implements Runnable {

	private static final int SIG_GEAR_1 = 1;
	private static final int SIG_GEAR_2 = 2;
	private static final int SIG_GEAR_3 = 3;
	private static final int SIG_FUEL_1 = 4;
	private static final int SIG_FUEL_2 = 5;
	private static final int SIG_FUEL_3 = 6;

	public TargetArray gearGoal;
	public TargetArray hopper;
	
	public TargetLocaterLow(CameraData camData, TargetArray gearGoal, TargetArray hopper) {
		super(camData);
		this.gearGoal = gearGoal;
		this.hopper = hopper;
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

		}
	}

	public void checkGear(Block currentblock) {

	}

	public void checkFuel(Block currentblock) {

	}

	public Frame getMostRecentFrame() {
		return data.frames[data.currentFrame];

	}

}
