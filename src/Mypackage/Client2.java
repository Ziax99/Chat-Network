package Mypackage;

import java.io.IOException;
import java.net.UnknownHostException;

public class Client2 {
	public static void main(String[] args) {
		try {

			Client client=new Client(7000);

		} catch (UnknownHostException e) {}
		catch (IOException e) {}
	}
}
