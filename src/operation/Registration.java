package operation;

import application.Client;
import application.Main;
import application.OriginalMUIA;
import common.Logger;
import java.util.HashMap;
import packets.MessagePacket;
import packets.RegistrationHeader;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
class Registration extends Operation {

	public Registration(MessagePacket messagePacket) {
		super(messagePacket);
	}

	@Override
	public HashMap<String, Object> exec() {
		HashMap<String, Object> resultMap = new HashMap<>();
		
		Logger.debug("Running registration operation...");
		OriginalMUIA originalMUIA = Main.getSelf();
		RegistrationHeader registrationHeader = ((RegistrationHeader) 
				messagePacket.getMessageHeader());
		Client client = registrationHeader.getClient();

		if (registrationHeader.isRegister()) {
			if (originalMUIA.hasClient(client)) {
				resultMap.put("status", 11);
			} else if (originalMUIA.addClient(client)) {
				resultMap.put("status", 10);
			} else {
				resultMap.put("status", 13);
			}
		} else {
			if (!originalMUIA.hasClient(client)) {
				resultMap.put("status", 12);
			} else if (originalMUIA.removeClient(client)) {
				resultMap.put("status", 10);
			} else {
				resultMap.put("status", 13);
			}
		}
		Logger.debug("Registration(" + registrationHeader.isRegister() 
				+ ") operation result: " + resultMap.get("status"));
		
		return resultMap;
	}
}
