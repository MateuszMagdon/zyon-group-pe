package droolsIntegration;

public class BattleState {
	
	private double arenaWidth;
	private double arenaHeight;
	private int roundsNumber;
	private int currentRound;
	private long timestamp;
	private int enemyQuantity;
	
	
	public BattleState(double arenaWidth, double arenaHeight, int roundsNumber,
			int currentRound, long timestamp, int enemyQuantity) {
		super();
		this.arenaWidth = arenaWidth;
		this.arenaHeight = arenaHeight;
		this.roundsNumber = roundsNumber;
		this.currentRound = currentRound;
		this.timestamp = timestamp;
		this.enemyQuantity = enemyQuantity;
	}


	public double getArenaWidth() {
		return arenaWidth;
	}


	public void setArenaWidth(double arenaWidth) {
		this.arenaWidth = arenaWidth;
	}


	public double getArenaHeight() {
		return arenaHeight;
	}


	public void setArenaHeight(double arenaHeight) {
		this.arenaHeight = arenaHeight;
	}


	public int getRoundsNumber() {
		return roundsNumber;
	}


	public void setRoundsNumber(int roundsNumber) {
		this.roundsNumber = roundsNumber;
	}


	public int getCurrentRound() {
		return currentRound;
	}


	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}


	public long getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


	public int getEnemyQuantity() {
		return enemyQuantity;
	}


	public void setEnemyQuantity(int enemyQuantity) {
		this.enemyQuantity = enemyQuantity;
	}
	

}
