package receiving;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import common.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import operation.Operation;
import operation.OperationFactory;
import packets.ChannelCreatingHeader;
import packets.ConnectionHeader;
import packets.MessageData;
import packets.MessagePacket;
import packets.MessagingHeader;
import packets.Packet;
import packets.RegistrationHeader;
import packets.SubscribeHeader;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class ConnectionHandler implements Runnable, ConnectionHandlerObservable {

	int id;

	private final Socket connection;

	ConnectionManager connectionManager;

	public ConnectionHandler(int id, Socket connection) {
		this.id = id;
		this.connection = connection;
	}

	@Override
	public void run() {
		String socketData = "";
		PrintWriter out = null;
		InputStream in = null;

		HashMap<String, Object> result = new HashMap<>();
		result.put("status", 1);

		try {
			in = connection.getInputStream();
			
			out = new PrintWriter(connection.getOutputStream(), true);
			Logger.debug("Receiving data in the ConnectionHandler " + id
					+ "...");
			
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
			
			Logger.debug("Message Line: \"" + receivedData.toString() + "\"");
			socketData = receivedData.toString();
			
			connection.shutdownInput();

			if ("".equals(socketData)) {
				Logger.debug("Stop signal received!");
				result.put("status", 0);
			} else {
				Logger.debug("JSON Message received: " + socketData);
				try {
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
					Packet packet = gson.fromJson(socketData, Packet.class);

					Logger.debug(packet.toString());

					//TODO: Authenticate the application
					
					Operation operation = OperationFactory.createOperation(
							packet.getMessagePacket());
					result = operation.exec();
				} catch (Exception ex) {
					if(ex instanceof MissingElementException){
						result.put("status", 1);
					} else if(ex instanceof InvalidValueException){
						result.put("status", 2);
					} else if(ex instanceof JsonParseException){
						result.put("status", 3);
					} else {
						result.put("status", 9);
					}
					Logger.error("Unable to parse the element. Error: "
							+ ex.getLocalizedMessage());
				}
			}
			
			Gson gson = new Gson();
			out.println(gson.toJson(result));
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.error("Unable to receive the socket data! Error: "
					+ ex.getMessage());
			result.put("status", -1);
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
		Logger.info("ConnectionHandler " + id + " result: " + result.get("status"));
		notifyObservers();
	}

	public int getId() {
		return id;
	}

	@Override
	public void notifyObservers() {
		connectionManager.update(this);
	}

	@Override
	public boolean addObserver(ConnectionManager conMan) {
		if (connectionManager == null) {
			connectionManager = conMan;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeObserver(ConnectionManager conMan) {
		if (connectionManager == conMan) {
			connectionManager = null;
			return true;
		} else {
			return false;
		}
	}
}
