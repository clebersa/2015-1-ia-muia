package common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableHandler<T> {
	public byte[] serialize( Serializable serializable ) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject( serializable );
			os.close();
		} catch( IOException e ) {
			e.printStackTrace();
		}
		
		return os.toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	public T deserialize( byte[] serializedObject ) {
		ByteArrayInputStream in = new ByteArrayInputStream( serializedObject );
		ObjectInputStream is = null;
		T concreteObject = null;
		try {
			is = new ObjectInputStream(in);
			concreteObject = (T) is.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return concreteObject;
	}
}
