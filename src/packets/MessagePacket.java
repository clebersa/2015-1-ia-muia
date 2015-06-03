package packets;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class MessagePacket {

	private MessageHeader messageHeader;
	private MessageData messageData;

	public MessagePacket(MessageHeader messageHeader, MessageData messageData) {
		this.messageHeader = messageHeader;
		this.messageData = messageData;
	}

	public MessageHeader getMessageHeader() {
		return messageHeader;
	}

	public void setMessageHeader(MessageHeader messageHeader) {
		this.messageHeader = messageHeader;
	}

	public MessageData getMessageData() {
		return messageData;
	}

	public void setMessageData(MessageData messageData) {
		this.messageData = messageData;
	}

}
