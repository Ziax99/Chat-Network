package Mypackage;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;

public class WindowDestroyer extends WindowAdapter {

	Client c;
	JFrame frame;

	public WindowDestroyer(Client c, JFrame frame) {
		this.c = c;
		this.frame = frame;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void windowClosing(WindowEvent e) {
		try {
			c.chatScreen.send("all: BYE");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
