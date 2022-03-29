package arpg.personae;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;

import arpg.base.map.GameMap;
import arpg.prameter.status.MonsterStatus;

import static arpg.main.Common.*;

public class Boss extends Monster {

	private static final int BOSS_SIZE = 64;
	private static BufferedImage image;
	private Point[] bossPosition;

	public Boss(String name, int x, int y, int territory, int id, Direction direction, MoveType type, int limit, MonsterStatus status, Wepon wepon, GameMap map) {
		super(name, x, y, territory, id, direction, type, limit, status, wepon, map);
		bossPosition = new Point[]{new Point(x, y), new Point(x + 1, y), new Point(x, y + 1), new Point(x + 1, y + 1)};

		area = new Rectangle(px, py, BOSS_SIZE, BOSS_SIZE);
		if(image == null) {
			lordImage();
		}
	}

	@Override
	protected void lordImage() {
		try {
			image = ImageIO.read(getClass().getResourceAsStream("image/chara/boss.png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void drawCharacter(int offsetX, int offsetY, Graphics g) {
		if(!isDefeated) {
			int sx = posture.getId() * BOSS_SIZE;
			int sy = id * (BOSS_SIZE * 4) + direction.getId() * BOSS_SIZE;
			int dx = px + offsetX;
			int dy = py + offsetY;

			g.drawImage(image, dx, dy, dx + BOSS_SIZE, dy + BOSS_SIZE, sx, sy, sx + BOSS_SIZE, sy + BOSS_SIZE, null);
		}
	}
	
	private boolean bossMoveCheck() {
		switch(direction) {
			case UP -> {
				if(map.moveCheck(bossPosition[0].x, bossPosition[0].y, direction, type) && 
					map.moveCheck(bossPosition[1].x, bossPosition[1].y, direction, type)) {
					return true;
				}
			}
			case DOWN -> {
				if(map.moveCheck(bossPosition[2].x, bossPosition[2].y, direction, type) && 
					map.moveCheck(bossPosition[3].x, bossPosition[3].y, direction, type)) {
					return true;
				}
			}
			case RIGHT -> {
				if(map.moveCheck(bossPosition[1].x, bossPosition[1].y, direction, type) && 
					map.moveCheck(bossPosition[3].x, bossPosition[3].y, direction, type)) {
					return true;
				}
			}
			case LEFT -> {
				if(map.moveCheck(bossPosition[0].x, bossPosition[0].y, direction, type) && 
					map.moveCheck(bossPosition[2].x, bossPosition[2].y, direction, type)) {
					return true;
				}
			}
		}
		return false;
	}

	private void setPosition(int x, int y) {
		bossPosition[0].setLocation(x, y);
		bossPosition[1].setLocation(x + 1, y);
		bossPosition[2].setLocation(x, y + 1);
		bossPosition[3].setLocation(x + 1, y + 1);
	}

	@Override
	protected void setAttackRange() {

		switch (direction) {
			case UP -> attackRange.setBounds(px, py - BOSS_SIZE, BOSS_SIZE, BOSS_SIZE);
			case DOWN -> attackRange.setBounds(px, py + BOSS_SIZE, BOSS_SIZE, BOSS_SIZE);
			case RIGHT -> attackRange.setBounds(px + BOSS_SIZE, py, BOSS_SIZE, BOSS_SIZE);
			case LEFT -> attackRange.setBounds(px - BOSS_SIZE, py, BOSS_SIZE, BOSS_SIZE);
		}
	}

	@Override
	protected boolean moveProcess() {
		
		if(bossMoveCheck()) {

			switch(direction) {

				case UP -> {

					py -= SPEED;
					if(py < 0) {
						py = 0;
					}
					distance += SPEED;
					if(distance >= CHIP_SIZE) {
						y--;
						setPosition(x, y);
						py = y * CHIP_SIZE;
						area.setLocation(px, py);
						moving = false;
						return true;
					}
				}
				case DOWN -> {

					py += SPEED;
					if(py > map.getHeight() - CHIP_SIZE) {
						py = map.getHeight() - CHIP_SIZE;
					}
					distance += SPEED;
					if(distance >= CHIP_SIZE) {
						y++;
						setPosition(x, y);
						py = y * CHIP_SIZE;
						area.setLocation(px, py);
						moving = false;
						return true;
					}
				}
				case RIGHT -> {

					px += SPEED;

					if(px > map.getWidth() - CHIP_SIZE) {
						px = map.getWidth() - CHIP_SIZE;
					}
					distance += SPEED;
					if(distance >= CHIP_SIZE) {
						x++;
						setPosition(x, y);
						px = x * CHIP_SIZE;
						area.setLocation(px, py);
						moving = false;
						return true;
					}
				}
				case LEFT -> {

					px -= SPEED;
					if(px < 0) {
						px = 0;
					}
					distance += SPEED;
					if(distance >= CHIP_SIZE) {
						x--;
						setPosition(x, y);
						px = x * CHIP_SIZE;
						area.setLocation(px, py);
						moving = false;
						return true;
					}
				}
			}
		}
		else {
			px = x * CHIP_SIZE;
			py = y * CHIP_SIZE;
			area.setLocation(px, py);
			setPosition(x, y);
			moving = false;	
		}
		return false;	
	}
}
