package arpg.base.magic;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

import javax.imageio.ImageIO;

import arpg.sound.Sound.SoundEffect;

public class Magic {

	private static final int NUMBER_OF_SHEETS = 8;
	private static final int ICON_SIZE = 24;

	public enum MagicGenru {
		ATTACK, RECOVERY, TREATMENT, OTHER
	}
	public enum Element {
		FIRE(1), WATER(0), EARTH(2), WIND(3), NONE(-1);

		private int icon;
		private Element(int icon) {
			this.icon = icon;
		}
		public int getIcon() {
			return icon;
		}
	}

	private static final Magic DEFAULT_MAGIC = new Magic("ダミー", 1, 0, 0, MagicGenru.OTHER, Element.NONE, -1, SoundEffect.BOTTUN);

	private String name;
	private int level;
	private double point;
	private int mp;
	private MagicGenru gengu;
	private Element element;
	private int effect;

	private static BufferedImage magicIcon;
	private SoundEffect sound;

	public Magic(String name, int level, double point, int mp, MagicGenru gengu, Element element, int effect, SoundEffect sound) {

		this.name = name;
		this.level = level;
		this.point = point;
		this.mp = mp;
		this.gengu = gengu;
		this.element = element;
		this.effect = effect;
		this.sound = sound;

		if(magicIcon== null) {
			lordImage();
		}
	}

	private void lordImage() {
		try {
			magicIcon = ImageIO.read(getClass().getResourceAsStream("image/magic_icon.png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void drawIcon(int dx, int dy, Graphics g) {
		int sx = (element.getIcon() % NUMBER_OF_SHEETS) * ICON_SIZE;
		int sy = (element.getIcon() / NUMBER_OF_SHEETS) * ICON_SIZE;
		g.drawImage(magicIcon, dx, dy, dx + ICON_SIZE, dy + ICON_SIZE, sx, sy, sx + ICON_SIZE, sy + ICON_SIZE, null);
	}

	public static Magic getReference(String name) {
		
		Map<String, Magic> map = Map.ofEntries(
			Map.entry("フィアド", new Magic("フィアド", 1, 1.2, 2, MagicGenru.ATTACK, Element.FIRE, 0, SoundEffect.FIRE)),
			Map.entry("アクエル", new Magic("アクエル", 1, 1.2 , 2, MagicGenru.ATTACK, Element.WATER, 2, SoundEffect.WATER)),
			Map.entry("ガイア", new Magic("ガイア", 1, 1.2, 2, MagicGenru.ATTACK, Element.EARTH, 1, SoundEffect.STORN)),
			Map.entry("スラッシュ", new Magic("スラッシュ", 1, 1.2, 2, MagicGenru.ATTACK, Element.WIND, 3, SoundEffect.WIND)),
			Map.entry("キュレ", new Magic("キュレ", 1, 1.0, 3, MagicGenru.RECOVERY, Element.WATER, 0, SoundEffect.HEAL)),
			Map.entry("リドーテ", new Magic("リドーテ", 1, 0, 3, MagicGenru.TREATMENT, Element.EARTH, 0, SoundEffect.STATUS)),
			Map.entry("リパイア", new Magic("リパイア", 1, 0, 3, MagicGenru.TREATMENT, Element.WIND, 0, SoundEffect.STATUS)),	
			Map.entry("リブラ", new Magic("リブラ", 1, 0, 3, MagicGenru.TREATMENT, Element.FIRE, 0, SoundEffect.STATUS)),
			Map.entry("毒", new Magic("毒", 1, 1, 0, MagicGenru.OTHER, Element.NONE, 6, SoundEffect.WIND))
		);
		return map.getOrDefault(name, DEFAULT_MAGIC);
	}

	public static Magic setDefault() {
		return DEFAULT_MAGIC;
	}

	public String getName() {
		return this.name;
	}

	public int getLevel() {
		return this.level;
	}

	public double getPoint() {
		return this.point;
	}

	public int getMp() {
		return this.mp;
	}

	public MagicGenru getGengu() {
		return this.gengu;
	}

	public Element getElement() {
		return this.element;
	}

	public int getEffect() {
		return this.effect;
	}

	public SoundEffect getSoundEffect() {
		return sound;
	}
}
