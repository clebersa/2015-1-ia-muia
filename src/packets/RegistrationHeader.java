package packets;

import application.Application;

/**
 *
 * @author Cleber Alcântara <cleber.93cd@gmail.com>
 */
public class RegistrationHeader extends MessageHeader {

	private Application application;
	private boolean register;

	public RegistrationHeader(Application application, boolean register) {
		this.application = application;
		this.register = register;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public boolean isRegister() {
		return register;
	}

	public void setRegister(boolean register) {
		this.register = register;
	}

}
