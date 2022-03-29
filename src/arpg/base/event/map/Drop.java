package arpg.base.event.map;

import static arpg.main.Common.*;

import java.awt.Graphics;

import arpg.base.item.Item;



public class Drop extends AbstractEvent {

	private Item item;
	public Drop(int x, int y, Item item) {
		super(x, y);
		this.item = item;
	}

	@Override
	protected void lordImage() {}
	
	@Override
	public void drawImage(int offsetX, int offsetY, Graphics g) {
		int dx = x * CHIP_SIZE + offsetX;
		int dy = y * CHIP_SIZE + offsetY;
		item.drawIcon(dx, dy, g);
	}

	public Item getItem() {
		return this.item;
	}
}
