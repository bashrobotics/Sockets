package org.usfirst.frc.team972.robot;

import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot {
	public void robotInit() {
		Jetson jetson = new Jetson();
		(new Thread(jetson)).start();
	}
}
