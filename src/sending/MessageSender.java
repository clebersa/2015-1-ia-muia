package sending;

import application.MUIA;
import application.Main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import common.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
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

	private final int id;
	private final Packet packet;
	private boolean sent;
	private int retryAmount;
	private MessageSenderObserver messageSenderObserver;

	public MessageSender(int id, MessagePacket messagePacket) {
		this.id = id;
		packet = new Packet(new ConnectionHeader(Main.getSelf().getName(), "muia"), 
				messagePacket);
		sent = false;
		retryAmount = 0;
	}

	@Override
	public void run() {
		PrintWriter out = null;
		InputStream in = null;
		try {
			MessagingHeader messagingHeader = (MessagingHeader) packet
					.getMessagePacket().getMessageHeader();
			
			MUIA destinationMUIA = Main.getSelf().getMUIAByClient(
					messagingHeader.getDestinations()[0]);
			
			if(destinationMUIA == null) {
				throw new IOException();
			}
			
			Socket socket = new Socket();
			if (destinationMUIA.getName().equals(Main.getSelf().getName())) {
				socket.connect(new InetSocketAddress(
						messagingHeader.getDestinations()[0].getAddress(),
						messagingHeader.getDestinations()[0].getPort()),
						(int) messagingHeader.getChannel().getTimeout());
			} else {
				socket.connect(new InetSocketAddress(
						destinationMUIA.getAddress(),
						destinationMUIA.getPort()),
						(int) messagingHeader.getChannel().getTimeout());
			}

			socket.setSoTimeout((int) messagingHeader.getChannel().getTimeout());
			
			out = new PrintWriter(socket.getOutputStream(), true);
			in = socket.getInputStream();

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
			StringBuilder receivedData = new StringBuilder();
			byte[] buff = new byte[1024];
			int readBytes = -1;
			String tmpString;
			while((readBytes = in.read(buff, 0, buff.length)) > -1) {
				tmpString = new String(buff).replaceAll("\u0000.*", "");
				receivedData.append(tmpString);
				buff = new byte[buff.length];
				
				tmpString = tmpString.replaceAll(System.lineSeparator(), "");
				if( buff.length != readBytes || buff.length != tmpString.length() ) {
					break;
				}
			}
			Logger.debug("Result Line: \"" + receivedData.toString() + "\"");

			HashMap<String, Object> result = gson.fromJson(receivedData.toString(), 
					HashMap.class);
			result.put("status", ((Number) result.get("status")).intValue());
			Logger.info("Sending operation result: " + result.get("status"));
			if ((Integer) result.get("status") == 0 || (Integer) result.get("status") == 40) {
				sent = true;
			}
		} catch (IOException | JsonSyntaxException ex) {
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

	public int getId() {
		return id;
	}

	public boolean isSent() {
		return sent;
	}

	public int getRetryAmount() {
		return retryAmount;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof MessageSender) 
				|| ((MessageSender)obj).getId() != this.id){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public void notifyAfterSend() {
		messageSenderObserver.update(this);
	}

	@Override
	public boolean addObserver(MessageSenderObserver mso) {
		messageSenderObserver = mso;
		return true;
	}

	@Override
	public boolean removeObserver(MessageSenderObserver mso) {
		if (messageSenderObserver != null && messageSenderObserver.equals(mso)) {
			messageSenderObserver = null;
			return true;
		} else {
			return false;
		}
	}

}
