package arpg.shop;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Rectangle;
import java.util.stream.IntStream;

import arpg.base.item.Item;
import arpg.main.MainPanel;
import arpg.main.Test;
import arpg.main.Common.KeyCode;
import arpg.sound.Sound.SoundEffect;
import arpg.ui.cursor.Cursor;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;

public class Custody extends AbstractShop {

	private class Deposit {
		private Item item;
		private int count;
		
		private Deposit(Item item, int count) {
			this.item = item;
			this.count = count;
		}
	}

	private static final int PAGE_SIZE = 8;
	private static final List<Deposit> DEPOSIT_LIST = new ArrayList<>();
	private static final String[] COMMAND = {"預ける", "引取る", "用はない"};

	private Cursor[] cursor;
	private Rectangle[] menu;
	private boolean check;
	private Item depositItem;
	private Deposit pickUp;
	private int startPosition;
	private int endPosition;
	private int page;

	public Custody(int x, int y, MainPanel panel) {
		super(x, y, panel);
		
		itemSet(33);
		cursor = new Cursor[] {
			new Cursor(new Point(3, ORIGIN), 0, BRIGHT, 3),
			new Cursor(new Point(10, ORIGIN), 0, BRIGHT, 0),
			new Cursor(new Point(21, ORIGIN), 0, BRIGHT, 2, 5),
			new Cursor(new Point(23, ORIGIN), 0, BRIGHT, 2, 5)
		};

		menu = new Rectangle[] {
			new Rectangle(50, 100, 135, 100),
			new Rectangle(190, 100, 220, status.getItemBag().size() * FONT_HEIGHT + 15),
			new Rectangle(190, 100, 260, setDepositWindowSize(true) * FONT_HEIGHT + 15),
			new Rectangle(415, 100, 120, 75),
			new Rectangle(455, 100, 120, 75)
		};
	}

	private int setDepositWindowSize(boolean isCursor) {

		if(DEPOSIT_LIST.size() > PAGE_SIZE) {
			if(isCursor) {
				return PAGE_SIZE + 1;
			}
			else {
				return PAGE_SIZE;
			}
		}
		else {
			return DEPOSIT_LIST.size();
		}
	}

	@Test
	private void itemSet(int set) {

		for(int i = 1; i <= set; i++) {
			DEPOSIT_LIST.add(new Deposit(Item.getReference("アイテム" + i), 1));
		}
	}

