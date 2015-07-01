package packets;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import receiving.InvalidValueException;
import receiving.MissingElementException;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class MessagePacket implements JsonDeserializer<MessagePacket>, 
		JsonSerializer<MessagePacket> {

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
		boolean needMessageData = false;

		if (json.getAsJsonObject().get("header-type") == null) {
			throw new MissingElementException("'header-type' not found!");
		}
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
			case "channel-get-available-channels":
				messageHeader = new GetAvailableChannelsHeader();
				break;
			case "messaging":
				messageHeader = new MessagingHeader();
				needMessageData = true;
				break;
			default:
				throw new InvalidValueException("Invalid value for 'header-type'.");
		}
		
		if (json.getAsJsonObject().get("header-data") == null) {
			throw new MissingElementException("'header-data' not found!");
		}
		messageHeader.deserialize(json.getAsJsonObject().get("header-data"),
				typeOfT, context);
		
		if(needMessageData){
			if (json.getAsJsonObject().get("message-data") == null) {
				throw new MissingElementException("'message-data' not found!");
			}
			messageData = new MessageData();
			messageData = messageData.deserialize(json.getAsJsonObject().get("message-data"),
					typeOfT, context);
		}
		return this;
	}

	@Override
	public JsonElement serialize(MessagePacket t, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		
		if(messageHeader instanceof RegistrationHeader){
			jsonObject.addProperty("header-type", "registration");
		}else if(messageHeader instanceof ChannelCreatingHeader){
			jsonObject.addProperty("header-type", "channel-creating");
		}else if(messageHeader instanceof SubscribeHeader){
			jsonObject.addProperty("header-type", "channel-subscribing");
		}else{
			jsonObject.addProperty("header-type", "messaging");
		}
		
		jsonObject.add("header-data", messageHeader.serialize(null, type, jsc));
		jsonObject.add("message-data", messageData.serialize(null, type, jsc));
		
		return jsonObject;
	}

}
