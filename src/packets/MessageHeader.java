package packets;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public abstract class MessageHeader implements JsonDeserializer<MessageHeader>, 
		JsonSerializer<MessageHeader>{

}
