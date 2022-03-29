package arpg.system;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import arpg.base.message.MessageWindow;
import arpg.system.window.IWindow;
import arpg.ui.cursor.Cursor2D;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;
import static arpg.main.MainPanel.*;

public class NameInput implements IWindow {

	private static final String[][] HIRAGANA = {
		{"あ","い","う","え","お", "ま","み","む","め","も", "だ","ぢ","づ","で","ど"},
		{"か","き","く","け","こ", "や","ー","ゆ","ー","よ", "ば","び","ぶ","べ","ぼ"},
		{"さ","し","す","せ","そ", "ら","り","る","れ","ろ", "ぱ","ぴ","ぷ","ぺ","ぽ"}, 
		{"た","ち","つ","て","と", "わ","ー","を","ー","ん", "ぁ","ぃ","ぅ","ぇ","ぉ"},
		{"な","に","ぬ","ね","の", "が","ぎ","ぐ","げ","ご", "っ","ゃ","ゅ","ょ","ー"}, 
		{"は","ひ","ふ","へ","ほ", "ざ","じ","ず","ぜ","ぞ", "カナ", "　", "戻る", "　", "完了"}
	};

	private static final String[][] KATAKANA = {
		{"ア","イ","ウ","エ","オ", "マ","ミ","ム","メ","モ", "ダ","ヂ","ヅ","デ","ド"},
		{"カ","キ","ク","ケ","コ", "ヤ","ー","ユ","ー","ヨ", "バ","ビ","ブ","ベ","ボ"}, 
		{"サ","シ","ス","セ","ソ", "ラ","リ","ル","レ","ロ", "パ","ピ","プ","ペ","ポ"},
		{"タ","チ","ツ","テ","ト", "ワ","ー","ヲ","ー","ン", "ァ","ィ","ゥ","ェ","ォ"}, 
		{"ナ","ニ","ヌ","ネ","ノ", "ガ","ギ","グ","ゲ","ゴ", "ッ","ャ","ュ","ョ","ヴ"},
		{"ハ","ヒ","フ","ヘ","ホ", "ザ","ジ","ズ","ゼ","ゾ", "かな", "　", "戻る", "　", "完了"}		
	};

	private static final int MARGIN_X = 30;
	private static final int MARGIN_Y = 70;

	private static final Rectangle WINDOW = new Rectangle(35, 150, 735, 235);
	private static final Rectangle NAME_WINDOW = new Rectangle(360, 100, 120, 35);

	private static final int COL = 15;
	private static final int ROW = 6;
	
	private String[] name;
	private String charaName;
	private String[][] viewCharacters;
	private Cursor2D cursor;
	private FontAndColor fc;
	private boolean nameInputMode;
	private MessageWindow window;

	public NameInput() {

		name = new String[]{"＊", "＊", "＊", "＊", "＊"};
		viewCharacters = HIRAGANA;
		cursor = new Cursor2D(new Point(3, 7), new Point(0, 0), BRIGHT, new Point(COL, ROW));
		fc = FontAndColor.STANDARD;
		window = new MessageWindow();
		charaName = "";
	}
	
	public void draw(Graphics g) {

		if(window.isVisible()) {
			window.drawMessage(fc, g);
		}

		g.setColor(Color.BLACK);
		drawWindow(WINDOW, g);
		
		g.setColor(fc.getColor());
		g.setFont(fc.getFont());

		drawWindow(NAME_WINDOW, g);

		for(int i = 0; i < name.length; i++) {
			int dx = NAME_WINDOW.x + 10 + (i * FONT_WIDTH);
			int dy = NAME_WINDOW.y + 25;
			g.drawString(name[i], dx, dy);
		}

		g.drawString("名前を入力して下さい", WINDOW.x + 280, WINDOW.y + 30);
		g.drawLine(WINDOW.x, WINDOW.y + 45, WINDOW.x + WINDOW.width, WINDOW.y + 45);

		for(int i = 0; i < viewCharacters.length; i++) {
			for(int j = 0; j < viewCharacters[i].length; j++) {
				String moji = viewCharacters[i][j];

				int space = switch (j) {
					case 5, 6, 7, 8, 9 -> 15;
					case 10, 11, 12, 13, 14 -> 30;
					default -> 0;
				};
				
				int dx = (WINDOW.x + MARGIN_X + space + (j * (FONT_WIDTH + 25)));
				int dy = (WINDOW.y + MARGIN_Y + (i * FONT_HEIGHT));
				g.drawString(moji, dx, dy);
			}
		}
		cursor.drawCursor(g);
	}

