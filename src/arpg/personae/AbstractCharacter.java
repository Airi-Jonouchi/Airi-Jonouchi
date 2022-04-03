package arpg.personae;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import arpg.base.map.GameMap;
import arpg.base.message.MessageWindow;
import arpg.main.MainPanel;
import arpg.sound.Sound;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;

public abstract class AbstractCharacter {
	
	protected static final int SPEED = 4;

	protected GameMap map;
	protected String name;
	protected int id;
	protected int x;
	protected int y;
	protected int px;
	protected int py;
	protected Rectangle area;
	protected Rectangle attackRange;
	protected boolean moving;
	protected int distance;
	protected Direction direction;
	protected Direction defaultDirection;
	protected MoveType type;
	protected Posture posture; 
	protected boolean canChenge;
	protected Wepon wepon;
	protected boolean isDefeated;
	protected boolean execution;
	protected int powerGauge;
	protected int limit;
	protected boolean charging;
	protected int defaultX;
	protected int defaultY;
	protected boolean isGhost;
	protected boolean isMotion;
	protected float transparency;
	protected Sound sound;

	private String battleMessage;
	private MessageWindow battleWindow;
	
	public AbstractCharacter(String name, int x, int y, int id, Direction direction, MoveType type, int limit, GameMap map) {

		this.name = name;

		this.x = x;
		this.y = y;
		this.px = x * CHIP_SIZE;
		this.py = y * CHIP_SIZE;
		this.id = id;
		this.direction = direction;
		this.defaultDirection = direction;
		this.posture = Posture.DEFAULT;
		this.type = type;
		this.map = map;
		this.defaultX = x;
		this.defaultY = y;
		this.limit = limit;

		wepon = Wepon.SWORD;
		setBattleMessage("");
		setBattleWindow(new MessageWindow());

		area = new Rectangle(px, py, CHIP_SIZE, CHIP_SIZE);
		attackRange = new Rectangle(px, py, CHIP_SIZE, CHIP_SIZE);
		transparency = 1.0f;
		
		sound = new Sound();
		animation();
	}

	public MessageWindow getBattleWindow() {
		return battleWindow;
	}

	public void setBattleWindow(MessageWindow battleWindow) {
		this.battleWindow = battleWindow;
	}

	public void setBattleMessage(String battleMessage) {
		this.battleMessage = battleMessage;
	}

	protected abstract void lordImage();
	public abstract void drawCharacter(int offsetX, int offsetY, Graphics g);
	public abstract void drawAction(int offsetX, int offsetY, Graphics g);

	public void drawBattalMessage(Graphics g) {
		getBattleWindow().drawMessage(FontAndColor.STANDARD, g);
	}

	public void setDirection(Direction direction) {
		distance = 0;
		moving = true;
		this.direction = direction;	
	}

	public void setDirection_2(Direction direction) {
		this.direction = direction;
	}

	public boolean randomDirection() {

		if(type == MoveType.ACTIVE || type == MoveType.BOSS) {
			double probability = Math.random();
			if(probability < 0.02) {
				int select = (int)(Math.random() * 4);
				setDirection(Direction.values()[select]);
				return true;
			}
		}
		return false;
	}

	public boolean move() {

		if(type == MoveType.STAY) {
			return false;
		}
		if(moveProcess()) {
			return true;
		}
		return false;
	}

