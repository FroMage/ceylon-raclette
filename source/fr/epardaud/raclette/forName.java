package fr.epardaud.raclette;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;

@Ceylon(major = 2)
@com.redhat.ceylon.compiler.java.metadata.Method
public class forName {

	@SuppressWarnings("unchecked")
	public static <T> Class<T> forName(String name){
		try {
			// FIXME: classloader
			return Class.<T>instance((java.lang.Class<T>)java.lang.Class.forName(name));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class not found: "+name);
		}
	}
}
