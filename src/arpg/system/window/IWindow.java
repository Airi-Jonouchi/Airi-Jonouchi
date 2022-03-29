package arpg.system.window;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import arpg.system.window.String.IDrawString;

import static arpg.main.Common.*;

public interface IWindow extends IDrawString {

	default void drawWindow(Rectangle window, Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		ColorAndStroke cs = ColorAndStroke.WINDOW;

		g.setColor(cs.getWindowColor());
		g.fillRoundRect(window.x, window.y, window.width, window.height, cs.getArc(), cs.getArc());

		g.setColor(cs.getFrameColor());
		g2.setStroke(cs.getStroke());
		g.drawRoundRect(window.x, window.y, window.width, window.height, cs.getArc(), cs.getArc());
	}
}
	



