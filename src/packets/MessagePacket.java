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
public class MessagePacket implements JsonDeserializer<MessagePacket> {

	private MessageHeader messageHeader;
	private MessageData messageData;

	public MessagePacket() {
	}

	public MessagePacket(MessageHeader messageHeader, MessageData messageData) {
		this.messageHeader = messageHeader;
		this.messageData = messageData;
	}

	public MessageHeader getMessageHeader() {
		return messageHeader;
	}

	public void setMessageHeader(MessageHeader messageHeader) {
		this.messageHeader = messageHeader;
	}

	public MessageData getMessageData() {
		return messageData;
	}

	public void setMessageData(MessageData messageData) {
		this.messageData = messageData;
	}

	@Override
	public String toString() {
		return "MessagePacket{messageHeader={" + messageHeader + "}, messageData={"
				+ messageData + "}}";
	}

	@Override
	public MessagePacket deserialize(JsonElement json, Type typeOfT, 
			JsonDeserializationContext context) throws JsonParseException {

		String headerType = json.getAsJsonObject().get("header-type").getAsString();
		if (null != headerType) switch (headerType) {
			case "registration":
				messageHeader = new RegistrationHeader();
				break;
			case "channel-subscribing":
				messageHeader = new SubscribeHeader();
				break;
			case "channel-creating":
				messageHeader = new ChannelCreatingHeader();
				break;
			case "messaging":
				messageHeader = new MessagingHeader();
				break;
			default:
				throw new JsonParseException("Invalid header type.");
		}
		messageHeader.deserialize(json.getAsJsonObject().get("header-data"),
				typeOfT, context);
		messageData = new MessageData();
		messageData = messageData.deserialize(json.getAsJsonObject().get("message-data"),
				typeOfT, context);
		return this;
	}

}
