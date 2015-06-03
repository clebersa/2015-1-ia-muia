package packets;

import application.Application;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class ConnectionHeader {

	private Application application;

	public ConnectionHeader(Application application) {
		this.application = application;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

}
