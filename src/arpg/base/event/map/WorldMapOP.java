package arpg.base.event.map;

import java.awt.Graphics;

import arpg.main.Common.Direction;

public class WorldMapOP extends AbstractEvent {

	private int reverseX;
	private int reverseY;
	private Direction direction;

	public WorldMapOP(int x, int y, int reverseX, int reverseY, Direction direction) {
		super(x, y);
		this.reverseX = reverseX;
		this.reverseY = reverseY;
		this.direction = direction;
	}

	@Override
	protected void lordImage() {}

	@Override
	public void drawImage(int offsetX, int offsetY, Graphics g) {}


	public int getReverseX() {
		return this.reverseX;
	}

	public int getReverseY() {
		return this.reverseY;
	}

	public Direction getDirection() {
		return this.direction;
	}
}