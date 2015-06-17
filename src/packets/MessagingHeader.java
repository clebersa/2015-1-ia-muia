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
public class MessagingHeader extends MessageHeader {

	private Channel channel;
	private Application source;
	private Application[] destination;

	public MessagingHeader(Channel channel, Application source,
			Application[] destination) {
		this.channel = channel;
		this.source = source;
		this.destination = destination;
	}

	public MessagingHeader() {
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Application getSource() {
		return source;
	}

	public void setSource(Application source) {
		this.source = source;
	}

	public Application[] getDestination() {
		return destination;
	}

	public void setDestination(Application[] destination) {
		this.destination = destination;
	}

	@Override
	public MessageHeader deserialize(JsonElement json, Type typeOfT, 
			JsonDeserializationContext context) throws JsonParseException {
		//TODO
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
