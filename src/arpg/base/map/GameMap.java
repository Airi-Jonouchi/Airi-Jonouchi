package arpg.base.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import arpg.base.event.map.AbstractEvent;
import arpg.base.event.map.Door;
import arpg.base.event.map.Drop;
import arpg.base.event.map.MapChengePoint;
import arpg.base.event.map.Post;
import arpg.base.event.map.Treasure;
import arpg.base.event.map.WorldMapOP;
import arpg.base.event.map.Door.DoorLock;
import arpg.base.event.story.ImageStatus;
import arpg.base.event.story.Story.MainEvent;
import arpg.base.item.Item;
import arpg.base.item.Item.ItemGenru;
import arpg.base.map.object.Crystal;
import arpg.main.MainPanel;
import arpg.main.ReadXml;
import arpg.main.Common.MoveType;
import arpg.main.path.MapDataPath;
import arpg.personae.AbstractCharacter;
import arpg.personae.Boss;
import arpg.personae.CharacterSet;
import arpg.personae.Hero;
import arpg.personae.Monster;
import arpg.personae.Npc;
import arpg.prameter.status.MonsterStatus;
import arpg.shop.AbstractShop;
import arpg.shop.Church;
import arpg.shop.Custody;
import arpg.shop.Inn;
import arpg.shop.ItemShop;
import arpg.sound.Sound.Bgm;
import arpg.sound.Sound.SoundEffect;
import arpg.system.DataLord;

import static arpg.main.Common.*;
import static arpg.main.MainPanel.*;


public class GameMap {

	enum ActionStatus {
		START, MIDDLE, END
	}

	private static final String 
		SEA = "Sea", GROUND = "Ground1", OBJECT = "Object", WALL = "Wall", FREE = "Free", NPC_X = "NpcX";
	
	private static final int[] RAW = {16, 3};
	private static final int POWER_SIZE = 96;
	
	private int mx;
	private int my;

	private int width;
	private int height;

	private MapDataPath select;
	private DataLord lord;
	private static BufferedImage mapImage;
	private static BufferedImage telopImage;
	private static BufferedImage powerImage;
	private static BufferedImage backGraoundImage;

	private ReadXml readXml;
	private MainPanel panel;
	private String[] keys;
	
	private List<AbstractCharacter> charaList;
	private List<AbstractEvent> eventList;
	private List<AbstractShop> shopList;
	private List<CharacterSet> newSettigList;
	private List<ImageStatus> drawImagelist;

	private AbstractCharacter eventCharacter;
	private static Map<AbstractCharacter, MoveType> eventParty;
	private int distance;
	private MoveType type;
	private Direction direction;
	private int moveCount;
	private boolean eventTalk;
	private ActionStatus actionStatus;
	private int power;
	private int effect;
	private int id;
	private int counter;

	private Crystal crystal;
	private int transparency;
	private int red;
	private int green;
	private int blue;
	private boolean isGradation;

	static {
		eventParty = new HashMap<>();
		lordImage();
		backGraoundImage = telopImage;
	}

	public GameMap(MapDataPath select, MainPanel panel) {

		this.panel = panel;
		this.select = select;
		readXml = new ReadXml();

		if(lord == null) {
			lord = new DataLord(this.select);
		}
		
		mx = lord.getWidth();
		my = lord.getHeight();
		keys = new String[]{SEA, GROUND, OBJECT, WALL, FREE};

		width = ChipToPixcel(mx);
		height = ChipToPixcel(my);

		charaList = new ArrayList<>();
		eventList = new ArrayList<>();
		shopList = new ArrayList<>();
		newSettigList = new ArrayList<>();
		drawImagelist = new ArrayList<>();

		if(select.getMapName().equals("オープニングB")) {
			crystal = new Crystal(21, 18);
			crystal.addCrystal();
		}
		if(select.getMapName().equals("辺境の町セポ") && gameFlag < 4) {
			setGradation(0, 0, 0);
		}
		
		lordEvent();
		lordMessage();
		power = -1;
	}
	public GameMap() {}

	private static void lordImage() {

		try {
			mapImage = ImageIO.read(GameMap.class.getResourceAsStream("image/mapChip.png"));
			telopImage = ImageIO.read(GameMap.class.getResourceAsStream("image/backGround/telop.png"));
			powerImage = ImageIO.read(GameMap.class.getResourceAsStream("image/effect/power.png"));
		} catch (IOException e) {	
			throw new UncheckedIOException(e);
		}
	}

