package packets;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import receiving.MissingElementException;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class Packet implements JsonDeserializer<Packet>, JsonSerializer<Packet> {

	private ConnectionHeader connectionHeader;
	private MessagePacket messagePacket;

	public Packet(ConnectionHeader connectionHeader,
			MessagePacket messagePacket) {
		this.connectionHeader = connectionHeader;
		this.messagePacket = messagePacket;
	}

	public Packet() {
	}

	public ConnectionHeader getConnectionHeader() {
		return connectionHeader;
	}

	public void setConnectionHeader(ConnectionHeader connectionHeader) {
		this.connectionHeader = connectionHeader;
	}

	public MessagePacket getMessagePacket() {
		return messagePacket;
	}

	public void setMessagePacket(MessagePacket messagePacket) {
		this.messagePacket = messagePacket;
	}

	@Override
	public String toString() {
		return "Packet{connectionHeader={" + connectionHeader + "}, messagePacket={"
				+ messagePacket + "}}";
	}

	@Override
	public Packet deserialize(JsonElement json, Type typeOfT, 
			JsonDeserializationContext context) throws JsonParseException {
		if (json.getAsJsonObject().get("connection-header") == null) {
			throw new MissingElementException("'connection-header' not found!");
		}

		if (json.getAsJsonObject().get("message-packet") == null) {
			throw new MissingElementException("'message-packet' not found!");
		}

		connectionHeader = new ConnectionHeader();
		connectionHeader.deserialize(json.getAsJsonObject().get("connection-header"), 
				typeOfT, context);

		messagePacket = new MessagePacket();
		messagePacket.deserialize(json.getAsJsonObject().get("message-packet"), 
				typeOfT, context);
		
		return this;
	}

	@Override
	public JsonElement serialize(Packet t, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.add("connection-header", connectionHeader.serialize(null, type, jsc));
		jsonObject.add("message-packet", messagePacket.serialize(null, type, jsc));

		return jsonObject;
	}

}
