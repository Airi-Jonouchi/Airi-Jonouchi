package arpg.base.event.map;

import java.awt.Graphics;

import arpg.personae.Hero;

public class MapChengePoint extends AbstractEvent {

	private int newX;
	private int newY;
	private String newMapId;

	public MapChengePoint(int x, int y, int newX, int newY, String newMapId) {
		super(x, y);
		this.newX = newX;
		this.newY = newY;
		this.newMapId = newMapId;
	}

	public void setLocation(Hero hero) {
		hero.setX(newX);
		hero.setY(newY);
	}

	@Override
	protected void lordImage() {}

	@Override
	public void drawImage(int offsetX, int offsetY, Graphics g) {}

	public int getNewX() {
		return this.newX;
	}

	public void setNewX(int newX) {
		this.newX = newX;
	}

	public int getNewY() {
		return this.newY;
	}

	public void setNewY(int newY) {
		this.newY = newY;
	}

	public String getNewMapId() {
		return newMapId;
	}
}
