package arpg.personae;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import arpg.base.map.GameMap;
import arpg.main.Common.Direction;
import arpg.main.Common.MoveType;
import arpg.personae.operation.AttackMagicOperation;
import arpg.personae.operation.AttackOperation;
import arpg.prameter.status.MonsterStatus;

import static arpg.main.Common.*;

public class Monster extends AbstractCharacter {
	
	private static final int RAW_SIZE = 2;

	protected int territory;
	protected AttackOperation aOperation;
	protected AttackMagicOperation mOperation;
	protected Hero hero;
	protected MonsterStatus status;
	protected boolean attackMode;
	private static BufferedImage image;

	public Monster(String name, int x, int y, int territory, int id, Direction direction, MoveType type, int limit, MonsterStatus status, Wepon wepon, GameMap map) {
		super(name, x, y, id, direction, type, limit, map);
		this.territory = territory;
		this.status = status;
		
		this.wepon = wepon;
		aOperation = new AttackOperation(this, wepon, attackRange);
		mOperation = new AttackMagicOperation(this);

		if(image == null) {	
			lordImage();
		}
	}

	@Override
	protected void lordImage() {
		try {
			image = ImageIO.read(getClass().getResourceAsStream("image/chara/monster.png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void drawCharacter(int offsetX, int offsetY, Graphics g) {

		if(!isDefeated) {
			int cx = (id % RAW_SIZE) * (CHIP_SIZE * 3) + posture.getId() * CHIP_SIZE;
			int cy = (id / RAW_SIZE) * (CHIP_SIZE * 4) + direction.getId() * CHIP_SIZE;
			int dx = px + offsetX;
			int dy = py + offsetY;
			
			g.drawImage(image, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, cx, cy, cx + CHIP_SIZE, cy + CHIP_SIZE, null); 
			//status.drawName(name, dx, dy, 1, g);
			g.drawString(String.valueOf(attackMode), dx, dy);
			g.drawString(String.valueOf(direction), dx, dy - CHIP_SIZE);
		}
	}

	public void drawAction(int offsetX, int offsetY, Graphics g) {

		if(aOperation.isAction()) {
			aOperation.drawWepon(offsetX, offsetY, g);
		}
		if(aOperation.isHit()) {
			status.drawDamage(offsetX, offsetY, g);	
		}
		if(mOperation.isUsed())	{
			mOperation.drawMagicShot(offsetX, offsetY, g);
		}
		if(mOperation.isHit()) {
			status.drawDamage(offsetX, offsetY, g);
		}
		if(status.isComplete()) {
			aOperation.reset();
			mOperation.reset();
		}
		status.drawMagic(x, y, offsetX, offsetY, g);
	}

	public void action() {
		hero = (Hero) map.getCharaList().get(0);
		if(isDefeated) {
			relocation(hero);
			return;
		}
		if(isMaxGauge()) {
			int action = status.actionSelect(mOperation);
			switch(action) {
				case ATK, PZ, PR, MS, BN -> {
					attack(action, hero);
				}
				case 0, 1, 2, 3 -> {						
					if(withInRange(hero)) {
						shot();
					}
				}
				case 4 -> {
					
				}
			}
		}
		if(!execution) {
			if(!charging) {
				charge();
			}	
			if(!tracking(hero)) {
				if(randomDirection()) {
					attackMode = false;
				}
			}		
		}	
	}

	private void chengeAttackMode() {
		if(!attackMode) {
			if(status.getHp() < status.getMaxHp()) {
				attackMode = true;
			}
			else {
				if(Math.random() < 0.3) {
					attackMode = true;
					powerGauge -= 100;
				}
			}	
		}
	}

	private void attack(int action, Hero hero) {
	
		if(map.search(x, y).equals(map.search(map.pixcelToChip(attackRange.x), map.pixcelToChip(attackRange.y)))) {	
			int xDifference = Math.abs(hero.getX() - x);
			int yDifference = Math.abs(hero.getY() - y);
			if(xDifference <= wepon.getRange() && yDifference <= wepon.getRange() && !attackMode) {
				chengeAttackMode();
			}
			if(attackMode) {
				setAttackRange();
				if(status.hitting(attackRange, hero.getArea())) {
					execution = true;
					aOperation.setting();
					if(action != ATK) {
						status.specialAttack(action);
					}
					setBattleMessage(status.getBattalMessage());
					getBattleWindow().vibrationOn();
					windowChenge();
					status.BattleMessageReset();
					status.damageEffectSetting();
					powerGauge = 0;
					execution = false;
				}
			}	
		}			
	}

	private boolean withInRange(Hero hero) {
		if(map.search(x, y).equals(map.search(hero.getX(), hero.getY()))) {
			int xDifference = Math.abs(hero.getX() - x);
			int yDifference = Math.abs(hero.getY() - y);
			
			if(x == hero.getX() && y < hero.getY()) {
				if(yDifference < 10 && yDifference > 3) {
					direction = Direction.DOWN;
					return true;
				}	
			}
			if(x == hero.getX() && y > hero.getY()) {
				if(yDifference < 10 && yDifference > 3) {
					direction = Direction.UP;
					return true;
				}
			}
			if(y == hero.getY() && x < hero.getX()) {
				if(xDifference < 10 && xDifference > 3) {
					direction = Direction.RIGHT;
					return true;
				}
			}
			if(y == hero.getY() && x > hero.getX()) {
				if(xDifference < 10 && xDifference > 3) {
					direction = Direction.LEFT;
					return true;
				}
			}
		}
		return false;	
	}

	private boolean tracking(Hero hero) {
		if(map.search(x, y).equals(map.search(hero.getX(), hero.getY()))) {
			int xDifference = Math.abs(hero.getX() - x);
			int yDifference = Math.abs(hero.getY() - y);
	
			if(xDifference < territory && yDifference < territory) {
				if(Math.random() < 0.7) {
					for(Entry<Direction, Point> entry : getNextArea().entrySet()) {
						if(Math.abs(hero.getX() - entry.getValue().x) < xDifference || Math.abs(hero.getY() - entry.getValue().y) < yDifference) {
							setDirection(entry.getKey());
							return true;
						}
					}	
				}
			}
		}
		return false;
	}

	public void magicShotMoving() {
		mOperation.move();
		if(!mOperation.isUsed()) {
			execution = false;
		}
	}

	private void shot() {
		chengeAttackMode();
		if(attackMode) {
			if(mOperation.setting()) {
				execution = true;
				powerGauge = 0;
			}
		}
	}

	protected Map<Direction, Point> getNextArea() {
		Map<Direction, Point> map = Map.of(
			Direction.UP,  new Point(x, y - 1), 
			Direction.DOWN, new Point(x, y + 1), 
			Direction.RIGHT, new Point(x + 1, y), 
			Direction.LEFT, new Point(x - 1, y)
		);
		return map;
	}

	protected void relocation(Hero hero) {

		if(isDefeated) {
			int xDifference = Math.abs(hero.getX() - defaultX);
			int yDifference = Math.abs(hero.getY() - defaultY);

			if(xDifference > 15 || yDifference > 15) {
				x = defaultX;
				y = defaultY;
				px = x * CHIP_SIZE;
				py = y * CHIP_SIZE;
				status.init();
				revival();
			}
		}
	}

	public MonsterStatus getStatus() {
		return status;
	} 

	public AttackOperation getEffect() {
		return this.aOperation;
	}

	public AttackOperation getAttackOperation() {
		return this.aOperation;
	}

	public AttackMagicOperation getAttackMagicOperation() {
		return this.mOperation;
	}
}
