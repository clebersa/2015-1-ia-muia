package packets;

import application.Client;
import application.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import common.Logger;
import java.lang.reflect.Type;
import receiving.InvalidValueException;
import receiving.MissingElementException;
import sending.Channel;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class MessagingHeader extends MessageHeader {

	private Channel channel;
	private Client source;
	private Client[] destinations;

	public MessagingHeader(Channel channel, Client source,
			Client[] destination) {
		this.channel = channel;
		this.source = source;
		this.destinations = destination;
	}

	public MessagingHeader() {
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Client getSource() {
		return source;
	}

	public void setSource(Client source) {
		this.source = source;
	}

	public Client[] getDestinations() {
		return destinations;
	}

	public void setDestinations(Client[] destinations) {
		this.destinations = destinations;
	}

	@Override
	public MessageHeader deserialize(JsonElement json, Type typeOfT, 
			JsonDeserializationContext context) throws JsonParseException {
		if (json.getAsJsonObject().get("channel") == null) {
			channel = Main.getSelf().getChannelReference("default");
		} else {
			channel = Main.getSelf().getChannelReference(
					json.getAsJsonObject().get("channel").getAsString());
		}
		if(channel == null){
			throw new InvalidValueException("channel '" 
					+ json.getAsJsonObject().get("channel").getAsString()
					+"' not found.");
		}
		
		if (json.getAsJsonObject().get("source") == null) {
			throw new MissingElementException("'source' not found!");
		}
		source = Main.getSelf().getClientReference(
				json.getAsJsonObject().get("source").getAsString());
		if(source == null){
			throw new InvalidValueException("source '" 
					+ json.getAsJsonObject().get("source").getAsString()
					+"' not found.");
		}
		
		if (json.getAsJsonObject().get("destination") == null) {
			throw new MissingElementException("'destination' not found!");
		}
		JsonArray jsonArray = json.getAsJsonObject().get("source").getAsJsonArray();
		destinations = new Client[jsonArray.size()];
		Client destination;
		int found = 0;
		for(int index = 0; index < jsonArray.size(); index++){
			destination = Main.getSelf().getClientReference(
					jsonArray.get(index).getAsString());
			if(destination == null){
				Logger.warning("Destination '" + jsonArray.get(index).getAsString()
						+ "' not found.");
			}else{
				found++;
				destinations[index] = destination;
			}
		}
		if(found == 0){
			throw new InvalidValueException("No one destination found.");
		}
		
		return this;
	}

	@Override
	public JsonElement serialize(MessageHeader t, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty("channel", channel.getId());
		jsonObject.addProperty("source", source.getName());
		JsonArray jsonArray = new JsonArray();
		JsonObject tempJsonObject;
		for(Client client : destinations){
			tempJsonObject = new JsonObject();
			tempJsonObject.addProperty("destination", client.getName());
			jsonArray.add(tempJsonObject.get("destination"));
		}
		jsonObject.add("destination", jsonArray);
		
		return jsonObject;
	}

}
