package operation;

import common.Logger;
import java.util.HashMap;
import packets.MessagePacket;
import packets.MessagingHeader;
import sending.Channel;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
class Messaging extends Operation {
	
	public Messaging(MessagePacket messagePacket) {
		super(messagePacket);
	}
	
	@Override
	public HashMap<String, Object> exec() {
		HashMap<String, Object> resultMap = new HashMap<>();
		Channel channel = ((MessagingHeader) messagePacket.getMessageHeader()).getChannel();
		resultMap.put("status", channel.publish(messagePacket));
		
		Logger.info("Messaging operation result: " + resultMap.get("status"));
		
		return resultMap;
	}
	
}
