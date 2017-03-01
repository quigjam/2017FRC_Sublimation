package org.usfirst.frc.team910.robot.IO;

public class ElectroPaul {//Shows what ports controls and motors are plugged into

	public final static boolean IS_COMP_BOT = true;
	
	// Drive
	public final static int LEFT_JOYSTICK_PORT = 0;
	public final static int RIGHT_JOYSTICK_PORT = 1;
	public final static int GAME_PAD_JOYSTICK_PORT = 2;
	public final static int CONTROL_BOARD_JOYSTICK_PORT = 3;
	//public final static int LEFT_MOTOR_PORT_1 = 0;
	//public final static int LEFT_MOTOR_PORT_2 = 2;
	//public final static int RIGHT_MOTOR_PORT_1 = 1;
	//public final static int RIGHT_MOTOR_PORT_2 = 3;

	// Sensors
	//public final static int LEFT_ENCODER_PORT_1 = 3;
	//public final static int LEFT_ENCODER_PORT_2 = 4;
	//public final static int RIGHT_ENCODER_PORT_1 = 5;
	//public final static int RIGHT_ENCODER_PORT_2 = 6;

	public final static int LEFT_DRIVE_CAN = 2;
	public final static int RIGHT_DRIVE_CAN = 13;
	public final static int LEFT_DRIVE = 1;
	public final static int RIGHT_DRIVE = 0;
	public final static int SHOOTER_MOTOR = 12;
	public static int TRANSPORTER_MOTOR;
	public final static int AGITATOR_MOTOR = 3;
	public final static int CLIMB_MOTOR_1 = 6;
	public final static int CLIMB_MOTOR_2 = 7;
	public final static int GEAR_ROLLER_MOTOR = 9;
	public final static int GEAR_PANEL_MOTOR_1 = 4;
	public final static int GEAR_PANEL_MOTOR_2 = 11; 
	
	public ElectroPaul(){
		if(IS_COMP_BOT){
			TRANSPORTER_MOTOR = 10;			
		} else {
			TRANSPORTER_MOTOR = 5;
		}
	}
	
} 