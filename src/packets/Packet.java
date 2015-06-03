package packets;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class Packet {

	private ConnectionHeader connectionHeader;
	private MessagePacket messagePacket;

	public Packet(ConnectionHeader connectionHeader,
			MessagePacket messagePacket) {
		this.connectionHeader = connectionHeader;
		this.messagePacket = messagePacket;
	}

	public ConnectionHeader getConnectionHeader() {
		return connectionHeader;
	}

	public void setConnectionHeader(ConnectionHeader connectionHeader) {
		this.connectionHeader = connectionHeader;
	}

	public MessagePacket getMessagePacket() {
		return messagePacket;
	}

	public void setMessagePacket(MessagePacket messagePacket) {
		this.messagePacket = messagePacket;
	}

}
