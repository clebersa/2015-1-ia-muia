package application.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import sending.Channel;
import application.Client;


/**
 * Interface to be implemented from Objects that wants to notify addition and removal of client and channels to the
 * registered {@link application.interfaces.CopyMUIAObserver}.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public interface CopyMUIAObservable extends Remote {
	/**
	 * Verify if the {@link application.interfaces.CopyMUIAObservable} is alive.
	 * @return Boolean true if the {@link application.interfaces.CopyMUIAObservable} is alive or false when not.
	 * @throws RemoteException if the {@link application.interfaces.CopyMUIAObserver} have lost connection with the 
	 * {@link application.interfaces.CopyMUIAObservable} or just can't communicate with it.
	 */
	public Boolean isAlive() throws RemoteException;
	
	/**
	 * Add and synchronize a new observer in the {@link application.interfaces.CopyMUIAObservable} observers list.
	 * @param observer - {@link application.interfaces.CopyMUIAObserver} to be added in the observers list.
	 * @return Boolean true if the observer was successfully added and synchronized in the observers list or false when
	 * not.
	 * @throws RemoteException if the {@link application.interfaces.CopyMUIAObserver} have lost connection with the 
	 * {@link application.interfaces.CopyMUIAObservable} or just can't communicate with it.
	 */
	public Boolean addCopyMUIAObserver( CopyMUIAObserver observer ) throws RemoteException;

	/**
	 * Remove a {@link application.interfaces.CopyMUIAObserver} from the list of observers of 
	 * {@link application.interfaces.CopyMUIAObservable} object.
	 * @param observer - {@link application.interfaces.CopyMUIAObserver} to be removed from the observers list.
	 * @return Boolean true if the observer was successfully removed from the observers list or Boolean false when not.
	 * @throws RemoteException if the {@link application.interfaces.CopyMUIAObserver} have lost connection with the 
	 * {@link application.interfaces.CopyMUIAObservable} or just can't communicate with it.
	 */
	public Boolean removeCopyMUIAObserver( CopyMUIAObserver observer ) throws RemoteException;
	
	/**
	 * Notify the observers that a new client was added in the {@link application.interfaces.CopyMUIAObservable} object.
	 * @param client - {@link application.Client} added in the {@link application.interfaces.CopyMUIAObservable} object.
	 * @throws RemoteException if the {@link application.interfaces.CopyMUIAObserver} have lost connection with the 
	 * {@link application.interfaces.CopyMUIAObservable} or just can't communicate with it.
	 */
	public void notifyClientAddition( Client client ) throws RemoteException;
	
	/**
	 * Notify the observers that a client was removed from the {@link application.interfaces.CopyMUIAObservable} object.
	 * @param client - {@link application.Client} removed from the {@link application.interfaces.CopyMUIAObservable} object.
	 * @throws RemoteException if the {@link application.interfaces.CopyMUIAObserver} have lost connection with the 
	 * {@link application.interfaces.CopyMUIAObservable} or just can't communicate with it.
	 */
	public void notifyClientRemoval( Client client ) throws RemoteException;
}
