package receiving;

import com.google.gson.JsonParseException;

/**
 *
 * @author cleber
 */
public class MissingElementException extends JsonParseException{

	public MissingElementException(String msg) {
		super(msg);
	}
	
}
