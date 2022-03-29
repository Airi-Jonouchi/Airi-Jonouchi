package arpg.main;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
	
	public MainFrame() {
		setTitle("ARPG");
        setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		MainPanel panel = new MainPanel();
		add(panel);
		pack();
	}
	
	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setVisible(true);
	}
}
