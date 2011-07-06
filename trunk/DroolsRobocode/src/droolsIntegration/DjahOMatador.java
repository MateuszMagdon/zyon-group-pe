package droolsIntegration;

import java.awt.Color;
import java.awt.Graphics2D;

public class DjahOMatador extends RulesBasedRobot {

	@Override
	public void run() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		super.run();
	}

	@Override
	public void defineRulesFile() {
		this.setRulesFile("djah.drl");
	}

	@Override
	public void onPaint(Graphics2D g) {
		drawInfo(g, getGunHeading(),   new Color(0xff, 0x00, 0x00, 0xff));
		drawInfo(g, getRadarHeading(), new Color(0x00, 0xff, 0x00, 0xff));
		drawInfo(g, getHeading(),      new Color(0x00, 0x00, 0xff, 0xff));
	}

	private void drawInfo(Graphics2D g, double angle, Color c) {

		double heading = Math.toRadians(angle);

		int pointX = (int)(getX() + Math.sin(heading) * 100);
		int pointY = (int)(getY() + Math.cos(heading) * 100);

		g.setColor(c);
		g.drawLine(pointX, pointY, (int)getX(), (int)getY());
	}

}