	public void drawMap(int offsetX, int offsetY, Graphics g) {
		
		int leftSide = pixcelToChip(-offsetX);
		int rightSide = leftSide + pixcelToChip(PANEL_WIDTH) + 2;
		rightSide = Math.min(rightSide, mx);

		int upSide = pixcelToChip(-offsetY);
		int downSide = upSide + pixcelToChip(PANEL_HEIGHT) + 2;
		downSide = Math.min(downSide, my);

		for(String key : keys) {
			drawParts(key, upSide, downSide, leftSide, rightSide, offsetX, offsetY, g);
		}
		eventList.forEach(v -> {
			if(v instanceof Drop) {
				v.drawImage(offsetX, offsetY, g);
			}
		});

		charaList.forEach(v -> {
			if(v.elimination()) {
				v.drawCharacter(offsetX, offsetY, g);
			}
			else {
				v.drawCharacter(offsetX, offsetY, g);
			}
			if(!(v instanceof Npc)) {
				v.drawAction(offsetX, offsetY, g);
			}
		});

		eventList.forEach(v -> {
			if(!(v instanceof MapChengePoint) && !(v instanceof Drop)) {
				v.drawImage(offsetX, offsetY, g);
			}
		});
		
		shopList.forEach(v -> {

			if(v instanceof Church) {
				Church church = (Church)v;
				church.draw(offsetX, offsetY, g);
			}
			else {
				v.draw(g);
			}
		});
		
		drawPower(offsetX, offsetY, g);
		if(crystal != null) {
			crystal.draw(offsetX, offsetY, g);
		}

		if(isGradation) {
			drawEventGraphics(g);
		}
		drawBackGround(g);
	}

	private void drawBackGround(Graphics g) {
		switch(select.getMapName()) {
			case "オープニングA" -> {
				if(gameFlag <= 1) {
					Graphics2D g2 = (Graphics2D)g;
					g.drawImage(backGraoundImage, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null);
					drawImagelist.forEach(v -> {
						v.drawImage(g2);
					});
					g2.setComposite(NOMAL);
				}
			}
		}
	}

	public void drawBattalMessage(Graphics g) {
		charaList.forEach(v -> {
			if(!(v instanceof Npc)) {
				v.drawBattalMessage(g);
			}
		});	
	}

	private void drawParts(String key, int upSide, int downSide, int leftSide, int rightSide, int offsetX, int offsetY, Graphics g) {

		for(int y = upSide; y < downSide; y++) {
			for(int x = leftSide; x < rightSide; x++) {
				int material = lord.getMapData(key)[y][x];
				if(material >= 0) {
					int dx = x * CHIP_SIZE + offsetX;
					int dy = y * CHIP_SIZE + offsetY;
					int sx = (material % RAW[0]) * CHIP_SIZE;
					int sy = (material / RAW[0]) * CHIP_SIZE;
					g.drawImage(mapImage, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, sx, sy, sx + CHIP_SIZE, sy + CHIP_SIZE, null);
				}
			}
		}
	}

	private void drawPower(int offsetX, int offsetY, Graphics g) {
		if(power >= 0) {
			int dx = charaList.get(id).getPx() - 28  + offsetX;
			int dy = charaList.get(id).getPy() - 28  + offsetY;
			int sx = ((power + effect) % 3) * POWER_SIZE;
			int sy = ((power + effect) / 3) * POWER_SIZE;
			g.drawImage(powerImage, dx, dy, dx + POWER_SIZE, dy + POWER_SIZE, sx, sy, sx + POWER_SIZE, sy + POWER_SIZE, null);
		}
	}

	private void drawEventGraphics(Graphics g) {
		g.setColor(new Color(red, green, blue, transparency));
		g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
	}

	public boolean MapCheck(int x, int y, MoveType type) {

		for(AbstractCharacter chara : charaList) {
			if(chara instanceof Boss && !chara.isDefeated() && type != MoveType.BOSS) {
				if(chara.getX() == x && chara.getY() == y ) {
					return true;
				}
				else if(chara.getX() + 1 == x && chara.getY() == y) {
					return true;
				}
				else if(chara.getX() == x && chara.getY() + 1 == y) {
					return true;
				}
				else if(chara.getX() + 1 == x && chara.getY() + 1 == y) {
					return true;
				}	
			}
			if(chara.getX() == x && chara.getY() == y) {
				if(!chara.isDefeated()) {
					return true;
				}
			}
		}

		for(AbstractEvent event : eventList) {
			if(event.getX() == x && event.getY() == y) {
				if(event instanceof Door) {
					Door door = (Door)event;
					if(!door.isOpen()) {
						return true;
					}
				}
				if(event instanceof Treasure) {
					return true;
				}
			}
		}
		return false;
	}

	public void charaMove() {

		charaList.stream().forEach(v -> {

			if(!(v instanceof Hero)) {
				if(v.isMoving()) {
					v.move();
				}
				else {
					if(v instanceof Monster) {
						Monster monster = (Monster)v;
						monster.action();
					}
					else {
						v.randomDirection();
					}
				}
			}	
		});
	}

