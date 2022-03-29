package arpg.personae;

import arpg.base.map.GameMap;
import arpg.base.map.WorldMap.WorldIcon;
import arpg.main.MainPanel;
import arpg.main.Common.Direction;
import arpg.main.Common.MapDataPath;
import arpg.main.Common.MoveType;

public class HeroCursor extends Npc {

	public HeroCursor(String name, int x, int y, int id, Direction direction, MoveType type, int limit, GameMap map) {
		super(name, x, y, id, direction, type, limit, map);
		
	}

	public void warp(MainPanel panel, WorldIcon icon) {

		MapDataPath path = MapDataPath.valueOf(icon.name());
		Hero hero = panel.getHero();

		hero.getMap().removeCharacter(hero);
		if(hero.getMap().getCrystal() != null) {
			hero.getMap().getCrystal().close();
		}
		hero.getMap().init();
		panel.lordMap(path);
		hero.setX(icon.getNewX());
		hero.setY(icon.getNewY());
		hero.setDirection_2(Direction.DOWN);
		if(panel.getSoundNumber() != map.getSoundNumber()) {
			panel.setBgm(panel.getSoundNumber());
		}
		panel.mapModeOff();
		panel.setOffset();
	}
	
}
