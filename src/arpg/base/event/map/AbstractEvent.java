package arpg.base.event.map;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class AbstractEvent {
	
	protected int x;
	protected int y;
	protected BufferedImage image;

	public AbstractEvent(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public AbstractEvent() {
	}

	protected abstract void lordImage();
	public abstract void drawImage(int offsetX, int offsetY, Graphics g);

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
