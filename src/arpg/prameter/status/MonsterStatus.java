package arpg.prameter.status;

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Map;
import java.util.Random;

import arpg.base.item.Item;
import arpg.base.magic.Magic;
import arpg.base.map.GameMap;
import arpg.personae.AbstractCharacter;
import arpg.personae.Hero;
import arpg.personae.operation.AttackMagicOperation;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;

public class MonsterStatus extends AbstractStatus {

	private Point drawPoint;
	private int mtk;
	private int damage;
	private int money;
	private int ex;
	private String itemKey;
	private int dropPercent;
	private int[] actions;

	public MonsterStatus(String name, int level, int hp, int mp, int str, int agi, int vit, int mgi, int res, int mtk, int money, int ex, String itemKey, int dropPercent, GameMap map, int action1, int action2, int action3, int action4, int action5, int action6) {
		super(name, level, hp, mp, str, agi, vit, mgi, res, map);

		this.mtk = mtk;
		this.money = money;
		this.ex = ex;
		this.itemKey = itemKey;
		this.dropPercent = dropPercent;

		damage = 0;
		drawPoint = new Point(0, 0);
		
		actions = new int[]{action1, action2, action3, action4, action5, action6};
	}

	public static MonsterStatus getMonsterStatus(String name, GameMap map) {
		Map<String, MonsterStatus> reference = Map.ofEntries(
			Map.entry("サソリ", new MonsterStatus("サソリ", 1, 3, 10, 7, 2, 5, 0, 1, 3, 2, 1, "薬草", 1, map, ATK, ATK, PZ, PZ, 0, 0)),
			Map.entry("ゴブリン", new MonsterStatus("ゴブリン", 1, 5, 0, 9, 3, 6, 0, 1, 0, 3, 2, "ブロンズソード", 1, map, ATK, ATK, PR, PR, MS, MS)),
			Map.entry("メフィスト", new MonsterStatus("メフィスト", 1, 5, 10, 9, 3, 6, 0, 1, 0, 3, 2, "ブロンズソード", 1, map, ATK, ATK, ATK, 0, 0, 0))
		);
		return reference.getOrDefault(name, new MonsterStatus("ダミー", 1, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, "薬草", 9999, map, ATK, ATK, ATK, ATK, ATK, ATK));
	}

