package org.usfirst.frc.team972.robot;

import edu.wpi.first.wpilibj.*;

public class Robot extends IterativeRobot {
	public void robotInit() {
		(new Thread(new Jetson())).start();
	}
}
