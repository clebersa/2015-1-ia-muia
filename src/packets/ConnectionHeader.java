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
		JsonSerializer<ConnectionHeader>{

	private Application application;

	public ConnectionHeader() {
	}

	public ConnectionHeader(Application application) {
		this.application = application;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@Override
	public String toString() {
		return "ConnectionHeader{application={" + application + "}}";
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
		
		String appName = json.getAsJsonObject().get("app-name").getAsString();
		String appType = json.getAsJsonObject().get("app-type").getAsString();
		
		if("client".equals(appType)){
			try {
				if (json.getAsJsonObject().get("app-address") == null) {
					throw new MissingElementException("'app-address' not found!");
				}
				String appAddress = json.getAsJsonObject().get("app-address")
						.getAsString();
				
				if (json.getAsJsonObject().get("app-port") == null) {
					throw new MissingElementException("'app-port' not found!");
				}
				int appPort = json.getAsJsonObject().get("app-port").getAsInt();
				application = new Client(appName, appAddress, appPort);
			} catch (UnknownHostException ex) {
				throw new InvalidValueException("Unable to create client. Error: " 
						+ ex.getMessage());
			}
		}else if("muia".equals(appType)){
			application = Main.getSelf().getMUIAReference(appName);
			if(application == null){
				throw new InvalidValueException("MUIA '"+ appName+"' not found.");
			}
		}else{
			throw new InvalidValueException("Invalid value for 'application-type'.");
		}

		return this;
	}

	@Override
	public JsonElement serialize(ConnectionHeader t, Type type, 
			JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty("app-name", application.getName());
		
		if(application instanceof MUIA){
			jsonObject.addProperty("app-type", "muia");			
		} else {
			jsonObject.addProperty("app-type", "client");
			jsonObject.addProperty("app-address", application.getAddress()
					.getHostAddress());
			jsonObject.addProperty("app-address", application.getPort());
		}
		
		return jsonObject;
	}

}
