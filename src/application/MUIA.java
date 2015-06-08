package application;

import java.io.Serializable;
import java.net.UnknownHostException;
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
public class MUIA extends Application implements MUIAObserver, MUIAObservable, Serializable {

	private static final long serialVersionUID = -7028287914779865311L;
	private Boolean isRemote;
	private Integer registryPort;

	/**
	 * List of observers of MUIA instance.
	 */
	private ArrayList<MUIAObserver> observers;

	private ArrayList<Channel> channels;
	private ArrayList<MUIA> knownMUIAs;
	/**
	 * List of clients registered in the MUIA instance.
	 */
	private ArrayList<Client> clients;
	
	/**
	 * Constructor method. Initialize the variables.
	 * 
	 * @throws java.net.UnknownHostException When the address parameter is not 
	 * found.
	 */
	public MUIA(String name, String address, int port) 
			throws UnknownHostException {
		this(name, address, port, false);
	}

	/**
	 * Constructor method. Initialize the variables.
	 * 
	 * @throws java.net.UnknownHostException When the address parameter is not 
	 * found.
	 */
	public MUIA(String name, String address, int port, Boolean isRemote)
			throws UnknownHostException {
		super(name, address, port);
		
		this.registryPort = 2002;

		this.isRemote = isRemote;
		this.observers = new ArrayList<>();
		this.clients = new ArrayList<>();

		if (!isRemote) {
			try {
				MUIAObservable muiaObservable = (MUIAObservable) UnicastRemoteObject.exportObject(this, 0);
				Main.getRegistry().bind(this.name, muiaObservable);
			} catch (Exception e) {
				Logger.error("Unable to export or bind the MUIA. Error: " 
						+ e.getMessage());
			}
		} else {
			try {
				MUIAObserver muiaObserver = (MUIAObserver) UnicastRemoteObject.exportObject(this, 0);

				Registry registry = LocateRegistry.getRegistry(this.address.getHostAddress(), this.registryPort);
				MUIAObservable muiaObervable = (MUIAObservable) registry.lookup(this.name);
				muiaObervable.addObserver(muiaObserver);
			} catch (Exception e) {
				System.out.println("Error ao se inscrever como observador do MUIA original: " + e.getMessage());
			}
		}
	}

	public MUIA getMUIAReference(String muiaName) {
		for (MUIA muia : knownMUIAs) {
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

	@Override
	public String toString() {
		return super.toString() + "[type = muia]";
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
					System.out.println(this.getName() + " - Não foi atualizar o observador " + e.getMessage());
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
			try {
				byte[] serializedClient = Application.serialize(client);
				observer.updateClientAddition(serializedClient);
			} catch (RemoteException e) {
				System.out.println(this.getName() + " - Não foi possível notificar um observador: " + e.getMessage());
			}
		}
	}

	@Override
	public void notifyClientRemoval(Client client) {
		Iterator<MUIAObserver> iterator = this.observers.iterator();
		while (iterator.hasNext()) {
			MUIAObserver observer = iterator.next();
			try {
				byte[] serializedClient = Application.serialize(client);
				observer.updateClientRemoval(serializedClient);
			} catch (RemoteException e) {
				System.out.println(this.getName() + " - Não foi possível notificar um observador: " + e.getMessage());
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
			if (hostedClient.equals(client)) {
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
