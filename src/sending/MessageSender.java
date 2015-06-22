package sending;

import application.MUIA;
import application.Main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import common.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import packets.ChannelCreatingHeader;
import packets.ConnectionHeader;
import packets.MessageData;
import packets.MessagePacket;
import packets.MessagingHeader;
import packets.Packet;
import packets.RegistrationHeader;
import packets.SubscribeHeader;
import sending.interfaces.MessageSenderObservable;
import sending.interfaces.MessageSenderObserver;

/**
 *
 * @author cleber
 */
public class MessageSender implements Runnable, MessageSenderObservable {

	private final Packet packet;
	private boolean sent;
	private int retryAmount;
	private MessageSenderObserver messageManager;

	public MessageSender(MessagePacket messagePacket) {
		packet = new Packet(new ConnectionHeader(Main.getSelf()), messagePacket);
		sent = false;
		retryAmount = 0;
	}

	@Override
	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			MessagingHeader messagingHeader = (MessagingHeader) packet
					.getMessagePacket().getMessageHeader();
			MUIA destinationMUIA = Main.getSelf().getMUIAByClient(
					messagingHeader.getDestination()[0]);
			Socket socket;
			if (destinationMUIA == Main.getSelf()) {
				socket = new Socket(
						messagingHeader.getDestination()[0].getAddress(),
						messagingHeader.getDestination()[0].getPort());
			} else {
				socket = new Socket(destinationMUIA.getAddress(),
						destinationMUIA.getPort());
			}

			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));

			Gson gson = new GsonBuilder()
					.registerTypeAdapter(Packet.class,
							new Packet())
					.registerTypeAdapter(ConnectionHeader.class,
							new ConnectionHeader())
					.registerTypeAdapter(MessagePacket.class,
							new MessagePacket())
					.registerTypeAdapter(MessagingHeader.class,
							new MessagingHeader())
					.registerTypeAdapter(RegistrationHeader.class,
							new RegistrationHeader())
					.registerTypeAdapter(ChannelCreatingHeader.class,
							new ChannelCreatingHeader())
					.registerTypeAdapter(SubscribeHeader.class,
							new SubscribeHeader())
					.registerTypeAdapter(MessageData.class,
							new MessageData())
					.create();
			out.println(gson.toJson(packet.serialize(null, null, null)));
			socket.shutdownOutput();

			Logger.debug("Waiting status...");
			String resultJson = "", line;
			while ((line = in.readLine()) != null) {
				Logger.debug("Result Line: \"" + line + "\"");
				resultJson += line;
			}

			HashMap<String, Object> result = gson.fromJson(resultJson, HashMap.class);
			result.put("status", ((Number) result.get("status")).intValue());
			Logger.info("Sending operation result: " + result.get("status"));
			if ((Integer) result.get("status") == 41) {
				sent = true;
			}
		} catch (Exception ex) {
			Logger.error("Unable to stop ConnectionManager! Error: "
					+ ex.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					Logger.error("Unable to close the BufferedReader. Error: "
							+ ex.getMessage());
				}
			}
		}
		retryAmount++;
		notifyAfterSend();
	}

	public boolean isSent() {
		return sent;
	}

	public int getRetryAmount() {
		return retryAmount;
	}

	@Override
	public void notifyAfterSend() {
		messageManager.update(this);
	}

	@Override
	public boolean addObserver(MessageSenderObserver mso) {
		messageManager = mso;
		return true;
	}

	@Override
	public boolean removeObserver(MessageSenderObserver mso) {
		if (messageManager != null && messageManager.equals(mso)) {
			messageManager = null;
			return true;
		} else {
			return false;
		}
	}

}
