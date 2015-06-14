package application;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import application.exceptions.UnableToCreateMUIAException;
import common.Logger;

/**
 * Abstract class of a MUIA. This class provides a basic structure for original MUIAs and copy MUIAs.
 * This structure provide a way to identify and get the MUIA instance, being or not a copy.
 *
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public abstract class MUIA extends Application implements Remote {
	/**
	 * Unique serial long used to identify the class in the serialization and deserialization.
	 */
	private static final long serialVersionUID = -7028287914779865311L;
	
	/**
	 * Integer containing the number of port where the registry of the MUIA is running.
	 */
	protected Integer registryPort;
	
	/**
	 * Boolean to identify if the MUIA is alive, i.e, if the MUIA is available and operational.
	 */
	protected Boolean alive;
	
	/**
	 * Remote containing the remote reference of the MUIA
	 */
	protected Remote selfRemoteReference;
	
	/**
	 * List containing the clients registered in the MUIA.
	 */
	protected ArrayList<Client> clients = new ArrayList<Client>();
	
	/**
	 * Creates a new instance of the MUIA class.
	 * @param name - String containing the name of the MUIA.
	 * @param address - String containing the IP address of the MUIA.
	 * @param port - Integer containing the port where the MUIA is running.
	 * @param registryPort - Integer containing the port of registry where the MUIA is binded.
	 * @throws UnknownHostException when the address parameter is not found.
	 * @throws UnableToCreateMUIAException when is not possible to create the remote reference of MUIA.
	 */
	public MUIA(String name, String address, Integer port, Integer registryPort) throws UnknownHostException,
			UnableToCreateMUIAException {
		super(name, address, port);
		this.registryPort = registryPort;
		alive = false;
		
		try {
			selfRemoteReference = UnicastRemoteObject.exportObject(this, 0);
		} catch (RemoteException e) {
			Logger.error("Unable to export remote reference of MUIA {" + this + "}. Error: " + e.getMessage());
			throw new UnableToCreateMUIAException();
		}
	}
	
	/**
	 * Gets the {@link application.Client} instance in the MUIA clients list based in a client name.
	 * @param clientName - String containing the name of client to be searched.
	 * @return {@link application.Client} instance of searched client name or {@value null} if have no client
	 * registered with the specified client name in the MUIA.
	 */
	public Client getClientReference( String clientName ) {
		for( Client client : clients ) {
			if( client.getName().equals(clientName) ) {
				return client;
			}
		}
		
		return null;
	}

	/**
	 * Adds (register) a client in the MUIA.
	 * If a client with the same name of the client to be registered is already registered in
	 * the MUIA, the register need to be canceled.
	 * @param client - {@link application.client} to be registered in the MUIA.
	 * @return Boolean true if the client was successfully added in the MUIA or false if a 
	 * client with the same name is already registered in the MUIA or the addition operation
	 * have a error.
	 */
	public Boolean addClient(Client client) {
		Boolean exists = false;
		for( Client alreadyAddedClient : clients ) {
			if (alreadyAddedClient.getName().equals(client.getName())) {
				exists = true;
				break;
			}
		}

		Boolean operation = false;
		if (!exists) {
			operation = clients.add(client);
		}

		return operation;
	}

	/**
	 * Removes (unregister) a client in the MUIA.
	 * @param client - {@link application.Client} to be removed in the MUIA.
	 * @return Boolean true if the client was successfully removed from the MUIA or false
	 * if the client doesn't exists in the MUIA or the removal operation have a error.
	 */
	public Boolean removeClient(Client client) {
		return clients.remove(client);
	}
	
	/**
	 * Verifies if the MUIA is alive, i.e, check if everything is fine and the MUIA is operational.
	 * @return Boolean true if the MUIA is alive of false when not.
	 */
	public Boolean isAlive() {
		return alive;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[type = muia]";
	}
}
