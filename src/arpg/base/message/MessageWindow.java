package arpg.base.message;

import static arpg.main.Common.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import arpg.base.message.option.Dialog;
import arpg.base.message.option.Ruby;
import arpg.main.MainPanel;
import arpg.sound.Sound;
import arpg.sound.Sound.SoundEffect;
import arpg.system.window.IVibrationWindow;

import static arpg.main.Common.FontOption.*;

public class MessageWindow implements IVibrationWindow {
	
	private static final Rectangle WINDOW = new Rectangle(140, 400, 500, 175);
	private static final int NO_DATA = -1;
	private static final FontAndColor OUT_LINE = FontAndColor.OUT_LINE;

	private static final int MAX_CHAR_IN_LINE = 24;
    private static final int MAX_LINE = 4;
    private static final int MAX_CHAR_IN_PAGE = MAX_CHAR_IN_LINE * MAX_LINE;
	private static final int NORMAL = 50;
	private static final int BATTLE = 20;

	private Dialog dialog;
	private char[] textData;
	private Map<Integer, Ruby> rubyMap;
	private int currentPos;
	private int startPos;
	private int currentPage;
	private boolean nextFlag;
	private boolean isVisible;
	private int count;
	private boolean isGhost;
	private boolean pointer;
	private Sound sound;
	private int messageSpeed;
	private int vibration;
	private boolean isVibration;
	private boolean selecter;
	private boolean autoMessage;
	private int[] index;
	private MainPanel panel;
	private boolean rightClose;
	private int soundEffect;
	
	public MessageWindow(Sound sound, MainPanel panel) {
		rubyMap = new HashMap<>();
		init();
		this.panel = panel;
		this.sound = sound;
		messageSpeed = NORMAL;
		dialog = new Dialog();
	}

	public MessageWindow() {
		rubyMap = new HashMap<>();
		init();
		messageSpeed = BATTLE;
	}

	public void drawMessage(FontAndColor fc, Graphics g) {

		if(!isVisible) {
			return;
		}
		Graphics2D g2 = (Graphics2D)g;
		drawWindow(WINDOW, vibration, g);
		
		g.setColor(fc.getColor());
		g.setFont(fc.getFont());

		for(int i = startPos; i < currentPos; i++) {
			String character = String.valueOf(textData[i]);
			float dx = WINDOW.x - vibration + 10 + FONT_WIDTH * (i % MAX_CHAR_IN_LINE);
			//float dy = WINDOW.y + vibration + FONT_HEIGHT + FONT_HEIGHT * ((i - startPos) / MAX_CHAR_IN_LINE);
			float dy = WINDOW.y + vibration + FONT_HEIGHT + FONT_HEIGHT * ((i - startPos) / MAX_CHAR_IN_LINE);

			g2.setColor(OUT_LINE.getColor());
			g2.setFont(OUT_LINE.getFont());
			g2.drawString(character, dx - 1.7f, dy + 1.1f);

			g2.setColor(fc.getColor());
			g2.setFont(fc.getFont());
			g2.drawString(character, dx, dy);

			if(rubyMap.size() > 0) {
				if(rubyMap.containsKey(i)) {
					rubyMap.get(i).drawRuby(dx, dy, g);
					g.setFont(fc.getFont());
				}
			}	
		}	

		if(nextFlag || pointer && currentPos == textData.length) {
			int dx = WINDOW.x + (WINDOW.width / 2);
			int dy = WINDOW.y + WINDOW.height - 18;
			g.drawString("▼", dx, dy);
		}

		if(selecter && currentPos == textData.length) {
			dialog.drawDialog(g);
		}
	}

	public void drawMessage(int px, int py, FontAndColor fc, Graphics g) {

		if(!isGhost) {
			return;
		}
		Graphics2D g2 = (Graphics2D)g;

		for(int i = startPos; i < currentPos; i++) {
			
			String character = String.valueOf(textData[i]);
			float dx = px - vibration + 10 + FONT_WIDTH * (i % MAX_CHAR_IN_LINE);
			float dy = py + vibration + FONT_HEIGHT + FONT_HEIGHT * ((i - startPos) / MAX_CHAR_IN_LINE);

			g2.setColor(OUT_LINE.getColor());
			g2.setFont(OUT_LINE.getFont());
			g2.drawString(character, dx - 0.6f, dy + 0.6f);

			g2.setColor(fc.getColor());
			g2.setFont(fc.getFont());
			g2.drawString(character, dx, dy);

			if(rubyMap.size() > 0) {
				if(rubyMap.containsKey(i)) {
					rubyMap.get(i).drawRuby(dx, dy, g);
					g.setFont(fc.getFont());
				}
			}
		}	

		if(selecter && currentPos == textData.length) {
			dialog.drawDialog(g);
		}
	}

