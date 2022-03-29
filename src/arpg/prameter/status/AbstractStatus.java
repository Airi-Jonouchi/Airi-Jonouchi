package arpg.prameter.status;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import arpg.base.magic.Magic;
import arpg.base.map.GameMap;
import arpg.prameter.Condition;
import arpg.sound.Sound;

import static arpg.main.Common.*;

public abstract class AbstractStatus {

	protected enum CommandEffect {
		HEAL(0, 13), CURE_POIZON(1, 15), CURE_PARALYSIS(2, 15), CURE_BLIND(3, 8), NONE(-1, -1);

		protected int id;
		protected int tatalEffect;
		private CommandEffect(int id, int tatalEffect) {
			this.id = id;
			this.tatalEffect = tatalEffect;
		}
	}
	
	protected static final int FONT_SIZE = 16;
	protected static final Font STANDARD = new Font("Impact", Font.ITALIC, FONT_SIZE);
	protected static final Color DAMAGE_COLOR = Color.RED;
	protected static final Color HEAL_COLOR = Color.CYAN;
	
	protected String name;
	protected int level;
	protected int hp;
	protected int mp;
	protected int str;
	protected int agi;
	protected int vit;
	protected int mgi;
	protected int res;
	protected int atk;
	protected int def;
	protected int maxHp;
	protected int maxMp;
	protected GameMap map;
	protected Condition condition;
	protected int motion;
	protected boolean complete;
	protected Sound sound;
	protected String battalMessage;

	protected CommandEffect commandEffect;
	protected int magicMotion;
	protected boolean isUse; 
	protected int count;

	protected static BufferedImage image;
	protected static BufferedImage magicEffect;
	protected static BufferedImage face;

	public AbstractStatus(String name, int level, int hp, int mp, int str, int agi, int vit, int mgi, int res, GameMap map) {

		this.name = name;
		this.level = level;
		this.hp = hp;
		this.mp = mp;
		this.str = str;
		this.agi = agi;
		this.vit = vit;
		this.mgi = mgi;
		this.res = res;
		this.map = map;
		this.maxHp = hp;
		this.maxMp = mp;

		atk = str;
		def = vit;
		condition = new Condition();
		sound = new Sound();
		battalMessage = "";

		if(image == null && magicEffect == null && face == null) {
			lordImage();
		}
	}

	public boolean hitting(Rectangle attacker, Rectangle target) {

		if(attacker.intersects(target)) {
			return true;
		}
		return false;
	}

	public void damageEffectSetting() {
		motion = 0;
		complete = false;
		animation();
	}

	private void animation() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(() -> {
			boolean chengeDirection = false;
			try {
				while(!complete) {
					if(!chengeDirection) {
						motion++;
						if(motion == 20) {
							chengeDirection = true;
						}	
					}
					else {
						motion--;
						if(motion == 0) {
							TimeUnit.MILLISECONDS.sleep(100);
							complete = true;
						}
					}
					TimeUnit.MILLISECONDS.sleep(10);
				}	
			}
			catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
			finally {
				service.shutdown();
			}
		});
	}

	public boolean isComplete() {
		return this.complete;
	}

	private void lordImage() {
		try {
			image = ImageIO.read(getClass().getResourceAsStream("image/status.png"));
			magicEffect = ImageIO.read(getClass().getResourceAsStream("image/effect/commandMagic.png"));
			face = ImageIO.read(getClass().getResourceAsStream("image/ceciliFace.png"));
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void drawMagic(int x, int y, int offsetX, int offsetY, Graphics g) {

		if(isUse) {
			int sx = magicMotion * CHIP_SIZE;
			int sy = commandEffect.id * CHIP_SIZE;
			int dx = x * CHIP_SIZE + offsetX;
			int dy = y * CHIP_SIZE + offsetY;
	
			g.drawImage(magicEffect, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, sx, sy, sx + CHIP_SIZE, sy + CHIP_SIZE, null);
		}
	}

	public abstract void drawDamage(int offsetX, int offsetY, Graphics g);
	public abstract boolean attack(int x, int y, Rectangle rect);
	public abstract boolean useAttackMagic(Magic magic, Rectangle rect);

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getHp() {
		return this.hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMp() {
		return this.mp;
	}

	public void setMp(int mp) {
		this.mp = mp;
	}

	public int getStr() {
		return this.str;
	}

	public void setStr(int str) {
		this.str = str;
	}

	public int getAgi() {
		return this.agi;
	}

	public void setAgi(int agi) {
		this.agi = agi;
	}

	public int getVit() {
		return this.vit;
	}

	public void setVit(int vit) {
		this.vit = vit;
	}

	public int getMgi() {
		return this.mgi;
	}

	public void setMgi(int mgi) {
		this.mgi = mgi;
	}

	public int getRes() {
		return this.res;
	}

	public void setRes(int res) {
		this.res = res;
	}

	public int getAtk() {
		return this.atk;
	}

	public void setAtk(int atk) {
		this.atk += atk;
	}

	public int getDef() {
		return this.def;
	}

	public void setDef(int def) {
		this.def += def;
	}

	public int getMaxHp() {
		return this.maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getMaxMp() {
		return this.maxMp;
	}

	public void setMaxMp(int maxMp) {
		this.maxMp = maxMp;
	}

	public GameMap getMap() {
		return this.map;
	}

	public void setMap(GameMap map) {
		this.map = map;
	}

	public Condition getCondition() {
		return this.condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public String getBattalMessage() {
		return this.battalMessage;
	}

	public void BattleMessageReset() {
		this.battalMessage = "";
	}

	protected boolean noAction() {

		if(condition.isParalysis()) {
			if(Math.random() < 0.5) {
				battalMessage = name + "は　麻痺して力が入らない";
				return true;
			}
		}
		return false;
	}

	protected void sealed() {

		if(condition.isMagicSeal()) {
			
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleWithFixedDelay(() -> {
					
				if(count < 30) {
					count++;
				}
				else {
					condition.cureMagicSeal();
					count = 0; 
					service.shutdown();
				
				}	
			}, 0, 1, TimeUnit.SECONDS);
		}
	}
}
