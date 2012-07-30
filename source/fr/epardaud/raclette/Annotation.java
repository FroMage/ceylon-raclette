package fr.epardaud.raclette;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;

@Ceylon(major = 2)
@com.redhat.ceylon.compiler.java.metadata.Class
public class Annotation {

	private java.lang.String value;

	public Annotation(java.lang.String value) {
		this.value = value;
	}

	@Override
	public java.lang.String toString(){
		return value;
	}
}
