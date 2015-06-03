package packets;

import application.Application;
import sending.Channel;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class SubscribeHeader extends ChannelingHeader {

	private Channel channel;
	private Application application;

	public SubscribeHeader(Channel channel, Application application) {
		this.channel = channel;
		this.application = application;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

}
