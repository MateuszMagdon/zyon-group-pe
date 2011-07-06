package droolsIntegration;

import robocode.AdvancedRobot;

public class Action {
	
	private ActionType type;
	private double parameter;
	private int priority;
	
	private AdvancedRobot robot;

	public Action(ActionType type, double parameter, int priority) {
		super();
		this.type = type;
		this.parameter = parameter;
		this.priority = priority;
	}

	public ActionType getType() {
		return type;
	}

	public void setType(ActionType type) {
		this.type = type;
	}

	public double getParameter() {
		return parameter;
	}

	public void setParameter(double parameter) {
		this.parameter = parameter;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public AdvancedRobot getRobot() {
		return robot;
	}

	public void setRobot(AdvancedRobot robot) {
		this.robot = robot;
	}
	
	public void runAction(){
		if(this.robot != null){
			switch(this.type){
			case AHEAD:
				this.robot.setAhead(parameter);
				break;
			case BACK:
				this.robot.setBack(parameter);
				break;
			case STOP:
				this.robot.stop();
				break;
			case FIRE:
				this.robot.fire(parameter);
				break;
			case TURN_GUN_LEFT:
				this.robot.turnGunLeft(parameter);
				break;
			case TURN_GUN_RIGHT:
				this.robot.turnGunRight(parameter);
				break;
			case TURN_RADAR_LEFT:
				this.robot.turnRadarLeft(parameter);
				break;
			case TURN_RADAR_RIGHT:
				this.robot.turnRadarRight(parameter);
				break;
			case TURN_LEFT:
				this.robot.turnLeft(parameter);
				break;
			case TURN_RIGHT:
				this.robot.turnRight(parameter);
				break;
			}
		}
	}
	
	@Override
	public String toString() {
		return "Action[" + this.type + ", " + this.parameter + ", " + this.priority + "]";
	}

}
