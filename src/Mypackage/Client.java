package Mypackage;
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
public class Client {
	Socket s;

	DataInputStream in;
	DataOutputStream out;

	BufferedReader br;
	String str="";

	String name;

	Screen chatScreen;
	public Client(int port) throws UnknownHostException, IOException {

		String ip = JOptionPane.showInputDialog("Enter your friend's IP Address");
		s=new Socket(ip,port);
		in = new DataInputStream(s.getInputStream());
		out=new DataOutputStream(s.getOutputStream());
		
		String pop = "Enter your username: ";
		while(true) {
			this.name = JOptionPane.showInputDialog(pop);	
			out.writeUTF(name);
			String respond = in.readUTF();
			if(respond.equals("true"))
				break;
			
			else
				pop = "This username is reserved. Please choose another one.";
		}
		
		
		JOptionPane.showMessageDialog(null, "Welcome " + this.name);
		
		this.chatScreen = new Screen(this);
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				String line;
				try {
					while((line = in.readUTF())!=null) {
						
						if(line.startsWith("memreq")) {
							JPanel memlist = chatScreen.memberList(line.substring(6));
							
							chatScreen.right.add(chatScreen.refresh);
							chatScreen.right.add(memlist);
							
							chatScreen.right.repaint();
							chatScreen.right.revalidate();
							chatScreen.refresh.repaint();
							chatScreen.refresh.revalidate();
							continue;
						}

						str= line;
						
						chatScreen.recieve(str);

					}
				} catch (IOException ignored) {
				}
			}

		}).start();


	}

	public static void main(String[] args) {
		try {

			new Client(6000);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
