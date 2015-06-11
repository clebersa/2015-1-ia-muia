package application.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import sending.Channel;
import application.Client;

public interface OriginalMUIAObservable extends Remote {
	public Boolean addOriginalMUIAObserver( OriginalMUIAObserver observer ) throws RemoteException;
	public Boolean removeOriginalMUIAObserver( OriginalMUIAObserver observer ) throws RemoteException;
	
	/**
	 * Notify the observers that a new channel was added in the {@link application.interfaces.CopyMUIAObservable} object.
	 * @param channel - {@link sending.Channel} added in the {@link application.interfaces.CopyMUIAObservable} object.
	 * @throws RemoteException if the {@link application.interfaces.CopyMUIAObserver} have lost connection with the 
	 * {@link application.interfaces.CopyMUIAObservable} or just can't communicate with it.
	 */
	public void notifyChannelAddition( Channel channel ) throws RemoteException;
	
	/**
	 * Notify the observers that a channel was removed from the {@link application.interfaces.CopyMUIAObservable} object.
	 * @param channel - {@link sending.Channel} removed from the {@link application.interfaces.CopyMUIAObservable} object.
	 * @throws RemoteException if the {@link application.interfaces.CopyMUIAObserver} have lost connection with the 
	 * {@link application.interfaces.CopyMUIAObservable} or just can't communicate with it.
	 */
	public void notifyChannelRemoval( Channel channel ) throws RemoteException;
	
	public void notifyChannelSubscribe( Channel channel, Client client ) throws RemoteException;
	public void notifyChannelUnsubscribe( Channel channel, Client client ) throws RemoteException;
}
