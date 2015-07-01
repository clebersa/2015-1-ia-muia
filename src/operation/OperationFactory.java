package operation;

import packets.AvailabilityHeader;
import packets.ChannelingHeader;
import packets.MessageHeader;
import packets.MessagePacket;
import packets.MessagingHeader;
import packets.RegistrationHeader;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class OperationFactory {
	public static Operation createOperation(MessagePacket messagePacket){
		MessageHeader header = messagePacket.getMessageHeader();
		if(header instanceof MessagingHeader){
			return new Messaging(messagePacket);
		}else if(header instanceof RegistrationHeader){
			return new Registration(messagePacket);
		}else if(header instanceof ChannelingHeader){
			return new Channeling(messagePacket);
		}else if(header instanceof AvailabilityHeader){
			return new Availability(messagePacket);
		}else{
			return null;
		}
	}
}
