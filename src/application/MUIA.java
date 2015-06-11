package application;

import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import application.exceptions.UnableToCreateMUIAException;
import application.exceptions.UnableToUpdateObserverException;
import application.interfaces.CopyMUIAObservable;
import application.interfaces.CopyMUIAObserver;
import application.interfaces.OriginalMUIAObservable;
import application.interfaces.OriginalMUIAObserver;
import common.Logger;
import common.SerializableHandler;
import sending.Channel;
import sending.interfaces.ChannelObserver;

/**
 * Class of a MUIA in the MUIA server application. This class identify a MUIA
 * instance that contains the instance location and management of instance
 * registered applications.
 *
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public class MUIA extends Application implements CopyMUIAObserver, OriginalMUIAObserver, ChannelObserver,
CopyMUIAObservable, OriginalMUIAObservable {
	/**
	 * Unique serial long used to identify the class in the serialization and deserialization.
	 */
	private static final long serialVersionUID = -7028287914779865311L;
	
	/**
	 * Number of port where the registry of the MUIA is running.
	 */
	private Integer registryPort;
	
	/**
	 * Boolean to identify if the MUIA is a copy or not.
	 */
	private Boolean copy;
	
	/**
	 * Boolean to identify if the MUIA is alive, i.e, if the MUIA is available and operational.
	 */
	private Boolean alive;
	
	/**
	 * Remote reference of the MUIA
	 */
	private Remote selfRemoteReference;
	
	/**
	 * Remote Reference of the original MUIA, only used by copy MUIAs.
	 */
	private CopyMUIAObservable remoteOriginalMUIA;

	/**
	 * List of Copy MUIA observers, only used by original MUIAs.
	 */
	private ArrayList<CopyMUIAObserver> copyMUIAobservers = new ArrayList<CopyMUIAObserver>();
	
	/**
	 * List of Original MUIA observers, only used by original MUIAs.
	 */
	private ArrayList<OriginalMUIAObserver> originalMUIAObservers = new ArrayList<OriginalMUIAObserver>();
	
	/**
	 * List of known MUIAs of original MUIA host, in practice, that knownMUIAs are copys of the original known MUIAs. 
	 */
	private ArrayList<MUIA> knownMUIAs = new ArrayList<MUIA>();
	
	/**
	 * List of clients registered in the MUIA instance.
	 */
	private ArrayList<Client> clients = new ArrayList<Client>();
	
	/**
	 * List of active channels in the MUIA network, only used by original MUIAs.
	 */
	private ArrayList<Channel> channels = new ArrayList<Channel>();
	
	/**
	 * Creates a new instance of the MUIA class.
	 * If the MUIA initialized is a copy, start the MUIA Checker.
	 * @param name - String containing the name of the MUIA.
	 * @param address - String containing the IP address of the MUIA.
	 * @param port - Integer containing the port where the MUIA is running.
	 * @param registryPort - Integer containing the port of registry where the MUIA is binded.
	 * @param isCopy - Boolean containing true if the MUIA is a copy or false if the MUIA is a original instance.
	 * @throws UnknownHostException when the address parameter is not found.
	 * @throws UnableToCreateMUIAException when is not possible to create the remote reference of MUIA or when have a
	 * problem to register the original MUIA host in the registry.
	 */
	public MUIA(String name, String address, Integer port, Integer registryPort, Boolean isCopy)
			throws UnknownHostException, UnableToCreateMUIAException {
		super(name, address, port);
		
		this.registryPort = registryPort;
		this.copy = isCopy;
		
		try {
			selfRemoteReference = UnicastRemoteObject.exportObject(this, 0);
		} catch (RemoteException e) {
			Logger.error("Unable to export remote reference of MUIA {" + this + "}. Error: " + e.getMessage());
			throw new UnableToCreateMUIAException();
		}
		
		if( isCopy ) {
			MUIAChecker checker = new MUIAChecker(this);
			Thread tchecker = new Thread(checker);
			tchecker.start();
			
			try {
				synchronizeCopyToOriginalMUIA();
			} catch (RemoteException | NotBoundException e) {
				Logger.error("Unable to synchronyze MUIA copy {" + this + "} with your real MUIA");
			}
		} else {
			try {
				registerMUIAHostInRegistry();
			} catch (RemoteException | AlreadyBoundException e) {
				Logger.error("Unable to register MUIA host in the registry");
				throw new UnableToCreateMUIAException();
			}
		}
	}
	
	/**
	 * Verifies if the connection with the original MUIA is alive.
	 * This method can't be used by original MUIAs.
	 * If the original MUIA connection is not alive and the last check is alive, the copy will set yourself to not
	 * alive and release the original MUIA reference.
	 * If the original MUIA connection is alive and the last check is not alive, the copy will synchronize with the
	 * original MUIA.
	 */
	public void keepAlive() {
		if( !isCopy() ) {
			return;
		}
		
		Boolean isAlive;
		try {
			isAlive = remoteOriginalMUIA.isAlive();
		} catch (Exception e) {
			isAlive = false;
		}
		
		if( isAlive && alive == true ) {
			return;
		} else if( !isAlive && alive == true ) {
			alive = false;
			remoteOriginalMUIA = null;
		}
		
		if( alive == false && remoteOriginalMUIA == null ) {
			try {
				synchronizeCopyToOriginalMUIA();
			} catch (RemoteException | NotBoundException e) {
				Logger.error("Unable to synchronyze MUIA copy {" + this + "} with your real MUIA");
			}
		}
	}
	
	/**
	 * Registers the original MUIA host in your registry.
	 * This method can't be used by copy MUIAs.
	 * @throws RemoteException while binding original MUIA host in your registry.
	 * @throws AlreadyBoundException when a application with the same name of the original MUIA host is registered in
	 * the registry.
	 */
	public void registerMUIAHostInRegistry() throws RemoteException, AlreadyBoundException {
		if( isCopy() ) {
			return;
		}
		
		Logger.info("Registering MUIA host in the registry...");
		Registry registry = LocateRegistry.getRegistry(this.address.getHostAddress(), this.registryPort);
		registry.bind(this.name, ((CopyMUIAObservable)selfRemoteReference));
		alive = true;
	}
	
	/**
	 * Establishes the connection of copy MUIA with your original MUIA and synchronize it by the subscription of the
	 * copy in the list of observers of the original MUIA. 
	 * This method can't be used by original MUIAs.
	 * @throws RemoteException while getting the original MUIA registry, while getting original MUIA remote reference
	 * in the registry or while adding the copy in the observer list of the original MUIA.
	 * @throws NotBoundException when the original MUIA is not founded in the registry.
	 */
	public void synchronizeCopyToOriginalMUIA() throws RemoteException, NotBoundException {
		if( !isCopy() ) {
			return;
		}
		
		channels.clear();
		clients.clear();
		
		Logger.info("Subscribing local MUIA copy {" + this + "} in the observer list of the real MUIA...");
		Registry registry = LocateRegistry.getRegistry(this.address.getHostAddress(), this.registryPort);
		remoteOriginalMUIA = (CopyMUIAObservable) registry.lookup(this.name);
		remoteOriginalMUIA.addCopyMUIAObserver(((CopyMUIAObserver) selfRemoteReference));
		alive = true;
	}
	
	/**
	 * Gets the {@link application.Client} instance in the MUIA clients list or in your network based in a client name.
	 * @param clientName - String containing the name of client to be searched.
	 * @return {@link application.Client} instance of searched client name or {@value null} if have no client
	 * registered with the specified client name in the MUIA network.
	 */
	public Client getClientReference( String clientName ) {
		// Local search
		for( Client client : clients ) {
			if( client.getName().equals(clientName) ) {
				return client;
			}
		}
		
		// Known MUIAs search
		Client client;
		for( MUIA knownMUIA : knownMUIAs ) {
			if( !knownMUIA.isAlive() ) {
				continue;
			}
			
			client = knownMUIA.getClientReference(clientName);
			if (client != null) {
				return client;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the {@link sending.Channel} instance in the MUIA channels list based in a channel id.
	 * This method can't be used by copy MUIAs.
	 * @param channelId - String containing the id of the channel to be searched.
	 * @return {@link sending.Channel} instance of searched channel id or {@value null} if have no channel registered
	 * with the specified id in the MUIA.
	 */
	public Channel getChannelReference( String channelId ) {
		if( isCopy() ) {
			return null;
		}
		
		for( Channel channel : channels ) {
			if( channel.getId().equals(channelId) ) {
				return channel;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the {@link application.MUIA} instance in the MUIA knownMuias list based in the MUIA name.
	 * @param muiaName - String containing the name of the known MUIA to be searched.
	 * @return {@link application.MUIA} instance of searched MUIA name or {@value null} if the MUIA don't
	 * knows the searched MUIA name.
	 */
	public MUIA getMUIAReference(String muiaName) {		
		for( MUIA knownMUIA : knownMUIAs ) {
			if( !knownMUIA.isAlive() ) {
				continue;
			}
			
			if (knownMUIA.getName().equals(muiaName)) {
				return knownMUIA;
			}
		}
		
		return null;
	}

	/**
	 * Adds (register) a client in the MUIA.
	 * If a client with the same name of the client to be registered is already registered in
	 * the MUIA, the register need to be canceled.
	 * If the register occurs smoothly and the MUIA is not a copy, the observers are notified.
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

		if (operation && !isCopy()) {
			notifyClientAddition(client);
		}

		return operation;
	}

	/**
	 * Removes (unregister) a client in the MUIA.
	 * If the removal occurs smoothly and the MUIA is not a copy, the observers are notified.
	 * @param client - {@link application.Client} to be removed in the MUIA.
	 * @return Boolean true if the client was successfully removed from the MUIA or false
	 * if the client doesn't exists in the MUIA or the removal operation have a error.
	 */
	public Boolean removeClient(Client client) {
		Boolean operation = clients.remove(client);

		if (operation && !isCopy()) {
			notifyClientRemoval(client);
		}

		return operation;
	}
	
	/**
	 * Adds (register) a channel in the MUIA.
	 * This method can't be used by copy MUIAs.
	 * If a channel with the same id of the channel to be registered is already registered in
	 * the MUIA, the register need to be canceled.
	 * If the register occurs smoothly, the observers are notified.
	 * When the channel is registered in the MUIA, the MUIA will be added in the observers
	 * list of the channel.
	 * @param channel - {@link sending.Channel} to be registered in the MUIA instance.
	 * @return Boolean true if the channel was successfully added in the MUIA or false if
	 * a channel with the same id is already registered in the MUIA or the addition
	 * operation have a error.
	 */
	public Boolean addChannel( Channel channel ) {
		if( isCopy() ) {
			return null;
		}
		
		Boolean exists = false;
		for( Channel alreadyAddedChannel : channels ) {
			if (alreadyAddedChannel.getId().equals(channel.getId())) {
				exists = true;
				break;
			}
		}
		
		Boolean operation = false;
		if (!exists) {
			operation = channels.add(channel);
		}

		if (operation) {
			channel.addObserver(this);
			notifyChannelAddition(channel);
		}

		return operation;
	}
	
	/**
	 * Removes (unregister) a channel in the MUIA.
	 * This method can't be used by copy MUIAs.
	 * If the removal occurs smoothly, the observers are notified.
	 * When the channel is removed from the MUIA, the MUIA will be removed from the
	 * observers list of the channel.
	 * @param channel - {@link sending.Channel} to be removed in the MUIA.
	 * @return Boolean true if the channel was successfully removed from the MUIA or false
	 * if the channel doesn't exists in the MUIA or the removal operation have a error.
	 */
	public Boolean removeChannel( Channel channel ){
		if( isCopy() ) {
			return null;
		}
		
		Boolean operation = channels.remove(channel);

		if (operation) {
			channel.removeObserver(this);
			notifyChannelRemoval(channel);
		}

		return operation;
	}
	
	public Boolean addKnownMUIA( MUIA muia ) {
		Boolean exists = false;
		for( MUIA knownMUIA : knownMUIAs ) {
			if (knownMUIA.getName().equals(muia.getName())) {
				exists = true;
				break;
			}
		}
		
		Boolean operation = false;
		if (!exists) {
			operation = knownMUIAs.add(muia);
		}

		if (operation) {
			//muia.addOriginalMUIAObserver(this);
		}

		return operation;
	}
	
	public Boolean removeKnownMUIA( MUIA muia ) {
		Boolean operation = knownMUIAs.remove(muia);

		if (operation) {
			//muia.removeOriginalMUIAObserver(this);
		}

		return operation;
	}
	
	/**
	 * Updates {@link application.interfaces.CopyMUIAObserver} with all clients registered in the original MUIA.
	 * This method can't be used by copy MUIAs.
	 * @param observer - {@link application.interfaces.CopyMUIAObserver} what will be updated with all clients
	 * registered in the original MUIA.
	 * @throws UnableToUpdateObserverException when the {@link application.interfaces.CopyMUIAObserver} can't receive
	 * a client update.
	 */
	private void updateObserverClientAllData(CopyMUIAObserver observer) throws UnableToUpdateObserverException {
		if( isCopy() ) {
			return;
		}
		
		SerializableHandler<Client> shClient = new SerializableHandler<Client>();
		byte[] serializedClient;
		for( Client client : clients ) {
			serializedClient = shClient.serialize(client);
			try {
				observer.updateClientAddition(serializedClient);
			} catch (RemoteException e) {
				throw new UnableToUpdateObserverException();
			}
		}
	}
	
	/**
	 * Updates {@link application.interfaces.OriginalMUIAObserver} with all channels registered in the original MUIA.
	 * This method can't be used by copy MUIAs.
	 * @param observer - {@link application.interfaces.OriginalMUIAObserver} what will be updated with all channels
	 * registered in the original MUIA.
	 * @throws UnableToUpdateObserverException when the {@link application.interfaces.OriginalMUIAObserver} can't
	 * receive a channel update.
	 */
	private void updateObserverChannelAllData( OriginalMUIAObserver observer ) throws UnableToUpdateObserverException {
		if( isCopy() ) {
			return;
		}
		
		SerializableHandler<Channel> shChannel = new SerializableHandler<Channel>();
		byte[] serializedChannel;
		for( Channel channel : channels ) {
			serializedChannel = shChannel.serialize(channel);
			try {
				observer.updateChannelAddition(serializedChannel);
			} catch (RemoteException e) {
				throw new UnableToUpdateObserverException();
			}
		}
	}
	
	/**
	 * Verifies if the MUIA is a copy or not.
	 * @return Boolean containing true if the MUIA is a copy or false when not.
	 */
	public Boolean isCopy() {
		return copy;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[type = muia]";
	}

	@Override
	public Boolean isAlive() {
		return alive;
	}
	
	@Override
	public Boolean addCopyMUIAObserver(CopyMUIAObserver observer) {
		if( isCopy() ) {
			return null;
		}
		
		Boolean addObsOperation = false;
		if (!copyMUIAobservers.contains(observer)) {
			Boolean updateObsOperation = true;
			try {
				updateObserverClientAllData(observer);
			} catch (UnableToUpdateObserverException e) {
				updateObsOperation = false;
				Logger.error( "Unable to send all clients and update the observer {"
						+ ((MUIA)observer).toString() + "}" );
			}
			
			if( updateObsOperation ) {
				addObsOperation = copyMUIAobservers.add(observer);
			}
		}

		return addObsOperation;
	}

	@Override
	public Boolean removeCopyMUIAObserver(CopyMUIAObserver observer) {
		if( isCopy() ) {
			return null;
		}
		
		return copyMUIAobservers.remove(observer);
	}

	@Override
	public void notifyClientAddition(Client client) {
		if( isCopy() ) {
			return;
		}
		
		SerializableHandler<Client> sh = new SerializableHandler<Client>();
		byte[] serializedClient = sh.serialize(client);
		
		for (CopyMUIAObserver observer : copyMUIAobservers ) {
			try {
				observer.updateClientAddition(serializedClient);
			} catch (RemoteException e) {
				Logger.error( "Cannot update client addition in the observer {" + ((MUIA)observer).toString() + "}" );
			}
		}
	}

	@Override
	public void notifyClientRemoval(Client client) {
		if( isCopy() ) {
			return;
		}
		
		for( CopyMUIAObserver observer : copyMUIAobservers ) {
			try {
				observer.updateClientRemoval(client.getName());
			} catch (RemoteException e) {
				Logger.error( "Cannot update client removal in the observer {" + ((MUIA)observer).toString() + "}" );
			}
		}
	}
	
	@Override
	public Boolean addOriginalMUIAObserver(OriginalMUIAObserver observer) {
		if( isCopy() ) {
			return null;
		}
		
		Boolean addObsOperation = false;
		if (!originalMUIAObservers.contains(observer)) {
			Boolean updateObsOperation = true;
			try {
				updateObserverChannelAllData(observer);
			} catch (UnableToUpdateObserverException e) {
				updateObsOperation = false;
				Logger.error( "Unable to send all channels and update the observer {"
						+ ((MUIA)observer).toString() + "}" );
			}
			
			if( updateObsOperation ) {
				addObsOperation = originalMUIAObservers.add(observer);
			}
		}

		return addObsOperation;
	}

	@Override
	public Boolean removeOriginalMUIAObserver(OriginalMUIAObserver observer) {
		if( isCopy() ) {
			return null;
		}
		
		return originalMUIAObservers.remove(observer);
	}
	
	@Override
	public void notifyChannelAddition(Channel channel) {
		if( isCopy() ) {
			return;
		}
		
		SerializableHandler<Channel> sh = new SerializableHandler<Channel>();
		byte[] serializedChannel = sh.serialize(channel);
		
		for( OriginalMUIAObserver observer : originalMUIAObservers ) {
			try {
				observer.updateChannelAddition(serializedChannel);
			} catch (RemoteException e) {
				Logger.error( "Cannot update channel addition in the observer {" + ((MUIA)observer).toString() + "}" );
			}
		}
	}

	@Override
	public void notifyChannelRemoval(Channel channel) {
		if( isCopy() ) {
			return;
		}
		
		for( OriginalMUIAObserver observer : originalMUIAObservers ) {
			try {
				observer.updateChannelRemoval( channel.getId() );
			} catch (RemoteException e) {
				Logger.error( "Cannot update channel removal in the observer {" + ((MUIA)observer).toString() + "}" );
			}
		}
	}
	
	@Override
	public void notifyChannelSubscribe(Channel channel, Client client) {
		if( isCopy() ) {
			return;
		}
		
		for( OriginalMUIAObserver observer : originalMUIAObservers ) {
			try{
				observer.updateChannelSubscribe( channel.getId(), client.getName() );
			} catch(RemoteException e) {
				Logger.error( "Cannot update channel subscribe in the observer {" 
						+ ((MUIA)observer).toString() + "}" );
			}
		}
	}

	@Override
	public void notifyChannelUnsubscribe(Channel channel, Client client) {
		if( isCopy() ) {
			return;
		}
		
		for( OriginalMUIAObserver observer : originalMUIAObservers ) {
			try{
				observer.updateChannelUnsubscribe( channel.getId(), client.getName() );
			} catch(RemoteException e) {
				Logger.error( "Cannot update channel unsubscribe in the observer {"
						+ ((MUIA)observer).toString() + "}" );
			}
		}
	}

	@Override
	public void updateClientAddition(byte[] serializedClient) {
		if( !isCopy() ) {
			return;
		}
		
		SerializableHandler<Client> sh = new SerializableHandler<Client>();
		Client client = sh.deserialize(serializedClient);
		addClient(client);
	}

	@Override
	public void updateClientRemoval(String clientName) {
		if( !isCopy() ) {
			return;
		}
		
		for( Client client : clients ) {
			if( client.getName().equals(clientName) ) {
				removeClient(client);
				break;
			}
		}
	}

	@Override
	public void updateChannelAddition(byte[] serializedChannel) {
		if( !isCopy() ) {
			return;
		}
		
		SerializableHandler<Channel> sh = new SerializableHandler<Channel>();
		Channel channel = sh.deserialize(serializedChannel);
		addChannel(channel);
	}

	@Override
	public void updateChannelRemoval(String channelId) {
		if( !isCopy() ) {
			return;
		}
		
		for( Channel channel : channels ) {
			if( channel.getId().equals(channelId) ) {
				removeChannel(channel);
				break;
			}
		}
	}
	
	@Override
	public void updateChannelSubscribe(String channelId, String clientName) throws RemoteException {
		if( !isCopy() ) {
			return;
		}
		
		Channel channel = getChannelReference(channelId);
		Client client = getClientReference(clientName);
		
		if( channel == null || client == null ) {
			throw new RemoteException();
		}
		
		channel.subscribeClient(client);
	}

	@Override
	public void updateChannelUnsubscribe(String channelId, String clientName) throws RemoteException {
		if( !isCopy() ) {
			return;
		}
		
		Channel channel = getChannelReference(channelId);
		Client client = getClientReference(clientName);
		
		if( channel == null || client == null ) {
			throw new RemoteException();
		}
		
		channel.unsubscribeClient(client);
	}

	@Override
	public void onChannelSubscribe(Channel channel, Client client) {
		notifyChannelSubscribe(channel, client);
	}

	@Override
	public void onChannelUnsubscribe(Channel channel, Client client) {
		notifyChannelUnsubscribe(channel, client);
	}
}
