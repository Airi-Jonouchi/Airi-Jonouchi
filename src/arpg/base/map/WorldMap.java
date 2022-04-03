package arpg.base.map;

import static arpg.main.Common.*;
import static arpg.main.MainPanel.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import arpg.main.MainPanel;
import arpg.main.Common.FontOption.FontAndColor;
import arpg.personae.HeroCursor;

public class WorldMap {

	public enum WorldIcon {
		SEPO("辺境の町セポ", 1, 5, 21, 5, 15, 38), 
		EMERIV("エメリヴ森林", 21, 5, 19, 6, 0, 0),
		TEHERAN("テヘランの郷",7, 5, 18, 3, 0, 0),
		EEL("鉱山都市イール", 6, 7, 22, 10, 0, 0),
		EEL_MINE("イール鉱山", 24, 8, 20, 12, 0, 0),
		MARAKAS("港街マラカス", 2, 9, 5, 13, 0, 0),
		GRANSYARYO("王都グランシャリオ", 4, 10, 8, 8, 0, 0);

		private String name;
		private int id;
		private int flag;
		private int x;
		private int y;
		private int newX;
		private int newY;
		
		private WorldIcon(String name, int id, int flag, int x, int y, int newX, int newY) {
			this.name = name;
			this.id = id;
			this.flag = flag;
			this.x = x;
			this.y = y;
			this.newX = newX;
			this.newY = newY;
		}

		public String getName() {
			return this.name;
		}

		public int getNewX(String mapName) {
			switch(this) {
				case EMERIV -> {
					switch(mapName) {
						case "テヘランの郷" -> {
							return 0;	
						}
						default -> {
							return this.newX;
						} 
					}
				}
				default -> {
					return this.newX;
				}
			}	
		}

		public int getNewY(String mapName) {
			switch(this) {
				case EMERIV -> {
					switch(mapName) {
						case "テヘランの郷" -> {
							return 0;	
						}
						default -> {
							return this.newY;
						} 
					}
				}
				default -> {
					return this.newX;
				}
			}	
		}
	}

	private static final FontAndColor ICON_INLINE = FontAndColor.ICON;
	private static final FontAndColor ICON_OUTLINE = FontAndColor.ICON_OUT_LINE;

	private static final int ICON_SIZE = 64;
	private static final int D = 0;
	private static final int L = 1;
	private static final int R = 2;
	private static final int U = 3;

	private static BufferedImage iconImage;
	private static BufferedImage worldImage;
	private static BufferedImage cursorImage;

	private MainPanel panel;
	private boolean[] reach;
	private WorldIcon current;
	private int destination;
	private int[] route;
	private int moveCount;
	private HeroCursor heroCursor;
	private boolean input;

	public WorldMap(MainPanel panel) {

		this.panel = panel;
		if(iconImage == null && worldImage == null) {
			lordImage();
			cursorImage = panel.getHero().getHeroImage();
		}
		current = WorldIcon.SEPO;
		reach = new boolean[WorldIcon.values().length];
		reach[0] = true;

		heroCursor = new HeroCursor("", current.x, current.y, 0, Direction.DOWN, MoveType.CURSOR, 0, panel.getCurrentMap());
	}

	private void lordImage() {
		try {
			iconImage = ImageIO.read(getClass().getResourceAsStream("image/world/worldIcon.png"));
			worldImage = ImageIO.read(getClass().getResourceAsStream("image/world/world.png"));
		} catch (IOException e) {	
			throw new UncheckedIOException(e);
		}
	}

	public void drawWorldMap(Graphics g) {

		g.drawImage(worldImage, 0, 0, null);
		Arrays.stream(WorldIcon.values()).forEach(v -> {
			if(v.flag <= gameFlag) {
				drawIcon(v, g);
			}
		});

		IntStream.range(0, reach.length).forEach(v -> {
			if(!reach[v] && WorldIcon.values()[v].flag <= gameFlag) {
				g.drawString("NEW", WorldIcon.values()[v].x * CHIP_SIZE, WorldIcon.values()[v].y * CHIP_SIZE - 10);
			}
		});

		drawCharacter(g);
		if(!input) {
			drawPointer(g);
		}
	}

	private void drawIcon(WorldIcon icon, Graphics g) {

		int sx = (icon.id % 6) * ICON_SIZE;
		int sy = (icon.id / 6) * ICON_SIZE;
		int dx = icon.x * CHIP_SIZE - (CHIP_SIZE / 2);
		int dy = icon.y * CHIP_SIZE - (CHIP_SIZE / 2);
		

		g.setFont(ICON_OUTLINE.getFont());
		g.setColor(ICON_OUTLINE.getColor());
		g.drawImage(iconImage, dx, dy, dx + ICON_SIZE, dy + ICON_SIZE, sx, sy, sx + ICON_SIZE, sy + ICON_SIZE, null);
		g.drawString(icon.name, (dx + (ICON_SIZE / 2)) - (13 * (icon.name.length() / 2)), dy + ICON_SIZE + 15);

		g.setFont(ICON_INLINE.getFont());
		g.setColor(ICON_INLINE.getColor());
		g.drawImage(iconImage, dx, dy, dx + ICON_SIZE, dy + ICON_SIZE, sx, sy, sx + ICON_SIZE, sy + ICON_SIZE, null);
		g.drawString(icon.name, (dx + (ICON_SIZE / 2)) - (13 * (icon.name.length() / 2)), dy + ICON_SIZE + 15);
	}

