package application;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Interface to be implemented from Objects that needs to notify addition and removal of applications to the registered
 * {@link application.MUIAObserver}.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public interface MUIAObservable extends Remote {
	/**
	 * Method to add a new observer thats can receive notifications of application addition or removal in the 
	 * observable object.
	 * @param observer - {@link application.MUIAObserver} to be added in the observers list.
	 * @return Boolean true if the observer was successfully added in the observers list or Boolean false when
	 * not.
	 */
	public Boolean addObserver( MUIAObserver observer ) throws RemoteException;

	/**
	 * Method to remove a observer from the list of observers that wants to receive notifications of application
	 * addition or removal in the observable object.
	 * @param observer - {@link application.MUIAObserver} to be removed from the observers list.
	 * @return Boolean true if the observer was successfully removed from the observers list or Boolean false when not.
	 */
	public Boolean removeObserver( MUIAObserver observer ) throws RemoteException;
	
	/**
	 * Method to notify the observers that was added a new application in the observable object.
	 * @param application - {@link classes.Application} added in the observable object.
	 */
	public void notifyClientAddition( Client client ) throws RemoteException;
	
	/**
	 * Method to notify the observers that an application was removed from the observable object.
	 * @param application - {@link classes.Application} removed from the observable object.
	 */
	public void notifyClientRemoval( Client client ) throws RemoteException;
}
