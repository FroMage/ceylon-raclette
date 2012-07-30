package fr.epardaud.raclette;

import java.lang.annotation.Annotation;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Defaulted;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.Sequenced;

@Ceylon(major = 2)
@com.redhat.ceylon.compiler.java.metadata.Class
public class Parameter {

	private java.lang.String name;
	private boolean variadic;
	private boolean defaulted;
	private Class<?> type;

	public Parameter(java.lang.Class<?> javaClass, Annotation[] annotations) {
		this.type = Util.getCeylonClass(javaClass);
		for(int i=0;i<annotations.length;i++){
			Annotation annotation = annotations[i];
			if(annotation instanceof Name)
				this.name = ((Name)annotation).value();
			else if(annotation instanceof Sequenced)
				this.variadic = true;
			else if(annotation instanceof Defaulted)
				this.defaulted = true;
			// FIXME: handle @TypeInfo
		}
	}
	
	public Class<?> getType(){
		return type;
	}

	public java.lang.String getName() {
		return name;
	}

	public boolean isVariadic() {
		return variadic;
	}

	public boolean isDefaulted() {
		return defaulted;
	}

}
