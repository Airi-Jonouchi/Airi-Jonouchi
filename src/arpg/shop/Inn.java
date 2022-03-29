package arpg.shop;



import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import arpg.main.MainPanel;
import arpg.sound.Sound.SoundEffect;
import arpg.ui.cursor.Cursor;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;
import static arpg.main.MainPanel.*;

public class Inn extends AbstractShop {

	private static final Rectangle MENU = new Rectangle(50, 100, 120, 75);
	private static final String[] SELECT = {"泊まる", "やめる"};
	
	private int price;
	private int transparency;
	private boolean isSleep;
	private Cursor cursor;
	private int soundNumber;

	public Inn(int x, int y, MainPanel panel, int price, int soundNumber) {
		super(x, y, panel);
		this.price = price;
		transparency = 255;
		cursor = new Cursor(new Point(3, ORIGIN), 0, BRIGHT, 2);
		this.soundNumber = soundNumber;
	}

	@Override
	public void draw(Graphics g) {
		
		if(isUsed && !isSleep) {

			drawWindow(MENU, g);
			
			IntStream.range(0, SELECT.length).forEach(v -> drawString(SELECT[v], MENU.x + 40, MENU.y + FONT_HEIGHT + (v * FONT_HEIGHT), g));
			cursor.drawCursor(g);
		}
		if(!isSleep) {
			window.drawMessage(FontAndColor.STANDARD, g);
		}
		else {
			g.setColor(new Color(0 , 0 , 0 , transparency));
			g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
		}
	}

	public void startingCustomerService() {
		window.setting("ようこそ旅の宿へ！　一泊" + price + "Cでお泊まりになりますか？");
		window.open();
		isUsed = true;
	}

	@Override
	protected void select() {
		
		if(cursor.getPos() == 0) {
			stay();
		}
		else if(cursor.getPos() == 1) {
			window.close();
			isUsed = false;
			cursor.locationUp();
			panel.getWindow().setRightclose();
			panel.getWindow().setting("またのご利用をおまちしております");
			panel.getWindow().open();
		}
	}

	private void stay() {
		
		if(current == 0) {
			if(money > price) {
				window.setPointer(true);
				window.setting("ありがとうございます！　ごゆっくりとおくつろぎ下さい");		
				money -= price;
				transparency = 255;
				panel.setBgm(-1);
				panel.jukebox();
				current++;	
			}
			else {
				window.setting("Cが足りないようですね。またのご利用をお待ちしております");
			}
		}
		else if(current == 1) {
			sleeping();
		}
	}

	@Override
	public void moveCursor(KeyCode key) {

		if(!isSleep) {
			switch(key) {
				case UP -> {
					if(cursor.getPos() == 1) {
						cursor.locationUp();
					}
				}
				case DOWN -> {
					if(cursor.getPos() == 0) {
						cursor.locationDown();
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
	}

	private void sleeping() {

		isSleep = true;
		sound.soundEffectStart(SoundEffect.INN);
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(() -> {
			
			if(transparency > 0) {
				transparency--;
			}
			if(transparency == 0) {			
				isSleep = false;
				service.shutdown();
				status.setHp(status.getMaxHp());
				status.setMp(status.getMaxMp());
				window.setPointer(false);
				window.setting("おはようございます！　良い旅を");
				panel.setBgm(soundNumber);
				current = 0;
				cursor.locationDown();
			}
		}, 0, 30, TimeUnit.MILLISECONDS);
	}

	@Override
	public void close() {}

	@Override
	protected void revert() {}

	@Override
	protected void changeColor() {}

	@Override
	protected void init() {}
}
