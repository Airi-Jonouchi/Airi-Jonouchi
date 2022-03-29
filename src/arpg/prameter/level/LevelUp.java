package arpg.prameter.level;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import arpg.personae.Hero;
import arpg.prameter.status.HeroStatus;
import arpg.sound.Sound;
import arpg.sound.Sound.SoundEffect;

import static arpg.main.Common.*;

public class LevelUp {
	
	private BufferedImage image;
	private int expansion;
	private Sound sound;
	private boolean isDisp;
	
	public LevelUp() {
		
		if(image == null) {
			lordImage();
		}
		sound = new Sound();
	}

	private void lordImage() {
		try {
			image = ImageIO.read(getClass().getResourceAsStream("image/levelup.png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void drawImage(Hero hero, int offsetX, int offsetY, Graphics g) {

		if(isDisp) {
			int dx = hero.getPx() - (expansion * 6) + offsetX;
			int dy = hero.getPy() - CHIP_SIZE - (expansion * 3) + offsetY;
			g.drawImage(image, dx, dy, 13 * expansion, 5 * expansion, null);
		}	
	}

	public void update(int level, HeroStatus status) {

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(LevelUp.class.getResourceAsStream("data/levelUp.csv")));) {

			String line;
			boolean flag = false;
			zoomUp();
			sound.soundEffectStart(SoundEffect.LEVEL);
			while(!flag) {
				line = reader.readLine();
				if(line.startsWith(String.valueOf(level))) {
					StringTokenizer st = new StringTokenizer(line, ",");
					st.nextToken();
					int maxHp = Integer.parseInt(st.nextToken());
					int maxMp = Integer.parseInt(st.nextToken());
					int str = Integer.parseInt(st.nextToken());
					int agi = Integer.parseInt(st.nextToken());
					int vit = Integer.parseInt(st.nextToken());
					int mgi = Integer.parseInt(st.nextToken());
					int res = Integer.parseInt(st.nextToken());

					status.setMaxHp(status.getMaxHp() + maxHp);
					status.setMaxMp(status.getMaxMp() + maxMp);
					status.setStr(status.getStr() + str);
					status.setAgi(status.getAgi() + agi);
					status.setVit(status.getVit() + vit);
					status.setMgi(status.getMgi() + mgi);
					status.setRes(status.getRes() + res);
					status.setAtk(status.getAtk() + str);
					status.setDef(status.getDef() + vit);
					flag = true;
				}
				else {
					continue;
				}
			}
		}
		catch (IOException  e) {
			throw new UncheckedIOException(e);
		}
	}

	private void zoomUp() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		isDisp = true;
		scheduler.scheduleAtFixedRate(() -> {
			if(expansion < 10) {
				expansion++;
			}
			if(expansion == 10) {
				scheduler.shutdown();
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				}
				isDisp = false;
			}
		}, 0, 30, TimeUnit.MILLISECONDS);
	}
}
