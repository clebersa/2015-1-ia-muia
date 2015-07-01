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
public class AvailabilityHeader extends MessageHeader {

	private Client client;
	private boolean available;

	public AvailabilityHeader(Channel channel, boolean available) {
		this.client = client;
		this.available = available;
	}

	public AvailabilityHeader() {
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Override
	public MessageHeader deserialize(JsonElement json, Type typeOfT, 
			JsonDeserializationContext context) throws JsonParseException {
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
		
		if (json.getAsJsonObject().get("available") != null) {
			available = json.getAsJsonObject().get("destinations").getAsBoolean();
		}else{
			throw new MissingElementException("'available' not found!");
		}
		
		return this;
	}

	@Override
	public JsonElement serialize(MessageHeader t, Type type, JsonSerializationContext jsc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
