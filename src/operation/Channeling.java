package operation;

import application.Main;
import java.util.HashMap;
import java.util.UUID;
import packets.ChannelCreatingHeader;
import packets.ChannelingHeader;
import packets.MessagePacket;
import packets.SubscribeHeader;
import sending.Channel;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
class Channeling extends Operation {

	public Channeling(MessagePacket messagePacket) {
		super(messagePacket);
	}

	@Override
	public HashMap<String, Object> exec() {
		HashMap<String, Object> resultMap = new HashMap<>();
		ChannelingHeader header = (ChannelingHeader) messagePacket.getMessageHeader();

		if (header instanceof ChannelCreatingHeader) {
			ChannelCreatingHeader cch = (ChannelCreatingHeader) header;
			Channel channel = new Channel(UUID.randomUUID().toString(),
					cch.getDescription(),
					cch.getMaxSubscribers(),
					cch.getMaxRetries(),
					cch.getRetryInterval(),
					cch.getTimeout());
			if (Main.getSelf().addChannel(channel)) {
				resultMap.put("status", 20);
				resultMap.put("channel-id", channel.getId());
			} else {
				resultMap.put("status", 21);
			}
		} else if (header instanceof SubscribeHeader) {
			SubscribeHeader subscribeHeader = (SubscribeHeader) header;
			if (subscribeHeader.isSubscribe()) {
				if (subscribeHeader.getChannel().subscribeClient(
						subscribeHeader.getClient())) {
					resultMap.put("status", 30);
				} else {
					resultMap.put("status", 31);
				}
			} else {
				if (subscribeHeader.getChannel().subscribeClient(
						subscribeHeader.getClient())) {
					resultMap.put("status", 30);
				} else {
					resultMap.put("status", 32);
				}
			}
		} else {
			resultMap.put("status", 2);
		}

		return resultMap;
	}

}
