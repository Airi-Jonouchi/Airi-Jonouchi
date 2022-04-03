package arpg.personae;

import arpg.base.event.map.WorldMapOP;
import arpg.base.map.GameMap;
import arpg.base.map.WorldMap.WorldIcon;
import arpg.main.MainPanel;
import arpg.main.ReadXml;
import arpg.main.Common.Direction;
import arpg.main.Common.MoveType;

public class HeroCursor extends Npc {

	private ReadXml readXml;

	public HeroCursor(String name, int x, int y, int id, Direction direction, MoveType type, int limit, GameMap map) {
		super(name, x, y, id, direction, type, limit, map);
		readXml = new ReadXml();
	}

	public void warp(MainPanel panel, WorldIcon icon) {

		//MapDataPath path = MapDataPath.valueOf(icon.name());
		String id = readXml.readFile(icon.name()).getId();
		Hero hero = panel.getHero();
	
		String currentMapName = hero.getMap().getMapName();

		hero.getMap().removeCharacter(hero);
		if(hero.getMap().getCrystal() != null) {
			hero.getMap().getCrystal().close();
		}
		hero.getMap().init();
		panel.lordMap(id);
		if(currentMapName.equals(icon.getName())) {
			WorldMapOP op = (WorldMapOP)hero.getMap().eventCheck(hero.getX(), hero.getY());
			if(op != null) {
				hero.setX(op.getReverseX());
				hero.setY(op.getReverseY());
			}
			else {
				hero.setX(icon.getNewX(currentMapName));
				hero.setY(icon.getNewY(currentMapName));
			}
		}
		else {
			hero.setX(icon.getNewX(currentMapName));
			hero.setY(icon.getNewY(currentMapName));
		}
		switch(hero.getDirection()) {
			case UP -> hero.setDirection_2(Direction.DOWN);
			case DOWN -> hero.setDirection_2(Direction.UP);
			case RIGHT -> hero.setDirection_2(Direction.LEFT);
			case LEFT -> hero.setDirection_2(Direction.RIGHT);
		}
		if(panel.getSoundNumber() != map.getSoundNumber()) {
			panel.setBgm(panel.getSoundNumber());
		}
		panel.mapModeOff();
		panel.setOffset();
	}
}
