package arpg.personae.operation;

import static arpg.main.MainPanel.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;

import java.io.IOException;
import java.io.UncheckedIOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import arpg.personae.AbstractCharacter;
import arpg.personae.Boss;
import arpg.personae.Hero;
import arpg.personae.Monster;
import arpg.sound.Sound;
import arpg.sound.Sound.SoundEffect;

import static arpg.main.Common.*;




public class AttackOperation {
	
	private static final Rectangle OUT_FREAME = new Rectangle(-100, -100, CHIP_SIZE, CHIP_SIZE);
	private static final int DEFAULT_POSITION = 6;
	private static final int NO_SHIFT = 0;

	private AbstractCharacter chara;
	private int px;
	private int py;
	private Rectangle arrow;
	private Direction direction;
	private int motion;
	private int effectMotion;
	private Wepon wepon;
	private int shift;
	private boolean action;
	private boolean isHit;
	private Rectangle range;

	private BufferedImage weponImage;
	private BufferedImage effectImage;
	private BufferedImage magicImage;

	private Sound sound;

	public AttackOperation(AbstractCharacter chara, Wepon wepon, Rectangle range) {
		this.chara = chara;
		this.wepon = wepon;
		this.range = range;

		if(weponImage == null && effectImage == null && magicImage == null) {
			lordImage();
		}
		arrow  = OUT_FREAME;
		sound = new Sound();
	}

