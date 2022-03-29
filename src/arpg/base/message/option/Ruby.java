package arpg.base.message.option;

import java.awt.Graphics;
import java.awt.Graphics2D;

import static arpg.main.Common.FontOption.*;

public class Ruby {
	private String[] ruby;
	private String text;
	private boolean isVisible;

	public Ruby(String ruby, String text) {
		this.ruby = ruby.split("");
		this.text = text;
	}

	public void drawRuby(float x, float y, Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		int textWidth = FONT_WIDTH * text.length();
		int rubyTextWidth = RUBY_WIDTH * ruby.length;
		int space = textWidth - rubyTextWidth;
		float part = space / (ruby.length + 1);

		for(int i = 0; i < ruby.length; i++) {
			float dx = x + (part * (i + 1)) + RUBY_WIDTH * i;
			float dy = y - RUBY_HEIGHT;
			g2.setColor(FontAndColor.RUBY_OUT_LINE.getColor());
			g2.setFont(FontAndColor.RUBY_OUT_LINE.getFont());
			g2.drawString(ruby[i], dx, dy);

			g2.setColor(FontAndColor.RUBY.getColor());
			g2.setFont(FontAndColor.RUBY.getFont());
			g2.drawString(ruby[i], dx, dy);
		}
	}

	public void rubyOn() {
		isVisible = true;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void rubyOff() {
		isVisible = false;
	}
}
