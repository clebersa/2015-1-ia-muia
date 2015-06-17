package packets;

import application.Client;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.net.UnknownHostException;

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
		try {
			client = new Client(
					json.getAsJsonObject().get("app-name").getAsString(),
					json.getAsJsonObject().get("app-address").getAsString(),
					json.getAsJsonObject().get("app-port").getAsInt());
		} catch (UnknownHostException ex) {
			throw new JsonParseException("Unable to create a Client "
					+ "instance from the application parameters. Error: " 
					+ ex.getMessage());
		}
		
		register = json.getAsJsonObject().get("register").getAsBoolean();
		
		return this;
	}

}
