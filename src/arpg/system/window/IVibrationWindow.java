package arpg.system.window;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import static arpg.main.Common.*;

public interface IVibrationWindow extends IWindow {
	
	default void drawWindow(Rectangle window, int vibration, Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		ColorAndStroke cs = ColorAndStroke.WINDOW;

		g.setColor(cs.getWindowColor());
		g.fillRoundRect(window.x  - vibration, window.y + vibration, window.width, window.height, cs.getArc(), cs.getArc());

		g.setColor(cs.getFrameColor());
		g2.setStroke(cs.getStroke());
		g.drawRoundRect(window.x  - vibration, window.y  + vibration, window.width, window.height, cs.getArc(), cs.getArc());
	}
}
