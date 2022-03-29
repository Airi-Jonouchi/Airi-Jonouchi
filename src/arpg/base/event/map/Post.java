package arpg.base.event.map;

import java.awt.Graphics;

public class Post extends AbstractEvent {

	String message;
	public Post(int x, int y, String message) {
		super(x, y);
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	@Override
	protected void lordImage() {}

	@Override
	public void drawImage(int offsetX, int offsetY, Graphics g) {}

}