	protected boolean moveProcess() {

		if(map.moveCheck(x, y, direction, type)) {

			switch(direction) {

				case UP -> {

					py -= SPEED;
					if(py < 0) {
						py = 0;
					}
					distance += SPEED;
					if(distance >= CHIP_SIZE) {
						y--;
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
			moving = false;	
		}
		return false;	
	}

	public void moveCansel() {
		px = x * CHIP_SIZE;
		py = y * CHIP_SIZE;
		area.setLocation(px, py);
		moving = false;	
	}

	protected void setAttackRange() {

		switch(wepon) {
			case SWORD, AXE, NEIL -> {
				switch (direction) {
					case UP -> attackRange.setBounds(px, py - CHIP_SIZE, CHIP_SIZE, CHIP_SIZE);
					case DOWN -> attackRange.setBounds(px, py + CHIP_SIZE, CHIP_SIZE, CHIP_SIZE);
					case RIGHT -> attackRange.setBounds(px + CHIP_SIZE, py, CHIP_SIZE, CHIP_SIZE);
					case LEFT -> attackRange.setBounds(px - CHIP_SIZE, py, CHIP_SIZE, CHIP_SIZE);
				}
			}
			case SPEAR -> {
				switch (direction) {
					case UP -> attackRange.setBounds(px, py - CHIP_SIZE * 2, CHIP_SIZE, CHIP_SIZE * 2);
					case DOWN -> attackRange.setBounds(px, py + CHIP_SIZE, CHIP_SIZE, CHIP_SIZE * 2);
					case RIGHT -> attackRange.setBounds(px + CHIP_SIZE, py, CHIP_SIZE * 2, CHIP_SIZE);
					case LEFT -> attackRange.setBounds(px - CHIP_SIZE * 2, py, CHIP_SIZE * 2, CHIP_SIZE);
				}
			}
			default -> {}
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GameMap getMap() {
		return this.map;
	}

	public void setMap(GameMap map) {
		this.map = map;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
		px = x * CHIP_SIZE;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
		py = y * CHIP_SIZE;
	}

	public int getPx() {
		return this.px;
	}

	public void setPx(int px) {
		this.px = px;
	}

	public int getPy() {
		return this.py;
	}

	public void setPy(int py) {
		this.py = py;
	}

	public int getDefaultX() {
		return this.defaultX;
	}

	public int getDefaultY() {
		return this.defaultY;
	}

	public Rectangle getArea() {
		return this.area;
	}

	public void setArea(Rectangle area) {
		this.area = area;
	}

	public boolean isMoving() {
		return this.moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public int getDistance() {
		return this.distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public Direction getDefaultDirection() {
		return this.defaultDirection;
	}

	public Posture getPosture() {
		return this.posture;
	}

	public void setPosture(Posture posture) {
		this.posture = posture;
	}

	public MoveType getType() {
		return this.type;
	}

	public void setType(MoveType type) {
		this.type = type;
	}

	public String getBattleMessage() {
		return this.battleMessage;
	}

	public void setWepon(Wepon wepon) {
		this.wepon = wepon;
	}

	public boolean isDefeated() {
		return this.isDefeated;
	}

	public void defeated() {
		this.isDefeated = true;
	}

	public void revival() {
		this.isDefeated = false;
	}

	public boolean isExecution() {
		return execution;
	}

	public void setGhost() {
		this.isGhost = true;
	}

	public Sound getSound() {
		return this.sound;
	}

	public int getPowerGauge() {
		return this.powerGauge;
	}

	public void setPowerGauge(int powerGauge) {
		this.powerGauge = powerGauge;
	}

	public void windowChenge() {
		if(!getBattleMessage().equals("")) {
			map.getCharaList().stream().forEach(v -> {
				if(v.battleWindow.isVisible()) {
					v.battleWindow.close();
				}
			});
			getBattleWindow().setting(getBattleMessage());
			getBattleWindow().open();
		}
	}

	private void animation() {

		if(!isDefeated) {
			ScheduledExecutorService step = Executors.newSingleThreadScheduledExecutor();
				step.scheduleAtFixedRate(() -> {
				switch(posture) {
					case BEFORE -> posture = Posture.DEFAULT;
					case DEFAULT -> posture = Posture.AFTER;
					case AFTER -> posture = Posture.BEFORE;
					case WEAPON -> {}
				}
				if(!MainPanel.isGameSwitch()) {
					step.shutdown();
				}
			}, 0, 200, TimeUnit.MILLISECONDS);
		}
	}

	public boolean isMaxGauge() {
		if(powerGauge < limit) {
			return false;
		}
		else {
			return true;
		}
	}

	public boolean elimination() {
		if(transparency == 0.1f) {
			return true;
		}
		return false;
	}

	public void charge() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		charging = true;
		service.scheduleAtFixedRate(() -> {
			if(powerGauge < limit) {
				powerGauge++;
			}
			if(powerGauge == limit) {
				service.shutdown();
				getBattleWindow().close();
				charging = false;
			}
		}, 0, 30, TimeUnit.MILLISECONDS);
	}

	public void setDieAnimation() {
		isMotion = true;
		dieAnimation();
	}

	private void dieAnimation() {

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(() -> {
			if(transparency > 0.1f) {
				transparency -= 0.01f;
			}
			else {
				transparency = 0.1f;
				setX(OUT_FRAME_X);
				setY(OUT_FRAME_Y);
				service.shutdown();
				isMotion = false;
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
	}
}
