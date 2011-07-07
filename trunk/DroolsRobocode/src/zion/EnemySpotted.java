package zion;

import java.io.Serializable;

public class EnemySpotted implements Serializable{

	private String enemyName;
	private double x;
	private double y;
	
	
	public EnemySpotted(String enemyName, double x, double y) {
		this.enemyName = enemyName;
		this.x = x;
		this.y = y;
	}

	public String getEnemyName() {
		return enemyName;
	}

	public void setEnemyName(String enemyName) {
		this.enemyName = enemyName;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public static Point calcularPonto(double x, double y, double heading, double enemyBearing, double enemyDistance){
		Point p = null;
		// Calculate enemy bearing
		double bearing = heading + enemyBearing;
		// Calculate enemy's position
		double enemyX = x + enemyDistance * Math.sin(Math.toRadians(bearing));
		double enemyY = y + enemyDistance * Math.cos(Math.toRadians(bearing));
		p = new Point(enemyX, enemyY);
		return p;
	}
	
}
