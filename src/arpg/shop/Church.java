package arpg.shop;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import arpg.main.MainPanel;
import arpg.sound.Sound.SoundEffect;
import arpg.system.DataOpration;
import arpg.system.DataOpration.SaveDataPath;
import arpg.ui.cursor.Cursor;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;
import static arpg.main.MainPanel.*;


public class Church extends AbstractShop {

	private static final String[] COMMAND = {"浄化", "記録の儀式", "用はない", "ロード"};

	private int price;
	private int motion;
	private Cursor[] cursor;
	private Rectangle[] menu;
	private DataOpration data;
	private boolean inProcess;
	private static BufferedImage image;

	public Church(int x, int y, MainPanel panel, int price) {
		super(x, y, panel);
		this.price = price;
		motion = -1;
		current = 0;
		data = new DataOpration(SaveDataPath.SAVE_1, this.panel);

		cursor = new Cursor[] {
			new Cursor(new Point(3, ORIGIN), 0, BRIGHT, 4),
			new Cursor(new Point(11, ORIGIN), 0, BRIGHT, 2),
			new Cursor(new Point(25, ORIGIN), 0, BRIGHT, 2)
		};

		menu = new Rectangle[]{
			new Rectangle(50, 100, 155, 100),
			new Rectangle(210, 100, 120, 75)
		};

		if(image == null) {
			image = panel.getHero().getStatus().getMagicEffect();
		}
	}

	@Override
	public void draw(Graphics g) {}

	public void draw(int offsetX, int offsetY, Graphics g) {
		if(isUsed) {
			
			drawWindow(menu[0], g);
			g.setColor(LightAndDark.values()[cursor[0].getBrightness()].getColor());
			g.setFont(FontAndColor.STANDARD.getFont());
			IntStream.range(0, COMMAND.length).forEach(e -> drawString(COMMAND[e], menu[0].x + 40, menu[0].y + FONT_HEIGHT + (e * FONT_HEIGHT), g));
			cursor[0].drawCursor(g);

			if(current > 0 && cursor[0].getPos() == 1) {
				int dx = 250;
				int dy = 130;
				drawWindow(menu[1], g);
				drawString("はい", dx, dy, g);
				drawString("いいえ", dx, dy + FONT_HEIGHT, g);
				cursor[1].drawCursor(g);
			}

			if(inProcess) {
				int sx = motion * CHIP_SIZE;
				int sy = 4 * CHIP_SIZE;
				int dx = hero.getPx() + offsetX;
				int dy = hero.getPy() + offsetY;
				g.drawImage(image, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, sx, sy, sx + CHIP_SIZE, sy + CHIP_SIZE, null);
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
		window.setting("ここは旅の教会です！　何かご用はございますか？");
		window.open();
		isUsed = true;
	}

	@Override
	protected void select() {
		
		switch(cursor[0].getPos()) {
			case 0 -> {
				purification();
			}
			case 1 -> {
				recordingData();
			}
			case 2 -> {
				close();	
			}
			case 3 -> {
				data.lord();
			}
		}
	}

	private void purification() {

		if(money > price) {
			if(!status.getCondition().isNomal()) {
				switch(current) {
					case 0 -> {
						window.setPointer(true);
						window.setting("体の浄化をお望みですね！\\n力を抜いてリラックスして下さい。\\n聖水の力で災いを取り除きましょう。");
						current++;
					}
					case 1 -> {
						inProcess = true;
						panel.setBgm(-1);
						panel.jukebox();

						sound.soundEffectStart(SoundEffect.CHURCH);
						sleep(2000);
						purificationEffect();
						init();
					}
				}
			}
			else {
				window.setting("健康体のようですね。　浄化の必要はありません。\\n他にご用はありますか？");
			}
		}
	}
 
	private void recordingData() {

		switch(current) {
			case 0 -> {
				sleep();
				window.setting("記録の儀式ですね！　今までの旅の思い出を冒険の書に記録しますか？");
				current++;
			}
			case 1 -> {
				sleep();
				if(cursor[1].getPos() == 0) {
					inProcess = true;
					data.save();
					sound.soundEffectStart(SoundEffect.SAVE);
					try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
					window.setting("記録は終わりました。　よい旅を！");
					init();
					inProcess = false;
				}
				else if(cursor[1].getPos() == 1){
					sound.soundEffectStart(SoundEffect.CANCEL);
					window.setting("わかりました。　記録しないでおきますね。\\n他にご用はありますか？");
					init();
				}
			}
			case 2 -> {
				
			}
		}
		changeColor();
	}

	@Override
	public void moveCursor(KeyCode key) {

		if(!inProcess) {
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
	}

	private void sleep(int time) {
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		}
		catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	private void purificationEffect() {

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(() -> {
			if(motion < 15) {
				motion++;
			}
			if(motion == 15) {
				service.shutdown();
				status.getCondition().cureAll();
				money -= price;
				panel.setBgm(0);
				window.setPointer(false);
				window.setting("浄化は終わりました。ご武運を");
				inProcess = false;
			}
		}, 0, 50, TimeUnit.MILLISECONDS);
	}

	public boolean isProcess() {
		return this.inProcess;
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

	public void close() {
		if(current == 0) {
			window.close();
			panel.getWindow().setRightclose();
			panel.getWindow().setting("旅のご無事をお祈りしております");
			panel.getWindow().open();
			isUsed = false;
			cursor[0].locationInit();
		}
		else {
			revert();
		}
	}
}
