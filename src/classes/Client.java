package classes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Iterator;

public class Client extends Application implements Serializable {
	private static final long serialVersionUID = -6556829733664324391L;
	private MUIA host;
	
	public Client( MUIA host ) {
		this.host = host;
	}

	public MUIA getHost() {
		return this.host;
	}
}
