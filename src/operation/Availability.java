package operation;

import application.Main;
import java.util.HashMap;
import packets.AvailabilityHeader;
import packets.MessagePacket;

/**
 *
 * @author cleber
 */
class Availability extends Operation {

	public Availability(MessagePacket messagePacket) {
		super(messagePacket);
	}

	@Override
	public HashMap<String, Object> exec() {
		AvailabilityHeader header = (AvailabilityHeader) messagePacket.getMessageHeader();
		header.getClient().setReadyToReceive(header.isAvailable());
		HashMap<String, Object> result = new HashMap<>();
		result.put("status", 50);
		return result;
	}
	
}
