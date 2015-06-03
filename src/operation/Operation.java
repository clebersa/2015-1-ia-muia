package operation;

import packets.MessagePacket;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public abstract class Operation {

	protected MessagePacket messagePacket;

	public Operation(MessagePacket messagePacket) {
		this.messagePacket = messagePacket;
	}

	public MessagePacket getMessagePacket() {
		return messagePacket;
	}

	public void setMessagePacket(MessagePacket messagePacket) {
		this.messagePacket = messagePacket;
	}

	public abstract int exec();
}
