package interfaces;

import classes.Application;

/**
 * Interface to be implemented from Objects that wants to receive notifications of addition and removal of
 * applications from {@link interfaces.Observable} where is registred.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public interface Observer {
	/**
	 * Method to receive the update of application addition from the {@link interfaces.Observable} object.
	 * @param application - {@link classes.Application} that was added in the {@link interfaces.Observable} object.
	 */
	public void updateApplicationAddition( Application application );
	
	/**
	 * Method to receive the update of application removal from the {@link interfaces.Observable} object.
	 * @param application - {@link classes.Application} that was removed from the {@link interfaces.Observable} object.
	 */
	public void updateApplicationRemoval( Application application );
}
