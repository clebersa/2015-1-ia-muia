package packets;

import application.Application;
import application.Client;
import application.Main;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.net.UnknownHostException;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class ConnectionHeader implements JsonDeserializer<ConnectionHeader>{

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
		String appName = json.getAsJsonObject().get("app-name").getAsString();
		String appType = json.getAsJsonObject().get("app-type").getAsString();
		
		if("client".equals(appType)){
			try {
				String appAddress = json.getAsJsonObject().get("app-address")
						.getAsString();
				int appPort = json.getAsJsonObject().get("app-port").getAsInt();
				application = new Client(appName, appAddress, appPort);
			} catch (UnknownHostException ex) {
				throw new JsonParseException("Unable to create a Client "
						+ "instance from the application parameters. Error: " 
						+ ex.getMessage());
			}
		}else if("muia".equals(appType)){
			application = Main.getSelf().getMUIAReference(appName);
			if(application == null){
				throw new JsonParseException("MUIA '"+ appName+"' not found.");
			}
		}else{
			throw new JsonParseException("Invalid value for 'application-type' type.");
		}

		return this;
	}

}
