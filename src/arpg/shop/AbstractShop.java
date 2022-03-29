package arpg.shop;

import java.util.concurrent.TimeUnit;

import java.awt.Graphics;

import arpg.base.map.GameMap;
import arpg.base.message.MessageWindow;
import arpg.main.MainPanel;
import arpg.main.Common.KeyCode;
import arpg.personae.Hero;
import arpg.prameter.status.HeroStatus;
import arpg.sound.Sound;
import arpg.system.window.IWindow;


public abstract class AbstractShop implements IWindow {

	public enum ShopGenru {
		INN, ITEM, CHURCH
	}
	protected int x;
	protected int y;
	protected ShopGenru genru;
	protected Hero hero;
	protected HeroStatus status;
	protected MainPanel panel;
	protected boolean isUsed;
	protected MessageWindow window;
	protected Sound sound;
	protected GameMap map;
	protected int current;
	protected boolean isAction;

	public AbstractShop(int x, int y, MainPanel panel) {
		this.x = x;
		this.y = y;
		this.panel = panel;
		hero = panel.getHero();
		status = hero.getStatus();
		sound = panel.getSound();
		map = panel.getCurrentMap();
		window = new MessageWindow(sound, panel);
	}

	
	public abstract void draw(Graphics g);
	public abstract void moveCursor(KeyCode key);
	public abstract void startingCustomerService();
	public abstract void close();

	protected abstract void select();
	protected abstract void revert();
	protected abstract void changeColor();
	protected abstract void init();

	protected void sleep() {
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public ShopGenru getGenru() {
		return this.genru;
	}

	public void setGenru(ShopGenru genru) {
		this.genru = genru;
	}

	public boolean isUsed() {
		return this.isUsed;
	}
}
