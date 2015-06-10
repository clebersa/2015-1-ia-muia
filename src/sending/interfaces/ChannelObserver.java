package sending.interfaces;

import sending.Channel;
import application.Client;

public interface ChannelObserver {
	public void onChannelSubscribe( Channel channel, Client client );
	public void onChannelUnsubscribe( Channel channel, Client client );
}
