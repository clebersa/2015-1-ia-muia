package sending;

import application.Application;
import common.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import packets.MessagePacket;
import packets.MessagingHeader;
import sending.interfaces.MessageSenderObserver;

/**
 *
 * @author cleber
 */
public class MessageManager implements Runnable, MessageSenderObserver {

	private final ArrayList<MessageSender> messageSenders;
	private final MessagePacket messagePacket;

	public MessageManager(MessagePacket messagePacket) {
		messageSenders = new ArrayList<>();
		this.messagePacket = messagePacket;
	}

	@Override
	public void run() {
		MessagingHeader originalMH = (MessagingHeader) messagePacket.getMessageHeader();
		MessagingHeader newMH;
		if (originalMH.getDestination().length == 0) {
			for (int index = 0; index < originalMH.getChannel().getSubscribers().size(); index++) {
				newMH = new MessagingHeader(originalMH.getChannel(),
						originalMH.getSource(),
						(Application[])originalMH.getChannel().getSubscribers()
								.subList(index, index).toArray());

				messageSenders.add(new MessageSender(new MessagePacket(
						newMH, messagePacket.getMessageData())));

				messageSenders.get(index).addObserver(this);

				new Thread(messageSenders.get(index)).start();
			}
		} else {
			for (int index = 0; index < originalMH.getDestination().length; index++) {
				newMH = new MessagingHeader(
						originalMH.getChannel(),
						originalMH.getSource(),
						Arrays.copyOfRange(originalMH.getDestination(), index, index));

				messageSenders.add(new MessageSender(new MessagePacket(
						newMH, messagePacket.getMessageData())));

				messageSenders.get(index).addObserver(this);

				new Thread(messageSenders.get(index)).start();
			}
		}
	}

	@Override
	public void update(MessageSender messageSender) {
		MessagingHeader originalMH = (MessagingHeader) messagePacket.getMessageHeader();
		if (!messageSender.isSent() && messageSender.getRetryAmount()
				< originalMH.getChannel().getMaxRetries()) {
			try {
				Thread.sleep((long) originalMH.getChannel().getRetryInterval());
			} catch (InterruptedException ex) {
				Logger.warning("Unable to wait to resend the message. Error: "
						+ ex.getMessage());
			}
			new Thread(messageSender).start();
		}
	}

}
