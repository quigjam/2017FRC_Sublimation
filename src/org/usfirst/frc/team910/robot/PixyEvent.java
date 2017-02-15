package org.usfirst.frc.team910.robot;

/**
 * Define event sent by PixyListener
 * 
 * @param String s
 *            method that is called when an object detect event is received
 *            from a Pixy; implemented by the person who defines this interface
 */
public interface PixyEvent {
	public void eventGet(String s);
}