	private void drawCharacter(Graphics g) {
		
		int cx = heroCursor.getPosture().getId() * CHIP_SIZE;
		int cy = (CHIP_SIZE * 4) + heroCursor.getDirection().getId() * CHIP_SIZE;
		int dx = heroCursor.getPx();
		int dy = heroCursor.getPy();
		g.drawImage(cursorImage, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, cx, cy, cx + CHIP_SIZE, cy + CHIP_SIZE, null);
	}

	private void drawPointer(Graphics g) {

		switch(current) {
			case SEPO -> {
				if(WorldIcon.EMERIV.flag <= gameFlag) {
					g.drawString("◀︎", heroCursor.getPx() - 15, heroCursor.getPy() + (CHIP_SIZE / 2));
				}
			}
			case EMERIV -> {
				if(WorldIcon.SEPO.flag <= gameFlag){
					g.drawString("▶︎", heroCursor.getPx() + CHIP_SIZE + 5, heroCursor.getPy() + (CHIP_SIZE / 2));
				}
				if(WorldIcon.TEHERAN.flag <= gameFlag){
					g.drawString("▲", heroCursor.getPx() + 10, heroCursor.getPy() - 10);
				}
				if(WorldIcon.EEL.flag <= gameFlag) {
					g.drawString("▼", heroCursor.getPx() + 10, heroCursor.getPy() + CHIP_SIZE + 20);
				}
			}

			case TEHERAN -> {
				if(WorldIcon.EMERIV.flag <= gameFlag) {
					g.drawString("▼", heroCursor.getPx() + 10, heroCursor.getPy() + CHIP_SIZE + 20);
				}
			}
			case EEL -> {
				if(WorldIcon.EMERIV.flag <= gameFlag) {
					g.drawString("◀︎", heroCursor.getPx() - 15, heroCursor.getPy() + (CHIP_SIZE / 2));
				}
				if(WorldIcon.EEL_MINE.flag <= gameFlag) {
					g.drawString("▲", heroCursor.getPx() + 10, heroCursor.getPy() - 10);
				}
			}
		}
	}

	public BufferedImage getWorldMapImage() {
		return worldImage;
	}

	private void check() {
		Arrays.stream(WorldIcon.values()).forEach(v -> {
			if(current == v) {
				reach[v.ordinal()] = true;	
			}
		});
	}

	public void move() {
		
		if(input) {
			if(heroCursor.isMoving()) {
				if(heroCursor.move()) {
					moveCount++;
				}
			}
			else {
				if(moveCount >= route.length) {
					heroCursor.setDirection_2(Direction.DOWN);
					current = WorldIcon.values()[destination];
					check();
					input = false;
					moveCount = 0;
					route = null;	
				}
				else {
					if(route != null) {
						heroCursor.setDirection(Direction.values()[route[moveCount]]);
					}
				}
			}
		}
	}

	private int[] moveRoute(int index) {

		int[][] routes = {
			{L, D, L},
			{R, U, R},
			{U, L, U, U},
			{D, D, R, D},
			{D, D, R, D, D, R, R},
			{L, L, U, U, L, U, U}
		};
		return routes[index];
	}

	public void movePointer(KeyCode key) {

		if(input) {
			return;
		}
		if(!heroCursor.isMoving()) {
			if(key == KeyCode.Z) {
				heroCursor.warp(panel, current);
			}

			switch (current) {

				case SEPO -> {

					if(key == KeyCode.LEFT) {
						if(WorldIcon.EMERIV.flag <= gameFlag) {
							route = moveRoute(0);
							destination = WorldIcon.EMERIV.ordinal();
						}
					}
				}
				case EMERIV -> {
					
					if(key == KeyCode.RIGHT) {
						if(WorldIcon.SEPO.flag <= gameFlag) {
							route = moveRoute(1);
							destination = WorldIcon.SEPO.ordinal();
						}
					}

					if(key == KeyCode.UP) {
						if(WorldIcon.TEHERAN.flag <= gameFlag) {
							route = moveRoute(2);
							destination = WorldIcon.TEHERAN.ordinal();
						}
					}
						
					if(key == KeyCode.DOWN) {
						if(WorldIcon.EEL.flag <= gameFlag) {
							route = moveRoute(4);
							destination = WorldIcon.EEL.ordinal();
						}
					}
				}
				case TEHERAN -> {

					if(key == KeyCode.DOWN) {
						if(WorldIcon.EMERIV.flag <= gameFlag) {
							route = moveRoute(3);
							destination = WorldIcon.EMERIV.ordinal();
						}
					}
				}
				case EEL -> {

					if(WorldIcon.EMERIV.flag <= gameFlag) {
						if(key == KeyCode.UP) {
							route = moveRoute(5);
							destination = WorldIcon.EMERIV.ordinal();		
						}
					}
				}
			}
		}
		if(route != null) {
			heroCursor.setDirection(Direction.values()[route[moveCount]]);
			input = true;
		}
	}
}
