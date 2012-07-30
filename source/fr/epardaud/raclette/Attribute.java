package fr.epardaud.raclette;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import ceylon.language.Set;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;

@Ceylon(major = 2)
@com.redhat.ceylon.compiler.java.metadata.Class
public class Attribute<Instance, Type> {

	private final String name;
	private final Class<Type> type;
	final Method javaMethod; // read by Class
	boolean isVariable; // set by Class
	Method setterJavaMethod;// set by Class
	private Set<Annotation> annotations;

	public Attribute(java.lang.reflect.Method javaMethod, String name) {
		this.name = name;
		this.type = (Class<Type>) Util.getCeylonClass(javaMethod.getReturnType());
		this.javaMethod = javaMethod;
	}
	
	public boolean isShared(){
		// FIXME: Ceylon interfaces have different rules
		return Modifier.isPublic(javaMethod.getModifiers());
	}
	
	public boolean isVariable(){
		return isVariable;
	}

	public Set<Annotation> getAnnotations(){
		if(annotations == null)
			annotations = Util.initAnnotations(javaMethod);
		return annotations;
	}

	public String getName() {
		return name;
	}

	public Class<Type> getType() {
		return type;
	}

	public Type get(Instance instance){
		try {
			return (Type) Util.box(javaMethod.invoke(instance), javaMethod.getReturnType());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public void set(Instance instance, Type value){
		// FIXME
		if(!isVariable)
			throw new RuntimeException("Not modifiable");
		try {
			setterJavaMethod.invoke(instance, Util.unbox(value, javaMethod.getReturnType()));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
