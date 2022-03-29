package arpg.base.event.map;



import java.awt.Graphics;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

import arpg.base.item.Item;
import arpg.personae.Hero;
import static arpg.main.Common.*;

public class Door extends AbstractEvent {

	public enum DoorLock {
		FREE, NORMAL, MAGIC, ELEMENT, EVENT
	}
	private static final int ROW_SIZE = 4;

	private int id;
	private DoorLock doorLock;
	private boolean isOpen;
	private String message;

	public Door(int x, int y, int id, DoorLock doorLock) {
		super(x, y);
		this.id = id;
		this.doorLock = doorLock;
		message = "";

		if(image == null) {
			lordImage();
		}
	}

	@Override
	protected void lordImage() {
		try {
			image = ImageIO.read(getClass().getResourceAsStream("image/door.png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void drawImage(int offsetX, int offsetY, Graphics g) {
		if(!isOpen) {
			int sx = (id % ROW_SIZE) * CHIP_SIZE;
			int sy = (id / ROW_SIZE) * CHIP_SIZE;
			int dx = x * CHIP_SIZE + offsetX;
			int dy = y * CHIP_SIZE + offsetY;
	
			g.drawImage(image, dx, dy, dx + CHIP_SIZE, dy + (CHIP_SIZE * 2), sx, sy, sx + CHIP_SIZE, sy + (CHIP_SIZE * 2), null);	
		}
	}

	public void doorOpen(Hero hero) {
		switch(doorLock) {
			case FREE -> {
				isOpen = true;
				message = "鍵を開けた";	
			}
			case NORMAL -> {
				if(hero.getStatus().containsItem(Item.getReference("銀の鍵"))) {
					isOpen = true;
					message = "鍵を開けた";
				}
				else {
					message = "ドアには鍵がかかっている";
				}
			}
			case MAGIC -> {
				if(hero.getStatus().containsItem(Item.getReference("魔法の鍵"))) {
					isOpen = true;
					message = "鍵を開けた";
				}
				else {
					if(hero.getStatus().containsItem(Item.getReference("銀の鍵"))) {
						message = "どうやら手持ちの鍵では開かないようだ";
					}
					else {
						message = "ドアには鍵がかかっている";
					}
				}
			}
			case ELEMENT -> {
				if(hero.getStatus().containsItem(Item.getReference("精霊の鍵"))) {
					isOpen = true;
					message = "鍵を開けた";
				}
				else {
					if(hero.getStatus().containsItem(Item.getReference("魔法の鍵")) || hero.getStatus().containsItem(Item.getReference("銀の鍵"))) {
						message = "どうやら手持ちの鍵では開かないようだ";
					}
					else {
						message = "ドアには鍵がかかっている";
					}
				}
			}
			default -> {}
		}
	}

	public void doorOpen() {
		if(doorLock == DoorLock.EVENT) {
			isOpen = true;
		}
	}

	public String getMessage() {
		return this.message;
	}

	public boolean isOpen() {
		return isOpen;
	}
}
