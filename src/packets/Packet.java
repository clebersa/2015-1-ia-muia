package packets;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class Packet implements JsonDeserializer<Packet> {

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
	public Packet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		connectionHeader = new ConnectionHeader();
		connectionHeader.deserialize(json.getAsJsonObject().get("connection-header"), typeOfT, context);

		messagePacket = new MessagePacket();
		messagePacket.deserialize(json.getAsJsonObject().get("message-packet"), typeOfT, context);
		return this;
	}

}
