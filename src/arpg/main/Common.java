package arpg.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.AlphaComposite;

import java.io.IOException;

import arpg.sound.Sound.SoundEffect;

public class Common {
	
	public static final int PANEL_HEIGHT = 600;
	public static final int PANEL_WIDTH = 800;

	public static final int BOSS_SIZE = 64;
	public static final int CHIP_SIZE = 32;
	public static final int ICON_SIZE = 24;

	public static final int MAX_ITEM_IN_BAG = 14;
	public static final int TOTAL_TREASURE = 50;

	public static final int BRIGHT = 0;
	public static final int DARK = 1;
	public static final int ORIGIN = 4;

	public static final int ATK = -1;
	public static final int PZ = 8;
	public static final int PR = 9;
	public static final int MS = 10;
	public static final int BN = 11;

	public static final int OUT_FRAME_X = -100;
	public static final int OUT_FRAME_Y = -100;

	public static final AlphaComposite GHOST = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	public static final AlphaComposite NOMAL = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

	private Common() {}

	public enum ColorAndStroke {

		WINDOW(new Color(0, 0, 0, 180), Color.WHITE, new BasicStroke(3), 5);

		private Color window;
		private Color frame;
		private BasicStroke stroke;
		private int arc;
		private ColorAndStroke(Color window, Color frame, BasicStroke storke, int arc) {
			this.window = window;
			this.frame = frame;
			this.stroke = storke;
			this.arc = arc;
		}

		public Color getWindowColor() {
			return this.window;
		}

		public Color getFrameColor() {
			return this.frame;
		} 

		public BasicStroke getStroke() {
			return this.stroke;
		}

		public int getArc() {
			return this.arc;
		}
	}

	public enum LightAndDark {

		STANDARD(Color.WHITE), DRAK(new Color(169, 169, 169, 100));

		private Color color;
		private LightAndDark(Color color) {
			this.color = color;
		}

		public Color getColor() {
			return this.color;
		}
	}

	public static final class FontOption {

		public static final int FONT_WIDTH = 20;
		public static final int FONT_HEIGHT = 30;
		public static final int RUBY_WIDTH = 6;
		public static final int RUBY_HEIGHT = 18;

		private static final Font IN_LINE_FONT = createGameFont("../base/font/rounded-mplus-1c-regular.ttf", "rounded-mplus-1c-regular", Font.BOLD, FONT_WIDTH);
		private static final Font OUT_LINE_FONT = createGameFont("../base/font/rounded-mplus-1c-regular.ttf", "rounded-mplus-1c-regular", Font.BOLD, FONT_WIDTH + 1);
		private static final Font RUBY_IN_LINE_FONT = createGameFont("../base/font/rounded-mplus-1c-regular.ttf", "rounded-mplus-1c-regular", Font.BOLD, RUBY_WIDTH);
		private static final Font RUBY_OUT_LINE_FONT = createGameFont("../base/font/rounded-mplus-1c-heavy.ttf", "rounded-mplus-1c-heavy", Font.BOLD, RUBY_WIDTH + 1);
		private static final Font ICON_IN_LINE_FONT = createGameFont("../base/font/rounded-mplus-1c-regular.ttf", "rounded-mplus-1c-regular", Font.BOLD, 12);
		private static final Font ICON_OUT_LINE_FONT = createGameFont("../base/font/rounded-mplus-1c-regular.ttf", "rounded-mplus-1c-regular", Font.BOLD, 14);

		private FontOption(){}

		private static Font createGameFont(String path, String fontName, int line, int size) {

			try {
				Font font = Font.createFont(Font.TRUETYPE_FONT, Common.class.getResourceAsStream(path));
				GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
				font = new Font(fontName, line, size);
				return font;
			} catch (FontFormatException | IOException e) {
				throw new IllegalStateException(e);
			}
		}

		public enum FontAndColor {

			STANDARD(Color.WHITE, IN_LINE_FONT),
			DARK(new Color(80, 30, 0), IN_LINE_FONT),
			OUT_LINE(new Color(0, 0, 0, 150), OUT_LINE_FONT),
			RUBY(Color.WHITE, RUBY_IN_LINE_FONT),
			RUBY_DARK(new Color(80, 30, 0), RUBY_IN_LINE_FONT),
			RUBY_OUT_LINE(new Color(0, 0, 0, 150), RUBY_OUT_LINE_FONT),
			ICON(Color.WHITE, ICON_IN_LINE_FONT),
			ICON_OUT_LINE(new Color(0, 0, 0, 150), ICON_OUT_LINE_FONT);
		
	
			private Color color;
			private Font font;
			private FontAndColor(Color color, Font font) {
				this.color = color;
				this.font = font;
			}
	
			public Color getColor() {
				return this.color;
			}
	
			public Font getFont() {
				return this.font;
			}
		}
	}

