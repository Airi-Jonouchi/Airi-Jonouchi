package arpg.base.message;

public class Talk implements Cloneable {
	
	private int flag;
	private String line;
	private int[] dialogMsgIndex;

	public Talk(int flag, String line) {
		this.flag = flag;
		this.line = line;
	}

	public int getFlag() {
		return this.flag;
	}

	public String getLine() {
		return this.line;
	}

	public int[] getDialogMsgIndex() {
		return this.dialogMsgIndex;
	}
}