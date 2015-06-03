package operation;

import packets.MessagePacket;

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
		//TODO
		return 0;
	}
}