	public void drawName(String name, int dx, int dy, int drawLine, Graphics g) {
		g.setColor(FontAndColor.STANDARD.getColor());
		g.setFont(FontAndColor.STANDARD.getFont());
		if(drawLine == 0) {
			drawLine = 1;
		}
		g.drawString("Lv" + level + "　" + name, dx, dy - CHIP_SIZE * drawLine);
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

	@Override
	public void drawDamage(int offsetX, int offsetY, Graphics g) {
		if(damage != -1) {
			
			g.setColor(DAMAGE_COLOR);
			g.setFont(STANDARD);
			g.drawString(String.valueOf(damage), drawPoint.x + offsetX, drawPoint.y + offsetY - motion);
		}
	}

	@Override
	public boolean attack(int x, int y, Rectangle actionRect) {
		Hero hero = (Hero)map.getCharaList().get(0);
		if(map.search(x, y).equals(map.search(hero.getX(), hero.getY()))) {
			if(hitting(actionRect, hero.getArea())) {
				drawPoint.setLocation(hero.getPx(), hero.getPy() - CHIP_SIZE);
				Random rand = new Random();
				int attackPoint = (atk - (hero.getStatus().getDef() / 2)) / 2;
				int randomPoint = attackPoint / 16 + 1;
				randomPoint = rand.nextInt(randomPoint * 2) - randomPoint;
				damage = attackPoint + randomPoint;
				if(damage < atk / 10) {
					damage = atk / 10;
					if(damage == 0) {
						damage = 1;
					}
				}
				if(hero.getStatus().getHp() - damage > 0) {
					battalMessage = name + "の攻撃！\\n" + hero.getName() + "に" + damage + "ダメージ！";
					hero.getStatus().setHp(hero.getStatus().getHp() - damage);
				}
				else {
					battalMessage = name + "の攻撃！\\n" + hero.getName() + "に" + damage + "ダメージ！\\n" + hero.getName() + "はやられてしまった。";
					hero.getStatus().setHp(0);
				}
				return true;
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
			if(chara instanceof Hero) {
				
				Hero hero = (Hero)chara;

				if(hitting(rect, hero.getArea())) {
					drawPoint.setLocation(hero.getPx(), hero.getPy() - CHIP_SIZE);
					Random rand = new Random();
					int attackPoint = (atk - (hero.getStatus().getDef() / 2)) / 2;
					int randomPoint = attackPoint / 16 + 1;
					randomPoint = rand.nextInt(randomPoint * 2) - randomPoint;
					damage = attackPoint + randomPoint;
					if(damage < atk / 10) {
						damage = atk / 10;
						if(damage == 0) {
							damage = 1;
						}
					}
					if(hero.getStatus().getHp() - damage > 0) {
						battalMessage = name + "の攻撃！\\n" + hero.getName() + "に" + damage + "ダメージ！";
						hero.getStatus().setHp(hero.getStatus().getHp() - damage);
					}
					else {
						battalMessage = name + "の攻撃！\\n" + hero.getName() + "に" + damage + "ダメージ！\\n" + name + "は　" + hero.getName() + "を倒した。";
						hero.getStatus().setHp(0);
					}
					return true;
				}
			}
		}		
		return false;
	}

	public void specialAttack(int action) {
		Hero hero = (Hero)map.getCharaList().get(0);
		HeroStatus status = hero.getStatus();
		String specialAttackName = "";
		boolean hit = false;

		switch(action) {
			case PZ -> {
				specialAttackName = "毒";
				if(Math.random() < 0.4) {
					status.getCondition().setPoizon();
					hit =true;
					hero.setting();
				}
			}
			case PR -> {
				specialAttackName = "麻痺";
				if(Math.random() < 0.4) {
					status.getCondition().setParalysis();
					hit =true;
					hero.setting();
				}
			}
			case MS -> {
				specialAttackName = "封印";
				if(Math.random() < 0.4) {
					status.getCondition().setMagicSeal();
					hit =true;
					hero.setting();	
					hero.getStatus().sealed();
				}
			}
			case BN -> {
				specialAttackName = "暗闇";
				if(Math.random() < 0.4) {
					status.getCondition().setBrightness();
					hit =true;	
					hero.setting();
				}
			}	
		}
		if(hero.getStatus().getHp() - damage > 0) {
			if(hit) {
				battalMessage = name + "の" + specialAttackName + "攻撃！\\n" + hero.getName() + "に" + damage + "ダメージ！" + hero.getName() + "は" + specialAttackName + "状態になった。";
			}
			else {
				battalMessage = name + "の" + specialAttackName + "攻撃！\\n" + hero.getName() + "に" + damage + "ダメージ！";
			}	
		}
		else {
			battalMessage = name + "の" + specialAttackName + "攻撃！\\n" + hero.getName() + "に" + damage + "ダメージ！\\n" + hero.getName() + "はやられてしまった。";
			hero.getStatus().setHp(0);
			hero.getStatus().getCondition().cureAll();
		}
	}

	@Override
	public boolean useAttackMagic(Magic magic, Rectangle rect) {
		
		damage = -1;
		if(noAction()) {
			return false;
		}	
		for(AbstractCharacter chara : map.getCharaList()) {
			if(chara instanceof Hero) {
				Hero hero= (Hero)chara;
				if(hero.isDefeated()) {
					return false;
				}
				if(hero.getArea().intersects(rect)) {
					drawPoint.setLocation(hero.getPx(), hero.getPy() - CHIP_SIZE);
					Random rand = new Random();
					int wandPoint = mtk;
					int attackPoint = (wandPoint + (int)(mgi * magic.getPoint()) - (hero.getStatus().getRes() / 2)) / 2;
					int randomPoint = attackPoint / 16 + 1;
					randomPoint = rand.nextInt(randomPoint * 2) - randomPoint;
					damage = attackPoint + randomPoint;
	
					if(damage <= 0) {
						damage = 1;
					}
					if(hero.getStatus().getHp() - damage > 0) {
						battalMessage = name + "は　" + magic.getName() + "を放った！\\n" + hero.getName() + "に" + damage + "ダメージ！";
						hero.getStatus().setHp(hero.getStatus().getHp() - damage);
					}
					else {
						battalMessage = name + "は　" + magic.getName() + "を放った！\\n" + hero.getName() + "に　" + damage + "ダメージ！\\n" + hero.getName() + "は　やられてしまった。";
						hero.getStatus().setHp(0);
						
						hero.defeated();
					}
					return true;
				}
			}
		}	
		return false;
	}

	public Item dropItem() {
		
		Random rand = new Random();
		int value = rand.nextInt(dropPercent) + 1;
		if(value == 1) {
			return Item.getReference(itemKey);
		}
		return null;
	}

	public int actionSelect(AttackMagicOperation operation) {
		Random rand = new Random();
		int action = actions[rand.nextInt(actions.length)];
		if(action >= 0 && action < 8) {
			String[] keys = {"フィアド", "アクエル", "ガイア", "スラッシュ", "キュレ", "リドーテ", "リパイア", "リブラ"};
			operation.setMagic(Magic.getReference(keys[action]));
		}
		return action;
	}

	public void init() {
		hp = maxHp;
		mp = maxMp;
		condition.cureAll();
	}

	public int getMoney() {
		return this.money;
	}

	public int getEx() {
		return this.ex;
	}
}
