package application;

import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import common.Logger;
import common.SerializableHandler;
import sending.Channel;
import sending.interfaces.ChannelObserver;
import application.exceptions.UnableToCreateMUIAException;
import application.exceptions.UnableToUpdateObserverException;
import application.interfaces.CopyMUIAObserver;
import application.interfaces.MUIAObservable;
import application.interfaces.OriginalMUIAObserver;

public class OriginalMUIA extends MUIA implements OriginalMUIAObserver, ChannelObserver, MUIAObservable {
	/**
	 * Unique serial long used to identify the class in the serialization and deserialization.
	 */
	private static final long serialVersionUID = 2168894143033011129L;
	
	/**
	 * List containing the copy MUIA observers.
	 */
	private ArrayList<CopyMUIAObserver> copyMUIAobservers = new ArrayList<CopyMUIAObserver>();
	
	/**
	 * List containing the original MUIA observers.
	 */
	private ArrayList<OriginalMUIAObserver> originalMUIAObservers = new ArrayList<OriginalMUIAObserver>();
	
	/**
	 * List containing the Copy MUIAs that are the known MUIAs of the original MUIA. 
	 */
	private ArrayList<CopyMUIA> knownMUIAs = new ArrayList<CopyMUIA>();
	
	/**
	 * List containing the active channels in the MUIA network.
	 */
	private ArrayList<Channel> channels = new ArrayList<Channel>();

	/**
	 * Creates a new instance of the OriginalMUIA class.
	 * @param name - String containing the name of the MUIA.
	 * @param address - String containing the IP address of the MUIA.
	 * @param port - Integer containing the port where the MUIA is running.
	 * @param registryPort - Integer containing the port of registry where the MUIA is binded.
	 * @throws UnknownHostException when the address parameter is not found.
	 * @throws UnableToCreateMUIAException when is not possible to create the remote reference of MUIA or when have a
	 * problem to register the original MUIA host in the registry.
	 */
	public OriginalMUIA(String name, String address, Integer port, Integer registryPort) throws UnknownHostException,
			UnableToCreateMUIAException {
		super(name, address, port, registryPort);
		
		try {
			registerMUIAHostInRegistry();
		} catch (RemoteException | AlreadyBoundException e) {
			Logger.error("Unable to register MUIA host in the registry");
			throw new UnableToCreateMUIAException();
		}
	}
	
	/**
	 * Registers the original MUIA in your registry.
	 * After the register is done, and only if that occurs successfully, the MUIA is changed to alive.
	 * @throws RemoteException when an error occurs while binding the original MUIA in your registry.
	 * @throws AlreadyBoundException when a application with the same name of the original MUIA host is registered in
	 * the registry.
	 */
	private void registerMUIAHostInRegistry() throws RemoteException, AlreadyBoundException {
		Registry registry = LocateRegistry.getRegistry(address.getHostAddress(), registryPort);
		registry.bind(name, ((MUIAObservable)selfRemoteReference));
		alive = true;
		
		Logger.info("Original MUIA host successfully registered in the registry");
	}
	
	/**
	 * Gets the {@link application.Client} instance in the MUIA clients list or in your network based in a client name.
	 * @param clientName - String containing the name of client to be searched.
	 * @return {@link application.Client} instance of searched client name or {@value null} if have no client
	 * registered with the specified client name in the MUIA network.
	 */
	@Override
	public Client getClientReference( String clientName ) {
		Client client = super.getClientReference(clientName);
		
		if( client != null ) {
			return client;
		}
		
		for( CopyMUIA knownMUIA : knownMUIAs ) {
			if( !knownMUIA.isAlive() ) {
				continue;
			}
			
			client = knownMUIA.getClientReference(clientName);
			if ( client != null ) {
				return client;
			}
		}
		
		return null;
	}
	
	/**
	 * Adds (register) a client in the MUIA.
	 * If a client with the same name of the client to be registered is already registered in
	 * the MUIA, the register need to be canceled.
	 * If the register occurs smoothly in the original MUIA, the copy observers are notified.
	 * @param client - {@link application.client} to be registered in the MUIA.
	 * @return Boolean true if the client was successfully added in the MUIA or false if a 
	 * client with the same name is already registered in the MUIA or the addition operation
	 * have a error.
	 */
	@Override
	public Boolean addClient(Client client) {
		Boolean operation = super.addClient(client);
		
		if( operation ) {
			notifyClientAddition(client);
		}

		return operation;
	}