	public boolean moveCheck(int x, int y, Direction direction, MoveType type) {

		if(type == MoveType.CURSOR) {
			return true;
		}
			
		Point next = switch(direction) {
			case UP -> new Point(x, y - 1);
			case DOWN -> new Point(x, y + 1);
			case RIGHT -> new Point(x + 1, y);
			case LEFT -> new Point(x - 1, y);
		};
		
		String currentLocation = search(x, y);
		String nextLocation = search(next.x, next.y);

		if(MapCheck(next.x,next.y, type)) {
			return false;
		}
		
		switch(nextLocation) {
			case WALL, OBJECT -> {
				return false;
			}
			case SEA -> {
				return false;
			}
			case FREE -> {
				return true;
			}
			case NPC_X -> {
				if(type == MoveType.PLAYER || type == MoveType.EVENT)  {
					return true;
				}
				else {
					return false;
				}
			}
			default -> {
				if(nextLocation.equals(currentLocation)) {
					return true;
				}
				else if(currentLocation.equals(FREE)) {
					return true;
				}
				else if(currentLocation.equals(NPC_X)) {
					return true;
				}
				else if(currentLocation.equals(OBJECT)) {
					return true;
				}
				else {
					return false;
				}
			}
		}
	}

	public String search(int x, int y) {
		String defaultKey = SEA;
		
		for(String key : lord.getFieldKeys()) {
			if(lord.getMapData(key)[y][x] != -1) {
				return key;
			}
		}
		return defaultKey;
	}

	public void addCharacter(AbstractCharacter character) {
		if(character instanceof Hero) {
			charaList.add(0, character);
		}
		else {
			charaList.add(character);
		}
		character.setMap(this);
	}

	public void removeCharacter(Hero hero) {
		charaList.remove(hero);
	}

	public void addEvent(AbstractEvent event) {
		eventList.add(event);
	}

	public void removeEvent(AbstractEvent event) {
		eventList.remove(event);
	}

	public List<AbstractCharacter> getCharaList() {
		return this.charaList;
	}
	public List<AbstractEvent> getEventList() {
		return this.eventList;
	}

	public AbstractCharacter talkDirectionCheck(int x, int y) {

		for(AbstractCharacter chara : charaList) {
			if(chara.getX() == x && chara.getY() == y) {
				return chara;
			}
		}
		return null;
	}

	public Monster monsterCheck(int x, int y) {

		for(AbstractCharacter chara : charaList) {
			if(chara instanceof Monster) {
				Monster monster = (Monster)chara;
				if(monster.getX() == x && monster.getY() == y) {
					return monster;
				}
			}
		}
		return null;
	}

	public AbstractEvent eventCheck(int x, int y) {

		for(AbstractEvent event : eventList) {
			if(event.getX() == x && event.getY() == y) {
				if(event instanceof Door) {
					Door door = (Door)event;
					if(door.isOpen()) {
						continue;
					}
					else {
						return event;
					}
				}
				else {
					return event;
				}
			}
		}
		return null;
	}

	public AbstractShop shopCheck(int x, int y) {

		for(AbstractShop shop: shopList) {
			if(shop.getX() == x && shop.getY() == y) {
				return shop;
			}
		}
		return null;
	}

	public boolean heightCheck(int cx, int cy, int px, int py, Direction direction) {
	
		int x = pixcelToChip(px);
		int y = pixcelToChip(py);

		if(direction == Direction.RIGHT) {
			x += 1;
		}
		else if (direction == Direction.DOWN) {
			y += 1;
		}

		String charaLocation = search(cx, cy);
		int charaHeight = lord.getHeightData(charaLocation);
 
		Point next = switch(direction) {
			case UP -> next = new Point(x, y - 1);	
			case DOWN -> next = new Point(x, y + 1);
			case RIGHT -> next = new Point(x + 1, y);
			case LEFT -> next = new Point(x - 1, y);
		};

		if(next.x == mx || next.y == my) {
			return false;
		}
		if(next.x < 0 || next.y < 0) {
			return false;
		}
		
		if(charaLocation.equals(FREE)) {
			if(direction == Direction.RIGHT || direction == Direction.LEFT) {
				return false;
			}
		}
		
		String nextLocation = search(next.x, next.y);
		int nextHeight = lord.getHeightData(nextLocation);
		

		if(charaHeight >= nextHeight) {
			return true;
		}
		else {	
			return false;
		}
	}