	public void setting(String message) {
		
		StringBuilder sb = new StringBuilder();
		int pos = 0;
		count = 0;
		init();

		for(int i = 0; i < message.length(); i++) {
			char c = message.charAt(i);

			switch(c) {
				case '\\' -> {
					i++;
					sb.append(lineOption(message.charAt(i), pos));
					pos += count;
				}
				case '@' -> {
					i++;
					if(index[0] == NO_DATA) {
						index[0] = Character.getNumericValue(message.charAt(i));
					}
					else if(index[1] == NO_DATA) {
						index[1] = Character.getNumericValue(message.charAt(i));
					}
				}
				case '|' -> {
					i++;
					String ruby = "", text = "";
					int rubyPos = pos;
	
					while(message.charAt(i) != '《') {
						text += String.valueOf(message.charAt(i));
						sb.append(message.charAt(i));
						pos++;
						i++;
					}
					i++;
					while(message.charAt(i) != '》') {
						ruby += String.valueOf(message.charAt(i));
						i++;
					}
					addRuby(ruby, text, rubyPos);
				}
				case '#' -> {
					sb.append(panel.getHero().getName());
					pos += panel.getHero().getName().length();
				}
				default -> {
					sb.append(String.valueOf(c));
					pos++;
				}
			}
		}
		if(selecter) {
			dialog.setDialog(index[0]);
		}
		textData = sb.toString().toCharArray();
		slowlyMessage();
	}

	private String lineOption(char c, int pos) {
		switch(c) {
			case 'n' -> count = MAX_CHAR_IN_LINE - pos % MAX_CHAR_IN_LINE;
			case 'f' -> count = MAX_CHAR_IN_PAGE - pos % MAX_CHAR_IN_PAGE;
			case 'e' -> soundEffect = 1;
			case 'd' -> selecter = true;
		}
		String space = String.join("", Collections.nCopies(count, "　"));
		return space;
	}

	private void addRuby(String ruby, String text, int index) {
		rubyMap.put(index, new Ruby(ruby, text)); 
	}

	public void cursorMove(KeyCode key) {
		if(selecter) {		
			dialog.moveCursor(key, index[1], this);
		}
	}

	public boolean nextMessage() {
		
		if(currentPos == textData.length) {
			if(selecter) {
				return false;
			}
			soundEffect = 0;
			return true;
		}
		if(nextFlag) {
			currentPage++;
			startPos = currentPage * MAX_CHAR_IN_PAGE;
			nextFlag = false;	
		}
		return false;
	}

	public void init() {
		currentPos = 0;
		currentPage = 0;
		startPos = 0;
		nextFlag = false;
		textData = null;
		vibration = 0;
		index = new int[]{NO_DATA, NO_DATA};
		if(rubyMap != null) {
			rubyMap.clear();
		}
	}

	public void open() {
		this.isVisible = true;
	}

	public void close() {
		this.isVisible = false;
		if(rightClose) {
			rightClose = false;
		}
	}

	public boolean isVisible() {
		return this.isVisible;
	}

	public void ghostOn() {
		isGhost = true;
		autoMessage = true;
	}

	public void ghostOff() {
		isGhost = false;
		autoMessage = false;
	}

	public void autoModeOn() {
		autoMessage = true;
	}

	public void autoModeOff() {
		autoMessage = false;
	}

	public boolean isGhost() {
		return this.isGhost;
	}

	public boolean isAutoMode() {
		return this.autoMessage;
	}

	public boolean isPointer() {
		return this.pointer;
	}

	public void setPointer(boolean pointer) {
		this.pointer = pointer;
	}

	public void vibrationOff() {
		this.isVibration = false;
	}

	public void vibrationOn() {
		this.isVibration = true;
	}

	public Dialog getDialog() {
		return this.dialog;
	}

	public void setSelecter(boolean selecter) {
		this.selecter = selecter;
	}

	public boolean getSelecter() {
		return this.selecter;
	}

	public void setRightclose() {
		rightClose = true;
	}

	public boolean isRightClose() {
		return this.rightClose;
	}

	public void soundEffectChenge(int sex) {
		if(sex == 1) {
			soundEffect = 1;
		}
	}

	private void sleep(int time) {
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	private void slowlyMessage() {
	
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(() -> {
			if(!nextFlag) {

				if(currentPos == 0) {
					if(isVibration) {
						IntStream.range(0, 4).forEach(i -> {
							if(i % 2 == 0) {
								vibration = 8;
							}
							else {
								vibration = 0;
							}
							sleep(30);
						});
					}	
				}	
				if(textData[currentPos] != '　') {
					if(!isGhost) {
						if(messageSpeed == NORMAL) {
							if(soundEffect == 1) {
								sound.soundEffectStart(SoundEffect.MESSAGE_2);
							}
							else {
								sound.soundEffectStart(SoundEffect.MESSAGE);
							}
						}
					}
					currentPos++;
				}
				else {
					while(textData[currentPos] == '　') {
						currentPos++;
					}
				}	
			}
			
			if(currentPos % MAX_CHAR_IN_PAGE == 0) {	
                nextFlag = true;
				if(autoMessage) {
					messageWait();
				}
            }
			if(currentPos == textData.length) {
				scheduler.shutdown();
				if(autoMessage) {
					messageWait();
				}
			}
			
		}, 0, messageSpeed, TimeUnit.MILLISECONDS);	
	}

	private void messageWait() {
		try {
			TimeUnit.MILLISECONDS.sleep(3400);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
		if(nextMessage()) {
			autoMessage = false;
			if(isGhost) {
				isGhost = false;
			}
			else {
				isVisible = false;
			}
		}
	}
}
