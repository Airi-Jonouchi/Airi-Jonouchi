package arpg.system.window.String;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.stream.IntStream;

import static arpg.main.Common.FontOption.*;

public interface IDrawString {
	
	default void drawString(String message, int x, int y, Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		String[] chars = message.split("");

		int dy = y;
		IntStream.range(0, chars.length).forEach(e -> {

			g.setFont(FontAndColor.OUT_LINE.getFont());
			g.setColor(FontAndColor.OUT_LINE.getColor());
			int dx = x + FONT_WIDTH * e;
			g2.drawString(chars[e], dx - 1.7f, dy + 1.1f);

			g.setFont(FontAndColor.STANDARD.getFont());
			g.setColor(FontAndColor.STANDARD.getColor());
			g.drawString(chars[e], dx, dy);
		});	
	}

	default void drawString(String message, int x, int y, Color color, Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		String[] chars = message.split("");

		int dy = y;
		IntStream.range(0, chars.length).forEach(e -> {

			g.setFont(FontAndColor.OUT_LINE.getFont());
			g.setColor(FontAndColor.OUT_LINE.getColor());
			int dx = x + FONT_WIDTH * e;
			g2.drawString(chars[e], dx - 0.6f, dy + 0.6f);

			g.setFont(FontAndColor.STANDARD.getFont());
			g.setColor(color);
			g.drawString(chars[e], dx, dy);
		});	
	}
}
