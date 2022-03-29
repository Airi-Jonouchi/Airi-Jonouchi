package arpg.base.event.map;

import static arpg.main.Common.*;

import java.awt.Graphics;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

import arpg.base.item.Item;

public class Treasure extends AbstractEvent {
	
	private static final int ROW_SIZE = 8;
	 
	private Item item;
	private int id;
	private int treasureNo;
	private static boolean[] treasureTable = new boolean[TOTAL_TREASURE];

	public Treasure(int x, int y, int id, int treasureNo, Item item) {
		super(x, y);
		this.id = id;
		this.treasureNo = treasureNo;
		this.item = item;

		if(image == null) {
			lordImage();
		}
	}

	public Treasure() {}
	
	@Override
	protected void lordImage() {
		try {
			image = ImageIO.read(getClass().getResourceAsStream("image/treasure.png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void drawImage(int offsetX, int offsetY, Graphics g) {

		if(id >= 0)  {
			if(treasureTable[treasureNo]) {
				setEmptyId();
			}
			int sx = (id % ROW_SIZE) * CHIP_SIZE;
			int sy = (id / ROW_SIZE) * CHIP_SIZE;	
			int dx = x * CHIP_SIZE + offsetX;
			int dy = y * CHIP_SIZE + offsetY;
	
			g.drawImage(image, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, sx, sy, sx + CHIP_SIZE, sy + CHIP_SIZE, null);		
		}	
	}

	public Item getting() {
		treasureTable[treasureNo] = true;
		return this.item;	
	}

	public boolean isEmpty() {
		if(treasureTable[treasureNo]) {
			return true;
		}
		return false;
	}

	public boolean[] getTreasureTable() {
		return treasureTable;
	}

	public void lordTreasureTable(int treasureNo, boolean data) {
		treasureTable[treasureNo] = data;
	}

	private void setEmptyId() {
		if(id < ROW_SIZE) {
			id += ROW_SIZE;
		}	
	}

	public int getId() {
		return this.id;
	}
}
