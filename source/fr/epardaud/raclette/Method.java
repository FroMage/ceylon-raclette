package fr.epardaud.raclette;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import ceylon.collection.MutableList;
import ceylon.language.Iterable;
import ceylon.language.List;
import ceylon.language.Set;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.Sequenced;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;

@Ceylon(major = 2)
@com.redhat.ceylon.compiler.java.metadata.Class
public class Method<Instance,Return> {

	private java.lang.reflect.Method javaMethod;
	private Set<Annotation> annotations;
	private MutableList<Parameter> parameters;
	private Class<Return> returnType;

	public Method(java.lang.reflect.Method javaMethod) {
		this.javaMethod = javaMethod;
	}

	public String getName(){
		return javaMethod.getName();
	}
	
	public boolean isShared(){
		// FIXME: Ceylon interfaces have different rules
		return Modifier.isPublic(javaMethod.getModifiers());
	}

	public Class<Return> getType(){
		// FIXME: handle java void -> c.l.Void
		if(returnType == null)
			returnType = Util.<Return>getCeylonClass((java.lang.Class<Return>)javaMethod.getReturnType());
		return returnType;
	}
	
	public List<Parameter> getParameters(){
		if(parameters == null)
			initParameters();
		return parameters;
	}
	
	private void initParameters() {
		parameters = new ceylon.collection.LinkedList<Parameter>();
		java.lang.Class<?>[] parameterTypes = javaMethod.getParameterTypes();
		java.lang.annotation.Annotation[][] parameterAnnotations = javaMethod.getParameterAnnotations();
		for(int i=0;i<parameterTypes.length;i++){
			parameters.add(new Parameter(parameterTypes[i], parameterAnnotations[i]));
		}
	}

	public Set<Annotation> getAnnotations(){
		if(annotations == null)
			annotations = Util.initAnnotations(javaMethod);
		return annotations;
	}
	
	@Ignore
	public java.lang.Object invoke(
			java.lang.Object instance){
		try {
			return convertReturnValue(javaMethod.invoke(instance));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	// FIXME: add a method for named param invocation
	
	@TypeInfo("ceylon.language.Object")
	public java.lang.Object invoke(
			@Name("instance")
			@TypeInfo("ceylon.language.Object")
			java.lang.Object instance,
			@Sequenced
		    @Name("parameters") 
		    @TypeInfo("ceylon.language.Iterable<ceylon.language.Object>")
		    final ceylon.language.Iterable<? extends java.lang.Object> parameters){
		try {
			// FIXME: handle default values
			return convertReturnValue(javaMethod.invoke(instance, 
					convertParameters(parameters)));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private Object convertReturnValue(Object value) {
		return Util.box(value, javaMethod.getReturnType());
	}

	private Object[] convertParameters(Iterable<? extends Object> parameters) {
		return Util.convertParameters(parameters, javaMethod.getParameterTypes());
	}
}