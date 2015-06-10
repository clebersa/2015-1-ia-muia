package application.exceptions;

public class UnableToUpdateObserverException extends Exception {
	private static final long serialVersionUID = -5075168587490758992L;

	public UnableToUpdateObserverException( String message ) {
		super(message);
	}
	
	public UnableToUpdateObserverException() {
		
	}
}
