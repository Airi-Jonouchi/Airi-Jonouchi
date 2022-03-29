package arpg.ui.cursor;

import java.awt.Graphics;
import java.awt.Point;

public class Cursor2D {

	private static final int CURSOR_WIDTH = 15;
	private static final int CURSOR_HEIGHT = 30;

	private Point cursorPoint;
	private Point menuPoint;
	private int brightness;
	private Point menuSize;

	public Cursor2D(Point cursorPoint, Point menuPoint, int brightness, Point menuSize) {
		this.cursorPoint = cursorPoint;
		this.menuPoint = menuPoint;
		this.brightness = brightness;
		this.menuSize = menuSize;
	}

	public void drawCursor(Graphics g) {
		g.drawString("▶︎", 	this.cursorPoint.x * CURSOR_WIDTH, this.cursorPoint.y * CURSOR_HEIGHT + 10);
	}

	public void locationUp() {
		this.cursorPoint.y--;
		this.menuPoint.y--;
	}

	public void locationDown() {
		this.cursorPoint.y++;
		this.menuPoint.y++;
	}

	public void locationRight() {
		this.cursorPoint.x += 3;
		this.menuPoint.x++;
	}

	public void locationLeft() {
		this.cursorPoint.x -= 3;
		this.menuPoint.x--;
	}

	public void shiftRight() {
		this.cursorPoint.x++;
	}

	public void shiftLeft() {
		this.cursorPoint.x--;
	}

	public void locationInit() {
		this.cursorPoint.y = 4;
		this.menuPoint.y = 0;
		this.cursorPoint.x = 4;
		this.menuPoint.x = 0;
	}

	public Point getCursorPoint() {
		return this.cursorPoint;
	}

	public void setCursorPoint(Point cursorPoint) {
		this.cursorPoint = cursorPoint;
	}

	public Point getMenuPoint() {
		return this.menuPoint;
	}

	public void setMenuPoint(Point menuPoint) {
		this.menuPoint = menuPoint;
	}

	public int getBrightness() {
		return this.brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public Point getMenuSize() {
		return this.menuSize;
	}

	public void setMenuSize(Point menuSize) {
		this.menuSize = menuSize;
	}
}
