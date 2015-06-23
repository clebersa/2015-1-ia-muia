package sending;

import application.Client;
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
		if (originalMH.getDestinations().length == 0) {
			for (int index = 0; index < originalMH.getChannel().getSubscribers().size(); index++) {
				newMH = new MessagingHeader(originalMH.getChannel(),
						originalMH.getSource(),
						(Client[]) originalMH.getChannel().getSubscribers()
						.subList(index, index).toArray());

				messageSenders.add(new MessageSender(index, new MessagePacket(
						newMH, messagePacket.getMessageData())));

				messageSenders.get(index).addObserver(this);

				new Thread(messageSenders.get(index)).start();
			}
		} else {
			for (int index = 0; index < originalMH.getDestinations().length; index++) {
				newMH = new MessagingHeader(
						originalMH.getChannel(),
						originalMH.getSource(),
						Arrays.copyOfRange(originalMH.getDestinations(), index, index));

				messageSenders.add(new MessageSender(index, new MessagePacket(
						newMH, messagePacket.getMessageData())));

				messageSenders.get(index).addObserver(this);

				new Thread(messageSenders.get(index)).start();
			}
		}

		while (messageSenders.size() > 0) {
			try {
				Thread.sleep((long) 1000);
			} catch (InterruptedException ex) {
				Logger.warning("Unable to wait to stop Message Manager. Error: "
						+ ex.getMessage());
			}
		}
	}

	@Override
	public void update(MessageSender messageSender) {
		Channel channel = ((MessagingHeader) messagePacket.getMessageHeader()).getChannel();
		if (!messageSender.isSent() && messageSender.getRetryAmount() < channel.getMaxRetries()) {
			try {
				Thread.sleep((long) channel.getRetryInterval());
			} catch (InterruptedException ex) {
				Logger.warning("Unable to wait to resend the message. Error: "
						+ ex.getMessage());
			}
			new Thread(messageSender).start();
			Logger.debug("Message Sender "+messageSender.getId() 
							+ " relaunched.");
		} else {
			synchronized (messageSenders) {
				if (messageSenders.remove(messageSender.getId()) != null) {
					Logger.debug("Message Sender " + messageSender.getId()
							+ " finalized.");
				} else {
					Logger.warning("Message Sender " + messageSender.getId()
							+ " not found to finalize.");
				}
			}
		}
	}

}
