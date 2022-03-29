package arpg.personae;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import arpg.base.event.map.AbstractEvent;
import arpg.base.event.map.Door;
import arpg.base.event.map.Drop;
import arpg.base.event.map.MapChenge;
import arpg.base.event.map.Post;
import arpg.base.event.map.Treasure;
import arpg.base.event.map.WorldMapOP;
import arpg.base.item.Item;
import arpg.base.map.GameMap;
import arpg.main.MainPanel;
import arpg.personae.operation.AttackMagicOperation;
import arpg.personae.operation.AttackOperation;
import arpg.prameter.status.HeroStatus;
import arpg.shop.AbstractShop;
import arpg.sound.Sound.SoundEffect;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;
import static arpg.main.MainPanel.*;

public class Hero extends AbstractCharacter {

	private AttackOperation aOperation;
	private AttackMagicOperation mOperation;
	private HeroStatus status;
	private int nextX; 
	private int nextY;
	private String message;
	private static BufferedImage heroImage;
	private static BufferedImage conditionImage;
	private static BufferedImage sealImage;
	private int[] effectNumber;
	private int[] count;
	
	public Hero(String name, int x, int y,  int id, Direction direction, MoveType type, int limit, GameMap map) {
		super(name, x, y, id, direction, type, limit, map);
		setAttackRange();

		aOperation = new AttackOperation(this, wepon, attackRange);
		mOperation = new AttackMagicOperation(this);

		powerGauge = 100;
		nextX = x;
		nextY = y;

		if(status == null) {
			status = new HeroStatus(this.name, 1,15, 50, 7, 5, 2, 5, 1, map);
		}
		
		if(heroImage == null  && conditionImage == null) {
			lordImage();
		}

		effectNumber = new int[]{0, 12, 24, 0};
		count = new int[]{0, 0, 0, 0};
		
	}