	/*public enum MapDataPath {

		OPENING_A("オープニングA", "../base/map/data/opening/opening1.json", "../event/map/data/opening/opening1.dat", "../message/data/opening/opening1.csv", "DUNGEON", 0),
		OPENING_B("オープニングB", "../base/map/data/opening/opening2.json", "../event/map/data/opening/opening2.dat", "../message/data/opening/opening2.csv", "DUNGEON", 1),
		SEPO("辺境の町セポ","../base/map/data/sepo/sepo.json", "../event/map/data/sepo/sepo.dat", "../message/data/sepo/sepo.csv", "CITY", 2),
		SEPO_B1("辺境の町セポB1","../base/map/data/sepo/sepo_b1.json", "../event/map/data/sepo/sepob1.dat", "../message/data/sepo/sepob1.csv", "CITY", 3),
		SEPO_DJ_B1("辺境の町セポ道場B1","../base/map/data/sepo/sepo_djb1.json", "../event/map/data/sepo/sepodjb1.dat", "../message/data/sepo/sepodjb1.csv", "CITY", 4),
		SEPO_INN_F2("辺境の町セポ宿屋2F","../base/map/data/sepo/sepo_inn2f.json", "../event/map/data/sepo/sepoinnf2.dat", "../message/data/sepo/sepoinnf2.csv", "CITY", 5),
		EMERIV("エメリヴ森林", "../base/map/data/emriv/meriv.json", "../event/map/data/emeriv/emeriv.dat", "../message/data/emriv/emeriv.csv", "DUNGEON", 6),
		EMERIV_2("エメリヴ森林2", "../base/map/data/emriv/meriv_2.json", "../event/map/data/emeriv/emeriv_2.dat", "../message/data/emriv/emeriv_2.csv", "DUNGEON", 7),
		EMERIV_3("エメリヴ森林3", "../base/map/data/emriv/meriv_3.json", "../event/map/data/emeriv/emeriv_3.dat", "../message/data/emriv/emeriv_3.csv", "DUNGEON", 8),
		EMERIV_4("エメリヴ森林4", "../base/map/data/emriv/meriv_4.json", "../event/map/data/emeriv/emeriv_4.dat", "../message/data/emriv/emeriv_4.csv", "DUNGEON", 9),
		EMERIV_5("エメリヴ森林5", "../base/map/data/emriv/meriv_5.json", "../event/map/data/emeriv/emeriv_5.dat", "../message/data/emriv/emeriv_5.csv", "DUNGEON", 10),
		EMERIV_B1("エメリヴ森林B1", "../base/map/data/emriv/meriv_b1.json", "../event/map/data/emeriv/emeriv_b1.dat", "../message/data/emriv/emeriv_b1.csv", "DUNGEON", 11),
		EMERIV_IDO("エメリヴ森林井戸", "../base/map/data/emriv/meriv_ido.json", "../event/map/data/emeriv/emeriv_ido.dat", "../message/data/emriv/emeriv_ido.csv", "DUNGEON", 12);
		//TEST("開発島","mapData/Test.json", "eventData/Test.dat", "messageData/Test.txt", 0),
		//TEST2("試験場","mapData/Test2.json", "eventData/Test2.dat", "messageData/Test2.txt", 1);

		private String name;
		private String imagePath;
		private String eventPath;
		private String messagePath;
		private String bgm;
		private int id;

		private MapDataPath(String name, String imagePath, String eventPath, String messagePath, String bgm, int id) {

			this.name = name;
			this.imagePath = imagePath;
			this.eventPath = eventPath;
			this.messagePath = messagePath;
			this.bgm = bgm;
			this.id = id;
		}

		public String getMapName() {
			return this.name;
		}
		public String getImagePath() {
			return this.imagePath;
		}

		public String getEventPath() {
			return this.eventPath;
		}

		public String getMessagePath() {
			return this.messagePath;
		}

		public String getBgm() {
			return  this.bgm;
		}

		public int getId() {
			return this.id;
		}
	}*/

	public enum KeyCode {
		DOWN, LEFT, RIGHT, UP, Z
	}

	public enum Direction {

		DOWN(0), LEFT(1) , RIGHT(2), UP(3);

		private int id;
		private Direction(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}
	}

	public enum MoveType {
		PLAYER, STAY, ACTIVE, EVENT, CURSOR, BOSS
	}

	public enum Posture {

		BEFORE(0), DEFAULT(1), AFTER(2), WEAPON(3);

		private int id;
		private Posture(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}
	}

	public enum Wepon {
		SWORD(0, 0, 5, 1, SoundEffect.SWORD), AXE(5, 1, 6, 1, SoundEffect.AXE), SPEAR(3, 5, 5, 2, SoundEffect.AXE), ARROW(4, -1, 0, 9, SoundEffect.WIND), NEIL(-1, 4, 5, 1, SoundEffect.SWORD), WAND(2, -1, 0, 1, SoundEffect.BOTTUN);

		private int weponCode;
		private int effectCode;
		private int totalEffect;
		private int range;
		private SoundEffect sound;

		private Wepon(int weponCode, int effectCode, int totalEffect, int range, SoundEffect sound) {
			this.weponCode = weponCode;
			this.effectCode = effectCode;
			this.totalEffect = totalEffect;
			this.range = range;
			this.sound = sound;
		}

		public int getWeponCode() {
			return this.weponCode;
		}

		public int getEffectCode() {
			return this.effectCode;
		}

		public int getTotalEffect() {
			return this.totalEffect;
		}

		public int getRange() {
			return this.range;
		}

		public SoundEffect getSoundEffect() {
			return this.sound;
		}
	}

	public enum Command {
		ITEM("アイテム"), EQUIPMENT("装備"), MAGIC("魔法"), STATUS("ステータス"), IMPORTANT("大事なもの"), NO_SELECT("未選択");

		private String title;
		private Command(String title) {
			this.title = title;
		}

		public String getTitle() {
			return this.title;
		}
	}
}
