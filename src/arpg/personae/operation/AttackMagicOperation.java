package arpg.personae.operation;

import static arpg.main.Common.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import arpg.base.magic.Magic;
import arpg.base.magic.Magic.Element;
import arpg.personae.AbstractCharacter;
import arpg.personae.Boss;
import arpg.personae.Hero;
import arpg.personae.Monster;
import arpg.prameter.status.HeroStatus;
import arpg.prameter.status.MonsterStatus;
import arpg.sound.Sound;

import static arpg.main.MainPanel.*;

import java.awt.Rectangle;

public class AttackMagicOperation { 

	private static final Rectangle OUT_FREAME = new Rectangle(-100, -100, CHIP_SIZE, CHIP_SIZE);

	private static final int ELEMENT_X = 105;
	private static final int ELEMENT_Y = 105;
	private static final int ELEMENT_X2 = 205;
	private static final int ELEMENT_Y2 = 205;

	private AbstractCharacter chara;
	private Wepon wand;
	private int px;
	private int py;

	private int motion;
	private Direction direction;
	private int effect;
	private int charge;
	private Rectangle magicBullet;
	private Rectangle wandRange;

	private Magic currentMagic;
	private boolean isUsed;
	private boolean isHit;

	private static BufferedImage effectImage;
	private static BufferedImage weponImage;
	private static BufferedImage chargeImage;

	private Sound sound;

	public AttackMagicOperation(AbstractCharacter chara) {

		this.chara = chara;
		wand = Wepon.WAND;
		magicBullet = OUT_FREAME;
		wandRange = OUT_FREAME;
		init();

		currentMagic = Magic.setDefault();

		if(weponImage == null && effectImage == null) {
			lordImage();
		}
		sound = new Sound();
	}

