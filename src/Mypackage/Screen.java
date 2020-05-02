package Mypackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.desktop.PreferencesEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

public class Screen extends JFrame implements KeyListener {

	JPanel screen, panel, chat, container, chatp, right, top;
	JScrollPane scroll;
	TextArea ta;
	JButton enter, refresh;
	Client client;

	public static final Color bg = new Color(22, 25, 37);

	public Screen(Client client) { // client is server or client
		this.client = client;

		this.setTitle(client.name);

		screen = new JPanel();
		screen.setLayout(new FlowLayout(FlowLayout.LEFT));
		screen.setBackground(bg);
		screen.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		
		
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(bg);
		panel.setMaximumSize(new Dimension(970, 750));
		panel.setPreferredSize(new Dimension(970, 750));
		chat = createChat();
		panel.add(chat);
		screen.add(panel);

		container = new JPanel();
		container.setBackground(bg);
		container.setMaximumSize(new Dimension(970, 130));
		container.setLayout(new FlowLayout(FlowLayout.LEFT));
		container.setBorder(BorderFactory.createCompoundBorder());

		ta = new TextArea();
		ta.setPreferredSize(new Dimension(900, 50));
		ta.setMaximumSize(new Dimension(900, 50));
		ta.setBackground(bg);
		ta.setFont(new Font(Font.SERIF, 0, 18));
		ta.setForeground(Color.WHITE);
		ta.addKeyListener(this);

		container.add(ta);
		panel.add(container);

		enter = new JButton("Send");
		enter.setPreferredSize(new Dimension(50, 50));
		enter.setFocusable(false);
		enter.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		enter.addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Screen.this.send(ta.getText());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
		container.add(enter);
		
		
		right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.setBackground(bg);
		right.setMaximumSize(new Dimension(300, 750));
		right.setPreferredSize(new Dimension(250, 750));
		
		top = new JPanel();
		right.setLayout(new FlowLayout(FlowLayout.LEFT));
		refresh = new JButton("Refresh");
		refresh.setPreferredSize(new Dimension(50, 50));
		refresh.setFocusable(false);
		refresh.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		refresh.addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						
						right.removeAll();
						
						try {
							client.out.writeUTF("req");
						} catch (IOException e1) {}
					}
				});
		right.add(refresh);

		
		try {
			client.out.writeUTF("req");
		} catch (IOException e1) {}
		
		screen.add(right);
		

		addWindowListener(new WindowDestroyer(client, this));

		add(screen);
		setSize(1300, 800);
		setBackground(bg);
		setLocationRelativeTo(null);

		setVisible(true);


		container.revalidate();
		container.repaint();
		panel.revalidate();
		panel.repaint();
		revalidate();
		repaint();

	}

	public void send(String text) throws IOException 
	{

		client.out.writeUTF(client.name + '`' + text);
		client.out.flush();

		if(text.toUpperCase().endsWith("BYE"))
			System.exit(0);

		text = text.split(": ")[1];


		container.removeAll();

		ta = new TextArea();
		ta.setPreferredSize(new Dimension(900, 50));
		ta.setMaximumSize(new Dimension(900, 50));
		ta.setBackground(bg);
		ta.setFont(new Font(Font.SERIF, 0, 18));
		ta.setForeground(Color.WHITE);
		ta.addKeyListener(this);
		container.add(ta);
		container.add(enter);


		JLabel label = new JLabel("<html>" + client.name + ": " + text.replaceAll("\n", "<br>") + "</html>");
		label.setFont(new Font(Font.SERIF, 0, 24));
		label.setForeground(Color.black);

		JPanel tmp2 = new JPanel();
		tmp2.setLayout(new FlowLayout(FlowLayout.LEFT));
		tmp2.setPreferredSize(new Dimension(2000, 50));
		tmp2.setBackground(new Color(235, 94, 40));
		tmp2.add(label);
		chatp.add(tmp2);


		container.revalidate();
		container.repaint();
		chatp.revalidate();
		chatp.repaint();
		tmp2.revalidate();
		tmp2.repaint();
		chat.revalidate();
		chat.repaint();

	}
	public void recieve(String text) {


		String[] arr = text.split(": ");


		String name = arr[0];
		String msg = arr[1];

		if(msg.toUpperCase().equals("BYE")) {

			JOptionPane.showMessageDialog(null, name + " has left the chat");

			return;
		}

		JLabel label = new JLabel("<html>" + name + ": " + msg.replaceAll("\n", "<br>") + "</html>");
		label.setFont(new Font(Font.SERIF, 0, 24));
		label.setForeground(Color.black);

		JPanel tmp = new JPanel();
		tmp.setLayout(new FlowLayout(FlowLayout.LEFT));
		tmp.setPreferredSize(new Dimension(2000, 50));
		tmp.setBackground(new Color(25, 133, 161));
		tmp.add(label);
		chatp.add(tmp);

		container.revalidate();
		container.repaint();
		chatp.revalidate();
		chatp.repaint();
		tmp.revalidate();
		tmp.repaint();
		chat.revalidate();
		chat.repaint();

	}

	public JPanel memberList(String text) {
		
		String[]members = text.split("`");
		
		JPanel memp = new JPanel();
		memp.setPreferredSize(new Dimension(180, 8000));
		memp.setMaximumSize(new Dimension(180, 8000));
		memp.setAutoscrolls(true);
		memp.setBackground(bg);
		memp.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JScrollPane memscroll = new JScrollPane(memp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		memscroll.setPreferredSize(new Dimension(210, 650));
		memscroll.setBackground(bg);
		
		memscroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		JPanel container1 = new JPanel();
		container1.add(memscroll);
		container1.setMaximumSize(new Dimension(230, 680));
		container1.setPreferredSize(new Dimension(230, 680));
		container1.setBackground(bg);
		container1.setBorder(BorderFactory.createLineBorder(Color.white, 1));
		
		System.out.println(Arrays.toString(members));
		for(String name : members) {
			if(name.equals("") || name.equals("all"))
				continue;
			JLabel label = new JLabel("<html>" + name + "</html>");
			label.setFont(new Font(Font.SERIF, 0, 20));
			label.setForeground(Color.WHITE);

			JPanel tmp = new JPanel();
			tmp.setLayout(new FlowLayout(FlowLayout.CENTER));
			tmp.setPreferredSize(new Dimension(210, 30));
			tmp.setBackground(bg);
			tmp.add(label);
			memp.add(tmp);
			
			label.revalidate();
			label.repaint();
			tmp.revalidate();
			tmp.repaint();
		}
		
		memp.revalidate();
		memp.repaint();
		memscroll.revalidate();
		memscroll.repaint();
		container1.revalidate();
		container1.repaint();
		return container1;
	}

	public JPanel createChat() {

		chatp = new JPanel();
		chatp.setPreferredSize(new Dimension(2000, 8000));
		chatp.setMaximumSize(new Dimension(2000, 8000));
		chatp.setAutoscrolls(true);
		chatp.setBackground(bg);
		chatp.setLayout(new FlowLayout(FlowLayout.LEFT));

		scroll = new JScrollPane(chatp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(940, 650));
		scroll.setBackground(bg);

		scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		JPanel container1 = new JPanel();
		container1.add(scroll);
		container1.setMaximumSize(new Dimension(970, 680));
		container1.setPreferredSize(new Dimension(970, 680));
		container1.setBackground(bg);
		container1.setBorder(BorderFactory.createLineBorder(Color.white, 1));
		chatp.revalidate();
		chatp.repaint();
		scroll.revalidate();
		scroll.repaint();
		container1.revalidate();
		container1.repaint();
		return container1;

	}


	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_ENTER) {
			String s = ta.getText();
			if(s == "")
				return;
			try {
				send(s);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
