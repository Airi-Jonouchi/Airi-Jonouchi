package arpg.personae;

import static arpg.main.Common.*;
import static arpg.main.MainPanel.*;

public class CharacterSet {

	private String name;
	private int x;
	private int y;
	private MoveType type;
	private int flag;
	
	public CharacterSet(String name, int x, int y, MoveType type, int flag) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.flag = flag;
		this.name = name;
	}

	public void setNew(AbstractCharacter chara) {

		if(gameFlag >= flag && name.equals(chara.getName())) {
			chara.setX(x);
			chara.setY(y);
			chara.setType(type);
		}
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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

	public MoveType getType() {
		return this.type;
	}

	public void setType(MoveType type) {
		this.type = type;
	}

	public int getFlag() {
		return this.flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
}
