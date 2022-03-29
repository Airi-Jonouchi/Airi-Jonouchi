package arpg.ui;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import arpg.base.message.MessageWindow;
import arpg.main.MainPanel;
import arpg.main.Common.Command;
import arpg.personae.Hero;
import arpg.personae.operation.AttackMagicOperation;
import arpg.personae.operation.AttackOperation;
import arpg.prameter.status.HeroStatus;
import arpg.sound.Sound;
import arpg.sound.Sound.SoundEffect;
import arpg.system.window.IWindow;
import arpg.ui.cursor.Cursor;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;

public class CommandWindow implements IWindow {

	private static final Rectangle commandRect = new Rectangle(
		50, 100, 155, FONT_HEIGHT * (Command.values().length - 1) + 15
	);

	private AttackOperation aOperation;
	private AttackMagicOperation mOperation;
	private Command mode;
	private MessageWindow window;
	private Hero hero;
	private HeroStatus status;
	private Cursor[] cursor;
	private int current;
	private boolean isVisible;
	private Sound sound;

	public CommandWindow(MainPanel panel) {
		
		aOperation = panel.getHero().getAttackOperation();
		mOperation = panel.getHero().getAttackMagicOperation();

		window = panel.getWindow();
		hero = panel.getHero();
		status = panel.getHero().getStatus();
		mode = Command.NO_SELECT;

		cursor = new Cursor[] {
			new Cursor(new Point(3, ORIGIN), 0, BRIGHT, Command.values().length - 1),
			new Cursor(new Point(11, ORIGIN), 0, BRIGHT, 0),
			new Cursor(new Point(22, ORIGIN), 0, BRIGHT, 0),
			new Cursor(new Point(33, ORIGIN), 0, BRIGHT, 0)
		};

		sound = panel.getSound();
	}

	public void drawCommand(Graphics g) {

		if(isVisible) {
			drawWindow(commandRect, g);
			//g.setColor(LightAndDark.values()[cursor[0].getBrightness()].getColor());
			//g.setFont(FontAndColor.STANDARD.getFont());
			Color color = LightAndDark.values()[cursor[0].getBrightness()].getColor();
			IntStream.range(0, Command.values().length - 1).forEach(e -> drawString(Command.values()[e].getTitle(), commandRect.x + 40, commandRect.y + FONT_HEIGHT + (e * FONT_HEIGHT), color, g));
			
			if(current > 0) {
				status.drawMenu(LightAndDark.values()[cursor[1].getBrightness()].getColor(), g);
			}
			if(current > 1) {
				status.drawMenu2(LightAndDark.values()[cursor[2].getBrightness()].getColor(), g);
			}
			if(current > 2) {
				status.drawMenu3(LightAndDark.values()[cursor[3].getBrightness()].getColor(), g);
			}

			if(mode != Command.IMPORTANT && mode != Command.STATUS) {
				IntStream.range(0, current + 1).forEach(e -> {
					if(cursor[e].getSize() != 0) {
						g.setColor(LightAndDark.values()[cursor[e].getBrightness()].getColor());
						cursor[e].drawCursor(g);
					}
				});
			}
			else {
				//g.setColor(LightAndDark.values()[cursor[0].getBrightness()].getColor());
				g.setColor(color);
				cursor[0].drawCursor(g);
			}			
		}	
	}

	private void changeColor() {
		for(int i = 0; i <= current; i++) {
			if(i != current) {
				cursor[i].setBrightness(DARK);
			}
			else {
				cursor[current].setBrightness(BRIGHT);
			}
		}
	}

	public void moveCursor(KeyCode key, Hero hero) {

		switch(key) {
			case UP -> {
				if(cursor[current].getPos() > 0) {
					cursor[current].locationUp();
				}
			}
			case DOWN -> {
				if(cursor[current].getPos() < cursor[current].getSize() - 1) {
					cursor[current].locationDown();
				}
			}
			case Z -> {
				this.hero = hero;
				select();
			}
			default -> {}
		}
	}

	private void sleep() {
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	private void select() {
		switch(current) {
			case 0 -> {
				mode = Command.values()[cursor[current].getPos()];
				current++;
				status.setMenuSize(mode, current);
			}
			case 1 -> {
				sleep();
				switch(mode) {
					case MAGIC, EQUIPMENT -> current++;	
					case ITEM -> {
						if(cursor[current].getPos() == status.getMeneSize(current) - 1) {
							status.tidyUp();
						}
						else {
							current++;
						}
					}
					default -> {}
				}
				sound.soundEffectStart(SoundEffect.BOTTUN);
				status.setPos(cursor[1].getPos());
				status.setMenuSize(mode, current);
			}
			case 2 -> {
				switch(mode) {
					case ITEM -> {
						sleep();
						if(cursor[current].getPos() == 0) {
							sound.soundEffectStart(SoundEffect.DECISION);
							status.useItem(cursor[1].getPos());
							if(cursor[1].getPos() == status.bagSize()) {
								cursor[1].locationUp();
							}
						}
						else if(cursor[current].getPos() == 1) {
							sound.soundEffectStart(SoundEffect.DECISION);
							if(status.removeItem(cursor[1].getPos())) {
								if(cursor[1].getPos() == status.bagSize()) {
									cursor[1].locationUp();
								}
							}
						}
						current--;
						if(cursor[1].getPos() < 0) {
							cursor[1].locationInit();
						}
						window.setting(status.getMessage());
						window.open();
					}
					case MAGIC -> {
						sleep();
						if(cursor[current].getPos() == 0) {
							if(hero.noEnemy()) {
								status.useCommandMagic(cursor[1].getPos(), mOperation);
								current--;
							}
							else {
								if(hero.getPowerGauge() < 100) {
									sound.soundEffectStart(SoundEffect.CANCEL);
									status.setMessage("敵が近くにいる場合は　チャージが完了していないと使えません");
									current--;
								}
								else {
									status.useCommandMagic(cursor[1].getPos(), mOperation);
									current--;
									hero.setPowerGauge(0);
								}
							}	
						}
						
						if(cursor[1].getPos() < 0) {
							sound.soundEffectStart(SoundEffect.CANCEL);
							cursor[1].locationInit();
						}
						window.setting(status.getMessage());
						window.open();
					}
					case EQUIPMENT -> {	
						sleep();
						sound.soundEffectStart(SoundEffect.BOTTUN);	
						current++;
						status.setMenuSize(mode, current);
					}
					default -> {}
				}
			}
			case 3 -> {
				if(cursor[current].getPos() == 0) {
					sound.soundEffectStart(SoundEffect.DECISION);
					status.setEquipment(cursor[1].getPos(), cursor[2].getPos(), hero, aOperation);
				}
				else if(cursor[current].getPos() == 1) {
					sound.soundEffectStart(SoundEffect.DECISION);
					status.removeEquipment(cursor[1].getPos(), cursor[2].getPos(), aOperation);
				}
				cursor[2].locationInit();
				current -= 2;
				window.setting(status.getMessage());
				window.open();
			}
		}
		if(mode != Command.STATUS) {
			if(status.getMeneSize(current) == 0) {
				current--;
			}
		}
		cursor[current].setSize(status.getMeneSize(current)); 
		changeColor();	
	}

	private void revert() {		
		cursor[current].locationInit();
		current--;
		changeColor();
	}

	public boolean isVisible() {
		return this.isVisible;
	}

	public void open() {
		this.isVisible = true;
	}

	public void close() {
		if(current == 0) {
			this.isVisible = false;
			hero.charge();
		}
		else {
			revert();
		}
	}
}
