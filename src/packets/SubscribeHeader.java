package packets;

import application.Application;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
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

	public SubscribeHeader() {
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

	@Override
	public MessageHeader deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		//TODO
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
