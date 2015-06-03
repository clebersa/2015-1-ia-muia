package packets;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class ChannelCreatingHeader extends ChannelingHeader {

	private String description;
	private int subscribersAmount;
	private int maxRetries;
	private long retryInterval;
	private long timeout;

	public ChannelCreatingHeader(String description, int subscribersAmount,
			int maxRetries, long retryInterval, long timeout) {
		this.description = description;
		this.subscribersAmount = subscribersAmount;
		this.maxRetries = maxRetries;
		this.retryInterval = retryInterval;
		this.timeout = timeout;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSubscribersAmount() {
		return subscribersAmount;
	}

	public void setSubscribersAmount(int subscribersAmount) {
		this.subscribersAmount = subscribersAmount;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public long getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(long retryInterval) {
		this.retryInterval = retryInterval;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
}
