package fr.epardaud.raclette;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ceylon.collection.MutableMap;
import ceylon.language.Map;
import ceylon.language.Set;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.Sequenced;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;

@Ceylon(major = 2)
@com.redhat.ceylon.compiler.java.metadata.Class
public class Class<T> {
	private final static java.util.Map<java.lang.Class<?>, Class<?>> classes 
		= new java.util.HashMap<java.lang.Class<?>, Class<?>>();
	
	@SuppressWarnings("unchecked")
	@Ignore
	public static <T> Class<T> instance(java.lang.Class<T> javaClass){
		Class<?> klass = classes.get(javaClass);
		// FIXME: GC (class unloading)
		// FIXME: concurrency (everywhere in this API)
		if(klass == null){
			klass = new Class<T>(javaClass);
			classes.put(javaClass, klass);
		}
		return (Class<T>) klass;
	}
	
	private final java.lang.Class<T> javaClass;
	private Constructor<T> mainConstructor;
	private MutableMap<ceylon.language.String, Method<T, java.lang.Object>> methods;
	private MutableMap<ceylon.language.String, Attribute<T, java.lang.Object>> attributes;
	private Set<Annotation> annotations;

	private Class(java.lang.Class<T> javaClass) {
		this.javaClass = javaClass;
	}
	
	public Set<Annotation> getAnnotations(){
		if(annotations == null)
			annotations = Util.initAnnotations(javaClass);
		return annotations;
	}

	@SuppressWarnings("unchecked")
	private Constructor<T> getMainConstructor(){
		if(mainConstructor == null){
			// FIXME: this is broken for Java types
			for(Constructor<?> c : javaClass.getDeclaredConstructors()){
				if(c.isAnnotationPresent(Ignore.class))
					continue;
				mainConstructor = (Constructor<T>) c;
				break;
			}
			if(mainConstructor == null)
				throw new RuntimeException("No constructor?");
		}
		return mainConstructor;
	}

	@Ignore
	public T newInstance(){
		try {
			// FIXME: handle default values
			Constructor<T> constructor = getMainConstructor();
			return constructor.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public T newInstance(
			@Sequenced
		    @Name("parameters") 
		    @TypeInfo("ceylon.language.Iterable<ceylon.language.Object>")
		    final ceylon.language.Iterable<? extends java.lang.Object> parameters){
		try {
			// FIXME: handle default values
			Constructor<T> constructor = getMainConstructor();
			return constructor.newInstance(Util.convertParameters(parameters, constructor.getParameterTypes()));
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public String getName() {
		return javaClass.getSimpleName();
	}

	public String getQualifiedName() {
		return javaClass.getName();
	}

	@Override
	public String toString() {
		return getQualifiedName();
	}
	
	@TypeInfo("ceylon.language.Map<ceylon.language.String,fr.epardaud.raclette.Method<T,ceylon.language.Void>>")
	public Map<ceylon.language.String,Method<T, java.lang.Object>> getMethods(){
		if(methods == null){
			initMethods();
		}
		return methods;
	}

	private void initMethods() {
		methods = new ceylon.collection.HashMap<ceylon.language.String,Method<T,java.lang.Object>>();
		for(java.lang.reflect.Method javaMethod : javaClass.getDeclaredMethods()){
			// skip ignored methods
			if(javaMethod.isAnnotationPresent(Ignore.class))
				continue;
			// skip getters
			if(Util.isJavaBeanGetter(javaMethod))
				continue;
			// skip setters that have a corresponding getter
			if(Util.isJavaBeanSetter(javaMethod)){
				String propertyName = javaMethod.getName().substring(3);
				java.lang.reflect.Method getter = getDeclaredMethodOrNull("get"+propertyName);
				java.lang.Class<?> type = javaMethod.getParameterTypes()[0];
				// for boolean stuff we also have isXXX
				if(getter == null && type == java.lang.Boolean.TYPE){
					getter = getDeclaredMethodOrNull("is"+propertyName);
				}
				// if we have a getter with the right type, skip this setter
				if(getter != null && getter.getReturnType() == type)
					continue;
				// turn this setter into a method
			}
			methods.put(ceylon.language.String.instance(javaMethod.getName()),
					new Method<T, java.lang.Object>(javaMethod));
		}
	}

	private java.lang.reflect.Method getDeclaredMethodOrNull(String name) {
		try {
			return javaClass.getDeclaredMethod(name);
		} catch (NoSuchMethodException e) {
			// not there, all well
			return null;
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@TypeInfo("ceylon.language.Map<ceylon.language.String,fr.epardaud.raclette.Attribute<T,ceylon.language.Void>>")
	public Map<ceylon.language.String,Attribute<T, java.lang.Object>> getAttributes(){
		if(attributes == null){
			initAttributes();
		}
		return attributes;
	}

	private void initAttributes() {
		attributes = new ceylon.collection.HashMap<ceylon.language.String,Attribute<T,java.lang.Object>>();
		for(java.lang.reflect.Method javaMethod : javaClass.getDeclaredMethods()){
			// skip ignored methods
			if(javaMethod.isAnnotationPresent(Ignore.class))
				continue;
			// skip non-getters
			if(!Util.isJavaBeanGetter(javaMethod))
				continue;
			// skip setters in this phase
			if(Util.isJavaBeanSetter(javaMethod)){
				continue;
			}
			String name = Util.getPropertyNameFromGetter(javaMethod.getName());
			attributes.put(ceylon.language.String.instance(name),
					new Attribute<T, java.lang.Object>(javaMethod, name));
		}
		// now do all the setters
		for(java.lang.reflect.Method javaMethod : javaClass.getDeclaredMethods()){
			// skip ignored methods
			if(javaMethod.isAnnotationPresent(Ignore.class))
				continue;
			// skip non-setters
			if(!Util.isJavaBeanSetter(javaMethod)){
				continue;
			}
			String name = Util.getPropertyNameFromSetter(javaMethod.getName());
			java.lang.Class<?> type = javaMethod.getParameterTypes()[0];
			// do we have an attribute?
			Attribute<T, java.lang.Object> attr = attributes.item(ceylon.language.String.instance(name));
			if(attr != null 
					&& attr.javaMethod.getReturnType() == type){
				attr.isVariable = true;
				attr.setterJavaMethod = javaMethod;
			}// else it's treated as a method
		}
	}
}