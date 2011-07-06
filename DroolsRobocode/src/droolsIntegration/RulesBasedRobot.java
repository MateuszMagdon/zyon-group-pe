package droolsIntegration;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Vector;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
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
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import robocode.util.Utils;

public abstract class RulesBasedRobot extends TeamRobot{

	private String rulesFile = "rules.drl";

	private static final String ACTION_QUERY = "actionQuery";
	private static final String ARG = "action";

	private KnowledgeBuilder kBuilder;
	private KnowledgeBase kBase;
	private StatefulKnowledgeSession kSession;
	private Vector<FactHandle> currentTasks;

	public RulesBasedRobot(){
		this.defineRulesFile();
		this.currentTasks = new Vector<FactHandle>();
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
		this.kBuilder.add(ResourceFactory.newClassPathResource(this.rulesFile, RulesBasedRobot.class), ResourceType.DRL);

		if(kBuilder.hasErrors()){
			System.err.println(kBuilder.getErrors().toString());
		}

		kBase = KnowledgeBaseFactory.newKnowledgeBase();
		kBase.addKnowledgePackages(kBuilder.getKnowledgePackages());

		kSession = kBase.newStatefulKnowledgeSession();
	}

	private void loadRobotState() {
		RobotState robotState = new RobotState(this);
		currentTasks.add(kSession.insert(robotState));
	}

	private void loadBattleState() {
		BattleState battleState = new BattleState(getBattleFieldWidth(), getBattleFieldHeight(), getNumRounds(), getRoundNum(), getTime(), getOthers());
		currentTasks.add(kSession.insert(battleState));
	}

	private void cleanPreviousTasks(){
		for (FactHandle fact : this.currentTasks) {
			kSession.retract(fact);
		}
		this.currentTasks.clear();
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
		currentTasks.add(kSession.insert(event));
	}

	@Override
	public void onBulletHitBullet(BulletHitBulletEvent event) {
		currentTasks.add(kSession.insert(event));
	}

	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		currentTasks.add(kSession.insert(event));
	}

	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		currentTasks.add(kSession.insert(event));
	}

	@Override
	public void onHitRobot(HitRobotEvent event) {
		currentTasks.add(kSession.insert(event));
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		currentTasks.add(kSession.insert(event));
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		currentTasks.add(kSession.insert(event));
	}
	
	@Override
	public void onMessageReceived(MessageEvent event) {
		currentTasks.add(kSession.insert(event));
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		currentTasks.add(kSession.insert(e));
	}

	public void setRulesFile(String rulesFile) {
		this.rulesFile = rulesFile;
	}

	public abstract void defineRulesFile();

}
