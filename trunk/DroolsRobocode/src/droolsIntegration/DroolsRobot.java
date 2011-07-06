package droolsIntegration;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;

import robocode.AdvancedRobot;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class DroolsRobot extends AdvancedRobot{

	private static final String RULES_FILE = "rules.drl";
	private static final String ACTION_QUERY = "actionQuery";
	private static final String ARG = "action";

	private KnowledgeBuilder kBuilder;
	private KnowledgeBase kBase;
	private StatefulKnowledgeSession kSession;
	private Vector<FactHandle> currentDoneTasks;

	public DroolsRobot(){
		this.currentDoneTasks = new Vector<FactHandle>();
	}

	public void run(){
		createKnowledgeBase();
		this.setAdjustGunForRobotTurn(true);
		this.setAdjustRadarForGunTurn(true);
		this.setAdjustRadarForRobotTurn(true);
		while(true){
			loadRobotState();
			loadBattleState();

			kSession.fireAllRules();
			cleanPreviousTasks();

			Vector<Action> actions = retrieveActions();
			runActions(actions);
			execute();
		}
	}


	private void createKnowledgeBase() {

		this.kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		this.kBuilder.add(ResourceFactory.newClassPathResource(RULES_FILE, DroolsRobot.class), ResourceType.DRL);

		if(kBuilder.hasErrors()){
			System.err.println(kBuilder.getErrors().toString());
		}

		kBase = KnowledgeBaseFactory.newKnowledgeBase();
		kBase.addKnowledgePackages(kBuilder.getKnowledgePackages());

		kSession = kBase.newStatefulKnowledgeSession();
//      // setup the audit logging
//		KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory
//				.newFileLogger(kSession, "log");
	}

	private void loadRobotState() {
		RobotState robotState = new RobotState(this);
		currentDoneTasks.add(kSession.insert(robotState));
	}

	private void loadBattleState() {
		BattleState battleState = new BattleState(getBattleFieldWidth(), getBattleFieldHeight(), getNumRounds(), getRoundNum(), getTime(), getOthers());
		currentDoneTasks.add(kSession.insert(battleState));
	}

	private void cleanPreviousTasks(){
		for (FactHandle fact : this.currentDoneTasks) {
			kSession.retract(fact);
		}
		this.currentDoneTasks.clear();
	}

	private Vector<Action> retrieveActions(){
		Action action;
		Vector<Action> actionsList = new Vector<Action>();

		QueryResults qr = kSession.getQueryResults(this.ACTION_QUERY);

		for (QueryResultsRow result : qr) {
			action = (Action) result.get(ARG);
			action.setRobot(this);
			actionsList.add(action);
			kSession.retract(result.getFactHandle(ARG));
		}

		return actionsList;
	}

	private void runActions(Vector<Action> actions){
		for (Action action : actions) {
			action.runAction();
		}
	}

	@Override
	public void onBulletHit(BulletHitEvent event) {
		currentDoneTasks.add(kSession.insert(event));
	}

	@Override
	public void onBulletHitBullet(BulletHitBulletEvent event) {
		currentDoneTasks.add(kSession.insert(event));
	}

	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		currentDoneTasks.add(kSession.insert(event));
	}

	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		currentDoneTasks.add(kSession.insert(event));
	}

	@Override
	public void onHitRobot(HitRobotEvent event) {
		currentDoneTasks.add(kSession.insert(event));
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		currentDoneTasks.add(kSession.insert(event));
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		currentDoneTasks.add(kSession.insert(event));
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		currentDoneTasks.add(kSession.insert(e));

		double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);
		double enemyBearing = e.getBearing();
		//System.out.println("Bearing: " + enemyBearing);
		//System.out.println("Heading: " + getHeading());
		//System.out.println("Scanner: " + getRadarHeading());
		//System.out.println("GunWeap: " + getGunHeading());
		 
	     // Calculate the coordinates of the robot
	     scannedX = (int)(getX() + Math.sin(angle) * e.getDistance());
	     scannedY = (int)(getY() + Math.cos(angle) * e.getDistance());
	     //System.out.println("Enemy bearing " + enemyBearing);
	}
	
	 // Paint a transparent square on top of the last scanned robot
	 public void onPaint(Graphics2D g) {
	     // Set the paint color to a red half transparent color
	     g.setColor(new Color(0xff, 0x00, 0x00, 0x80));
	 
	     // Draw a line from our robot to the scanned robot
	     g.drawLine(scannedX, scannedY, (int)getX(), (int)getY());
	 
	     // Draw a filled square on top of the scanned robot that covers it
	     g.fillRect(scannedX - 20, scannedY - 20, 40, 40);
	     
	     //double heading = Math.toRadians(getHeading());
	     double heading = Math.toRadians(getRadarHeading());
	     
	     int pointX = (int)(getX() + Math.sin(heading) * 100);
	     int pointY = (int)(getY() + Math.cos(heading) * 100);
	     
	     g.setColor(new Color(0x00, 0x00, 0xff, 0xff));
	     g.drawLine(pointX, pointY, (int)getX(), (int)getY());
	     //g.setColor(new Color(0x00, 0xff, 0x00, 0xff));
	     //g.drawLine((int)(getX() + Math.sin(enemyBearing) * 20), (int)(getY() + Math.cos(enemyBearing) * 20), (int)getX(), (int)getY());
	 }
	
	// The coordinates of the last scanned robot
	 int scannedX = Integer.MIN_VALUE;
	 int scannedY = Integer.MIN_VALUE;
	 double enemyBearing = Double.MIN_VALUE;
	
//	public static void main(String[] args) {
//		DroolsRobot dr = new DroolsRobot();
//		dr.createKnowledgeBase();
//		//dr.loadRobotState();
//		//dr.loadBattleState();
//
//		ScannedRobotEvent e = new ScannedRobotEvent("pepe", 100, 10, 10, 10, 10);
//        FactHandle ref = dr.kSession.insert(e);
//        dr.currentDoneTasks.add(ref);
//		
//		dr.kSession.fireAllRules();
//		dr.retrieveActions();
//	}

}