	@Override
	protected void select() {
		
		switch(cursor[0].getPos()) {
			case 0 -> {
				cursor[1].setSize(status.getItemBag().size());
				depositItem();
			}
			case 1 -> {
				if(current == 0) {
					endPosition = setDepositWindowSize(false);
				}
				cursor[1].setSize(setDepositWindowSize(true));
				PickUpItem();
			}
			case 2 -> {
				close();	
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
	public void draw(Graphics g) {
		
		if(isUsed) {
			drawWindow(menu[0], g);
			g.setColor(LightAndDark.values()[cursor[0].getBrightness()].getColor());
			g.setFont(FontAndColor.STANDARD.getFont());

			IntStream.range(0, COMMAND.length).forEach(e -> g.drawString(COMMAND[e], menu[0].x + 40, menu[0].y + FONT_HEIGHT + (e * FONT_HEIGHT)));
			cursor[0].drawCursor(g);

			if(current > 0) {

				int dx = 230;
				int dy = 130;
				
				if(cursor[0].getPos() == 0) {

					if(status.getItemBag().size() > 0) {
						drawWindow(menu[1], g);
						IntStream.range(0, status.getItemBag().size()).forEach(e -> {
							status.getItemBag().get(e).drawIcon(dx, dy + e * FONT_HEIGHT - 18, g);
							drawString(status.getItemBag().get(e).getName(), dx + ICON_SIZE, dy + e * FONT_HEIGHT, g);
						});
						cursor[1].drawCursor(g);
					}
				}
				else if(cursor[0].getPos() == 1) {

					if(DEPOSIT_LIST.size() > 0) {
						drawWindow(menu[2], g);
						IntStream.range(startPosition, endPosition).forEach(e -> {
							DEPOSIT_LIST.get(e).item.drawIcon(dx, dy + (e % PAGE_SIZE) * FONT_HEIGHT - 18, g);
							drawString(DEPOSIT_LIST.get(e).item.getName(), dx + ICON_SIZE, dy + (e % PAGE_SIZE) * FONT_HEIGHT, g);
							drawString(String.valueOf(DEPOSIT_LIST.get(e).count), dx + ICON_SIZE + (FONT_WIDTH *  9), dy + (e % PAGE_SIZE) * FONT_HEIGHT, g);
						});
						if(DEPOSIT_LIST.size() > endPosition) {
							drawString("次へ", dx + ICON_SIZE, dy + PAGE_SIZE * FONT_HEIGHT, g);
						}
						cursor[1].drawCursor(g);
					}
				}
			}
			if(current > 1) {
				if(cursor[0].getPos() == 0) {
					int dx = 455;
					int dy = 130;
					drawWindow(menu[3], g);
					drawString("はい", dx, dy, g);
					drawString("いいえ", dx, dy + FONT_HEIGHT, g);
					cursor[2].drawCursor(g);					
				}
				else if(cursor[0].getPos() == 1) {
					int dx = 495;
					int dy = 130;
					drawWindow(menu[4], g);
					drawString("はい", dx, dy, g);
					drawString("いいえ", dx, dy + FONT_HEIGHT, g);
					cursor[3].drawCursor(g);
				}
			}
		}
		window.drawMessage(FontAndColor.STANDARD, g);
	}
	
	private void pushNext() {

		startPosition += PAGE_SIZE;
		
		if(startPosition + PAGE_SIZE >= DEPOSIT_LIST.size()) {
			endPosition = DEPOSIT_LIST.size();
			int size = endPosition - startPosition;
			menu[2].setSize(260, size * FONT_HEIGHT + 15);
			cursor[current].setSize(size);
		}
		else {
			endPosition = startPosition + PAGE_SIZE;	
		}
		cursor[current].locationInit();
		page++;
	}

	private void depositItem() {
		
		switch(current) {
			case 0 -> {
				sleep();
				if(status.getItemBag().size() == 0) {
					window.setting("何もお持ちではないようですね");
					init();
				}
				window.setting("どちらをお預かりしましょうか？");
				current++;
			}
			case 1 -> {
				sleep();
				depositItem = status.getItemBag().get(cursor[current].getPos());
				window.setting(depositItem.getName() + "ですね！　こちらをお預かりしましょうか？");
				current++;			
			}
			case 2 -> {
				if(cursor[current].getPos() == 0) {
					DEPOSIT_LIST.forEach(v -> {
						if(v.item.getName().equals(depositItem.getName())) {
							v.count++;
							check = true;
						}
					});
					if(check == false ) {
						DEPOSIT_LIST.add(new Deposit(depositItem, 1));
						menu[2].setSize(260, DEPOSIT_LIST.size() * FONT_HEIGHT + 15);
						endPosition++;
					}
					status.sellItem(cursor[1].getPos());
					window.setting("では　" + depositItem.getName() + "を　お預かりしますね　他にご用はありますか？");
					init();
				}
				else if(cursor[current].getPos() == 1) {
					window.setting("残念です　他にご用はありますか？");
					init();
				}
			}
		}
	}

	private void PickUpItem() {

		switch(current) {

			case 0 -> {
				sleep();
				if(DEPOSIT_LIST.size() == 0) {
					window.setting("現在　お客様から預かっている　アイテムはありません");
					init();
				}
				else {
					window.setting("どちらを　お引取りになりますか？");
					current++;
				}	
			}
			case 1 -> {
				sleep();

				if(status.isMax()) {
					window.setting("どうやら　荷物がいっぱいのようですね\f何かお預かりしましょうか？");
					check = true;
					current = 3;
				}
				else if(cursor[1].getPos() == PAGE_SIZE) {
					pushNext();
				}	
				else {
					pickUp = DEPOSIT_LIST.get(cursor[current].getPos() + page * PAGE_SIZE);
					window.setting(pickUp.item.getName() + "ですね　こちらをお引取になりますか？");
					current = 3;
				}
			}
			case 3 -> {
				sleep();
				if(cursor[current].getPos() == 0) {
					if(check) {
						init();
						depositItem();
					}
					else {
						if(pickUp.count > 1) {
							DEPOSIT_LIST.get(cursor[1].getPos() + page * PAGE_SIZE).count--;
						}
						else {
							DEPOSIT_LIST.remove(cursor[1].getPos() + page * PAGE_SIZE);
							menu[2].setSize(260, DEPOSIT_LIST.size() * FONT_HEIGHT + 15);
							endPosition--;
						}
						status.addItemBag(pickUp.item);
						window.setting(pickUp.item.getName() + "を　お渡ししますね　他にご用はありますか？");
						init();
					}	
				}
				else {
					window.setting("残念です　他にご用はありますか？");
					init();
				}
			}
		}	
	}

	@Override
	public void startingCustomerService() {	
		window.setting("ようこそ預かり所へ！　何かご用はございますか？");
		window.open();
		isUsed = true;
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

	@Override
	protected void init() {
		Arrays.stream(cursor).forEach(v -> v.locationInit());
		menu[2].setSize(260, setDepositWindowSize(true) * FONT_HEIGHT + 15);
		current = 0;
		page = 0;
		startPosition = 0;
		endPosition = setDepositWindowSize(false);
		check = false;
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

	@Override
	protected void revert() {		
		cursor[current].locationInit();
		page = 0;
		startPosition = 0;
		menu[2].setSize(260, setDepositWindowSize(true) * FONT_HEIGHT + 15);
		endPosition = setDepositWindowSize(false);
		if(cursor[0].getPos() == 1 && current == 3) {
			current = 1;
		}
		else {
			current--;
		}
		changeColor();
	}
}
