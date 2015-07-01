package application;

import java.net.UnknownHostException;

public class Client extends Application {
	private boolean readyToReceive;
	private static final long serialVersionUID = 1344598284365542129L;

	public Client(String name, String address, int port) throws UnknownHostException {
		super(name, address, port);
		readyToReceive = false;
	}
	
	public Client(String name){
		super(name);
		readyToReceive = false;
	}

	public boolean isReadyToReceive() {
		return readyToReceive;
	}

	public void setReadyToReceive(boolean readyToReceive) {
		this.readyToReceive = readyToReceive;
	}

	@Override
	public String toString() {
		return super.toString() + "[type = client]";
	}
	
}
