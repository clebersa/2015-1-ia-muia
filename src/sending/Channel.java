package sending;

import java.io.Serializable;
import java.util.ArrayList;

import application.Client;
import packets.MessagePacket;
import sending.interfaces.ChannelObservable;
import sending.interfaces.ChannelObserver;

/**
 * Class to provide a logical channel to transport the messages. The channel can
 * apply some filters and rules to the message before send.
 *
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public class Channel implements ChannelObservable, Serializable {

	private static final long serialVersionUID = -3916512796973352410L;
	private final String id;
	private final String description;
	private final int maxSubscribers;
	private final int maxRetries;
	private final long retryInterval;
	private final long timeout;

	private ArrayList<ChannelObserver> observers = new ArrayList<ChannelObserver>();
	private ArrayList<Client> subscribers = new ArrayList<Client>();

	public Channel(String id, String description, int maxSubscribers,
			int maxRetries, long retryInterval, long timeout) {
		this.id = id;
		this.description = description;
		this.maxSubscribers = maxSubscribers;
		this.maxRetries = maxRetries;
		this.retryInterval = retryInterval;
		this.timeout = timeout;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public int getMaxSubscribers() {
		return maxSubscribers;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public long getRetryInterval() {
		return retryInterval;
	}

	public long getTimeout() {
		return timeout;
	}

	public ArrayList<ChannelObserver> getObservers() {
		return observers;
	}

	public ArrayList<Client> getSubscribers() {
		return subscribers;
	}
	
	public int publish(MessagePacket mp){
		int result = 41;
		
		//Persist the message packet at somewhere.
		
		new Thread(new MessageManager(mp)).start();
		return 40;
	}

	public Boolean subscribeClient(Client client) {
		Boolean exists = false;
		for (Client subscriber : subscribers) {
			if (subscriber.getName().equals(client.getName())) {
				exists = true;
				break;
			}
		}

		Boolean operation = false;
		if (!exists) {
			operation = subscribers.add(client);
		}

		if (operation) {
			notifySubscribe(client);
		}

		return operation;
	}

	public Boolean unsubscribeClient(Client client) {
		Boolean operation = subscribers.remove(client);

		if (operation) {
			notifyUnsubscribe(client);
		}

		return operation;
	}

	@Override
	public Boolean addObserver(ChannelObserver observer) {
		Boolean operation = false;
		if (!observers.contains(observer)) {
			operation = observers.add(observer);
		}

		return operation;
	}

	@Override
	public Boolean removeObserver(ChannelObserver observer) {
		return observers.remove(observer);
	}

	@Override
	public void notifySubscribe(Client client) {
		for (ChannelObserver observer : observers) {
			observer.onChannelSubscribe(this, client);
		}
	}

	@Override
	public void notifyUnsubscribe(Client client) {
		for (ChannelObserver observer : observers) {
			observer.onChannelUnsubscribe(this, client);
		}
	}
}
