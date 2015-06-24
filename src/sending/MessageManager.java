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
			for (int index = 0, messageSenderIndex = 0; 
					index < originalMH.getChannel().getSubscribers().size(); index++) {
				if(originalMH.getSource().getName().equals(
						originalMH.getChannel().getSubscribers().get(index).getName()))
					continue;
				
				Object[] objectsList = originalMH.getChannel().getSubscribers()
						.subList(index, index + 1).toArray();
				Client[] clients = new Client[objectsList.length];
				for(int index2 = 0; index2 < objectsList.length; index2++){
					clients[index2] = (Client) objectsList[index2];
				}
				newMH = new MessagingHeader(originalMH.getChannel(),
						originalMH.getSource(), clients);

				messageSenders.add(new MessageSender(index, new MessagePacket(
						newMH, messagePacket.getMessageData())));

				messageSenders.get(messageSenderIndex).addObserver(this);

				new Thread(messageSenders.get(messageSenderIndex)).start();
				messageSenderIndex++;
			}
		} else {
			for (int index = 0, messageSenderIndex = 0; 
					index < originalMH.getDestinations().length; index++) {
				if(originalMH.getSource().getName().equals(
						originalMH.getDestinations()[index].getName()))
					continue;
				newMH = new MessagingHeader(
						originalMH.getChannel(),
						originalMH.getSource(),
						Arrays.copyOfRange(originalMH.getDestinations(), index, index + 1));

				messageSenders.add(new MessageSender(index, new MessagePacket(
						newMH, messagePacket.getMessageData())));

				messageSenders.get(messageSenderIndex).addObserver(this);

				new Thread(messageSenders.get(messageSenderIndex)).start();
				messageSenderIndex++;
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
				if (messageSenders.remove(messageSender)) {
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
