package application;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

import common.Logger;
import sending.Channel;

/**
 * Class of a MUIA in the MUIA server application. This class identify a MUIA
 * instance that contains the instance location and management of instance
 * registered applications.
 *
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public class MUIA extends Application implements MUIAObserver, MUIAObservable {
	private static final long serialVersionUID = -7028287914779865311L;
	private Boolean copy;
	private Integer registryPort;
	private Boolean alive = true;
	private Remote selfRemoteReference;
	private MUIAObservable originalMUIA;

	/**
	 * List of observers of MUIA instance.
	 */
	private ArrayList<MUIAObserver> observers = new ArrayList<MUIAObserver>();
	private ArrayList<MUIA> knownMUIAs = new ArrayList<MUIA>();
	/**
	 * List of clients registered in the MUIA instance.
	 */
	private ArrayList<Client> clients = new ArrayList<Client>();
	private ArrayList<Channel> channels = new ArrayList<Channel>();

	/**
	 * Constructor method. Initialize the variables.
	 * 
	 * @throws java.net.UnknownHostException When the address parameter is not 
	 * found.
	 */
	public MUIA(String name, String address, Integer port, Integer registryPort, Boolean isCopy)
			throws UnknownHostException, UnableToCreateMUIAException {
		super(name, address, port);
		
		this.registryPort = registryPort;
		this.copy = isCopy;
		
		try {
			selfRemoteReference = UnicastRemoteObject.exportObject(this, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			Registry registry = LocateRegistry.getRegistry(this.address.getHostAddress(), this.registryPort);
			
			if( !isCopy ) {
				Logger.info("Registering MUIA host in the registry...");
				registry.bind(this.name, ((MUIAObservable)selfRemoteReference));
			} else {
				Logger.info("Subscribing local MUIA copy {" + this + "} in the observer list of the real MUIA...");
				originalMUIA = (MUIAObservable) registry.lookup(this.name);
				originalMUIA.addObserver(((MUIAObserver) selfRemoteReference));
			}
			Logger.info("Done!");
		} catch (Exception e) {
			String errorMessage = (!isCopy) ? "Unable to register MUIA host in the registry" : 
				"Unable to subscribe the MUIA copy {" + this + "} like a observer of the real MUIA";
			Logger.error(errorMessage + " Error: " + e.getMessage());
			throw new UnableToCreateMUIAException();
		}
	}

	public void keepAlive() {
		Boolean isAlive = false;
		try {
			isAlive = originalMUIA.isAlive();
		} catch (Exception e) {
			isAlive = false;
		}
		
		if( !isAlive ) {
			if( this.alive == true ) {
				this.alive = false;
				this.originalMUIA = null;
				this.channels.clear();
				this.clients.clear();
			}
		} else {
			if( this.alive == false ) {
				syncronizeOriginalMuia();
			}
		}
	}
	
	public void syncronizeOriginalMuia() {
		try {
			Registry registry = LocateRegistry.getRegistry(this.address.getHostAddress(), this.registryPort);
			this.originalMUIA = (MUIAObservable) registry.lookup(this.name);
			this.originalMUIA.addObserver(((MUIAObserver) selfRemoteReference));
			this.alive = true;
		} catch (Exception e) {}
	}
	
	public Client getClientReference( String client ) {
		// Local search
		Iterator<Client> cIterator = clients.iterator();
		while( cIterator.hasNext() ) {
			Client clientReference = cIterator.next();
			if( clientReference.getName().equals(client) ) {
				return clientReference;
			}
		}
		
		// Known MUIAs search
		Iterator<MUIA> mIterator = knownMUIAs.iterator();
		Client clientReference;
		while( mIterator.hasNext() ) {
			MUIA muia = mIterator.next();
			if( !muia.isAlive() ) {
				continue;
			}
			
			clientReference = muia.getClientReference(client);
			if (clientReference != null) {
				return clientReference;
			}
			
		}
		
		return null;
	}
	
	public MUIA getMUIAReference(String muiaName) {
		Iterator<MUIA> iterator = knownMUIAs.iterator();
		while( iterator.hasNext() ) {
			MUIA muia = iterator.next();
			if( !muia.isAlive() ) {
				continue;
			}
			
			if (muia.getName().equals(muiaName)) {
				return muia;
			}
		}
		return null;
	}

	/**
	 * Method to add (register) a application in the MUIA instance. If the
	 * addition occurs smoothly, the observers are updated.
	 *
	 * @param application - {@link application.Application} to be registered in
	 * the MUIA instance.
	 * @return Boolean true if the application was successfully added in the
	 * MUIA instance or false if the application is already contained in the
	 * MUIA instance or the addition operation have a error.
	 */
	public Boolean addClient(Client client) {
		Boolean operation = false;

		if (!this.clients.contains(client)) {
			operation = this.clients.add(client);
		}

		if (operation == true) {
			this.notifyClientAddition(client);
		}

		return operation;
	}

	/**
	 * Method to remove (unregister) a application in the MUIA instance. If the
	 * removal occours smoothly, the observers are updated.
	 *
	 * @param application - {@link application.Application} to be removed in the
	 * MUIA instance.
	 * @return Boolean true if the application was successfully removed from the
	 * MUIA instance or false if the application doesn't exists in the MUIA
	 * instance or the removal operation have a error.
	 */
	public Boolean removeClient(Client client) {
		Boolean operation = this.clients.remove(client);

		if (operation == true) {
			this.notifyClientRemoval(client);
		}

		return operation;
	}
	
	public Boolean isCopy() {
		return this.copy;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[type = muia]";
	}

	@Override
	public Boolean isAlive() {
		return this.alive;
	}
	
	@Override
	public Boolean addObserver(MUIAObserver observer) {
		System.out.println("Adicionando observer...");
		Boolean operation = false;

		if (!this.observers.contains(observer)) {
			operation = this.observers.add(observer);
		}

		if (operation == true) {
			Iterator<Client> it = this.clients.iterator();
			while (it.hasNext()) {
				Client client = it.next();
				byte[] serializedClient = Application.serialize(client);

				try {
					observer.updateClientAddition(serializedClient);
				} catch (RemoteException e) {
					Logger.warning( "Could not update client in the observer " + ((MUIA)observer).toString() +
							" Error: " + e.getMessage() );
				}
			}
		}
		return operation;
	}

	@Override
	public Boolean removeObserver(MUIAObserver observer) {
		Boolean operation = this.observers.remove(observer);
		return operation;
	}

	@Override
	public void notifyClientAddition(Client client) {
		Iterator<MUIAObserver> iterator = this.observers.iterator();
		while (iterator.hasNext()) {
			MUIAObserver observer = iterator.next();
			byte[] serializedClient = Application.serialize(client);
			
			try {
				observer.updateClientAddition(serializedClient);
			} catch (RemoteException e) {
				Logger.warning( "Could not update client in the observer " + ((MUIA)observer).toString() +
						" Error: " + e.getMessage() );
			}
		}
	}

	@Override
	public void notifyClientRemoval(Client client) {
		Iterator<MUIAObserver> iterator = this.observers.iterator();
		while (iterator.hasNext()) {
			MUIAObserver observer = iterator.next();
			byte[] serializedClient = Application.serialize(client);
			
			try {
				observer.updateClientRemoval(serializedClient);
			} catch (RemoteException e) {
				Logger.warning( "Could not update client in the observer " + ((MUIA)observer).toString() +
						" Error: " + e.getMessage() );
			}
		}
	}

	@Override
	public void updateClientAddition(byte[] serializedClient) {
		Client client = (Client) Application.deserialize(serializedClient);

		Boolean exists = false;
		Iterator<Client> it = this.clients.iterator();
		while (it.hasNext()) {
			Client hostedClient = it.next();
			if (hostedClient.getName().equals(client.getName())) {
				exists = true;
				break;
			}
		}

		if (exists == false) {
			this.clients.add(client);
		}
	}

	@Override
	public void updateClientRemoval(byte[] client) {
		this.clients.remove(client);
	}
}
