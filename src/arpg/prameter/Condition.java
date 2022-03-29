package arpg.prameter;

public class Condition {

	private static final byte NORMAL = 0b0000;
	private static final byte POIZON = 0b0001;
	private static final byte ANTI_POIZON = 0b1110;
	private static final byte MAGIC_SEAL = 0b0010;
	private static final byte ANTI_MAGIC_SEAL = 0b1101;
	private static final byte PARALYSIS = 0b0100;
	private static final byte ANTI_PARALYSIS = 0b1011;
	private static final byte BRIGHTNESS = 0b1000;
	private static final byte ANTI_BRIGHTNESS = 0b0111;

	private byte state;

	public Condition() {
		state = NORMAL;
	}

	public void setPoizon() {
		 state |= POIZON;
	}

	public void curePoizon() {
		state &= ANTI_POIZON;
	}

	public boolean isPoizon() {
		int data = state & POIZON;
		if(data == 0) {
			return false;
		}
		return true;
	}

	public void setMagicSeal() {
		state |= MAGIC_SEAL;
	}

	public void cureMagicSeal() {
		state &= ANTI_MAGIC_SEAL;
	}

	public boolean isMagicSeal() {
		int data = state & MAGIC_SEAL;
		if(data == 0) {
			return false;
		}
		return true;
	}

	public void setParalysis() {
		state |= PARALYSIS;
	}

	public void cureParalysis() {
		state &= ANTI_PARALYSIS;
	}

	public boolean isParalysis() {
		int data = state & PARALYSIS;
		if(data == 0) {
			return false;
		}
		return true;
	}

	public void setBrightness() {
		state |= BRIGHTNESS;
	}

	public void cureBrightness() {
		state &= ANTI_BRIGHTNESS;
	}

	public boolean isBrightness() {
		int data = state & BRIGHTNESS;
		if(data == 0) {
			return false;
		}
		return true;
	}

	public void cureAll() {
		state = NORMAL;
	}

	public boolean isNomal() {
		if(state == NORMAL) {
			return true;
		}
		return false;
	}

	public byte getCharaCondition() {
		return this.state;
	}
}
