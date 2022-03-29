package arpg.ui.key;

public class ActionKey {

	public enum InputMode {
		NOMAL(0), DETECT_INTIAL_PRESS_ONLY(1);

		private InputMode() {
		}

		private int code;
		private InputMode(int code) {
			this.code = code;
		}

		public int getCode() {
			return this.code;
		}
	}

	private enum KeyState {
		RELEASED(0), PRESSED(1), WAITING_FOR_RELEASE(2);

		private int code;
		private KeyState(int code) {
			this.code = code;
		}
	}

	private int mode;
	private int amount;
	private int state;

	public ActionKey() {
		this(InputMode.NOMAL);
	}

	public ActionKey(InputMode inputMode) {
		this.mode = inputMode.code;
		reset();
	}

	public void reset() {
		state = KeyState.RELEASED.code;
		amount = 0;
	}

	public void press() {
		if(state != KeyState.WAITING_FOR_RELEASE.code) {
			amount++;
			state = KeyState.PRESSED.code;
		}
	}

	public void release() {
		state = KeyState.RELEASED.code;
	}

	public boolean isPressed() {
		if(amount != 0) {
			if(state == KeyState.RELEASED.code) {
				amount = 0;
			}
			else if(mode == InputMode.DETECT_INTIAL_PRESS_ONLY.code) {
				state = KeyState.WAITING_FOR_RELEASE.code;
				amount = 0;
			}
			return true;
		}
		return false;
	}
}