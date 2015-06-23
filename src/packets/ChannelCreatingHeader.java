package packets;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import receiving.InvalidValueException;
import receiving.MissingElementException;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class ChannelCreatingHeader extends ChannelingHeader {

	private String description;
	private int maxSubscribers;
	private int maxRetries;
	private long retryInterval;
	private long timeout;

	public ChannelCreatingHeader(String description, int maxSubscribers,
			int maxRetries, long retryInterval, long timeout) {
		this.description = description;
		this.maxSubscribers = maxSubscribers;
		this.maxRetries = maxRetries;
		this.retryInterval = retryInterval;
		this.timeout = timeout;
	}

	public ChannelCreatingHeader() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMaxSubscribers() {
		return maxSubscribers;
	}

	public void setMaxSubscribers(int maxSubscribers) {
		this.maxSubscribers = maxSubscribers;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public long getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(long retryInterval) {
		this.retryInterval = retryInterval;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public MessageHeader deserialize(JsonElement json, Type typeOfT, 
			JsonDeserializationContext context) throws JsonParseException {
		if (json.getAsJsonObject().get("description") == null) {
			throw new MissingElementException("'description' not found!");
		}
		if (json.getAsJsonObject().get("max-subscribers") == null) {
			throw new MissingElementException("'max-subscribers' not found!");
		}
		if (json.getAsJsonObject().get("max-retries") == null) {
			throw new MissingElementException("'max-retries' not found!");
		}
		if (json.getAsJsonObject().get("retry-interval") == null) {
			throw new MissingElementException("'retry-interval' not found!");
		}
		if (json.getAsJsonObject().get("timeout") == null) {
			throw new MissingElementException("'timeout' not found!");
		}
		
		description = json.getAsJsonObject().get("description").getAsString();
		if("".equals(description)){
			throw new InvalidValueException("empty description not found.");
		}
		
		maxSubscribers = json.getAsJsonObject().get("max-subscribers").getAsInt();
		if(maxSubscribers < 0){
			throw new InvalidValueException("negative max-subscribers.");
		}
		
		maxRetries = json.getAsJsonObject().get("max-retries").getAsInt();
		if(maxRetries < 0){
			throw new InvalidValueException("negative max-retries.");
		}
		
		retryInterval = json.getAsJsonObject().get("retry-interval").getAsLong();
		if(retryInterval < 0){
			throw new InvalidValueException("negative retry-interval.");
		}
		
		timeout = json.getAsJsonObject().get("timeout").getAsLong();
		if(timeout < 0){
			throw new InvalidValueException("negative timeout.");
		}
		
		return this;
	}

	@Override
	public JsonElement serialize(MessageHeader t, Type type, JsonSerializationContext jsc) {
		//This classe will never be serialized.
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
