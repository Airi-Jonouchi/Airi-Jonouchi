package arpg.base.event.map;

import java.awt.Graphics;

import static arpg.main.Common.*;
import arpg.personae.Hero;

public class MapChenge extends AbstractEvent {

	private int newX;
	private int newY;
	private MapDataPath newMapId;

	public MapChenge(int x, int y, int newX, int newY, MapDataPath newMapId) {
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

	public MapDataPath getNewMapId() {
		return newMapId;
	}
}