	private void lordImage() {
		try {
			effectImage = ImageIO.read(getClass().getResourceAsStream("../image/effect/magic.png"));
			weponImage = ImageIO.read(getClass().getResourceAsStream("../image/wepon.png"));
			chargeImage = ImageIO.read(getClass().getResourceAsStream("../image/effect/element.png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void drawMagicShot(int offsetX, int offsetY, Graphics g) {

		if(isUsed && !isHit) {

			int sx = motion * CHIP_SIZE;
			int sy = (wand.getWeponCode() * (CHIP_SIZE * 4)) + (direction.getId() * CHIP_SIZE);
	
			int sx2 = effect * CHIP_SIZE;
			int sy2 = (currentMagic.getEffect() * (CHIP_SIZE * 4)) + (direction.getId() * CHIP_SIZE);
	
			int dx = 0;
			int dy = 0;
			int dx2 = 0;
			int dy2 = 0;
	
			switch(direction) {
	
				case UP -> {
					dx = wandRange.x + offsetX;
					dy = wandRange.y - CHIP_SIZE + offsetY;
					dx2 = magicBullet.x + offsetX;
					dy2 = magicBullet.y - CHIP_SIZE + offsetY;
				}
				case DOWN -> {
					dx = wandRange.x + offsetX;
					dy = wandRange.y + CHIP_SIZE + offsetY;
					dx2 = magicBullet.x + offsetX;
					dy2 = magicBullet.y + CHIP_SIZE + offsetY;
				}
				case RIGHT -> {
					dx = wandRange.x + CHIP_SIZE + offsetX;
					dy = wandRange.y + offsetY;
					dx2 = magicBullet.x + CHIP_SIZE + offsetX;
					dy2 = magicBullet.y + offsetY;
				}
				case LEFT -> {
					dx = wandRange.x - CHIP_SIZE + offsetX;
					dy = wandRange.y + offsetY;
					dx2 = magicBullet.x - CHIP_SIZE + offsetX;
					dy2 = magicBullet.y + offsetY;
				}
			}
			if(charge < 10) {
				int sx3 = charge * ELEMENT_X;
				int sy3 = currentMagic.getEffect() * ELEMENT_Y;
				int dx3 = px - 39 + offsetX;
				int dy3 = py - 52 + offsetY;
				if(chara instanceof Boss) {
					g.drawImage(chargeImage, dx3 - 57, dy3 - 78, dx3 + ELEMENT_X2, dy3 + ELEMENT_Y2, sx3, sy3, sx3 + ELEMENT_X, sy3 + ELEMENT_Y, null);
				}
				else {
					g.drawImage(chargeImage, dx3, dy3, dx3 + ELEMENT_X, dy3 + ELEMENT_Y, sx3, sy3, sx3 + ELEMENT_X, sy3 + ELEMENT_Y, null);
				}
			}
			else {
				if(chara instanceof Boss) {
					g.drawImage(effectImage, dx2, dy2, dx2 + BOSS_SIZE, dy2 + BOSS_SIZE, sx2, sy2, sx2 + CHIP_SIZE, sy2 + CHIP_SIZE, null);
				}
				else {
					g.drawImage(effectImage, dx2, dy2, dx2 + CHIP_SIZE, dy2 + CHIP_SIZE, sx2, sy2, sx2 + CHIP_SIZE, sy2 + CHIP_SIZE, null);
				}
			}
			if(chara instanceof Hero) {
				g.drawImage(weponImage, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, sx, sy, sx + CHIP_SIZE, sy + CHIP_SIZE, null);
			}
		}
	}

	private void init() {
		px = chara.getPx();
		py = chara.getPy();
		wandRange.setLocation(px, py); 
		magicBullet = (Rectangle) wandRange.clone();
		direction = chara.getDirection();	
		motion = 0;
		effect = 0;
		charge = 0;
	}

	private void delete() {
		if(chara instanceof Hero) {
			Hero hero = (Hero)chara;
			if(hero.getStatus().useAttackMagic(currentMagic, magicBullet)) {
				isHit = true;
				magicBullet = OUT_FREAME;
				hero.getStatus().damageEffectSetting();
				isUsed = false;
				sound.soundEffectStart(Sound.SoundEffect.HIT);
				hero.setBattleMessage(hero.getStatus().getBattalMessage());
				hero.getBattleWindow().vibrationOff();
				hero.windowChenge();
				hero.getStatus().BattleMessageReset();		
			}

			if(magicBullet.y > hero.getPy() + (PANEL_HEIGHT / 2) || magicBullet.y < hero.getPy() - (PANEL_HEIGHT / 2) 
				|| magicBullet.x > hero.getPx() + (PANEL_WIDTH / 2) || magicBullet.x < hero.getPx() - (PANEL_WIDTH / 2)) {
				magicBullet = OUT_FREAME;
				isUsed = false;
			}

			if(!hero.getMap().heightCheck(chara.getX(), chara.getY(), magicBullet.x, magicBullet.y, direction)) {
				
				magicBullet = OUT_FREAME;
				isUsed = false;
			}
		}
		else if(chara instanceof Monster){
			Monster monster = (Monster)chara;
			if(monster.getStatus().useAttackMagic(currentMagic, magicBullet)) {
				isHit = true;
				magicBullet = OUT_FREAME;
				monster.getStatus().damageEffectSetting();
				isUsed = false;
				sound.soundEffectStart(Sound.SoundEffect.HIT);
				monster.setBattleMessage(monster.getStatus().getBattalMessage());
				monster.getBattleWindow().vibrationOn();
				monster.windowChenge();
				monster.getStatus().BattleMessageReset();	
			}

			if(magicBullet.y > monster.getPy() + (PANEL_HEIGHT / 2) || magicBullet.y < monster.getPy() - (PANEL_HEIGHT / 2) 
				|| magicBullet.x > monster.getPx() + (PANEL_WIDTH / 2) || magicBullet.x < monster.getPx() - (PANEL_WIDTH / 2)) {
				magicBullet = OUT_FREAME;
				isUsed = false;
			}

			if(!monster.getMap().heightCheck(chara.getX(), chara.getY(), magicBullet.x, magicBullet.y, direction)) {
			
				magicBullet = OUT_FREAME;
				isUsed = false;
			}
		}
	}

	public boolean setting() {
		if(!isUsed && !currentMagic.getName().equals("ダミー")) {
			
			if(chara instanceof Hero) {
				Hero hero = (Hero)chara;
				HeroStatus status = hero.getStatus();
				if(status.getMp() >= currentMagic.getMp()) {

					if(gameFlag > 0) {
						chara.setPosture(Posture.WEAPON);
					}
					init();
					isUsed = true;
					hero.getStatus().setMp(status.getMp() - currentMagic.getMp());
					sound.soundEffectStart(currentMagic.getSoundEffect());
					animationEffect();
					return true;
				}
			}
			if(chara instanceof Monster) {
				Monster monster = (Monster)chara;
				MonsterStatus status = monster.getStatus();
				if(status.getMp() >= currentMagic.getMp()) {
					init();
					isUsed = true;
					monster.getStatus().setMp(status.getMp() - currentMagic.getMp());
					sound.soundEffectStart(currentMagic.getSoundEffect());
					animationEffect();
					return true;
				}
			}
		}
		return false;
	}

	public void setOutFrame() {
		magicBullet = OUT_FREAME;
		isUsed = false;
	}

	public void move() {

		if(isUsed && charge == 10) {
			switch(direction) {
				case UP -> {
					magicBullet.y -= 12;
				}
				case DOWN -> {
					magicBullet.y += 12;
				}
				case RIGHT -> {
					magicBullet.x += 12;
				}
				case LEFT -> {
					magicBullet.x -= 12;
				}
			}
			delete();
		}
	}

	public boolean isUsed() {
		return this.isUsed;
	}

	public boolean isHit() {
		return this.isHit;
	}

	public void reset() {
		this.isHit = false;
	}

	public void setMagic(Magic magic) {
		currentMagic = magic;
	}

	private void animationEffect() {

		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(() -> {

			try{
				while(charge < 10) {
					charge++;
					TimeUnit.MILLISECONDS.sleep(50);
				}
				
				while(isUsed) {

					if(currentMagic.getElement() != Element.WIND) {
						if(effect < 7) {
							effect++;
						}
						else {
							effect = 0;
						}
					}
					else {
						if(effect < 5) {
							effect++;
						}
						else {
							effect = 0;
						}
					}
					if(motion == 0) {
						motion = 1;
					}
					TimeUnit.MILLISECONDS.sleep(20);
				}
				if(chara instanceof Hero) {
					chara.setPosture(Posture.DEFAULT);
				}
			}
			catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
			finally {
				service.isShutdown();
			}
		});
	}
}
