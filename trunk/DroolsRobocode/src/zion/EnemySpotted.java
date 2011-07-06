package zion;

import java.io.Serializable;

public class EnemySpotted implements Serializable{

	private String enemyName;

	public String getEnemyName() {
		return enemyName;
	}

	public void setEnemyName(String enemyName) {
		this.enemyName = enemyName;
	}

	public EnemySpotted(String enemyName) {
		super();
		this.enemyName = enemyName;
	}
	
	
	
}