	private void input() {
		String inputChar = viewCharacters[cursor.getMenuPoint().y][cursor.getMenuPoint().x];
		
		switch (inputChar) {
			case "かな"-> viewCharacters = HIRAGANA;	
			case "カナ"-> viewCharacters = KATAKANA;	
			case "戻る" -> {
				for(int i = name.length -1; i >= 0; i--) {
					if(!name[i].equals("＊")) {
						name[i] = "＊";
						return;
					}
				}	
			}
			case "完了" -> {
				for(int i = 0; i < name.length; i++) {
					if(name[i].equals("＊")) {
						continue;
					}
					else {
						charaName += name[i];
					}
				}
				if(checkName()) {
					setHeroName(charaName);
					//nameInputMode = false; デバッグ用
				}
			}
			default -> {
				for(int i = 0; i < name.length; i++) {
					if(name[i].equals("＊")) {
						name[i] = inputChar;
						return;
					}
				}	
			}
		}
	}

	public boolean checkName() {

		if(charaName.matches("^[っゃゅょぁぃぅぇぉをんッャュョァィゥェォーヲン].+$") || charaName.matches("^(.)\\1+$") || charaName.equals("トーマス")) {
			window.setting("その名前はつけられません");
			window.open();
			charaName = "";
			return false;
		}
		else if(charaName.length() < 2) {
			window.setting("名前は2文字以上入力して下さい");
			window.open();
			charaName = "";
			return false;
		}
		else {
			return true;
		}
	}

	public String getName() {
		return charaName;
	}

	public void moveCursor(KeyCode key) {

		switch(key) {
			case UP -> {
				if(cursor.getMenuPoint().y > 0) {
					cursor.locationUp();
				}
			}
			case DOWN -> {
				if(cursor.getMenuPoint().y < cursor.getMenuSize().y - 1) {
					if(cursor.getMenuPoint().x == 11 && cursor.getMenuPoint().y == 4) {
						cursor.locationRight();
					}
					else if(cursor.getMenuPoint().x == 13 && cursor.getMenuPoint().y == 4) {
						cursor.locationRight();
					}
					cursor.locationDown();
				}
			}
			case RIGHT -> {
				if(cursor.getMenuPoint().x < cursor.getMenuSize().x -1) {
					if(cursor.getMenuPoint().x % 5 == 4) {
						cursor.shiftRight();
					}
					else if(cursor.getMenuPoint().x > 9 && cursor.getMenuPoint().y == cursor.getMenuSize().y -1) {
						cursor.locationRight();
					}
					cursor.locationRight();
				}
			}
			case LEFT -> {
				if(cursor.getMenuPoint().x > 0) {
					if(cursor.getMenuPoint().x % 5 == 0) {
						cursor.shiftLeft();
					}
					else if(cursor.getMenuPoint().x > 10 && cursor.getMenuPoint().y == cursor.getMenuSize().y -1) {
						cursor.locationLeft();
					}
					cursor.locationLeft();
				}
			}
			case Z -> {
				if(window.isVisible()) {
					if(window.nextMessage()) {
						window.close();
					}
				}
				else {
					input();
				}
			}
			default -> {}
		}
	}

	public boolean isNameInputMode() {
		return this.nameInputMode;
	}

	public void setNameInputMode(boolean nameInputMode) {
		this.nameInputMode = nameInputMode;
	}
}