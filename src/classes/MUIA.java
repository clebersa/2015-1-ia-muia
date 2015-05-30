package classes;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

import main.Main;
import interfaces.MUIAObservable;
import interfaces.MUIAObserver;

/**
 * Class of a MUIA in the MUIA server application.
 * This class identify a MUIA instance that contains the instance location and management of instance registered
 * applications.
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
	 * Constructor method.
	 * Initialize the variables.
	 */
	public MUIA( Boolean isRemote ) {
		this.name = "MUIA1";
		try {
			this.address = InetAddress.getByName("localhost");
		} catch (UnknownHostException e1) {}
		this.registryPort = 2002;
		
		this.isRemote = isRemote;
		this.observers = new ArrayList<>();
		this.clients = new ArrayList<>();
		
		if( !isRemote ) {
			try {
				MUIAObservable MObservableStub = (MUIAObservable) UnicastRemoteObject.exportObject( this, 0 );
				Main.getRegistry().bind( this.name , MObservableStub );
			} catch (Exception e) {
				System.out.println( "Error ao exportar objeto para o RMI do MUIA original: " + e.getMessage() );
			}
		} else {
            try {
            	MUIAObserver MObserverStub = (MUIAObserver) UnicastRemoteObject.exportObject( this, 0 );
            	
            	Registry registry = LocateRegistry.getRegistry( this.address.getHostAddress(), this.registryPort );
                MUIAObservable MObservableStub = (MUIAObservable) registry.lookup( this.name );
				MObservableStub.addObserver( MObserverStub );
			} catch (Exception e) {
				System.out.println( "Error ao se inscrever como observador do MUIA original: " + e.getMessage() );
			}
		}
	}
	
	public MUIA() {
		this( false );
	}
	
	/**
	 * Method to add (register) a application in the MUIA instance.
	 * If the addition occurs smoothly, the observers are updated.
	 * @param application - {@link classes.Application} to be registered in the MUIA instance.
	 * @return Boolean true if the application was successfully added in the MUIA instance or false if the application
	 * is already contained in the MUIA instance or the addition operation have a error.
	 */
	public Boolean addClient( Client client ) {
		Boolean operation = false;
		
		if( !this.clients.contains( client ) ) {
			operation = this.clients.add( client );
		}
		
		if( operation == true ) {
			this.notifyClientAddition( client );
		}
		
		return operation;
	}
	
	/**
	 * Method to remove (unregister) a application in the MUIA instance.
	 * If the removal occours smoothly, the observers are updated.
	 * @param application - {@link classes.Application} to be removed in the MUIA instance.
	 * @return Boolean true if the application was successfully removed from the MUIA instance or false if the
	 * application doesn't exists in the MUIA instance or the removal operation have a error.
	 */
	public Boolean removeClient( Client client ) {
		Boolean operation = this.clients.remove( client );
		
		if( operation == true ) {
			this.notifyClientRemoval( client );
		}
		
		return operation;
	}
	
	@Override
	public Boolean addObserver(MUIAObserver observer) {
		System.out.println( "Adicionando observer...");
		Boolean operation = false;
		
		if( !this.observers.contains( observer ) ) {
			operation = this.observers.add( observer );
		}
		
		if( operation == true ) {
			Iterator<Client> it = this.clients.iterator();
			while( it.hasNext() ) {
				Client client = it.next();
				byte[] serializedClient = Application.serialize(client);
				
				try {
					observer.updateClientAddition( serializedClient );
				} catch (RemoteException e) {
					System.out.println( this.getName() + " - Não foi atualizar o observador " + e.getMessage() );
				}
			}
		}
		return operation;
	}

	@Override
	public Boolean removeObserver(MUIAObserver observer) {
		Boolean operation = this.observers.remove( observer );
		return operation;
	}

	@Override
	public void notifyClientAddition(Client client) {
		Iterator<MUIAObserver> iterator = this.observers.iterator();
		while( iterator.hasNext() ) {
			MUIAObserver observer = iterator.next();
			try {
				byte[] serializedClient = Application.serialize(client);
				observer.updateClientAddition( serializedClient );
			} catch (RemoteException e) {
				System.out.println( this.getName() + " - Não foi possível notificar um observador: " + e.getMessage() );
			}
		}
	}

	@Override
	public void notifyClientRemoval(Client client) {
		Iterator<MUIAObserver> iterator = this.observers.iterator();
		while( iterator.hasNext() ) {
			MUIAObserver observer = iterator.next();
			try {
				byte[] serializedClient = Application.serialize( client );
				observer.updateClientRemoval( serializedClient );
			} catch (RemoteException e) {
				System.out.println( this.getName() + " - Não foi possível notificar um observador: " + e.getMessage() );
			}
		}
	}

	@Override
	public void updateClientAddition(byte[] serializedClient) {
		Client client = (Client)Application.deserialize(serializedClient);
		
		Boolean exists = false;
		Iterator<Client> it = this.clients.iterator();
		while( it.hasNext() ) {
			Client hostedClient = it.next();
			if( hostedClient.equals( client ) ) {
				exists = true;
				break;
			}
		}
		
		if( exists == false ) {
			this.clients.add(client);
		}
	}

	@Override
	public void updateClientRemoval(byte[] client) {
		this.clients.remove( client );
	}
	
}
