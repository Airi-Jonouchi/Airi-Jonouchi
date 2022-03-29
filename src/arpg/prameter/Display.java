package arpg.prameter;

import static arpg.main.Common.FontOption.*;
import static arpg.main.MainPanel.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Rectangle;

import arpg.personae.Hero;
import arpg.system.window.String.IDrawString;

public class Display implements IDrawString {

	private  static final int ARC = 5;
	private static final Rectangle HP_MERTER = new Rectangle(300, 25, 110, 10);
	private static final Rectangle MP_MERTER = new Rectangle(
		HP_MERTER.x + 200, HP_MERTER.y, HP_MERTER.width, HP_MERTER.height
	);
	private static final Rectangle POWER_GAUGE = new Rectangle(130, 75, 110, 10);
	
	public void drawData(Hero hero, Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		FontAndColor fc = FontAndColor.STANDARD;

		g.setColor(fc.getColor());
		g.setFont(fc.getFont());
		drawString("LV", HP_MERTER.x - 160, HP_MERTER.y + 10, fc.getColor(), g);
		drawString("HP", HP_MERTER.x - 54, HP_MERTER.y + 10, fc.getColor(), g);
		drawString("MP", MP_MERTER.x - 54, MP_MERTER.y + 10, fc.getColor(), g);

		g.setColor(new Color(0,0,0,100));
		g.fillRoundRect(HP_MERTER.x, HP_MERTER.y, HP_MERTER.width, HP_MERTER.height, ARC, ARC);
		g.fillRoundRect(MP_MERTER.x, MP_MERTER.y, MP_MERTER.width, MP_MERTER.height, ARC, ARC);

		GradientPaint hpPaint = new GradientPaint(
			HP_MERTER.x, HP_MERTER.y, Color.GREEN,
			HP_MERTER.x + (int)(HP_MERTER.width * ((double) hero.getStatus().getHp() / (double)hero.getStatus().getMaxHp())), HP_MERTER.y + HP_MERTER.height, new Color(200, 255, 180)
		);
		g2.setPaint(hpPaint);
		g2.fillRoundRect(HP_MERTER.x, HP_MERTER.y, (int)(HP_MERTER.width * ((double) hero.getStatus().getHp() / (double)hero.getStatus().getMaxHp())), HP_MERTER.height, ARC, ARC);

		GradientPaint mpPaint = new GradientPaint(
			MP_MERTER.x, MP_MERTER.y, Color.RED,
			MP_MERTER.x + (int)(MP_MERTER.width * ((double) hero.getStatus().getMp() / (double)hero.getStatus().getMaxMp())), MP_MERTER.y + MP_MERTER.height, new Color(255, 180, 200)
		);
		g2.setPaint(mpPaint);
		g.fillRoundRect(MP_MERTER.x, MP_MERTER.y, (int)(MP_MERTER.width * ((double) hero.getStatus().getMp() / (double)hero.getStatus().getMaxMp())), MP_MERTER.height, ARC, ARC);
		
		//g.setColor(fc.getColor());
		g.setFont(fc.getFont());
		
		drawString(String.valueOf(hero.getName()),HP_MERTER.x - 280, HP_MERTER.y + 10, fc.getColor(), g);
		drawString(String.valueOf(hero.getStatus().getLevel()), HP_MERTER.x - 100, HP_MERTER.y + 10, fc.getColor(), g);
		drawString(String.valueOf(hero.getStatus().getHp() + " / " + hero.getStatus().getMaxHp()), HP_MERTER.x + 10,  HP_MERTER.y + 5, fc.getColor(), g);
		drawString(String.valueOf(hero.getStatus().getMp() + " / " + hero.getStatus().getMaxMp()), MP_MERTER.x + 10,  MP_MERTER.y + 5, fc.getColor(), g);
		drawString("C : ".concat(String.valueOf(money)), MP_MERTER.x + 150, MP_MERTER.y + 10, fc.getColor(), g);


		g.setColor(new Color(0,0,0,100));
		g.fillRoundRect(POWER_GAUGE.x, POWER_GAUGE.y, POWER_GAUGE.width, POWER_GAUGE.height, ARC, ARC);

		GradientPaint powerPaint = new GradientPaint(
			POWER_GAUGE.x, POWER_GAUGE.y, powercolor(hero)[0],
			POWER_GAUGE.x + (int)(POWER_GAUGE.width * ((double)hero.getPowerGauge() / 100)), POWER_GAUGE.y + POWER_GAUGE.height, powercolor(hero)[1]
		);
		g2.setPaint(powerPaint);
		g.fillRoundRect(POWER_GAUGE.x, POWER_GAUGE.y, (int)(POWER_GAUGE.width * ((double)hero.getPowerGauge() / 100)), POWER_GAUGE.height, ARC, ARC);

		g.setColor(fc.getColor());
		g.setFont(fc.getFont());
		drawString(String.valueOf(hero.getPowerGauge()).concat("%"), POWER_GAUGE.x + 30, POWER_GAUGE.y + 5, fc.getColor(), g);
		drawString(chargeTitle(hero), POWER_GAUGE.x - 110, POWER_GAUGE.y + 5, fc.getColor(), g);

	}

	private Color[] powercolor(Hero hero) {
		Color[] colors = new Color[2];

		if(hero.getPowerGauge() == 100) {
			colors[0] = Color.GREEN;
			colors[1] = new Color(200, 255, 180);
		}
		else {
			colors[0] = Color.ORANGE;
			colors[1] = new Color(255,215,0);
		}
		return colors;
	}

	private String chargeTitle(Hero hero) {

		String title = "";
		if(hero.getPowerGauge() == 100) {
			title = "チャージ完了";
		}
		else {
			title = "チャージ中";
		}
		return title;
	}
}
