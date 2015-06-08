package packets;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import common.Logger;
import java.lang.reflect.Type;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class MessageData implements JsonDeserializer<MessageData> {

	byte[] value;

	public MessageData() {
	}

	public MessageData(byte[] value) {
		this.value = value;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	@Override
	public MessageData deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		if(json == null || json.getAsJsonObject().get("value") == null){
			Logger.debug("Null message data received.");
			return null;
		}
		String valueString = json.getAsJsonObject().get("value").getAsString();
		value = DatatypeConverter.parseBase64Binary(valueString);
		return this;
	}

}
