package packets;

import application.Client;
import application.Main;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import receiving.InvalidValueException;
import receiving.MissingElementException;
import sending.Channel;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class SubscribeHeader extends ChannelingHeader {

	private Channel channel;
	private Client client;
	private boolean subscribe;

	public SubscribeHeader(Channel channel, Client client, boolean subscribe) {
		this.channel = channel;
		this.client = client;
	}

	public SubscribeHeader() {
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Client getClient() {
		return client;
	}

	public boolean isSubscribe() {
		return subscribe;
	}
	
	public void setApplication(Client client) {
		this.client = client;
	}

	public void setSubscribe(boolean subscribe) {
		this.subscribe = subscribe;
	}

	@Override
	public MessageHeader deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (json.getAsJsonObject().get("channel") == null) {
			throw new MissingElementException("'channel' not found!");
		}
		channel = Main.getSelf().getChannelReference(
				json.getAsJsonObject().get("channel").getAsString());
		if(channel == null){
			throw new InvalidValueException("channel '" 
					+ json.getAsJsonObject().get("channel").getAsString()
					+"' not found.");
		}
		
		if (json.getAsJsonObject().get("client") == null) {
			throw new MissingElementException("'client' not found!");
		}
		client = Main.getSelf().getClientReference(
				json.getAsJsonObject().get("client").getAsString());
		if(client == null){
			throw new InvalidValueException("client '" 
					+ json.getAsJsonObject().get("client").getAsString()
					+"' not found.");
		}
		
		if (json.getAsJsonObject().get("subscribe") == null) {
			throw new MissingElementException("'subscribe' not found!");
		}
		subscribe = json.getAsJsonObject().get("subscribe").getAsBoolean();
		
		return this;
	}

	@Override
	public JsonElement serialize(MessageHeader t, Type type, JsonSerializationContext jsc) {
		//This classe will never be serialized.
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
