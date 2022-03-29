package arpg.system.window;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;

public interface IVariableWindow extends IWindow {
	
	default void drawWindow(Rectangle window, int size, Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		ColorAndStroke cs = ColorAndStroke.WINDOW;

		if(size == 0) {
			size = 1;
		}
		g.setColor(cs.getWindowColor());
		g.fillRoundRect(
			window.x, window.y,window.width,
			window.height + (size * FONT_HEIGHT), cs.getArc(), cs.getArc()
		);

		g.setColor(cs.getFrameColor());
		g2.setStroke(cs.getStroke());
		g2.drawRoundRect(
			window.x, window.y, window.width, 
			window.height + (size * FONT_HEIGHT), cs.getArc(), cs.getArc()
		);
	}
}
