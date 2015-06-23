package receiving;

import com.google.gson.JsonParseException;

/**
 *
 * @author cleber
 */
public class InvalidValueException extends JsonParseException{

	public InvalidValueException(String msg) {
		super(msg);
	}
	
}
