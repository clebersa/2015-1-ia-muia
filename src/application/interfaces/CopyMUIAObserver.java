package application.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface to be implemented from Objects that wants to receive notifications of addition and removal of
 * applications from {@link application.interfaces.CopyMUIAObservable} where is registred.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public interface CopyMUIAObserver extends Remote {
	/**
	 * Method to receive the update of application addition from the {@link application.interfaces.CopyMUIAObservable} object.
	 * @param application - {@link classes.Application} that was added in the {@link application.interfaces.CopyMUIAObservable} object.
	 * @throws RemoteException 
	 */
	public void updateClientAddition( byte[] serializedClient ) throws RemoteException;
	
	/**
	 * Method to receive the update of application removal from the {@link application.interfaces.CopyMUIAObservable} object.
	 * @param application - {@link classes.Application} that was removed from the {@link application.interfaces.CopyMUIAObservable} object.
	 */
	public void updateClientRemoval( String clientName ) throws RemoteException;
}
