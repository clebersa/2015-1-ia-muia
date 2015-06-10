package application.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface to be implemented from Objects that wants to receive notifications of addition and removal of
 * applications from {@link application.interfaces.MUIAObservable} where is registred.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public interface MUIAObserver extends Remote {
	/**
	 * Method to receive the update of application addition from the {@link application.interfaces.MUIAObservable} object.
	 * @param application - {@link classes.Application} that was added in the {@link application.interfaces.MUIAObservable} object.
	 * @throws RemoteException 
	 */
	public void updateClientAddition( byte[] serializedClient ) throws RemoteException;
	
	/**
	 * Method to receive the update of application removal from the {@link application.interfaces.MUIAObservable} object.
	 * @param application - {@link classes.Application} that was removed from the {@link application.interfaces.MUIAObservable} object.
	 */
	public void updateClientRemoval( byte[] serializedClient ) throws RemoteException;
	
	public void updateChannelAddition( byte[] serializedChannel ) throws RemoteException;
	public void updateChannelRemoval( byte[] serializedChannel ) throws RemoteException;
	public void updateChannelSubscribe( String channelId, String clientName ) throws RemoteException;
	public void updateChannelUnsubscribe( String channelId, String clientName ) throws RemoteException;
}
