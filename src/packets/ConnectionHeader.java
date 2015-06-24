package packets;

import application.Application;
import application.Client;
import application.MUIA;
import application.Main;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import receiving.InvalidValueException;
import receiving.MissingElementException;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class ConnectionHeader implements JsonDeserializer<ConnectionHeader>,
		JsonSerializer<ConnectionHeader> {

	private String applicationName;
	private String applicationType;

	public ConnectionHeader() {
	}

	public ConnectionHeader(String applicationName, String applicationType) {
		this.applicationName = applicationName;
		this.applicationType = applicationType;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	@Override
	public String toString() {
		return "ConnectionHeader{app-name=\"" + applicationName 
				+ "\", app-type=\"" + applicationType + "\"}";
	}

	@Override
	public ConnectionHeader deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		if (json.getAsJsonObject().get("app-name") == null) {
			throw new MissingElementException("'app-name' not found!");
		}

		if (json.getAsJsonObject().get("app-type") == null) {
			throw new MissingElementException("'app-type' not found!");
		}

		applicationName = json.getAsJsonObject().get("app-name").getAsString();
		applicationType = json.getAsJsonObject().get("app-type").getAsString();

		if (!"client".equals(applicationType) && !"muia".equals(applicationType)) {
			throw new InvalidValueException("Invalid value for 'application-type'.");
		}

		return this;
	}

	@Override
	public JsonElement serialize(ConnectionHeader t, Type type,
			JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("app-name", applicationName);
		jsonObject.addProperty("app-type", applicationType);

		return jsonObject;
	}

}
