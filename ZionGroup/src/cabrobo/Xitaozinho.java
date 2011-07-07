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
package cabrobo;


import java.awt.geom.Point2D;

import robocode.Droid;
import robocode.MessageEvent;
import robocode.TeamRobot;
import robocode.util.Utils;
import static robocode.util.Utils.normalRelativeAngleDegrees;


/**
 * SimpleDroid - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
 * <p/>
 * Follows orders of team leader
 */
public class Xitaozinho extends TeamRobot implements Droid {

	/**
	 * run:  Droid's default behavior
	 */
	public void run() {
		out.println("MyFirstDroid ready.");
	}

	/**
	 * onMessageReceived:  What to do when our leader sends a message
	 */
	public void onMessageReceived(MessageEvent e) {
		// Fire at a point
		if (e.getMessage() instanceof Point) {
			Point p = (Point) e.getMessage();
			// Calculate x and y to target
			double dx = p.getX() - this.getX();
			double dy = p.getY() - this.getY();
			// Calculate angle to target
			double theta = Math.toDegrees(Math.atan2(dx, dy));

			// Turn gun to target
			turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
			// Fire hard!
			fire(3);
		} // Set our colors
		else if (e.getMessage() instanceof RobotColors) {
			RobotColors c = (RobotColors) e.getMessage();
			setBodyColor(c.bodyColor);
			setGunColor(c.gunColor);
			setRadarColor(c.radarColor);
			setScanColor(c.scanColor);
			setBulletColor(c.bulletColor);
		}
		else if (e.getMessage() instanceof RobotColors) {
			
		}
		else if (e.getMessage() instanceof EnemyData) {
			EnemyData ed = (EnemyData) e.getMessage();
			Point p = ed.p;
			double enemyX = p.getX();
			double dx = enemyX - this.getX();
			double enemyY = p.getY();
			double dy = enemyY - this.getY();
			double theta = Math.toDegrees(Math.atan2(dx, dy));
			
			double bulletPower = Math.min(3.0,getEnergy());
			
			double deltaTime = 0;
			double battleFieldHeight = getBattleFieldHeight(), 
			       battleFieldWidth = getBattleFieldWidth();
			double predictedX = enemyX, predictedY = enemyY;
			while((++deltaTime) * (20.0 - 3.0 * bulletPower) < Point2D.Double.distance(getX(), getY(), predictedX, predictedY)){		
				predictedX += Math.sin(ed.enemyHeading) * ed.enemyVelocity;	
				predictedY += Math.cos(ed.enemyHeading) * ed.enemyVelocity;
				if(	predictedX < 18.0 
					|| predictedY < 18.0
					|| predictedX > battleFieldWidth - 18.0
					|| predictedY > battleFieldHeight - 18.0){
					predictedX = Math.min(Math.max(18.0, predictedX), 
			                    battleFieldWidth - 18.0);	
					predictedY = Math.min(Math.max(18.0, predictedY), 
			                    battleFieldHeight - 18.0);
					break;
				}
			}
			 
			setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
			fire(bulletPower);
		}
	}
	
	
}
