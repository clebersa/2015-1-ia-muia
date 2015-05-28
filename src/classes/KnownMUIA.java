package classes;

import java.util.ArrayList;
import java.util.Iterator;

import interfaces.Observable;
import interfaces.Observer;

/**
 * Class of a Known MUIA in the MUIA server application.
 * This class identify a MUIA instance that contains the instance location and management of instance registered
 * applications.
 * @author Bruno Soares da Silva
 * @since 28/05/2015
 */
public class KnownMUIA implements Observer, Observable {
	/**
	 * List of observers of MUIA instance.
	 */
	private ArrayList<Observer> observers;
	/**
	 * List of applications registered in the MUIA instance.
	 */
	private ArrayList<Application> applications;
	
	/**
	 * Constructor method.
	 * Initialize the variables.
	 */
	public KnownMUIA() {
		this.observers = new ArrayList<Observer>();
		this.applications = new ArrayList<Application>();
	}
	
	/**
	 * Method to add (register) a application in the MUIA instance.
	 * If the addition occurs smoothly, the observers are updated.
	 * @param application - {@link classes.Application} to be registered in the MUIA instance.
	 * @return Boolean true if the application was successfully added in the MUIA instance or false if the application
	 * is already contained in the MUIA instance or the addition operation have a error.
	 */
	public Boolean addApplication( Application application ) {
		Boolean operation = false;
		
		if( !this.applications.contains( application ) ) {
			operation = this.applications.add( application );
		}
		
		if( operation == true ) {
			this.notifyApplicationAdd( application );
		}
		
		return operation;
	}
	
	/**
	 * Method to remove (unregister) a application in the MUIA instance.
	 * If the removal occours smoothly, the observers are updated.
	 * @param application - {@link classes.Application} to be removed in the MUIA instance.
	 * @return Boolean true if the application was successfully removed from the MUIA instance or false if the
	 * application doesn't exists in the MUIA instance or the removal operation have a error.
	 */
	public Boolean removeApplication( Application application ) {
		Boolean operation = this.applications.remove( application );
		
		if( operation == true ) {
			this.notifyApplicationRemoval( application );
		}
		
		return operation;
	}
	
	@Override
	public Boolean addObserver(Observer observer) {
		Boolean operation = false;
		
		if( !this.observers.contains( observer ) ) {
			operation = this.observers.add( observer );
		}
		
		return operation;
	}

	@Override
	public Boolean removeObserver(Observer observer) {
		Boolean operation = this.observers.remove( observer );
		return operation;
	}

	@Override
	public void notifyApplicationAdd(Application application) {
		Iterator<Observer> iterator = this.observers.iterator();
		while( iterator.hasNext() ) {
			Observer observer = iterator.next();
			observer.updateAddApplication( application );
		}
	}

	@Override
	public void notifyApplicationRemoval(Application application) {
		Iterator<Observer> iterator = this.observers.iterator();
		while( iterator.hasNext() ) {
			Observer observer = iterator.next();
			observer.updateRemoveApplication( application );
		}
	}

	@Override
	public void updateAddApplication(Application application) {
		this.applications.add( application );
	}

	@Override
	public void updateRemoveApplication(Application application) {
		this.applications.remove( application );
	}
	
}
