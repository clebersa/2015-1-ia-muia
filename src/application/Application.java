package application;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Abstract class to represent a application in the MUIA. This class needs to
 * provide a way to identify the real application location.
 * 
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public abstract class Application implements Serializable {
	private static final long serialVersionUID = 1512600640647137975L;
	protected String name;
	protected InetAddress address;
	protected Integer port;

	public Application(String name, String address, Integer port) throws UnknownHostException {
		this.name = name;
		this.address = InetAddress.getByName(address);
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "Application{name=" + name + ", address=" 
				+ address.getHostAddress() + ", port=" + port + "}";
	}
}
