package Mypackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class MZZerver {
	public ServerSocket ss;
	public Socket s;

	public HashMap<String, EchoThread> clients;

	public HashSet<String> names;

	public ServerSocket ss2;
	public Socket s2;
	public ServerThread server2;

	public MZZerver(int port1, int port2) throws Exception {

		ss = new ServerSocket(port1);

		clients = new HashMap<String, MZZerver.EchoThread>();
		names = new HashSet<String>();

		ss2 = new ServerSocket(port2);
		server2 = new ServerThread();
		server2.start();
		
		names.add("all");

		while(true) {
			try {
				
				s = ss.accept();
				
				
				EchoThread e = new EchoThread(s);
				
				
				String name;
				while(true) {
					name = e.in.readUTF();
					if(names.contains(name))
						e.out.writeUTF("false");
					
					else {
						e.out.writeUTF("true");
						break;
					}
				}

				e.name = name;

				server2.out.writeUTF("newClient " + name);
				names.add(name);

				clients.put(name, e);

				e.start();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class ServerThread extends Thread {

		public DataInputStream in;
		public DataOutputStream out;

		public String line = "";

		public ServerThread() {
			try {

				s2 = ss2.accept();

				in = new DataInputStream(s2.getInputStream());
				out = new DataOutputStream(s2.getOutputStream()); 

			} catch (IOException e) {

			}
		}

		public void run() {

			while(true) {
				try {
					line = in.readUTF();

					if(line.startsWith("newClient")) {
						names.add(line.split(" ")[1]);
						continue;
					}

					String[] msg = line.split("`");//msg[0] = src; msg[1] = dest; msg[2] = text;

					String src = msg[0],
							dest = msg[1].split(": ")[0],
							text = msg[1].split(": ")[1];


					if(text.toUpperCase().equals("BYE")) {
						broadCast(src + ": " + text);
						names.remove(src);
						break;
					}

					EchoThread e = clients.get(dest);

					e.out.writeUTF(src + ": " + text);

				} catch (IOException e) {
				}
			}
		}

		public void broadCast(String txt) throws IOException {


			for(EchoThread e : clients.values())
				e.out.writeUTF(txt);
		}
	}

	class EchoThread extends Thread {

		protected Socket socket;

		public DataInputStream in;
		public DataOutputStream out;

		public String line = "";

		public String name;


		EchoThread(Socket socket){
			this.socket = socket;
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream()); 
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		public void run() {

			while(true) {
				try {
					line = in.readUTF();
					
					System.out.println(line);
					if(line.equals("req")) {
						String mems = "memreq";
						System.out.println("in");
						for(String s : names)
							mems += "`" + s;
						System.out.println(mems);
						out.writeUTF(mems);
						continue;
					}
					
					
					String[] msg = line.split("`");//msg[0] = dest; msg[1] = src; msg[2] = text;

					String src = msg[0],
							dest = msg[1].split(": ")[0],
							text = msg[1].split(": ")[1];

					
					
					if(text.toUpperCase().equals("BYE")) {
						broadCast(src + ": " + text);
						server2.out.writeUTF(line);
						names.remove(src);
						clients.remove(src);
						break;
					}

					if(clients.containsKey(dest))
						clients.get(dest).out.writeUTF(src+ ": " + text);

					else
						server2.out.writeUTF(line);


				} catch (IOException e) {
				}
			}
			try {

				clients.remove(name);
				in.close();
				out.close();
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void broadCast(String txt) throws IOException {

			for(EchoThread e : clients.values())
				if(e != this)
					e.out.writeUTF(txt);
		}
	}


	public static void main(String[] args) {
		try {
			MZZerver server = new MZZerver(6000, 8000);
		} catch (Exception e) {
			System.exit(0);
		}
	}

}
