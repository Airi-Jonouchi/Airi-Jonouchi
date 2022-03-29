package arpg.base.event.story;

import static arpg.main.MainPanel.*;

import java.util.concurrent.ConcurrentMap;

import arpg.base.map.GameMap;
import arpg.main.MainPanel;
import arpg.main.Common.Direction;

public class Story {

	public enum MainEvent {
		OPENING_TELOP("../event/story/data/main/opening0.dat", 0),
		OPENING_A("../event/story/data/main/opening1.dat", 0),
		OPENING_B("../event/story/data/main/opening2.dat", 1),
		OPENING_C("../event/story/data/main/opening3.dat", 2),
		SEPO_NON_MOVE("../event/story/data/other/sepo_nonmove.dat", 2),
		OPENING_D("../event/story/data/main/opening4.dat", 3),
		;
		
		private String path;
		private int flag;
		private MainEvent(String path, int flag) {
			this.path = path;
			this.flag = flag;
		}

		public String getPath() {
			return path;
		}

		public  int getFlag() {
			return flag;
		}
	};
	
	private MainPanel panel;
	private GameMap currentMap;

	public Story(MainPanel panel) {
		this.panel = panel;	
	}

	public void lordMainEvent() {
		currentMap = panel.getCurrentMap();
		switch(gameFlag) {
			case 0 -> {
				if(currentMap.getMapName().equals("オープニングA")) {
					currentMap.lordStory(MainEvent.OPENING_TELOP);
					gameFlag++;	
				}
			}
			case 1 -> {
				if(currentMap.getMapName().equals("オープニングA")) {
					currentMap.lordStory(MainEvent.OPENING_A);
					gameFlag++;	
				}
			}
			case 2 -> {
				if(currentMap.getMapName().equals("オープニングB")) {
					currentMap.lordStory(MainEvent.OPENING_B);
					gameFlag++;	
				}
			}
			case 3 -> {
				if(currentMap.getMapName().equals("辺境の町セポ")) {
					panel.getHero().setId();
					currentMap.lordStory(MainEvent.OPENING_C);
					gameFlag++;	
				}
			}
			case 4 -> {
				if(currentMap.getMapName().equals("辺境の町セポ")) {
					if(panel.getHero().getY() > 15) {
						currentMap.lordStory(MainEvent.SEPO_NON_MOVE);
					}
				}
				if(currentMap.getMapName().equals("辺境の町セポ道場B1")) {
					currentMap.lordStory(MainEvent.OPENING_D);
					gameFlag++;	
				}
			}
			case 5 -> {
				
			}
		}
	}
}
