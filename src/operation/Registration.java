package operation;

import application.Application;
import application.Client;
import application.MUIA;
import application.Main;
import common.Logger;
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
	public int exec() {
		Logger.debug("Running registration operation...");
		MUIA muia = Main.getSelf();
		Client client = ((RegistrationHeader)messagePacket.getMessageHeader()).getClient();
		int result;
		if(muia.addClient(client))
			result = 0;
		else
			result = 1;
		Logger.debug("Registration operation result: " + result);
		return result;
	}
}