	@Override
	protected void lordImage() {
		try {
			heroImage = ImageIO.read(getClass().getResourceAsStream("image/chara/cecili.png"));
			conditionImage = ImageIO.read(getClass().getResourceAsStream("image/effect/magic_2.png"));
			sealImage = ImageIO.read(getClass().getResourceAsStream("image/effect/seal_1.png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void drawCharacter(int offsetX, int offsetY, Graphics g) {
		int cx = posture.getId() * CHIP_SIZE;
		int cy = (id * (CHIP_SIZE * 4)) + direction.getId() * CHIP_SIZE;
		int dx = px + offsetX;
		int dy = py + offsetY;
		
		g.drawImage(heroImage, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, cx, cy, cx + CHIP_SIZE, cy + CHIP_SIZE, null);
		drawCondition(offsetX, offsetY, g);
		
		drawPoint(dx, dy, 1, g);
		status.drawLevelUpRogo(this, offsetX, offsetY, g);
		status.drawFieldDamage(g);
	}

	public void setting() {
		conditionAnimation();
	}

	private void drawCondition(int offsetX, int offsetY, Graphics g) {

		int[] cx = new int[4];
		int[] cy = new int[4];

		int dx = px + offsetX;
		int dy = py - CHIP_SIZE + offsetY;

		if(status.getCondition().isBrightness()) {
			cx[0] = (effectNumber[0] % 6) * CHIP_SIZE;
			cy[0] = (effectNumber[0] / 6) * (CHIP_SIZE);
			g.drawImage(conditionImage, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, cx[0], cy[0], cx[0] + CHIP_SIZE, cy[0] + CHIP_SIZE, null);
		}

		if(status.getCondition().isParalysis()) {
			cx[1] = (effectNumber[1] % 6) * CHIP_SIZE;
			cy[1] = (effectNumber[1] / 6) * (CHIP_SIZE);
			g.drawImage(conditionImage, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, cx[1], cy[1], cx[1] + CHIP_SIZE, cy[1] + CHIP_SIZE, null);
		}

		if(status.getCondition().isPoizon()) {
			cx[2] = (effectNumber[2] % 6) * CHIP_SIZE;
			cy[2] = (effectNumber[2] / 6) * (CHIP_SIZE);
			g.drawImage(conditionImage, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, cx[2], cy[2], cx[2] + CHIP_SIZE, cy[2] + CHIP_SIZE, null);
		}

		if(status.getCondition().isMagicSeal()) {
			cx[3] = (effectNumber[3] % 5) * CHIP_SIZE;
			cy[3] = (effectNumber[3] / 5) * (CHIP_SIZE);
			g.drawImage(sealImage, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, cx[3], cy[3], cx[3] + CHIP_SIZE, cy[3] + CHIP_SIZE, null);
		}
	}

	public void drawPoint(int dx, int dy, int drawLine, Graphics g) {
		g.setColor(FontAndColor.STANDARD.getColor());
		g.setFont(FontAndColor.STANDARD.getFont());
		if(drawLine == 0) {
			drawLine = 1;
		}
		g.drawString("X = " + x + ", Y = " + y, dx, dy - CHIP_SIZE * drawLine);
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

	public boolean noEnemy() {
		for(AbstractCharacter chara : map.getCharaList()) {
			if(chara instanceof Monster) {
				Monster monster = (Monster)chara;
				if(!monster.isDefeated()) {
					int xDifference = Math.abs(monster.getX() - x);
					int yDifference = Math.abs(monster.getY() - y);
					if(xDifference < 3 && yDifference < 3) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void action() {
		if(isMaxGauge()) {
			if(wepon == Wepon.ARROW) {
				execution = true;
				aOperation.setting();
				powerGauge = 0;
			}
			else {
				execution = true;
				setAttackRange();
				aOperation.setRange(attackRange);
				aOperation.setting();
				setBattleMessage(status.getBattalMessage());
				getBattleWindow().vibrationOff();
				windowChenge();
				status.BattleMessageReset();
				powerGauge = 0;
				status.damageEffectSetting();
				execution = false;	
			}
		}
		if(!charging) {
			charge();
		}
	}

	public void magicShotMoving() {
		mOperation.move();
		if(!mOperation.isUsed()) {
			execution = false;
		}
	}

	public void arrowMoving() {
		aOperation.move();
		if(!aOperation.isAction()) {
			execution = false;
		}
	}

	public void shot() {
		if(isMaxGauge()) {
			if(mOperation.setting()) {
				execution = true;
				powerGauge = 0;
			}
		}
		if(!charging) {
			charge();
		}
	}

	public void setNext() {
		
		switch (direction) {
			case UP -> {
				nextX = x;
				nextY = y-1;
			}
			case DOWN -> {
				nextX = x;
				nextY = y+1;
			}
			case RIGHT -> {
				nextX = x+1;
				nextY = y;
			}
			case LEFT -> {
				nextX = x-1;
				nextY = y;
			}
		}
	}

	public String getMessage() {
		message = "";
		
		if(noEnemy()) {
			setNext();
			canTalking();
			if(message.equals("")) {
				serchEvent();
			}
		}
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private void canTalking() {

		AbstractCharacter chara = map.talkDirectionCheck(nextX, nextY);
		if(chara instanceof Npc) {
			Npc npc = (Npc)chara;
		
			if(npc != null) {
				switch (direction) {
					case UP -> npc.setDirection(Direction.DOWN);
					case DOWN -> npc.setDirection(Direction.UP);
					case RIGHT -> npc.setDirection(Direction.LEFT);
					case LEFT -> npc.setDirection(Direction.RIGHT);
				}
				message = npc.getTalk().getLine();
			}
		}
	}

	private void serchEvent() {
		
		AbstractEvent event = map.eventCheck(x, y);
		if(event instanceof Drop) {
			Drop drop = (Drop)event;
			if(!status.isMax()) {
				Item item = drop.getItem();
				status.getItem(item);
				message = name + "は　足元を調べた！\\nなんと　" + item.getName() + "を見つけた！\\n" + name + "は　" + item.getName() + "を手に入れた";
				map.removeEvent(event);
			}
			else {
				message = "持ち物が　いっぱいです";
			}
		}
		else if(event == null) {
			event = map.eventCheck(nextX, nextY);
			if(event instanceof Treasure) {
				Treasure treasure = (Treasure)event;
				if(treasure.isEmpty()) {
					if(!map.getObjectName(nextX, nextY).equals("")) {
						message = name + "は　" + map.getObjectName(nextX, nextY) + "を調べた！\\nしかし　何も見つからなかった";
					}
					else {
						message = name + "は　宝箱を見つけた！\\nしかし　宝箱は空っぽだった";
					}
				}	
				else if(!status.isMax()) {
					Item item = treasure.getting();
					
					String treasureName = map.getObjectName(nextX, nextY);
					if(treasureName.equals("")) {
						treasureName = "宝箱";
					}
					
					if(item.getName().equals("お金")) {
						money += item.getPoint();
						message = name + "は　" + treasureName + "を調べた！\\n" + name + "は　" + item.getPoint() + "Cを手に入れた";
					}
					else {
						status.getItem(item);
						message = name + "は　" + treasureName + "を調べた！\\n" + name + "は　" + item.getName() + "を手に入れた";
					}
				}
				else {
					message = "持ち物が　いっぱいです";
				}
			}
			else if(event instanceof Door) {
				Door door = (Door)event;
				if(!door.isOpen()) {
					door.doorOpen(this);
					message = door.getMessage();
				}
			}
			else if(event instanceof Post) {
				Post post = (Post)event;
				if(map.getObjectName(nextX, nextY).equals("本棚")) {
					message = name + "は　本棚を調べた！\\n" + post.getMessage();
				}
				else {
					message = post.getMessage();
				}
			}
		}
		if(message.equals("")) {
			String object = map.getObjectName(nextX, nextY);
			if(!object.equals("")) {
				if(object.equals("本棚")) {
					message = name + "は　本棚を調べた！\\n特に変わった本はない";
				}
				else {
					message = name + "は　" + object + "を調べた！\\nしかし　何も見つからなかった";
				}
			}
			else {
				message = name + "は　足元を調べた！\\nしかし　何も見つからなかった";
			}
		}
	}

	public boolean warp(MainPanel panel) {

		AbstractEvent event = map.eventCheck(x, y);
		
		if(event instanceof MapChenge) {
			MapChenge chenge = (MapChenge)event;
			sound.soundEffectStart(SoundEffect.STAIRS);
			map.removeCharacter(this);
			if(map.getCrystal() != null) {
				map.getCrystal().close();
			}
			map.init();
			panel.lordMap(chenge.getNewMapId());
			chenge.setLocation(this);
			if(panel.getSoundNumber() != map.getSoundNumber()) {
				panel.setBgm(panel.getSoundNumber());
			}
			
			return true;	
		}
		return false;
	}

	public AbstractShop serchShop() {
		setNext();
		AbstractShop shop = map.shopCheck(nextX, nextY);
		if(shop != null) {
			return shop;
		}
		return null;
	}

	public void worldMapModeOn(MainPanel panel) {

		AbstractEvent event = map.eventCheck(x, y);
		if(event instanceof WorldMapOP) {
			panel.mapModeOn();
		}
	}

	public HeroStatus getStatus() {
		return this.status;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		status.setName(name);
	}

	public Wepon getWepon() {
		return this.wepon;
	}

	public AttackOperation getAttackOperation() {
		return this.aOperation;
	}

	public AttackMagicOperation getAttackMagicOperation() {
		return this.mOperation;
	}

	public BufferedImage getHeroImage() {
		return heroImage;
	}

	public void setId() {
		if(gameFlag == 3) {
			id++;
		}
	}

	@Override
	public void setMap(GameMap map) {
		this.map = map;
		status.setMap(map);
	}

	private void conditionAnimation() {

		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {

			if(status.getCondition().isBrightness()) {
				if(count[0] < 11) {
					effectNumber[0]++;
					count[0]++;
				}
				else {
					effectNumber[0] -= 11;
					count[0] = 0;
				}	
			}
			if(status.getCondition().isParalysis()) {
				if(count[1] < 11) {
					effectNumber[1]++;
					count[1]++;
				}
				else {
					effectNumber[1] -= 11;
					count[1] = 0;
				}		
			}
			if(status.getCondition().isPoizon()) {
				if(count[2] < 11) {
					effectNumber[2]++;
					count[2]++;
				}
				else {
					effectNumber[2] -= 11;
					count[2] = 0;
				}	
			}
			if(status.getCondition().isMagicSeal()) {
				if(count[3] < 11) {
					effectNumber[3]++;
					count[3]++;
				}
				else {
					effectNumber[3] -= 11;
					count[3] = 0;
				}	
			}
			
			if(status.getCondition().isNomal()) {
				scheduler.shutdown();
			}
		}, 0, 30, TimeUnit.MILLISECONDS);
	}
}
