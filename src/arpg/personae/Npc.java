package arpg.personae;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.imageio.ImageIO;

import arpg.base.map.GameMap;
import arpg.base.message.Talk;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;

import static arpg.main.Common.*;
import static arpg.main.MainPanel.*;



public class Npc extends AbstractCharacter {

	private static final int MATERIAL_SHITE_SIZE = 4;
	private List<Talk> texts;
	protected static BufferedImage image;
	
	public Npc(String name, int x, int y, int id, Direction direction, MoveType type, int limit, GameMap map) {
		super(name, x, y, id, direction, type, limit, map);
		texts = new ArrayList<>();
		if(image == null) {
			lordImage();
		}
		
	}

	@Override
	protected void lordImage() {
		try {
			image = ImageIO.read(getClass().getResourceAsStream("image/chara/character.png"));
		} catch (IOException e) {	
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void drawCharacter(int offsetX, int offsetY, Graphics g) {
		int cx = (id % MATERIAL_SHITE_SIZE) * (CHIP_SIZE * 3) + posture.getId() * CHIP_SIZE;
		int cy = (id / MATERIAL_SHITE_SIZE) * (CHIP_SIZE * 4) + direction.getId() * CHIP_SIZE;
		int dx = px + offsetX;
		int dy = py + offsetY;

		if(isMotion) {
			Graphics2D g2 = (Graphics2D)g;
			AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency);
			g2.setComposite(alphaComposite);
			g2.drawImage(image, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, cx, cy, cx + CHIP_SIZE, cy + CHIP_SIZE, null);
			g2.setComposite(NOMAL);
		}
		else {
			if(isGhost) {	
				drawGhost(dx, dy, cx, cy, g);
			}
			else {
				g.drawImage(image, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, cx, cy, cx + CHIP_SIZE, cy + CHIP_SIZE, null);
			}
		}
	}

	private void drawGhost(int dx, int dy, int cx, int cy, Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setComposite(GHOST);
		g2.drawImage(image, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, cx, cy, cx + CHIP_SIZE, cy + CHIP_SIZE, null);
		g2.setComposite(NOMAL);
	}

	@Override
	public void drawAction(int offsetX, int offsetY, Graphics g) {}

	public void setTalk(int flag, String line) {
	
		Talk talk = new Talk(flag, line);
		texts.add(talk);	
	}

	public Talk getTalk() {
		ListIterator<Talk> it = texts.listIterator(texts.size());
		while(it.hasPrevious()) {
			Talk talk = it.previous();
			if(talk.getFlag() <= gameFlag) {
				return talk;
			}
		}
		return new Talk(0, "");
	}
}
