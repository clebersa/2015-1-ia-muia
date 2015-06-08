package packets;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class ChannelsGettingHeader extends ChannelingHeader {

	@Override
	public MessageHeader deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return this;
	}

}
