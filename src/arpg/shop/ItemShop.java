package arpg.shop;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import arpg.base.item.Item;
import arpg.main.MainPanel;
import arpg.sound.Sound.SoundEffect;
import arpg.ui.cursor.Cursor;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;
import static arpg.main.MainPanel.*;

public class ItemShop extends AbstractShop {

	private static final String[] COMMAND = {"買う", "売る", "用はない"};

	private String shopName;
	private List<Item> items;
	private Cursor[] cursor;
	private Rectangle[] menu;
	private Item tradeItem;
	
	public ItemShop(int x, int y, MainPanel panel, String shopName, List<Item> items) {
		super(x, y, panel);
		this.shopName = shopName;
		this.items = items;

		cursor = new Cursor[] {
			new Cursor(new Point(3, ORIGIN), 0, BRIGHT, 3),
			new Cursor(new Point(10, ORIGIN), 0, BRIGHT, 0),
			new Cursor(new Point(21, ORIGIN), 0, BRIGHT, 2, 5)
		};

		menu = new Rectangle[] {
			new Rectangle(50, 100, 135, 100),
			new Rectangle(190, 100, 220, items.size() * FONT_HEIGHT + 15),
			new Rectangle(190, 100, 220, status.getItemBag().size() * FONT_HEIGHT + 15),
			new Rectangle(415, 100, 120, 75)
		};

		tradeItem = null;
	}

	@Override
	public void draw(Graphics g) {
		if(isUsed) {
			drawWindow(menu[0], g);
			g.setColor(LightAndDark.values()[cursor[0].getBrightness()].getColor());
			g.setFont(FontAndColor.STANDARD.getFont());

			IntStream.range(0, COMMAND.length).forEach(e -> drawString(COMMAND[e], menu[0].x + 40, menu[0].y + FONT_HEIGHT + (e * FONT_HEIGHT), g));
			cursor[0].drawCursor(g);

			if(current > 0) {

				int dx = 230;
				int dy = 130;
				
				if(cursor[0].getPos() == 0) {
					drawWindow(menu[1], g);
					IntStream.range(0, items.size()).forEach(e -> {
						items.get(e).drawIcon(dx, dy + e * FONT_HEIGHT - 18, g);
						drawString(items.get(e).getName(), dx + ICON_SIZE, dy + e * FONT_HEIGHT, g);
					});
				}
				else if(cursor[0].getPos() == 1) {
					drawWindow(menu[2], g);
					IntStream.range(0, status.getItemBag().size()).forEach(e -> {
						status.getItemBag().get(e).drawIcon(dx, dy + e * FONT_HEIGHT - 18, g);
						drawString(status.getItemBag().get(e).getName(), dx + ICON_SIZE, dy + e * FONT_HEIGHT, g);
					});
				}
				cursor[1].drawCursor(g);
			}
			if(current > 1) {

				int dx = 455;
				int dy = 130;
				drawWindow(menu[3], g);
				drawString("はい", dx, dy, g);
				drawString("いいえ", dx, dy + FONT_HEIGHT, g);
				cursor[2].drawCursor(g);
			}
		}
		window.drawMessage(FontAndColor.STANDARD, g);	
	}

	@Override
	protected void changeColor() {
		for(int i = 0; i <= current; i++) {
			if(i != current) {
				cursor[i].setBrightness(DARK);
			}
			else {
				cursor[current].setBrightness(BRIGHT);
			}
		}
	}

	public void startingCustomerService() {
		window.setting("ここは" + shopName + "です！　何かご用はございますか？");
		window.open();
		isUsed = true;
	}

	@Override
	protected void select() {
		
		switch(cursor[0].getPos()) {
			case 0 -> {
				cursor[1].setSize(items.size());
				buy();
			}
			case 1 -> {
				cursor[1].setSize(status.getItemBag().size());
				sell();
			}
			case 2 -> {
				close();	
			}
		}
	}

	private void buy() {
		
		switch(current) {
			case 0 -> {
				sleep();
				window.setting("何をお買い上げになりますか？");
				current++;
			}
			case 1 -> {
				sleep();
				if(status.isMax()) {
					window.setting("持ち物がいっぱいのようです。　売るか捨てるかして下さい。　他にご用はありますか？");
					close();
				}
				else if(money > items.get(cursor[current].getPos()).getPrice()) {	
					tradeItem = items.get(cursor[current].getPos());
					window.setting(tradeItem.getName() + "ですね！　" + tradeItem.getName() + "なら" + tradeItem.getPrice() + "Cになります！\\nお買い上げになりますか？");
					current++;
				}
				else {
					window.setting("残念ですがCが足りないようですね。　他にご用はありますか？");
					close();
				}
			}
			case 2 -> {
				sleep();
				if(cursor[current].getPos() == 0) {
					sound.soundEffectStart(SoundEffect.DECISION);
					status.addItemBag(tradeItem);
					money -= tradeItem.getPrice();
					window.setting("お買い上げありがとうございます。　他にご用はありますか？");
					init();
				}
				else if(cursor[current].getPos() == 1) {
					sound.soundEffectStart(SoundEffect.CANCEL);
					window.setting("残念です。　他にご用はありますか？");
					init();
				}
			}
		}
		changeColor();
	}

	private void sell() {

		switch(current) {
			case 0 -> {
				sleep();
				window.setting("何を売ってくれますか？");
				menu[2].setSize(220, status.getItemBag().size() * FONT_HEIGHT + 15);
				current++;
			}
			case 1 -> {
				sleep();
				if(status.bagSize() > 0) {
					tradeItem = status.getItemBag().get(cursor[current].getPos());
					if(tradeItem.isNowEquipment()) {
						window.setting("装備中のアイテムは売れません");
						close();
					}
					else {
						window.setting(tradeItem.getName() + "ですね！　" + tradeItem.getName() + "なら　" + tradeItem.getSell() + "Cで買い取ります！\\n売ってくれますか？");
						current++;
					}
				}
			}
			case 2 -> {
				sleep();
				if(cursor[current].getPos() == 0) {
					sound.soundEffectStart(SoundEffect.DECISION);
					status.sellItem(cursor[1].getPos());
					money += tradeItem.getSell();
					window.setting("では" + tradeItem.getSell() + "Cで買い取りますね。　他にご用はありますか？");
					init();
				}
				else if(cursor[current].getPos() == 1) {
					sound.soundEffectStart(SoundEffect.CANCEL);
					window.setting("残念です。　他にご用はありますか？");
					init();
				}
			}
		}
	}

	@Override
	public void moveCursor(KeyCode key) {
		switch(key) {
			case UP -> {
				if(cursor[current].getPos() > 0) {
					cursor[current].locationUp();
				}
			}
			case DOWN -> {
				if(cursor[current].getPos() < cursor[current].getSize() - 1) {
					cursor[current].locationDown();
				}
			}
			case Z -> {
				if(window.nextMessage()) {
					select();
				}	
			}
			default -> {}
		}
	}

	@Override
	protected void revert() {		
		cursor[current].locationInit();
		current--;
		changeColor();
	}

	@Override
	protected void init() {
		Arrays.stream(cursor).forEach(v -> v.locationInit());
		current = 0;
		changeColor();
	}

	@Override
	public void close() {
		sound.soundEffectStart(SoundEffect.CANCEL);
		if(current == 0) {
			window.close();
			panel.getWindow().setRightclose();
			panel.getWindow().setting("ありがとうございました");
			panel.getWindow().open();
			isUsed = false;
			cursor[0].locationInit();
		}
		else {
			revert();
		}
	}
}
