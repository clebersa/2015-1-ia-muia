package sending.interfaces;

import application.Client;

public interface ChannelObservable {
	public Boolean addObserver( ChannelObserver observer );
	public Boolean removeObserver( ChannelObserver observer );
	public void notifySubscribe( Client client );
	public void notifyUnsubscribe( Client client );
}
