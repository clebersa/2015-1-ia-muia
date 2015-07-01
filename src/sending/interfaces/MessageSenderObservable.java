package sending.interfaces;

/**
 *
 * @author cleber
 */
public interface MessageSenderObservable {
	public void notifyAfterSend();
	public boolean addObserver(MessageSenderObserver mso);
	public boolean removeObserver(MessageSenderObserver mso);
}