	public void init() {
		charaList.forEach(v -> {
			if(v.isDefeated()) {
				v.revival();
			}
			v.setX(v.getDefaultX());
			v.setY(v.getDefaultY());
			v.setDirection_2(v.getDefaultDirection());
			if(v instanceof Hero) {
				Hero hero = (Hero)v;
				hero.getAttackMagicOperation().setOutFrame();
			}
			else if(v instanceof Monster) {
				Monster monster = (Monster)v;
				monster.getAttackMagicOperation().setOutFrame();
			}
		});
		List<AbstractEvent> deleteList = new ArrayList<>();
		eventList.forEach(v -> {
			if(v instanceof Drop) {
				deleteList.add(v);
			}
		});
		deleteList.forEach(v -> {
			eventList.remove(v);
		});
		resetCharacter();
	}

	public void lordEvent() {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(select.getEventPath())));) {
	
			String line;
			while((line = reader.readLine()) != null) {
				if(line.equals("") || line.startsWith("*")) {
					continue;
				}
				StringTokenizer st = new StringTokenizer(line, ",");
				String category = st.nextToken();

				switch(category) {
					case "MONSTER" -> lordMonster(st);
					case "BOSS" -> lordBoss(st);
					case "NPC" -> lordNpc(st);
					case "TREASURE" -> lordTreasure(st);
					case "DOOR" -> lordDoor(st);
					case "INN" -> lordInn(st);
					case "SHOP" -> lordShop(st);
					case "DEPOSIT" -> lordDepositShop(st);
					case "CHURCH" -> lordChurch(st);
					case "CHENGE" -> lordMapChenge(st);
					case "POST" -> lordPost(st);
					case "SET" -> setPosition(st);
					case "WARP" ->lordWorldMapOP(st);
				}
			}
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void lordMonster(StringTokenizer st) {

		String name = st.nextToken();
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int territory = Integer.parseInt(st.nextToken());
		int id = Integer.parseInt(st.nextToken());
		Direction direction = Direction.valueOf(st.nextToken());
		MoveType type = MoveType.valueOf(st.nextToken());
		int limit = Integer.parseInt(st.nextToken());
		MonsterStatus status = MonsterStatus.getMonsterStatus(name, this);
		Wepon wepon = Wepon.values()[Integer.parseInt(st.nextToken())];

		Monster monster = new Monster(name, x, y, territory, id, direction, type, limit, status, wepon, this);
		charaList.add(monster);
	}

	private void lordBoss(StringTokenizer st) {

		String name = st.nextToken();
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int territory = Integer.parseInt(st.nextToken());
		int id = Integer.parseInt(st.nextToken());
		Direction direction = Direction.valueOf(st.nextToken());
		MoveType type = MoveType.valueOf(st.nextToken());
		int limit = Integer.parseInt(st.nextToken());
		MonsterStatus status = MonsterStatus.getMonsterStatus(name, this);
		Wepon wepon = Wepon.values()[Integer.parseInt(st.nextToken())];

		Boss boss = new Boss(name, x, y, territory, id, direction, type, limit, status, wepon, this);
		charaList.add(boss);
	}

	private void lordNpc(StringTokenizer st) {

		String name = st.nextToken();
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int id = Integer.parseInt(st.nextToken());
		Direction direction = Direction.valueOf(st.nextToken());
		MoveType type = MoveType.valueOf(st.nextToken());
		int limit = 0;

		Npc npc = new Npc(name, x, y, id, direction, type, limit, this);
		if(name.matches("^.*GHOST")) {
			npc.setGhost();
		}
		charaList.add(npc);
		if(actionStatus == ActionStatus.END) {
			synchronized(panel.getMain()) {
				panel.getMain().notifyAll();
			}
			actionStatus = ActionStatus.START;
		}
	}

	private void lordTreasure(StringTokenizer st) {

		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int id = Integer.parseInt(st.nextToken());
		int treasureNo = Integer.parseInt(st.nextToken());
		Item item = Item.getReference(st.nextToken());

		Treasure treasure = new Treasure(x, y, id, treasureNo, item);
		eventList.add(treasure);
	}

	private void lordDoor(StringTokenizer st) {

		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int id = Integer.parseInt(st.nextToken());
		DoorLock lock = DoorLock.valueOf(st.nextToken());

		Door door = new Door(x, y, id, lock);
		eventList.add(door);
	}

	private void lordInn(StringTokenizer st) {

		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int price = Integer.parseInt(st.nextToken());

		Inn inn = new Inn(x, y, this.panel, price, this.getSoundNumber());
		shopList.add(inn);
	}

	private void lordShop(StringTokenizer st) {

		List<Item> list = new ArrayList<>();
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		String shopName = st.nextToken();

		while(st.hasMoreTokens()) {
			Item item = Item.getReference(st.nextToken());
			list.add(item);
		}
		ItemShop itemShop = new ItemShop(x, y, this.panel, shopName, list);
		shopList.add(itemShop);
	}

	private void lordDepositShop(StringTokenizer st) {
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		
		Custody custody= new Custody(x, y, panel);
		shopList.add(custody);
	}

	private void lordChurch(StringTokenizer st) {

		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int price = Integer.parseInt(st.nextToken());

		Church church = new Church(x, y, this.panel, price);
		shopList.add(church);
	}

	private void lordMapChenge(StringTokenizer st) {

		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int newX = Integer.parseInt(st.nextToken());
		int newY = Integer.parseInt(st.nextToken());
		String newMapId = st.nextToken();

		MapChengePoint chenge = new MapChengePoint(x, y, newX, newY, newMapId);
		eventList.add(chenge);
	}

	private void lordPost(StringTokenizer st) {
		
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		String message = st.nextToken();

		Post post = new Post(x, y, message);
		eventList.add(post);
	}

	private void setPosition(StringTokenizer st) {
		
		String name = st.nextToken();
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		MoveType type = MoveType.valueOf(st.nextToken());
		int flag = Integer.parseInt(st.nextToken());
		AbstractCharacter character = null;
		
		for(AbstractCharacter chara : charaList) {
			if(chara.getName().equals(name)) {
				character = chara;
			}
		}

		if(character != null) {
			newSettigList.add(new CharacterSet(name, x, y, type, flag));
		}
	}

	private void lordWorldMapOP(StringTokenizer st) {
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int reverseX = Integer.parseInt(st.nextToken());
		int reverseY = Integer.parseInt(st.nextToken());
		Direction direction = Direction.valueOf(st.nextToken());

		WorldMapOP op = new WorldMapOP(x, y, reverseX, reverseY, direction);
		eventList.add(op);
	}

	private void lordMessage() {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(select.getMessagePath())));) {
			String line;
			
			while((line = reader.readLine())!= null) {

				if(line.equals("") || line.startsWith("\"")) {
					continue;
				}

				StringTokenizer st = new StringTokenizer(line, ",");
				String name = st.nextToken();
				String msgName = "";

				if(name.matches("^.*[A-Z]$")) {
					msgName = name.replaceFirst("[A-Z]$", "");
				}
				else {
					msgName = name;
				}
				int flag = Integer.parseInt(st.nextToken());
				String message = msgName + "「" + st.nextToken();
				
				charaList.forEach(v -> {
					if(v instanceof Npc) {
						if(v.getName().equals(name)) {
							Npc npc = (Npc)v;
							npc.setTalk(flag, message);
						}
					}
				});
			}
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void lordStory(MainEvent event) {
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(() -> {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(event.getPath())));) {
				String line;
				panel.eventStart();
				while((line = reader.readLine()) != null) {
			
					if(line.equals("") || line.startsWith("*")) {
						continue;
					}
					StringTokenizer st = new StringTokenizer(line, ",");
					String category = st.nextToken();
					
					switch (category) {
						case "TALK", "A_TALK" -> {
							if(category.equals("A_TALK")) {
								panel.getWindow().autoModeOn();
							}
							setEventTalk(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}		
						case "MOVE" -> {
							setAutoMoveing(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "ALL_MOVE", "ALL_DIRECTION" -> {
							setAllDirection(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "STEP" -> {
							step(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "DIRECTION" -> {
							setAutoDirection(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "ITEM" -> {
							getItem(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "MUSIC" -> {
							panel.setBgm(Integer.parseInt(st.nextToken()));
						}
						case "SOUND_EFFECT" -> {
							panel.getSound().soundEffectStart(SoundEffect.valueOf(st.nextToken()));
						}
						case "DOOR" -> {
							doorOpen(st);	
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "CHARA_SET" -> {
							characterSet(st);
						}
						case "WARP" -> {
							warp(st);
						}
						case "NAME" -> {
							setName(st);
						}
						case "DIE" -> {
							characterDead(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "VOICE_OF_HEAVEN" -> {
							setHevensVoice(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "COLOR_SET" -> {
							gradually(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "COLOR_OFFSET" -> {
							reversed();
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "POWER" -> {
							powerEffect(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "WAIT" -> {
							stop(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}	
						}
						case "NAME_INPUT" -> {
							panel.nameInputModeStart();
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "MONEY" -> {
							getMoney(st);
							synchronized(panel.getMain()) {
								panel.getMain().wait();
							}
						}
						case "BACKGROUND" -> {
							setBackGround(st);
						}
						case "TELOP_IMAGE_SET" -> {
							setTelopImage(st);
						}
					};
				}
			}
			catch(IOException | InterruptedException e) {
				throw new IllegalStateException(e);
			}
			finally {
				executor.shutdown();
				panel.eventEnd();
				if(eventCharacter != null) {
					eventCharacter = null;
				}
				if(isGradation) {
					transparency = 0;
					isGradation = false;
				}
				type = null;
			}
		});
	}

	private void setEventTalk(StringTokenizer st) {
		
		String name = st.nextToken();
		
		if(name.equals("null")) {
			panel.getWindow().setting(st.nextToken());
			panel.getWindow().open();
			eventTalk= true;
		}
		else {
			if(name.endsWith("^.*[A-Z+]$")) {
				name.replaceFirst("[A-Z+]$", "");
			}
			if(name.equals("#")) {
				name = panel.getHero().getName();
			}
			panel.getWindow().setting(name + "「" + st.nextToken());
			panel.getWindow().open();
			eventTalk= true;
		}
	}

	public void eventNotifyAll() {
		if(!panel.getWindow().isVisible() && !panel.getWindow().isGhost()) {
			if(eventTalk) {
				synchronized(panel.getMain()) {
					panel.getMain().notifyAll();
				}
				eventTalk = false;
			}
		}
		if(eventParty.size() > 0) {
			if(moveCount >= distance) {
				distance = 0;
				moveCount = 0;
				for(Map.Entry<AbstractCharacter, MoveType> entry : eventParty.entrySet()) {
					for(AbstractCharacter chara : charaList) {
						if(chara.getName().equals(entry.getKey().getName())) {
							chara.setType(entry.getValue());
						}
						chara.setMoving(false);
					}
				}
				eventParty.clear();

				synchronized(panel.getMain()) {
					panel.getMain().notifyAll();
				}

			}
		}
		if(panel.getNameInput().isNameInputMode()) {
			if(!panel.getNameInput().getName().equals("")) {
				panel.nameInputModeEnd();
				synchronized(panel.getMain()) {
					panel.getMain().notifyAll();
				}
			}
		}
		
		if(actionStatus == ActionStatus.END) {
			synchronized(panel.getMain()) {
				panel.getMain().notifyAll();
			}
			actionStatus = ActionStatus.START;
		}
		if(eventCharacter != null) {
			if(moveCount > 0) {
				if(eventCharacter instanceof Hero) {
					if(moveCount >= distance) {
						Hero hero = (Hero)eventCharacter;
						distance = 0;
						moveCount = 0;
						synchronized(panel.getMain()) {
							panel.getMain().notifyAll();
						}
						eventCharacter.setType(type);
						eventCharacter = null;
						if(hero.warp(panel, (MapChengePoint)eventCheck(hero.getX(), hero.getY()))) {
							panel.setOffset();
							panel.getStory().lordMainEvent();
						}
					}
				}
				else {
					if(moveCount >= distance) {
						distance = 0;
						moveCount = 0;
						synchronized(panel.getMain()) {
							panel.getMain().notifyAll();
						}
						eventCharacter.setType(type);
						eventCharacter = null;
					}
				}
			}
			else if(eventCharacter.elimination()) {
				synchronized(panel.getMain()) {
					panel.getMain().notifyAll();
				}
				eventCharacter = null;
			}
		}
	}

	private void setAutoMoveing(StringTokenizer st) {

		String name = st.nextToken();
		String dir = st.nextToken();
		if(name.equals("#")) {
			name = panel.getHero().getName();
		}
		distance = Integer.parseInt(st.nextToken());

		for(AbstractCharacter chara : charaList) {
			if(chara.getName().equals(name)) {
				eventCharacter = chara;
				type = eventCharacter.getType();
				eventCharacter.setType(MoveType.EVENT);
			}
		}

		direction = switch(dir) {
			case "U" -> Direction.UP;
			case "D" -> Direction.DOWN;
			case "R" -> Direction.RIGHT;
			case "L" -> Direction.LEFT;
			default -> null;
		};
		eventCharacter.setDirection(direction);
	}
		
	public void autoMove() {
		if(eventCharacter != null && distance != 0) {
			if(eventCharacter.isMoving()) {
				if(eventCharacter.move()) {
					moveCount++;
				}
			}
			else {
				eventCharacter.setDirection(direction);
			}
			if(eventCharacter instanceof Hero) {
				panel.setOffset();
			}
		}
		else if(eventParty.size() > 0) {
			int index = 0;

			for(Entry<AbstractCharacter, MoveType> entry : eventParty.entrySet()) {
				if(entry.getKey().isMoving()) {
					if(entry.getKey().move()) {
						if(index == eventParty.size() - 1) {
							moveCount++;
							index = 0;
						}
						else {
							index++;
						}
					}	
				}
				else {
					entry.getKey().setDirection(direction);
				}
				if(entry.getKey() instanceof Hero) {
					panel.setOffset();
				}
			}
		}
	}

	private void setAllDirection(StringTokenizer st) {

		String dir = st.nextToken();
		distance = Integer.parseInt(st.nextToken());

		direction = switch(dir) {
			case "U" -> Direction.UP;
			case "D" -> Direction.DOWN;
			case "R" -> Direction.RIGHT;
			case "L" -> Direction.LEFT;
			default -> null;
		};

		while(st.hasMoreTokens()) {
			
			String name = st.nextToken();
			if(name.equals("#")) {
				name = panel.getHero().getName();
			}
			for(AbstractCharacter chara : charaList) {
				if(chara.getName().equals(name)) {
					MoveType etype = chara.getType();
				    chara.setType(MoveType.EVENT);
					if(distance == 0) {
						chara.setDirection_2(direction);
						eventParty.put(chara, etype);
					}
					else {
						chara.setDirection(direction);
						eventParty.put(chara, etype);
					}
				}
			}
		}
	}

	private void step(StringTokenizer st) {
		String name = st.nextToken();
		if(name.equals("#")) {
			name = panel.getHero().getName();
		}
		for(AbstractCharacter chara : charaList) {
			if(chara.getName().equals(name)) {
				eventCharacter = chara;
				type = eventCharacter.getType();
				eventCharacter.setType(MoveType.EVENT);
				eventCharacter.setDirection(chara.getDirection());
			}
		}
	}

	private void setAutoDirection(StringTokenizer st) {

		String name = st.nextToken();
		String dir = st.nextToken();
		if(name.equals("#")) {
			name = panel.getHero().getName();
		}

		switch(dir) {
			case "U" -> direction = Direction.UP;
			case "D" -> direction = Direction.DOWN;
			case "R" -> direction = Direction.RIGHT;
			case "L" -> direction = Direction.LEFT;
		}
		
		for(AbstractCharacter chara : charaList) {
			if(chara.getName().equals(name)) {
				eventCharacter = chara;
				type = eventCharacter.getType();
				eventCharacter.setType(MoveType.EVENT);
				eventCharacter.setDirection_2(direction);
				moveCount++;
			}
		}
	}
	
	private void characterDead(StringTokenizer st) {

		String name = st.nextToken();
		if(name.equals("#")) {
			name = panel.getHero().getName();
		}

		for(AbstractCharacter chara : charaList) {
			if(chara.getName().equals(name)) {
				eventCharacter = chara;	
			}
		}
		eventCharacter.setDieAnimation();
	}

	private void doorOpen(StringTokenizer st) {
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());

		Door door = (Door) eventCheck(x, y);
		if(door != null) {
			panel.getSound().soundEffectStart(SoundEffect.DOOR);
			door.doorOpen();
		}
		actionStatus = ActionStatus.END;
	}

	private void characterSet(StringTokenizer st) {
		String name = st.nextToken();
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());

		for(AbstractCharacter chara : charaList) {
			if(chara.getName().equals(name)) {
				eventCharacter = chara;
				type = eventCharacter.getType();
				eventCharacter.setType(MoveType.EVENT);
				setPosition(x, y, chara);
			}
		}
	}

	private void warp(StringTokenizer st) {

		Hero hero = (Hero)charaList.get(0);
		int x = hero.getX();
		int y = hero.getY();
		int newX = Integer.parseInt(st.nextToken());
		int newY = Integer.parseInt(st.nextToken());
		String newMapId = st.nextToken();

		MapChengePoint chenge = new MapChengePoint(x, y, newX, newY, newMapId);
		removeCharacter(hero);
		if(crystal != null) {
			crystal.close();
		}
		init();
		panel.lordMap(chenge.getNewMapId());
		
		chenge.setLocation(hero);
		if(panel.getSoundNumber() != hero.getMap().getSoundNumber()) {
			panel.setBgm(hero.getMap().getSoundNumber());
		}
		panel.setOffset();
		panel.getStory().lordMainEvent();
	}

	private void setName(StringTokenizer st) {

		String name = st.nextToken();
		String newName = st.nextToken();

		for(AbstractCharacter chara : charaList) {
			if(chara.getName().equals(name)) {
				chara.setName(newName);
			}
		}
	}

	private void setPosition(int x, int y, AbstractCharacter chara) {
		chara.setX(x);
		chara.setY(y);
	}

	private void setHevensVoice(StringTokenizer st) {
		panel.getWindow().setting(st.nextToken());
		panel.getWindow().ghostOn();
		eventTalk = true;
	}

	private void getItem(StringTokenizer st) {

		Item item = Item.getReference(st.nextToken());
		Hero hero = (Hero)charaList.get(0);
		eventTalk = true;
		if(item.getGenru() == ItemGenru.IMPORTANT) {
			hero.getStatus().addKeyItem(item);
		}
		else {
			if(!hero.getStatus().isMax()) {
				hero.getStatus().getItem(item);
			}
		}
		panel.getWindow().setting(hero.getStatus().getMessage());
		panel.getWindow().open();
	}

	private void getMoney(StringTokenizer st) {
		eventTalk = true;
		Hero hero = (Hero)charaList.get(0);
		int bonus = Integer.parseInt(st.nextToken());
		money += bonus;
		panel.getWindow().setting(hero.getName() + "は" + bonus + "Cを　手に入れた");
		panel.getWindow().open();
	}

	private void setBackGround(StringTokenizer st) {
		String imageName = st.nextToken();

		switch(imageName) {
			case "Telop" -> {
				backGraoundImage = telopImage;
			}
			case "Map" -> {
				backGraoundImage = panel.getWorlMap().getWorldMapImage();
			}
		}
	}

	private void setTelopImage(StringTokenizer st) {

		String order = st.nextToken();
		String keyName = st.nextToken();

		if(order.equals("add")) {
			drawImagelist.add(ImageStatus.getReference(keyName));
		}
		else if(order.equals("remove")) {
			drawImagelist.removeIf(v -> v.getKeyName().equals(keyName));
		}
	}

	private void gradually(StringTokenizer st) {

		isGradation = true;
		int red = Integer.parseInt(st.nextToken());
		if(red > 255) {
			red = 255;
		}
		else if(red < 0) {
			red = 0;
		}

		int green = Integer.parseInt(st.nextToken());
		if(green > 255) {
			green = 255;
		}
		else if(green < 255) {
			green = 0;
		}

		int blue = Integer.parseInt(st.nextToken());
		if(blue > 255) {
			blue = 255;
		}
		else if(blue < 0) {
			blue = 0;
		}

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(() -> {
			if(transparency < 255) {
				transparency++;
			}
			else {		
				service.shutdown();
				synchronized(panel.getMain()) {
					panel.getMain().notifyAll();
				}
			}
		}, 0, 30, TimeUnit.MILLISECONDS);
	}

	private void reversed() {
		if(isGradation) {
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(() -> {
				if(transparency > 0) {
					transparency--;
				}
				else {		
					service.shutdown();
					isGradation = false;
					synchronized(panel.getMain()) {
						panel.getMain().notifyAll();
					}
				}
			}, 0, 30, TimeUnit.MILLISECONDS);
		}
	}

	private void powerEffect(StringTokenizer st) {
		
		id = Integer.parseInt(st.nextToken());
		if(id == 0) {
			effect = 0;
		}
		else {
			effect = 9;
		}
		int limit = Integer.parseInt(st.nextToken());
		panel.getSound().soundEffectStart(SoundEffect.POWER);
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(() -> {
			
			if(power < 8) {
				power++;
			}
			else {
				power = 0;
			}
			if(counter == 5) {
				panel.getSound().soundEffectStart(SoundEffect.POWER);
			}
			counter++;
			if(counter == limit) {		
				service.shutdown();
				power = -1;
				counter = 0;
				synchronized(panel.getMain()) {
					panel.getMain().notifyAll();
				}	
			}
		}, 0, 200, TimeUnit.MILLISECONDS);	
	}

	private void stop(StringTokenizer st) {
		long time = Integer.parseInt(st.nextToken());
		counter = 0;
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(() -> {
			if(counter < time) {
				counter++;
			}
			else {
				service.shutdown();
				counter = 0;
				synchronized(panel.getMain()) {
					panel.getMain().notifyAll();
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	public String getMapName() {
		return select.getMapName();
	}

	public int pixcelToChip(double pixcel) {
        return (int)Math.floor(pixcel / CHIP_SIZE);
    }

	private int ChipToPixcel(int chip) {
        return chip * CHIP_SIZE;
    }

	public MainPanel getPanel() {
		return this.panel;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getMx() {
		return this.mx;
	}

	public int getMy() {
		return this.my;
	}

	public MapDataPath getSelectMap() {
		return this.select;
	}

	public Map<String, Integer> getHeightDatas() {
		return lord.getHeightDatas();
	}

	public int getSoundNumber() {
		int soundNumber = Bgm.valueOf(select.getBgm()).ordinal();
		return soundNumber;
	}

	public void setGradation(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		transparency = 255;
		isGradation = true;
	}

	public Crystal getCrystal() {
		return this.crystal;
	}

	public void resetCharacter() {
		for(int i = 0; i < newSettigList.size(); i++) {
			for(int j = 0; j < charaList.size(); j++) {
				AbstractCharacter chara = charaList.get(j);
				String name = newSettigList.get(i).getName();
				if(chara.getName().equals(name)) {
					newSettigList.get(i).setNew(chara);
				}
			}
		}
	}

	public String getObjectName(int x, int y) {

		switch (lord.getMapData(OBJECT)[y][x]) {
			case 104 -> {
				return "壺";
			}
			case 106 -> {
				return "タンス";
			}
			case 120 -> {
				return "樽";
			}
			case 122 -> {
				return "本棚";
			}
		}
		return "";
	}
}
