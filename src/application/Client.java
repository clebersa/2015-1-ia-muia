package application;

import java.net.UnknownHostException;

public class Client extends Application {
	private static final long serialVersionUID = 1344598284365542129L;

	public Client(String name, String address, int port) throws UnknownHostException {
		super(name, address, port);
	}

	@Override
	public String toString() {
		return super.toString() + "[type = client]";
	}
	
}
