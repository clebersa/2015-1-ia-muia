package interfaces;

import classes.Application;

/**
 * Interface to be implemented from Objects that needs to notify addition and removal of applications to the registered
 * {@link interfaces.Observer}.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public interface Observable {
	/**
	 * Method to add a new observer thats can receive notifications of application addition or removal in the 
	 * observable object.
	 * @param observer - {@link interfaces.Observer} to be added in the observers list.
	 * @return Boolean true if the observer was successfully added in the observers list or Boolean false when
	 * not.
	 */
	public Boolean addObserver( Observer observer );
	
	/**
	 * Method to remove a observer from the list of observers that wants to receive notifications of application
	 * addition or removal in the observable object.
	 * @param observer - {@link interfaces.Observer} to be removed from the observers list.
	 * @return Boolean true if the observer was successfully removed from the observers list or Boolean false when not.
	 */
	public Boolean removeObserver( Observer observer );
	
	/**
	 * Method to notify the observers that was added a new application in the observable object.
	 * @param application - {@link classes.Application} added in the observable object.
	 */
	public void notifyApplicationAddition( Application application );
	
	/**
	 * Method to notify the observers that an application was removed from the observable object.
	 * @param application - {@link classes.Application} removed from the observable object.
	 */
	public void notifyApplicationRemoval( Application application );
}
