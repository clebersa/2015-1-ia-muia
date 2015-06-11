package application.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OriginalMUIAObserver extends Remote {
	public void updateChannelAddition( byte[] serializedChannel ) throws RemoteException;
	public void updateChannelRemoval( String channelId ) throws RemoteException;
	public void updateChannelSubscribe( String channelId, String clientName ) throws RemoteException;
	public void updateChannelUnsubscribe( String channelId, String clientName ) throws RemoteException;
}
