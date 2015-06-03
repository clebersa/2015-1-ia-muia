package packets;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class MessageData {

	byte[] value;

	public MessageData(byte[] value) {
		this.value = value;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

}
