package turcomenistao;

/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Maintainance
 *******************************************************************************/


import java.awt.Color;
import java.awt.Point;
import java.io.IOException;

import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;


/**
 * MyFirstLeader - a sample team robot by Mathew Nelson, and maintained by Flemming N. Larsen
 * <p/>
 * Looks around for enemies, and orders teammates to fire
 */
public class Bebeto extends TeamRobot {

	/**
	 * run:  Leader's default behavior
	 */
	public void run() {
		/*
		// Prepare RobotColors object
		RobotColors c = new RobotColors();

		c.bodyColor = Color.red;
		c.gunColor = Color.red;
		c.radarColor = Color.red;
		c.scanColor = Color.yellow;
		c.bulletColor = Color.yellow;
		*/
		
		// Set the color of this robot containing the RobotColors
		setBodyColor(Color.yellow);
		setGunColor(Color.yellow);
		setRadarColor(Color.yellow);
		setScanColor(Color.yellow);
		setBulletColor(Color.yellow);
		/*
		try {
			// Send RobotColors object to our entire team
			//broadcastMessage(c);
		} catch (IOException ignored) {}
		*/
		// Normal behavior
		while (true) {
			setTurnRadarRight(10000);
			ahead(100);
			back(100);
		}
	}

	/**
	 * onScannedRobot:  What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Don't fire on teammates
		if (isTeammate(e.getName())) {
			return;
		}
		// Calculate enemy bearing
		double enemyBearing = this.getHeading() + e.getBearing();
		// Calculate enemy's position
		double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
		double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));

		try {
			// Send enemy position to teammates
			broadcastMessage(new Point( (int)enemyX, (int)enemyY));
		} catch (IOException ex) {
			out.println("Unable to send order: ");
			ex.printStackTrace(out);
		}
	}

	/**
	 * onHitByBullet:  Turn perpendicular to bullet path
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		turnLeft(90 - e.getBearing());
	}
}
