package packets;

import application.Client;
import application.Main;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import receiving.InvalidValueException;
import receiving.MissingElementException;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class RegistrationHeader extends MessageHeader {

	private Client client;
	private boolean register;

	public RegistrationHeader() {
	}

	public RegistrationHeader(Client application, boolean register) {
		this.client = application;
		this.register = register;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public boolean isRegister() {
		return register;
	}

	public void setRegister(boolean register) {
		this.register = register;
	}

	@Override
	public String toString() {
		return "RegistrationHeader={application={" + client + "}, register="
				+ register + "}";
	}
	
	@Override
	public MessageHeader deserialize(JsonElement json, Type typeOfT, 
			JsonDeserializationContext context) throws JsonParseException {
		
		if (json.getAsJsonObject().get("register") == null) {
			throw new MissingElementException("'register' not found!");
		}
		register = json.getAsJsonObject().get("register").getAsBoolean();
		
		try {
			if (json.getAsJsonObject().get("app-name") == null) {
				throw new MissingElementException("'app-name' not found!");
			}
			if(register){
				if (json.getAsJsonObject().get("app-address") == null) {
					throw new MissingElementException("'app-address' not found!");
				}
				if (json.getAsJsonObject().get("app-port") == null) {
					throw new MissingElementException("'app-port' not found!");
				}
				client = new Client(
						json.getAsJsonObject().get("app-name").getAsString(),
						json.getAsJsonObject().get("app-address").getAsString(),
						json.getAsJsonObject().get("app-port").getAsInt());
			} else {
				client = Main.getSelf().getClientReference(
						json.getAsJsonObject().get("app-name").getAsString());
				if(client == null){
					client = new Client(json.getAsJsonObject().get("app-name")
							.getAsString());
				}
			}
		} catch (UnknownHostException ex) {
			throw new InvalidValueException("Unable to create client. Error: " 
					+ ex.getMessage());
		}
		
		return this;
	}

	@Override
	public JsonElement serialize(MessageHeader t, Type type, JsonSerializationContext jsc) {
		//This classe will never be serialized.
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
