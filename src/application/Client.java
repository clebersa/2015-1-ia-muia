package application;

import java.net.UnknownHostException;

public class Client extends Application {
	private static final long serialVersionUID = 1344598284365542129L;
	private MUIA host;

	public Client(String name, String address, int port) throws UnknownHostException {
		super(name, address, port);
	}

	public MUIA getHost() {
		return this.host;
	}

	public void setHost(MUIA host) {
		this.host = host;
	}

	@Override
	public String toString() {
		return super.toString() + "[type = client]";
	}
	
}
