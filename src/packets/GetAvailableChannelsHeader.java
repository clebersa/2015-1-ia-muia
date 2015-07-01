package packets;

import java.lang.reflect.Type;

import receiving.InvalidValueException;
import receiving.MissingElementException;
import application.Main;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class GetAvailableChannelsHeader extends ChannelingHeader {
	
	public GetAvailableChannelsHeader() {
	}
	
	@Override
	public MessageHeader deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return this;
	}

	@Override
	public JsonElement serialize(MessageHeader src, Type typeOfSrc,
			JsonSerializationContext context) {
		//This classe will never be serialized.
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
