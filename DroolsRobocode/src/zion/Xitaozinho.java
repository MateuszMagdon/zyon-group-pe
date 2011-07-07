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
package zion;


import java.awt.Color;
import java.awt.geom.Point2D;

import robocode.BulletHitEvent;
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
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		setBodyColor(Color.yellow);
		setGunColor(Color.blue);
		setRadarColor(Color.red);
		setScanColor(Color.yellow);
		setBulletColor(Color.red);
	}

	@Override
	public void onBulletHit(BulletHitEvent event) {
		String hitRobot = event.getName();
		if(isTeammate(hitRobot)){
			turnRight(90);
			ahead(100);
		}
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
		else if (e.getMessage() instanceof EnemySpotted) {
			EnemySpotted es = (EnemySpotted)e.getMessage();
			//			double bearing = es.getEnemyBearing();
			//			double distance = es.getEnemyDistance();
			//			double sergioX = es.getLeaderX();
			//			double sergioY = es.getLeaderY();
			//			double sergioHeading = es.getLeaderHeading();
			//System.out.println("RECEBI QUE O INIMIGO FOI VISTO: " + bearing + " " + distance + " " + es.getEnemyName());
			//			
			//			// Calculate enemy bearing
			//			double enemyBearing = sergioHeading + bearing;
			//			// Calculate enemy's position
			//			double enemyX = sergioX + distance * Math.sin(Math.toRadians(enemyBearing));
			//			double enemyY = sergioY + distance * Math.cos(Math.toRadians(enemyBearing));

			Point p = new Point(es.getX(),es.getY());
			double dx = p.getX() - this.getX();
			double dy = p.getY() - this.getY();
			// Calculate angle to target
			double theta = Math.toDegrees(Math.atan2(dx, dy));
			
			// Calculate distance to target
			double distancia = Math.sqrt( Math.pow( (dx),2 ) + Math.pow( (dy),2 ) );
			
			
			if (distancia > 150) {
				//gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));

				//turnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight,
				
				double desvio = (Math.random()*40) - 20;
				
				//turnRight(normalRelativeAngleDegrees(theta - getHeading()) + desvio);
				
				turnRight(theta); // and see how much Tracker improves...
				//turnRight(Math.random() * 360);
				//ahead (Math.random() * 100);
				// (you'll have to make Tracker an AdvancedRobot)
				ahead(distancia - 140);
				
			//} else {
				// Our target is close.
				//gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
				//turnGunRight(gunTurnAmt);
				//fire(3);
			} else if (distancia < 100) {
				if (theta > -90 && theta <= 90) {
					back(40);
				} else {
					ahead(40);
				}
			}

			// Turn gun to target
			turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
			// Fire hard!
			fire(Math.min(3.0,getEnergy()));
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