	/**
	 * Removes (unregister) a client in the MUIA.
	 * If the removal occurs smoothly in the original MUIA, the copy observers are notified.
	 * @param client - {@link application.Client} to be removed in the MUIA.
	 * @return Boolean true if the client was successfully removed from the MUIA or false
	 * if the client doesn't exists in the MUIA or the removal operation have a error.
	 */
	@Override
	public Boolean removeClient(Client client) {
		Boolean operation = super.removeClient(client);

		if ( operation ) {
			notifyClientRemoval(client);
		}

		return operation;
	}
	
	/**
	 * Gets the {@link sending.Channel} instance in the original MUIA channels list based in a channel id.
	 * @param channelId - String containing the id of the channel to be searched.
	 * @return {@link sending.Channel} instance of searched channel id or {@value null} if have no channel registered
	 * with the specified id in the original MUIA.
	 */
	public Channel getChannelReference( String channelId ) {
		for( Channel channel : channels ) {
			if( channel.getId().equals(channelId) ) {
				return channel;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the {@link application.CopyMUIA} instance in the MUIA knownMuias list based in the MUIA name.
	 * @param muiaName String containing the name of the known MUIA to be searched.
	 * @return {@link application.CopyMUIA} instance of searched MUIA name or {@value null} if the MUIA don't
	 * knows the searched MUIA name.
	 */
	public CopyMUIA getMUIAReference(String muiaName) {		
		for( CopyMUIA knownMUIA : knownMUIAs ) {
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
	 * Gets the {@link application.MUIA} instance that contains the specified client.
	 * @param destination - {@link application.Client} thats the MUIA need contains.
	 * @return {@link application.MUIA} instance thats contains the specified {@link application.Client} or {@value 
	 * null } if no one MUIA in the MUIA network contains the specified client. 
	 */
	public MUIA getMUIAByClient(Client destination) {
		Client client = super.getClientReference(destination.getName());
		
		if( client != null ) {
			return this;
		}
		
		for( CopyMUIA knownMUIA : knownMUIAs ) {
			if( !knownMUIA.isAlive() ) {
				continue;
			}
			
			client = knownMUIA.getClientReference(destination.getName());
			if ( client != null ) {
				return knownMUIA;
			}
		}
		
		return null;
	}
	
	/**
	 * Adds (register) a channel in the original MUIA.
	 * If a channel with the same id of the channel to be registered is already registered in
	 * the original MUIA, the register will be canceled.
	 * If the register occurs smoothly, the original observers will be notified.
	 * When the channel is registered in the original MUIA, the MUIA will be added in the
	 * observers list of the channel.
	 * @param channel - {@link sending.Channel} to be registered in the MUIA instance.
	 * @return Boolean true if the channel was successfully added in the MUIA or false if
	 * a channel with the same id is already registered in the MUIA or the addition
	 * operation have a error.
	 */
	public Boolean addChannel( Channel channel ) {
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
	 * Removes (unregister) a channel from the original MUIA.
	 * If the removal occurs smoothly, the original observers will be notified.
	 * When the channel is removed from the original MUIA, the MUIA will be removed from the
	 * observers list of the channel.
	 * @param channel - {@link sending.Channel} to be removed in the MUIA.
	 * @return Boolean true if the channel was successfully removed from the MUIA or false
	 * if the channel doesn't exists in the MUIA or the removal operation have a error.
	 */
	public Boolean removeChannel( Channel channel ){
		Boolean operation = channels.remove(channel);

		if (operation) {
			channel.removeObserver(this);
			notifyChannelRemoval(channel);
		}

		return operation;
	}
	
	/**
	 * Adds (register) a copy MUIA in the original MUIA known MUIAs list.
	 * When the copy MUIA is registered in the original MUIA, the original MUIA tries to
	 * register in the original observers list of the remote original MUIA of the copy.
	 * @param muia - {@link application.CopyMUIA} to be added in the original MUIA known network.
	 * @return Boolean true if the Copy MUIA was successfully added in the original MUIA or false
	 * if the Copy MUIA with the same name is registered in the original MUIA or the add operation
	 * have failed.
	 */
	public Boolean addKnownMUIA( CopyMUIA muia ) {
		Boolean exists = false;
		for( CopyMUIA knownMUIA : knownMUIAs ) {
			if (knownMUIA.getName().equals(muia.getName())) {
				exists = true;
				break;
			}
		}
		
		Boolean operation = false;
		if (!exists) {
			operation = knownMUIAs.add(muia);
		}

		return operation;
	}
	
	/**
	 * Removes (unregister) a copy MUIA from the original MUIA known MUIAs list.
	 * @param muia - {@link application.CopyMUIA} to be removed from the original MUIA.
	 * @return Boolean true if the copy MUIA was successfully removed from the original MUIA or
	 * false if the copy MUIA doesn't exists in the MUIA or the removal operation have a error.
	 */
	public Boolean removeKnownMUIA( CopyMUIA muia ) {
		Boolean operation = knownMUIAs.remove(muia);

		if (operation) {
			try {
				muia.getRemoteOriginalMUIA().removeOriginalMUIAObserver(this);
			} catch (RemoteException | NullPointerException e) {
				Logger.warning( "Cannot unregister the original MUIA from the original MUIA observer list of MUIA {"
						+ muia + "}");
			}
		}

		return operation;
	}
	
	/**
	 * Registers the original MUIA in the original observers list of the remote original MUIA
	 * of the copy
	 * @param knownCopyMUIA - {@link application.CopyMUIA} copy of remote original MUIA observable.
	 * @throws RemoteException when the MUIA copy not exists in the original MUIA known list or if
	 * the copy remote original MUIA observer addition fails.
	 */
	public void registerKnownMuiaOriginalObserver(CopyMUIA knownCopyMUIA) throws RemoteException {
		Boolean exists = false;
		for( CopyMUIA knownMUIA : knownMUIAs ) {
			if (knownMUIA.getName().equals(knownCopyMUIA.getName())) {
				exists = true;
				break;
			}
		}
		
		if( !exists ) {
			throw new RemoteException();
		}
		
		try {
			knownCopyMUIA.getRemoteOriginalMUIA().addOriginalMUIAObserver(this);
		} catch (RemoteException | NullPointerException e) {
			Logger.warning( "Failed to register the original MUIA like a original MUIA observer in the MUIA {"
					+ ((MUIA)knownCopyMUIA).toString() + "}" );
			throw new RemoteException();
		}
	}
	
	/**
	 * Updates {@link application.interfaces.CopyMUIAObserver} with all clients registered in the original MUIA.
	 * @param observer - {@link application.interfaces.CopyMUIAObserver} what will be updated with all clients
	 * registered in the original MUIA.
	 * @throws UnableToUpdateObserverException when the {@link application.interfaces.CopyMUIAObserver} can't receive
	 * a client update.
	 */
	private void updateObserverClientAllData(CopyMUIAObserver observer) throws UnableToUpdateObserverException {
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
	 * @param observer - {@link application.interfaces.OriginalMUIAObserver} what will be updated with all channels
	 * registered in the original MUIA.
	 * @throws UnableToUpdateObserverException when the {@link application.interfaces.OriginalMUIAObserver} can't
	 * receive a channel update.
	 */
	private void updateObserverChannelAllData( OriginalMUIAObserver observer ) throws UnableToUpdateObserverException {
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
	
	@Override
	public Boolean addCopyMUIAObserver(CopyMUIAObserver observer) {
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
		return copyMUIAobservers.remove(observer);
	}

	@Override
	public void notifyClientAddition(Client client) {
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
		return originalMUIAObservers.remove(observer);
	}
	
	@Override
	public void notifyChannelAddition(Channel channel) {
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
		for( OriginalMUIAObserver observer : originalMUIAObservers ) {
			try {
				observer.updateChannelRemoval(channel.getId());
			} catch (RemoteException e) {
				Logger.error( "Cannot update channel removal in the observer {" + ((MUIA)observer).toString() + "}" );
			}
		}
	}
	
	@Override
	public void notifyChannelSubscribe(Channel channel, Client client) {
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
	public void updateChannelAddition(byte[] serializedChannel) {
		SerializableHandler<Channel> sh = new SerializableHandler<Channel>();
		Channel channel = sh.deserialize(serializedChannel);
		addChannel(channel);
		
		Logger.debug("Channel " + channel.getId() + " added in the MUIA {" + this + "}");
	}

	@Override
	public void updateChannelRemoval(String channelId) {
		for( Channel channel : channels ) {
			if( channel.getId().equals(channelId) ) {
				removeChannel(channel);
				break;
			}
		}
	}
	
	@Override
	public void updateChannelSubscribe(String channelId, String clientName) throws RemoteException {
		Channel channel = getChannelReference(channelId);
		Client client = getClientReference(clientName);
		
		if( channel == null || client == null ) {
			throw new RemoteException();
		}
		
		channel.subscribeClient(client);
	}

	@Override
	public void updateChannelUnsubscribe(String channelId, String clientName) throws RemoteException {
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
