package cabrobo;

import java.io.Serializable;

import robocode.ScannedRobotEvent;

public class EnemyData implements Serializable {

	Point p;
	double enemyVelocity;
	double enemyHeading;
	
	
	public EnemyData(Point p, double enemyVelocity, double enemyHeading) {
		super();
		this.p = p;
		this.enemyVelocity = enemyVelocity;
		this.enemyHeading = enemyHeading;
	}

	
	
}
