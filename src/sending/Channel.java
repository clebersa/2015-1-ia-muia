package sending;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
	@Expose
	@SerializedName("id")
	private final String id;
	@Expose
	@SerializedName("description")
	private final String description;
	@Expose
	@SerializedName("max-subscribers")
	private final int maxSubscribers;
	@Expose
	@SerializedName("max-retries")
	private final int maxRetries;
	@Expose
	@SerializedName("retry-interval")
	private final long retryInterval;
	@Expose
	@SerializedName("timeout")
	private final long timeout;
	
	@Expose(serialize = false, deserialize = false)
	private ArrayList<ChannelObserver> observers = new ArrayList<ChannelObserver>();
	@Expose(serialize = false, deserialize = false)
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
		int result = 40;
		
		//Persist the message packet at somewhere.
		
		new Thread(new MessageManager(mp)).start();
		return result;
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
