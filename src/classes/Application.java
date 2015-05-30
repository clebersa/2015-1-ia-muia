package classes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;

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
	
	public static byte[] serialize( Application application ) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject( application );
			os.close();
		} catch( IOException e ) {
			e.printStackTrace();
		}
		
		return os.toByteArray();
	}
	
	public static Application deserialize( byte[] serializedApplication ) {
		ByteArrayInputStream in = new ByteArrayInputStream( serializedApplication );
		ObjectInputStream is = null;
		Application application = null;
		try {
			is = new ObjectInputStream(in);
			application = (Application) is.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return application;
	}
}