	private void lordImage() {
		try {
			weponImage = ImageIO.read(getClass().getResourceAsStream("../image/wepon.png"));
			effectImage = ImageIO.read(getClass().getResourceAsStream("../image/effect/attack.png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void drawWepon(int offsetX, int offsetY, Graphics g) {

		int sx = motion * CHIP_SIZE;
		int sy = (wepon.getWeponCode() * (CHIP_SIZE * 4)) + (direction.getId() * CHIP_SIZE);
		int sx2 = effectMotion * (CHIP_SIZE * 2);
		int sy2 = (wepon.getEffectCode() * (CHIP_SIZE * 8)) + (direction.getId() * (CHIP_SIZE * 2));
		int sx3 = 1 * CHIP_SIZE;
		int sy3 = (wepon.getWeponCode() * (CHIP_SIZE * 4)) + (direction.getId() * CHIP_SIZE);
		int dx = 0; 
		int dy = 0;
		int dx2 = 0;
		int dy2 = 0;

		switch(direction) {

			case UP -> {
				
				if(wepon == Wepon.SPEAR) {
					dx = px + offsetX + 15;
					dy = py - CHIP_SIZE - shift + offsetY;
					dx2 = dx - 23;
					dy2 = dy;
				}
				else if(wepon == Wepon.ARROW) {
					dx = px + offsetX;
					dy = py - CHIP_SIZE + offsetY;
					dx2 = arrow.x + offsetX;
					dy2 = arrow.y - CHIP_SIZE + offsetY;
				}
				else {
					dx = px - 5 + shift + offsetX;
					dy = py - CHIP_SIZE + offsetY;
					if(chara instanceof Boss) {
						dx2 = px - CHIP_SIZE - 14 + offsetX;
						dy2 = dy - (CHIP_SIZE / 2) - 10;
					}
					else {
						dx2 = px - 14 + offsetX;
						dy2 = dy - 10;
					}
				}
			}
			case DOWN -> {

				if(wepon == Wepon.SPEAR) {
					dx = px - 14 + offsetX + 15;
					dy = py + CHIP_SIZE + shift + offsetY;
					dx2 = dx - 23;
					dy2 = dy - CHIP_SIZE;
				}
				else if(wepon == Wepon.ARROW) {
					dx = px + offsetX;
					dy = py + CHIP_SIZE + offsetY;
					dx2 = arrow.x + offsetX;
					dy2 = arrow.y + CHIP_SIZE + offsetY;
				}
				else {
					dx = px + 5 - shift + offsetX;
					dy = py + CHIP_SIZE + offsetY;
					if(chara instanceof Boss) {
						dx2 = px - (CHIP_SIZE / 2)  + offsetX;
						dy2 = dy + 10;
					}
					else {
						dx2 = px - (CHIP_SIZE / 2)  + offsetX;
						dy2 = dy + 10 - CHIP_SIZE;
					}
				}
			}
			case RIGHT -> {

				if(wepon == Wepon.SPEAR) {
					dx = px + CHIP_SIZE + shift +  offsetX;
					dy = py + offsetY;
					dx2 = dx + 15 - CHIP_SIZE;
					dy2 = dy - 11;
				}
				else if(wepon == Wepon.ARROW) {
					dx = px + CHIP_SIZE + offsetX;
					dy = py + offsetY;
					dx2 = arrow.x + CHIP_SIZE + offsetX;
					dy2 = arrow.y + offsetY;
				}
				else {
					dx = px + CHIP_SIZE + offsetX;
					dy = py + shift - 10 + offsetY;
					dx2 = dx - 20;
					dy2 = py - (CHIP_SIZE / 2) + offsetY;
				}	
			}
			case LEFT -> {

				if(wepon == Wepon.SPEAR) {
					dx = px - CHIP_SIZE - shift + offsetX;
					dy = py + offsetY;
					dx2 = dx - 15;
					dy2 = dy - 11;
				}
				else if(wepon == Wepon.ARROW) {
					dx = px - CHIP_SIZE + offsetX;
					dy = py + offsetY;
					dx2 = arrow.x - CHIP_SIZE + offsetX;
					dy2 = arrow.y + offsetY;	
				}
				else {
					dx = px - CHIP_SIZE + offsetX;
					dy = py + shift - 10 + offsetY;
					if(chara instanceof Boss) {
						dx2 = dx - CHIP_SIZE - 10;
						dy2 = py - (CHIP_SIZE / 2) + offsetY;
					}
					else {
						dx2 = dx - 10;
						dy2 = py - (CHIP_SIZE / 2) + offsetY;
					}
				}	
			}
		}
		if(wepon == Wepon.ARROW) {
			g.drawImage(weponImage, dx2, dy2, dx2 + CHIP_SIZE, dy2 + CHIP_SIZE, sx3, sy3, sx3 + CHIP_SIZE, sy3 + CHIP_SIZE, null);
		}
		else {
			if(chara instanceof Boss) {
				g.drawImage(effectImage, dx2, dy2, dx2 + (BOSS_SIZE * 2), dy2 + BOSS_SIZE + (CHIP_SIZE / 2), sx2, sy2, sx2 + (CHIP_SIZE * 2), sy2 + (CHIP_SIZE * 2), null);	
			}
			else {
				g.drawImage(effectImage, dx2, dy2, dx2 + (CHIP_SIZE * 2), dy2 + (CHIP_SIZE * 2), sx2, sy2, sx2 + (CHIP_SIZE * 2), sy2 + (CHIP_SIZE * 2), null);
				if(chara instanceof Hero) {
					g.drawImage(weponImage, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, sx, sy, sx + CHIP_SIZE, sy + CHIP_SIZE, null);
				}
			}	
		}	
	}

	public void setWepon(Wepon wepon) {
		this.wepon = wepon;
	}

	public boolean isAction() {
		return action;
	}

	private void init() {
		px = chara.getPx();
		py = chara.getPy();
		if(wepon == Wepon.ARROW) {
			arrow.setLocation(px, py);
		}
		direction = chara.getDirection();
		motion = 0;
		shift = NO_SHIFT;
	}

	private void delete() {
		if(chara instanceof Hero) {
			Hero hero = (Hero)chara;
			if(hero.getStatus().attack(arrow)) {
				isHit = true;
				arrow = OUT_FREAME;
				hero.getStatus().damageEffectSetting();
				action = false;
				sound.soundEffectStart(Sound.SoundEffect.HIT);
				hero.setBattleMessage(hero.getStatus().getBattalMessage());
				hero.getBattleWindow().vibrationOff();
				hero.windowChenge();
				hero.getStatus().BattleMessageReset();		
			}

			if(arrow.y > hero.getPy() + (PANEL_HEIGHT / 2) || arrow.y < hero.getPy() - (PANEL_HEIGHT / 2) 
				|| arrow.x > hero.getPx() + (PANEL_WIDTH / 2) || arrow.x < hero.getPx() - (PANEL_WIDTH / 2)) {
				arrow = OUT_FREAME;
				action = false;
			}

			if(!hero.getMap().heightCheck(chara.getX(), chara.getY(), arrow.x, arrow.y, direction)) {
				
				arrow = OUT_FREAME;
				action = false;
			}
		}
		else if(chara instanceof Monster){
			Monster monster = (Monster)chara;
			if(monster.getStatus().attack(arrow)) {
				isHit = true;
				arrow = OUT_FREAME;
				monster.getStatus().damageEffectSetting();
				action = false;
				sound.soundEffectStart(Sound.SoundEffect.HIT);
				monster.setBattleMessage(monster.getStatus().getBattalMessage());
				monster.getBattleWindow().vibrationOn();
				monster.windowChenge();
				monster.getStatus().BattleMessageReset();	
			}

			if(arrow.y > monster.getPy() + (PANEL_HEIGHT / 2) || arrow.y < monster.getPy() - (PANEL_HEIGHT / 2) 
				|| arrow.x > monster.getPx() + (PANEL_WIDTH / 2) || arrow.x < monster.getPx() - (PANEL_WIDTH / 2)) {
				arrow = OUT_FREAME;
				action = false;
			}

			if(!monster.getMap().heightCheck(chara.getX(), chara.getY(), arrow.x, arrow.y, direction)) {
			
				arrow = OUT_FREAME;
				action = false;
			}
		}
	}

	public void move() {

		if(wepon == Wepon.ARROW) {
			if(action) {
				switch(direction) {
					case UP -> {
						arrow.y -= 12;
					}
					case DOWN -> {
						arrow.y += 12;
					}
					case RIGHT -> {
						arrow.x += 12;
					}
					case LEFT -> {
						arrow.x -= 12;
					}
				}
				delete();
			}
		}
	}
 
	public void setting() {
	
		init();
		action = true;
		
		if(wepon != Wepon.ARROW) {
			setEffectMotion();
			if(chara instanceof Hero) {
				if(gameFlag > 0) {
					chara.setPosture(Posture.WEAPON);
				}
				Hero hero = (Hero)chara;
				if(hero.getStatus().attack(hero.getX(), hero.getY(), range)) {
					isHit = true;
				}
			}
			else if(chara instanceof Monster) {
				Monster monster = (Monster)chara;
				if(monster.getStatus().attack(monster.getX(), monster.getY(), range)) {
					isHit = true;
				}
			}
			animationEffect();
		}
	}

	public boolean isHit() {
		return this.isHit;
	}

	public void reset() {
		this.isHit = false;
	}

	public Rectangle getRange() {
		
		return this.range;
	}

	public void setRange(Rectangle range) {
		this.range = range;
	}

	private void imageShift() {

		switch(wepon) {
			case SWORD , SPEAR-> {
				if(motion >= 2) {
					shift = CHIP_SIZE;
				}
			}
			case AXE -> {
				if(motion >= 1) {
					shift = CHIP_SIZE;
				}
			}
			default -> {}
		}
	}

	private void setEffectMotion() {
		switch (wepon) {
			case SWORD, AXE, NEIL-> {
				if(direction == Direction.RIGHT || direction == Direction.UP) {
					effectMotion = wepon.getTotalEffect() - 1;
				}
				else {
					effectMotion = 0;
				}
			}
			case SPEAR -> {
				if(direction == Direction.LEFT) {
					effectMotion = wepon.getTotalEffect() - 1;
				}
				else {
					effectMotion = 0;
				}
			}
			default -> {}
		}
	}

	private void effectMove() {
		switch(wepon) {
			case SWORD, AXE, NEIL -> {
				if(direction == Direction.RIGHT || direction == Direction.UP) {
					effectMotion--;
				}
				else {
					effectMotion++;
				}
			}
			case SPEAR -> {
				if(direction == Direction.LEFT) {
					effectMotion--;
				}
				else {
					effectMotion++;
				}
			}
			default -> {}
		}
	}
	

	private void animationEffect() {

		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(() -> {
			int count = 0;
			sound.soundEffectStart(wepon.getSoundEffect());
			try {
				while(count < wepon.getTotalEffect()) {
					effectMove();
					imageShift();

					if(motion < 3) {
						motion++;
					}
					else {
						motion = DEFAULT_POSITION;
					}
					count++;
					TimeUnit.MILLISECONDS.sleep(80);	
				}
				chara.setPosture(Posture.DEFAULT);
				effectMotion = DEFAULT_POSITION;
			}
			catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
			finally {
				service.shutdown();
				if(isHit) {
					sound.soundEffectStart(SoundEffect.HIT);
				}
				action = false;
			}
		});
	}
}
