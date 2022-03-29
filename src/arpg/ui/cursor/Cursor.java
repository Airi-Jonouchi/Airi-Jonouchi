package arpg.ui.cursor;

import java.awt.Point;

import java.awt.Graphics;

public class Cursor {

	private static final int CURSOR_WIDTH = 20;
	private static final int CURSOR_HEIGHT = 30;

	private Point point;
	private int menuPos;
	private int brightness;
	private int menuSize;
	private int shift;

	public Cursor(Point point, int menuPos, int brightness, int menuSize) {
		this.point = point;
		this.menuPos = menuPos;
		this.brightness = brightness;
		this.menuSize = menuSize;
	}

	public Cursor(Point point, int menuPos, int brightness, int menuSize, int shift) {
		this.point = point;
		this.menuPos = menuPos;
		this.brightness = brightness;
		this.menuSize = menuSize;
		this.shift = shift;
	}
	
	public void drawCursor(Graphics g) {
		g.drawString("▶︎", 	this.point.x * CURSOR_WIDTH + shift, this.point.y * CURSOR_HEIGHT + 10);
	}

	public Point getPoint() {
		return this.point;
	}

	public void locationUp() {
		this.point.y--;
		this.menuPos--;
	}

	public void locationDown() {
		this.point.y++;
		this.menuPos++;
	}

	public void locationInit() {
		this.point.y = 4;
		this.menuPos = 0;
	}

	public int getPos() {
		return this.menuPos;
	}

	public int getBrightness() {
		return this.brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getSize() {
		return this.menuSize;
	}

	public void setSize(int size) {
		this.menuSize = size;
	}
}
