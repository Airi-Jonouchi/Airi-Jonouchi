package arpg.prameter.status;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Point;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import arpg.base.event.map.Drop;
import arpg.base.item.Equipment;
import arpg.base.item.Item;
import arpg.base.item.Item.ItemGenru;
import arpg.base.magic.Magic;
import arpg.base.map.GameMap;
import arpg.personae.AbstractCharacter;
import arpg.personae.Hero;
import arpg.personae.Monster;
import arpg.personae.operation.AttackMagicOperation;
import arpg.personae.operation.AttackOperation;
import arpg.prameter.level.LevelUp;
import arpg.sound.Sound;
import arpg.sound.Sound.SoundEffect;
import arpg.system.window.IVariableWindow;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;
import static arpg.main.MainPanel.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class HeroStatus extends AbstractStatus implements IVariableWindow {

	private static final Equipment SET_DEFAULT = new Equipment(
		Item.getReference("短剣"), Item.getReference("なし腕"), Item.getReference("服"), Item.getReference("なし頭")
	);

	private static final int X = 100;
    private static final int Y = 50;
    private static final int[] LINE_HEIGHT = {30, 40};

	private static final int[] LEVEL_UP = {
		0,1,23,47,110,220,450,800,1300,2000,
		2900,4000,5500,7500,10000,13000,17000,21000,25000,29000,
		33000,37000,41000,45000,49000,53000,57000,61000,65000,65535
	};

	private Point[] drawPoints;

	private int ex;
	private int damage;

	private List<Item> itemBag;
	private List<Item> keyItems;
	private List<Magic> masterMagics;
	private Equipment equipment;
	private Rectangle[] menuWindow;
	private int[] menuSize;
	private int pos;
	private LevelUp levelUp;

	private Command command;
	private String message;
	private int transparency;

	public HeroStatus(String name, int level, int hp, int mp, int str, int agi, int vit, int mgi, int res, GameMap map) {
		super(name, level, hp, mp, str, agi, vit, mgi, res, map);

		damage = -1;
		
		itemBag = new LinkedList<>();
		keyItems = new LinkedList<>();
		masterMagics = new ArrayList<>();

		levelUp = new LevelUp();
		equipment = SET_DEFAULT;

		drawPoints = new Point[] {
			new Point(X + 30, Y + 30),
			new Point(X, Y),
			new Point(X + 516, Y + 50),
			new Point(X + 100, Y + 290),
			new Point(0, 0)
		};

		menuSize = new int[]{Command.values().length, 0, 0, 0};
		menuWindow = new Rectangle[] {
			new Rectangle(
				210, 100, 215, menuSize[1] * LINE_HEIGHT[0] + 15
			),
			new Rectangle(
				430, 100, 125, menuSize[2] * LINE_HEIGHT[0] + 15
			),
			new Rectangle(
				430, 100, 175, menuSize[2] * LINE_HEIGHT[0] + 15
			),
			new Rectangle(
				430, 100, 215, menuSize[2] * LINE_HEIGHT[0] + 15
			),
			new Rectangle(
				650, 100, 105, menuSize[3] * LINE_HEIGHT[0] + 15
			),
			new Rectangle(
				210, 100, 215, 1 * LINE_HEIGHT[0] + 15
			)
		};

		command = Command.NO_SELECT;
		message = "";
		battalMessage = "";
		commandEffect = CommandEffect.NONE;

		sound = new Sound();
	}

	public BufferedImage getMagicEffect() {
		return magicEffect;
	}

	@Override
	public void drawDamage(int offsetX, int offsetY, Graphics g) {
		
		if(damage != -1) {
			
			//g.setColor(DAMAGE_COLOR);
			//g.setFont(STANDARD);
			drawString(String.valueOf(damage), drawPoints[4].x + offsetX, drawPoints[4].y + offsetY - motion, DAMAGE_COLOR, g);	
		}
	}

	public void drawStatus(FontAndColor fc, Graphics g) {

		g.drawImage(image, X, Y, null);
		g.drawImage(face, X + 50, Y + 90, 96, 96, null);

		g.setFont(fc.getFont());
		g.setColor(Color.WHITE);
		drawString(name, X + 50, Y + 47, g);	
		g.setColor(fc.getColor());
		
		String[] array = {
			String.valueOf(level), hp + " / " + maxHp, mp + " / " + maxMp, String.valueOf(str),
			String.valueOf(agi), String.valueOf(vit), String.valueOf(mgi), String.valueOf(res),
			String.valueOf(atk), String.valueOf(def)
		};
		g.drawString(String.valueOf(LEVEL_UP[level] - ex), drawPoints[3].x + ICON_SIZE + 5, drawPoints[3].y - LINE_HEIGHT[1]);
		IntStream.range(0, array.length).forEach(e -> drawData(array[e], e, g));
		IntStream.range(0, equipment.getEquipmentSet().length).forEach(e -> drawData(equipment.getEquipmentSet()[e], e, g));
	}

	private void drawData(String data, int line, Graphics g) {
		int x = drawPoints[2].x;
		int y = drawPoints[2].y + line * (LINE_HEIGHT[1]);

		int rightPosition  = x;
		FontMetrics fontMetrics = g.getFontMetrics();
		g.drawString(data, rightPosition - fontMetrics.stringWidth(data), y);	
	}

	private void drawData(Item eqip, int line, Graphics g) {

		int dx = drawPoints[3].x;
		int dy = (drawPoints[3].y - 17) + line * (LINE_HEIGHT[1]);
		eqip.drawIcon(dx, dy, g);

		String name = eqip.getName();
		int x = drawPoints[3].x + ICON_SIZE + 5;
		int y = drawPoints[3].y + line * (LINE_HEIGHT[1]);
		drawString(name, x, y, g);
	}

	public void drawLevelUpRogo(Hero hero, int offsetX, int offsetY, Graphics g) {
		levelUp.drawImage(hero, offsetX, offsetY, g);
	}

	public void drawMenu(Color color, Graphics g) {
		int dx = 245;
		int dy = 130;

		switch(command) {
			case ITEM -> {
				if(itemBag.size() > 0) {
					drawWindow(menuWindow[0], menuSize[1], g);
					//g.setColor(color);
					IntStream.range(0, itemBag.size()).forEach(e -> {
						itemBag.get(e).drawIcon(dx, dy + e * LINE_HEIGHT[0] - 18, g);
						drawString(itemBag.get(e).getName(), dx + ICON_SIZE, dy + e * LINE_HEIGHT[0], color, g);
					});
					drawString("並び替え", dx + ICON_SIZE, dy + (menuSize[1] - 1) * LINE_HEIGHT[0], color, g);
				}
				else {
					drawWindow(menuWindow[5], g);
					drawString("なし", dx + ICON_SIZE, dy +  LINE_HEIGHT[0], color, g);
				}
			}
			case MAGIC -> {
				if(masterMagics.size() > 0) {
					drawWindow(menuWindow[0], menuSize[1], g);
					//g.setColor(color);
					IntStream.range(0, menuSize[1]).forEach(e -> {
						masterMagics.get(e).drawIcon(dx, dy + e * LINE_HEIGHT[0] - 18, g);
						drawString(masterMagics.get(e).getName(), dx + ICON_SIZE, dy + e * LINE_HEIGHT[0], color, g);
					});	
				}
				else {
					drawWindow(menuWindow[5], g);
					drawString("未習得", dx + ICON_SIZE, dy +  LINE_HEIGHT[0], color, g);
				}	
			}
			case EQUIPMENT -> {
				drawWindow(menuWindow[0], menuSize[1], g);
				//g.setColor(color);
				IntStream.range(0, menuSize[1]).forEach(e -> {
					equipment.getEquipmentSet()[e].drawIcon(dx, dy + e * LINE_HEIGHT[0] - 18, g);
					drawString(equipment.getEquipmentSet()[e].getName(), dx + ICON_SIZE, dy + e * LINE_HEIGHT[0], color, g);
				});	
			}
			case STATUS -> drawStatus(FontAndColor.DARK, g);
			case IMPORTANT -> {
				if(keyItems.size() > 0) {
					drawWindow(menuWindow[0], menuSize[1], g);
					IntStream.range(0, menuSize[1]).forEach(e -> {
						keyItems.get(e).drawIcon(dx, dy + e * LINE_HEIGHT[0] - 18, g);
						drawString(keyItems.get(e).getName(), dx + ICON_SIZE, dy + e * LINE_HEIGHT[0], g);
					});
				}
				else {
					drawWindow(menuWindow[5], g);
					drawString("なし", dx + ICON_SIZE, dy +  LINE_HEIGHT[0], color, g);
				}		
			}
			default -> {}
		}
	}

	public void drawMenu2(Color color, Graphics g) {
		int dx = 465;
		int dy = 130;

		switch(command) {
			case ITEM -> {
				drawWindow(menuWindow[1], menuSize[2], g);
				//g.setColor(color);
				drawString("使う", dx, dy, color, g);
				drawString("捨てる", dx, dy + LINE_HEIGHT[0], color, g);
			}
			case EQUIPMENT -> {
				drawWindow(menuWindow[3], menuSize[2], g);
				//g.setColor(color);
				List<Item> list = itemBag.stream().filter(v -> v.getGenru() == genruSelection(pos)).collect(Collectors.toList());
				IntStream.range(0, list.size()).forEach(e -> {
					list.get(e).drawIcon(dx, dy + e * LINE_HEIGHT[0] - 18, g);
					drawString(list.get(e).getName(), dx + ICON_SIZE, dy + e * LINE_HEIGHT[0], color, g);
				});
			}
			case MAGIC -> {
				drawWindow(menuWindow[2], menuSize[2], g);
				//g.setColor(color);
				drawString("使う・セット", dx, dy, color, g);
			}
			default -> {}
		}
	}

	public void drawMenu3(Color color, Graphics g) {

		int dx = 685;
		int dy = 130;
		g.setColor(color);

		switch(command) {
			case EQUIPMENT -> {
				drawWindow(menuWindow[4], menuSize[3], g);
				drawString("装備", dx, dy, color, g);
				drawString("外す", dx, dy + LINE_HEIGHT[0], color, g);
			}
			default -> {}
		}
	}

	@Override
	public boolean attack(int x, int y, Rectangle actionRect) {
		damage = -1;
		if(noAction()) {
			return false;
		}
		for(AbstractCharacter chara : map.getCharaList()) {
			if(chara instanceof Monster) {
				
				Monster monster = (Monster)chara;

				if(monster.isDefeated()) {
					continue;
				}
				
				if(map.search(x, y).equals(map.search(monster.getX(), monster.getY()))) {
					if(hitting(actionRect, monster.getArea())) {
						
						drawPoints[4].setLocation(monster.getPx(), monster.getPy() - CHIP_SIZE);
						Random rand = new Random();
						int attackPoint = (atk - (monster.getStatus().getDef() / 2)) / 2;
						int randomPoint = attackPoint / 16 + 1;
						randomPoint = rand.nextInt(randomPoint * 2) - randomPoint;
						damage = attackPoint + randomPoint;
						if(damage <= 0) {
							damage = 1;
						}
						if(monster.getStatus().getHp() - damage > 0) {
							battalMessage = name + "の攻撃！\\n" + monster.getName() + "に" + damage + "ダメージ！";
							monster.getStatus().setHp(monster.getStatus().getHp() - damage);
						}
						else {
							battalMessage = name + "の攻撃！\\n" + monster.getName() + "に" + damage + "ダメージ！\\n" + name + "は　" + monster.getName() + "を倒した。";
							monster.getStatus().setHp(0);
							ex += monster.getStatus().getEx();
							Item item = monster.getStatus().dropItem();
							if(item != null) {
								if(map.eventCheck(monster.getX(), monster.getY()) == null) {
									Drop drop = new Drop(monster.getX(), monster.getY(), item);
									map.addEvent(drop);
								}
							}
							if(ex >= LEVEL_UP[level]) {
								level++;
								levelUp.update(level, this);
								battalMessage += "\\n" + name + "のレベルが" + level + "になった！";	
							}
							money += monster.getStatus().getMoney();
							monster.defeated();
						}
						return true;
					}
				}
			}		
		}
		return false;
	}

	public boolean attack(Rectangle rect) {
		damage = -1;
		if(noAction()) {
			return false;
		}
		for(AbstractCharacter chara : map.getCharaList()) {
			if(chara instanceof Monster) {
				
				Monster monster = (Monster)chara;

				if(monster.isDefeated()) {
					continue;
				}
				if(hitting(rect, monster.getArea())) {
					drawPoints[4].setLocation(monster.getPx(), monster.getPy() - CHIP_SIZE);
					Random rand = new Random();
					int attackPoint = (atk - (monster.getStatus().getDef() / 2)) / 2;
						int randomPoint = attackPoint / 16 + 1;
						randomPoint = rand.nextInt(randomPoint * 2) - randomPoint;
						damage = attackPoint + randomPoint;
						if(damage <= 0) {
							damage = 1;
						}
						if(monster.getStatus().getHp() - damage > 0) {
							battalMessage = name + "の攻撃！\\n" + monster.getName() + "に" + damage + "ダメージ！";
							monster.getStatus().setHp(monster.getStatus().getHp() - damage);
						}
						else {
							battalMessage = name + "の攻撃！\\n" + monster.getName() + "に" + damage + "ダメージ！\\n" + name + "は　" + monster.getName() + "を倒した。";
							monster.getStatus().setHp(0);
							ex += monster.getStatus().getEx();
							Item item = monster.getStatus().dropItem();
							if(item != null) {
								if(map.eventCheck(monster.getX(), monster.getY()) == null) {
									Drop drop = new Drop(monster.getX(), monster.getY(), item);
									map.addEvent(drop);
								}
							}
							if(ex >= LEVEL_UP[level]) {
								level++;
								levelUp.update(level, this);
								battalMessage += "\\n" + name + "のレベルが" + level + "になった！";	
							}
							money += monster.getStatus().getMoney();
							monster.defeated();
						}
						return true;
					}
				}
			}		
		return false;
	}

	public boolean useAttackMagic(Magic magic, Rectangle rect) {
		damage = -1;
		if(noAction() || condition.isMagicSeal()) {
			return false;
		}	
		for(AbstractCharacter chara : map.getCharaList()) {
			if(chara instanceof Monster) {
				Monster monster = (Monster)chara;
				if(monster.isDefeated()) {
					continue;
				}
				if(monster.getArea().intersects(rect)) {
					drawPoints[4].setLocation(monster.getPx(), monster.getPy() - CHIP_SIZE);
					Random rand = new Random();
					OptionalInt wand = keyItems.stream().mapToInt(Item::getPoint).max();
					int wandPoint = wand.getAsInt();
					int attackPoint = (wandPoint + (int)(mgi * magic.getPoint()) - (monster.getStatus().getRes() / 2)) / 2;
					int randomPoint = attackPoint / 16 + 1;
					randomPoint = rand.nextInt(randomPoint * 2) - randomPoint;
					damage = attackPoint + randomPoint;
	
					if(damage <= 0) {
						damage = 1;
					}
					if(monster.getStatus().getHp() - damage > 0) {
						battalMessage = name + "は　" + magic.getName() + "を放った！\\n" + monster.getName() + "に" + damage + "ダメージ！";
						monster.getStatus().setHp(monster.getStatus().getHp() - damage);
					}
					else {
						battalMessage = name + "は　" + magic.getName() + "を放った！\\n" + monster.getName() + "に　" + damage + "ダメージ！\\n" + name + "は　" + monster.getName() + "を倒した。";
						monster.getStatus().setHp(0);
						ex += monster.getStatus().getEx();
						Item item = monster.getStatus().dropItem();
						if(item != null) {
							if(map.eventCheck(monster.getX(), monster.getY()) == null) {
								Drop drop = new Drop(monster.getX(), monster.getY(), item);
								map.addEvent(drop);
							}
						}
						if(ex >= LEVEL_UP[level]) {
							level++;
							levelUp.update(level, this);
							battalMessage += "\\n" + name + "のレベルが" + level + "になった！";
						}
						money += monster.getStatus().getMoney();
						monster.defeated();
					}
					return true;
				}
			}
		}	
		return false;
	}

	public void useCommandMagic(int index, AttackMagicOperation opreation) {
		
		
		Magic magic = masterMagics.get(index);
		message = "";
		if(condition.isMagicSeal()) {
			message = "魔法は封印されている";
		}
		else if(mp > magic.getMp()) {
			switch (magic.getGengu()) {
				case ATTACK -> {
					opreation.setMagic(magic);
					sound.soundEffectStart(SoundEffect.PUSH);
					message = name.concat("は　").concat(magic.getName()).concat("を　セットした");
				}
				case RECOVERY -> {
					if(hp == maxHp) {
						sound.soundEffectStart(SoundEffect.HEAL);
						message = name.concat("は　")
						.concat(magic.getName()).concat("を使った！\\n")
						.concat("しかし　何も起こらなかった");
					}
					else if(hp < maxHp) {
						isUse = true;
						commandEffect = CommandEffect.HEAL;
						magicEffect();
						sound.soundEffectStart(SoundEffect.HEAL);
						OptionalInt wand = keyItems.stream().mapToInt(Item::getPoint).max();
						int healPoint = wand.getAsInt() + (int)(mgi * magic.getPoint());
						if(hp + healPoint < maxHp) {
							hp += healPoint;
						}
						else {
							hp = maxHp;
						}
						mp -= magic.getMp();
						message = name.concat("は　")
							.concat(magic.getName()).concat("を使った！\\n")
							.concat(name).concat("のHPが　").concat(String.valueOf(healPoint))
							.concat("回復した");
					}
				}
				case TREATMENT -> {
					
					switch (magic.getName()) {
						case "リドーテ" -> {
							if(condition.isPoizon()) {
								isUse = true;
								commandEffect = CommandEffect.CURE_POIZON;
								magicEffect();
								sound.soundEffectStart(SoundEffect.STATUS);
								condition.curePoizon();
								mp -= magic.getMp();
								message = name.concat("は　")
									.concat(magic.getName()).concat("を使った！\\n")
									.concat(name.concat("の毒が　").concat("消えた"));
							}
						}
						case "リパイア" -> {
							if(condition.isParalysis()) {
								isUse = true;
								commandEffect = CommandEffect.CURE_POIZON;
								magicEffect();
								sound.soundEffectStart(SoundEffect.STATUS);
								condition.cureParalysis();
								mp -= magic.getMp();
								message = name.concat("は　")
									.concat(magic.getName()).concat("を使った！\\n")
									.concat(name.concat("の麻痺が　").concat("消えた"));
							}
						}
						case "リブラ" -> {
							if(condition.isBrightness()) {
								isUse = true;
								commandEffect = CommandEffect.CURE_POIZON;
								magicEffect();
								sound.soundEffectStart(SoundEffect.STATUS);
								condition.cureBrightness();
								mp -= magic.getMp();
								message = name.concat("は　")
									.concat(magic.getName()).concat("を使った！\\n")
									.concat(name.concat("の盲目が　").concat("治った"));
							}
						}
					}

					if(message == "") {
						sound.soundEffectStart(SoundEffect.STATUS);
						message = name.concat("は　")
						.concat(magic.getName()).concat("を使った！\\n")
						.concat("しかし　何も起こらなかった");
					}
				}
				default -> {}
			}
		}
		else {
			message = "MPが足りない";
		}
	}

	public void masterMagic(Magic magic) {
		masterMagics.add(magic);
		message = name.concat("は　").concat(magic.getName()).concat("を　覚えた！");
		menuSize[1] = masterMagics.size();	
	}

	private void magicEffect() {
		
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(() -> {
			try {
				while(magicMotion < commandEffect.tatalEffect) {
					magicMotion++;
					TimeUnit.MILLISECONDS.sleep(50);
				}
			}
			catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
			finally {
				service.shutdown();
				isUse = false;
				magicMotion = 0;
			}
		});
	}

	public void useItem(int index) {
		
		Item item = itemBag.get(index);
		message = "";

		switch(item.getGenru()) {

			case RECOVERY -> {
				switch(item.getName()) {
					case "薬草", "上薬草" -> {
						if(hp == maxHp) {
							message = "HPは減っていません";
						}
						else {
							hp += item.getPoint();
							if(hp > maxHp) {
								hp = maxHp;
							}
							message = name.concat("は　")
								.concat(item.getName()).concat("を使った！\\n")
								.concat(name).concat("のHPが　").concat(String.valueOf(item.getPoint()))
								.concat("回復した");
							itemBag.remove(index);
							menuSize[1] = itemBag.size() + 1;
						}
					}
					case "魔法の薬", "上魔法の薬" -> {
						if(mp == maxMp) {
							message = "MPは減っていません";
						}
						else {
							mp += item.getPoint();
							if(mp > maxMp) {
								mp = maxMp;
							}
							message = name.concat("は　")
								.concat(item.getName()).concat("を使った！\\n")
								.concat(name).concat("のMPが　").concat(String.valueOf(item.getPoint()))
								.concat("回復した");
							itemBag.remove(index);
							menuSize[1] = itemBag.size() + 1;
						}
					}
				}	
			}
			case STATUS -> {
				switch(item.getName()) {
					case "ポイズンハーブ" -> {
						if(condition.isPoizon()) {
							condition.curePoizon();

							message = name.concat("は　")
								.concat(item.getName()).concat("を使った！\\n")
								.concat(name.concat("の毒が　").concat("消えた"));
							itemBag.remove(index);
							menuSize[1] = itemBag.size() + 1;
						}
						
					}
					case "麻痺消し" -> {
						if(condition.isParalysis()) {
							condition.cureParalysis();
						}
						message = name.concat("は　")
							.concat(item.getName()).concat("を使った！\\n")
							.concat(name.concat("の麻痺が　").concat("消えた"));
						itemBag.remove(index);
						menuSize[1] = itemBag.size() + 1;
					}
					
				}
				if(message == "") {
					message = item.getName().concat("は").concat(name).concat("には必要なさそうだ。");
				}
			}
			default -> {
				message = name.concat("は　").concat(item.getName()).concat("を使った！\\n").concat("しかし　何も起こらなかった");
			}
		}
	}

	public void getItem(Item item) {
		message = "";
		if(item.getGenru() == ItemGenru.IMPORTANT || item.getGenru() == ItemGenru.WAND) {
			keyItems.add(item);
		}
		else if(!isMax()) {
			itemBag.add(item);
		}
		else {
			message = "持ち物がいっぱいです";
		}
		if(message.equals("")) {
			message = name.concat("は　").concat(item.getName()).concat("を　手に入れた！");
		}
		menuSize[1] = itemBag.size();
	}

	public void tidyUp() {
		itemBag.sort(Comparator.comparing(Item::getGenru).thenComparing(Item::getPoint).thenComparing(Item::getName));	
	}

	public boolean removeItem(int index) {
		Item item = itemBag.get(index);
		if(item.isNowEquipment()) {
			message = "装備中のものは捨てられません";
			return false;
		}
		else {
			itemBag.remove(index);	
			message = name.concat("は　").concat(item.getName()).concat("を　捨てた");
			menuSize[1] = itemBag.size() + 1;
			return true;
		}
	}

	public void addItemBag(Item item) {
		this.itemBag.add(item);	
	}

	public void addKeyItem(Item item) {
		this.keyItems.add(item);
		message = name + "は　" + item.getName() + "を　手に入れた";
		keyItems.sort(Comparator.comparing(Item::getGenru).thenComparing(Item::getName));
	}

	public void setEquipment(int genru, int index, Hero hero, AttackOperation opreation) {

		List<Item> list = itemBag.stream().filter(v -> v.getGenru() == genruSelection(genru)).collect(Collectors.toList());
		Item newItem = list.get(index);
		Item oldItem = null;
		Optional<Item> op = list.stream().filter(v -> v.isNowEquipment()).findFirst();
		message = "";
			
		if(newItem.isNowEquipment()) {
			message = "そのアイテムは装備中です";
		}
		else {
			switch(genru) {
				case 0 -> {
					oldItem = op.orElse(Item.getReference("短剣"));
					setAtk(-oldItem.getAtk());
					equipment.setWepon(newItem);
					Wepon wepon = Wepon.values()[newItem.getPoint()];
					hero.setWepon(wepon);
					opreation.setWepon(wepon);
				}
				case 1 -> {
					oldItem = op.orElse(Item.getReference("なし腕"));
					setDef(-oldItem.getDef());
					equipment.setShield(newItem);
				} 
				case 2 -> {
					oldItem = op.orElse(Item.getReference("服"));
					setDef(-oldItem.getDef());
					equipment.setArmor(newItem);
				}
				case 3 -> {
					oldItem = op.orElse(Item.getReference("なし頭"));
					setDef(-oldItem.getDef());
					equipment.setHelmet(newItem);
				}
				default -> message = "そのアイテムは装備できません";
			}
		}
		if(message.equals("")) {
			message = name.concat("は　").concat(newItem.getName()).concat("を装備した！");
			if(newItem.getGenru() == ItemGenru.WEAPON) {
				setAtk(newItem.getAtk());
			}
			else {
				setDef(newItem.getDef());
			}
			newItem.setNowEquipment(true);
			oldItem.setNowEquipment(false);
		}
	}

	public void lordEquipment(Item item, AttackOperation operation) {
		
		switch(item.getGenru()) {
			case WEAPON -> {
				equipment.setWepon(item);
				setAtk(item.getAtk());
			}
			case SHIELD -> {
				equipment.setShield(item);
				setDef(item.getDef());
			}
			case ARMOR -> {
				equipment.setArmor(item);
				setDef(item.getDef());
			}
			case HELMET -> {
				equipment.setHelmet(item);
				setDef(item.getDef());
			}
			default -> {}
		}
		item.setNowEquipment(true);
	}

	public void removeEquipment(int genru, int index, AttackOperation opreation) {

		List<Item> list = itemBag.stream().filter(v -> v.getGenru() == genruSelection(genru)).collect(Collectors.toList());
		Item item = list.get(index);
		message = "";
		if(!item.isNowEquipment()) {
			message = "装備していないので外せません";
		}
		else {
			switch(genru) {
				case 0 -> {
					equipment.setWepon(Item.getReference("短剣"));
					opreation.setWepon(Wepon.SWORD);
				}
				case 1 -> equipment.setShield(Item.getReference("なし腕"));
				case 2 -> equipment.setArmor(Item.getReference("服"));
				case 3 -> equipment.setHelmet(Item.getReference("なし頭"));
				default -> message = "装備していないので外せません";
			}
		}
		if(message.equals("")) {
			message = name.concat("は　").concat(item.getName()).concat("を外した！");
			if(item.getGenru() == ItemGenru.WEAPON) {
				atk -= item.getAtk();
			}
			else {
				def -= item.getDef();
			}
			item.setNowEquipment(false);
		}
	}

	public boolean isMax() {
		if(itemBag.size() >= MAX_ITEM_IN_BAG) {
			return true;
		}
		return false;
	}

	public boolean containsItem(Item item) {
		if(keyItems.contains(item)) {
			return true;
		}
		return false;
	}

	private ItemGenru genruSelection(int index) {
		ItemGenru genru = ItemGenru.WEAPON;
		switch (index) {
			case 0 -> genru = ItemGenru.WEAPON;
			case 1 -> genru = ItemGenru.SHIELD;
			case 2 -> genru = ItemGenru.ARMOR;
			case 3 -> genru = ItemGenru.HELMET;
		}
		return genru;
	}

	public void setMenuSize(Command command, int current) {
		this.command = command;
		switch(command) {
			case ITEM -> {
				if(current == 1) {
					menuSize[1] = itemBag.size() + 1;
				}
				else if(current == 2) {
					menuSize[2] = 2;
				}	
			}
			case MAGIC -> {
				if(current == 1) {
					menuSize[1] = masterMagics.size();
				}
				else if(current == 2) {
					menuSize[2] = 1;
				}
			}
			case EQUIPMENT -> {
				if(current == 1) {
					menuSize[1] = 4;
				}
				else if(current == 2) {
					menuSize[2] = itemBag.stream().filter(v -> v.getGenru() == genruSelection(pos)).collect(Collectors.toList()).size();
				}
				else if(current == 3) {
					menuSize[3] = 2;
				}
			}
			case IMPORTANT -> menuSize[1] = keyItems.size();
			default -> {}
		}
	}

	public int getMeneSize(int index) {
		return this.menuSize[index];
	}

	public int bagSize() {
		return this.itemBag.size();
	}

	public void sellItem(int index) {
		this.itemBag.remove(index);
		menuSize[1] = itemBag.size();
	}

	public void empty() {
		this.itemBag.clear();
		this.keyItems.clear();
		this.masterMagics.clear();
		equipment = SET_DEFAULT;
		atk = str;
		def = vit;
	}
	
	public boolean fieldDamage(int moveCount) {

		if(condition.isPoizon() && moveCount % 4 == 0) {
			if(hp > 1) {
				sound.soundEffectStart(SoundEffect.HIT);
				poizonEffect();
				hp -= 1;
			}
			return true;
		}
		return false;
	}

	public void drawFieldDamage(Graphics g) {

		if(condition.isPoizon()) {
			g.setColor(new Color(255, 0, 0, transparency));
			g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
		}		
	}

	private void poizonEffect() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(() -> {
			if(transparency < 100) {
				transparency++;
			}
			else if(transparency == 100) {
				service.shutdown();
				transparency = 0;
			}
		}, 0, 800, TimeUnit.MICROSECONDS);
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public List<Item> getItemBag() {
		return this.itemBag;
	}

	public List<Item> getKeyItems() {
		return this.keyItems;
	}

	public Equipment getEquipmentSet() {
		return this.equipment;
	}

	public List<Magic> getMasterMagics() {
		return this.masterMagics;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
