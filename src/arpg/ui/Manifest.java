package arpg.ui;
import java.awt.Graphics;

import arpg.system.window.IWindow;
import static arpg.main.Common.FontOption.*;

import java.awt.Rectangle;
import java.util.stream.IntStream;

public class Manifest implements IWindow {

	private static final Rectangle MANIFEST_WINDOW = new Rectangle(100, 100, 600, 470);

	private static final String[] KEY_MENU = {
		"＜キー操作＞",
		"  矢印キー : キャラクター、カーソルの移動",
	 	"  SPACEキー  : 攻撃", 
	 	"  CONTROLキー : コマンドの表示/非表示",
		"  SHIFTキー : キャンセル",
		"  Zキー : 決定",
		"  Mキー : 地図の表示/非表示",
		"  Hキー : マニフェスト表示/非表示",
		""
	};

	private static final String[] COMMENT = {

		"＜施設について＞",
		"  宿屋 : 宿泊することでHPとMPが回復します", 
		"  道具屋 : アイテムの売買ができます",
	 	"  武器と防具屋 : 装備品の売買ができます",
		"  教会 : ステータス異常の回復や、データの保存ができます",
		"  預かり所 : アイテムを預けたり、引出したりできます"
	};

	public void drawManifestWindow(Graphics g) {

		int x = MANIFEST_WINDOW.x + 30;
		int y = MANIFEST_WINDOW.y + 30;
		int y2 = y + KEY_MENU.length * FONT_HEIGHT;
		drawWindow(MANIFEST_WINDOW, g);

		IntStream.range(0, KEY_MENU.length).forEach(e -> g.drawString(KEY_MENU[e], x, y + (e * FONT_HEIGHT)));
		IntStream.range(0, COMMENT.length).forEach(e -> g.drawString(COMMENT[e], x, y2 + (e * FONT_HEIGHT)));
	}
}
