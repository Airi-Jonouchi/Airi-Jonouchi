package arpg.base.message.option;

import static arpg.main.Common.*;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.stream.IntStream;

import arpg.base.message.MessageWindow;
import arpg.ui.cursor.Cursor;
import arpg.system.window.IWindow;

public class Dialog implements IWindow {

	private static final int FONT_WIDTH = 18;
	private static final int FONT_HEIGHT = 30;

	private String[] texts;
	private Rectangle dialg;
	private int width;
	private int height;

	private Cursor cursor;

	private void setSize() {
		
		height = texts.length;
		for(String text : texts) {
			if(width < text.length()) {
				width = text.length();
			}
		}
	}

	public void drawDialog(Graphics g) {
		int dx = dialg.x + 30;
		int dy = dialg.y + 30;
		drawWindow(dialg, g);
		IntStream.range(0, height).forEach(e -> g.drawString(texts[e], dx, dy + e * FONT_HEIGHT));
		cursor.drawCursor(g);
	}

	private String dialogDefaultMessage(int index) {
		
		String[] data = {
			"はい,いいえ",
			"はい,いいえ,どちらでもない"
		};
		return data[index];
	}

	private String answerMessage(int index) {
		String[] data = {
			"「はい」を選びました,「いいえ」を選びました",
			"兵士「ここは開発島！　GAMEを開発している島だ\\nGAMEの様々な機能をここでテストしている,兵士「モンスターには気をつけろよ！,兵士「おかしなやつだ",
			"兵士「よく言った,\\d兵士「本当にそうか？@0@2"
		};
		return data[index];
	}

	public void setDialog(int index) {
		String data = dialogDefaultMessage(index);
		this.texts = data.split(",");
		setSize();
		cursor = new Cursor(new Point(4, ORIGIN), 0, BRIGHT, height);
		dialg = new Rectangle(50, 100, FONT_WIDTH * width + 40, FONT_HEIGHT * height + 15);
	}

	private void setMessage(int index, MessageWindow window) {
		String answer = answerMessage(index).split(",")[cursor.getPos()];
		window.setting(answer);
	}

	public void moveCursor(KeyCode key, int index, MessageWindow window) {

		switch(key) {
			case UP -> {
				if(cursor.getPos() > 0) {
					cursor.locationUp();
				}
			}
			case DOWN -> {
				if(cursor.getPos() < cursor.getSize() - 1) {
					cursor.locationDown();
				}
			}
			case Z -> {
				window.setSelecter(false);
				setMessage(index, window);	
			}
			default -> {}
		}
	}
}
