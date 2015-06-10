package application.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import sending.Channel;
import application.Client;


/**
 * Interface to be implemented from Objects that wants to notify addition and removal of client and channels to the
 * registered {@link application.interfaces.MUIAObserver}.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public interface MUIAObservable extends Remote {
	/**
	 * Verify if the {@link application.interfaces.MUIAObservable} is alive.
	 * @return Boolean true if the {@link application.interfaces.MUIAObservable} is alive or false when not.
	 * @throws RemoteException if the {@link application.interfaces.MUIAObserver} have lost connection with the 
	 * {@link application.interfaces.MUIAObservable} or just can't communicate with it.
	 */
	public Boolean isAlive() throws RemoteException;
	
	/**
	 * Add and synchronize a new observer in the {@link application.interfaces.MUIAObservable} observers list.
	 * @param observer - {@link application.interfaces.MUIAObserver} to be added in the observers list.
	 * @return Boolean true if the observer was successfully added and synchronized in the observers list or false when
	 * not.
	 * @throws RemoteException if the {@link application.interfaces.MUIAObserver} have lost connection with the 
	 * {@link application.interfaces.MUIAObservable} or just can't communicate with it.
	 */
	public Boolean addObserver( MUIAObserver observer ) throws RemoteException;

	/**
	 * Remove a {@link application.interfaces.MUIAObserver} from the list of observers of 
	 * {@link application.interfaces.MUIAObservable} object.
	 * @param observer - {@link application.interfaces.MUIAObserver} to be removed from the observers list.
	 * @return Boolean true if the observer was successfully removed from the observers list or Boolean false when not.
	 * @throws RemoteException if the {@link application.interfaces.MUIAObserver} have lost connection with the 
	 * {@link application.interfaces.MUIAObservable} or just can't communicate with it.
	 */
	public Boolean removeObserver( MUIAObserver observer ) throws RemoteException;
	
	/**
	 * Notify the observers that a new client was added in the {@link application.interfaces.MUIAObservable} object.
	 * @param client - {@link application.Client} added in the {@link application.interfaces.MUIAObservable} object.
	 * @throws RemoteException if the {@link application.interfaces.MUIAObserver} have lost connection with the 
	 * {@link application.interfaces.MUIAObservable} or just can't communicate with it.
	 */
	public void notifyClientAddition( Client client ) throws RemoteException;
	
	/**
	 * Notify the observers that a client was removed from the {@link application.interfaces.MUIAObservable} object.
	 * @param client - {@link application.Client} removed from the {@link application.interfaces.MUIAObservable} object.
	 * @throws RemoteException if the {@link application.interfaces.MUIAObserver} have lost connection with the 
	 * {@link application.interfaces.MUIAObservable} or just can't communicate with it.
	 */
	public void notifyClientRemoval( Client client ) throws RemoteException;
	
	/**
	 * Notify the observers that a new channel was added in the {@link application.interfaces.MUIAObservable} object.
	 * @param channel - {@link sending.Channel} added in the {@link application.interfaces.MUIAObservable} object.
	 * @throws RemoteException if the {@link application.interfaces.MUIAObserver} have lost connection with the 
	 * {@link application.interfaces.MUIAObservable} or just can't communicate with it.
	 */
	public void notifyChannelAddition( Channel channel ) throws RemoteException;
	
	/**
	 * Notify the observers that a channel was removed from the {@link application.interfaces.MUIAObservable} object.
	 * @param channel - {@link sending.Channel} removed from the {@link application.interfaces.MUIAObservable} object.
	 * @throws RemoteException if the {@link application.interfaces.MUIAObserver} have lost connection with the 
	 * {@link application.interfaces.MUIAObservable} or just can't communicate with it.
	 */
	public void notifyChannelRemoval( Channel channel ) throws RemoteException;
	
	public void notifyChannelSubscribe( Channel channel, Client client ) throws RemoteException;
	public void notifyChannelUnsubscribe( Channel channel, Client client ) throws RemoteException;
}
