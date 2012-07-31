package fr.epardaud.raclette;

import java.util.Locale;

import ceylon.collection.HashSet;
import ceylon.collection.LinkedList;
import ceylon.collection.MutableList;
import ceylon.collection.MutableSet;
import ceylon.language.Iterable;

import com.redhat.ceylon.compiler.java.metadata.Annotations;
import com.redhat.ceylon.compiler.java.metadata.Ignore;

@Ignore
public class Util {
	
	public static <T> Class<T> getCeylonClass(java.lang.Class<T> javaClass) {
		return (Class<T>) Class.instance(translate(javaClass));
	}
	
	private static java.lang.Class<?> translate(java.lang.Class<?> javaClass) {
		// FIXME: java mappings
		if(javaClass == java.lang.String.class)
			return ceylon.language.String.class;
		if(javaClass == java.lang.Boolean.TYPE)
			return ceylon.language.Boolean.class;
		if(javaClass == java.lang.Long.TYPE)
			return ceylon.language.Integer.class;
		if(javaClass == java.lang.Double.TYPE)
			return ceylon.language.Float.class;
		if(javaClass == java.lang.Integer.TYPE)
			return ceylon.language.Character.class;
		return javaClass;
	}

	/**
	 * When invoking Ceylon methods, we always get invoked with non-primitive wrappers
	 * such as ceylon.language.String and ceylon.language.Integer, which we may need to
	 * unbox to their erased primitives (in this case java.lang.String and long).
	 */
	public static Object[] convertParameters(
			Iterable<? extends Object> parameters,
			java.lang.Class<?>[] parameterTypes) {
		Object[] params = com.redhat.ceylon.compiler.java.Util.toArray(parameters, java.lang.Object.class);
		// FIXME: handle variadic methods
		for(int i=0;i<parameterTypes.length;i++){
			java.lang.Class<?> parameterType = parameterTypes[i];
			Object param = params[i];
			params[i] = unbox(param, parameterType);
		}
		return params;
	}

	public static Object unbox(Object param, java.lang.Class<?> parameterType) {
		// FIXME: handle Java primitives
		if(parameterType == java.lang.String.class){
			if(param instanceof ceylon.language.String){
				return ((ceylon.language.String)param).toString();
			}
		}else if(parameterType == java.lang.Long.TYPE){
			if(param instanceof ceylon.language.Integer){
				return ((ceylon.language.Integer)param).longValue();
			}
		}else if(parameterType == java.lang.Double.TYPE){
			if(param instanceof ceylon.language.Float){
				return ((ceylon.language.Float)param).doubleValue();
			}
		}else if(parameterType == java.lang.Boolean.TYPE){
			if(param instanceof ceylon.language.Boolean){
				return ((ceylon.language.Boolean)param).booleanValue();
			}
		}else if(parameterType == java.lang.Integer.TYPE){
			// FIXME: this is only valid for non-Java methods
			if(param instanceof ceylon.language.Character){
				return ((ceylon.language.Character)param).intValue();
			}
		}
		return param;
	}

	public static boolean isJavaBeanGetter(java.lang.reflect.Method javaMethod) {
		String name = javaMethod.getName();
		return javaMethod.getParameterTypes().length == 0
				&& ((name.startsWith("is") && name.length() > 2 && javaMethod.getReturnType() == java.lang.Boolean.TYPE)
						|| (name.startsWith("get") && name.length() > 3));
	}

	public static boolean isJavaBeanSetter(java.lang.reflect.Method javaMethod) {
		String name = javaMethod.getName();
		return javaMethod.getParameterTypes().length == 1
				&& name.startsWith("set") && name.length() > 3
				&& javaMethod.getReturnType() == java.lang.Void.TYPE;
	}

	public static String getPropertyNameFromGetter(String name) {
		return uncapitalise(name.startsWith("is") ? name.substring(2) : name.substring(3));
	}

	public static String getSetterNameForGetter(String name) {
		return "set" + (name.startsWith("is") ? name.substring(2) : name.substring(3));
	}

	public static String getPropertyNameFromSetter(String name) {
		return uncapitalise(name.substring(3));
	}

	private static String uncapitalise(String string) {
		return string.substring(0, 1).toLowerCase(Locale.ENGLISH) + string.substring(1);
	}

	public static MutableSet<Annotation> initAnnotations(
			java.lang.reflect.AnnotatedElement element) {
		MutableSet<Annotation> annotations = new HashSet<Annotation>();
		Annotations javaAnnotations = element.getAnnotation(Annotations.class);
		if(javaAnnotations != null){
			for(com.redhat.ceylon.compiler.java.metadata.Annotation javaAnnotation : javaAnnotations.value()){
				annotations.add(new Annotation(javaAnnotation.value()));
			}
		}
		return annotations;
	}

	/**
	 * Return values from reflective invocation is always boxed 
	 */
	public static Object box(Object value, java.lang.Class<?> type) {
		// FIXME: handle java methods
		if(type == java.lang.String.class){
			if(value != null){
				return ceylon.language.String.instance((java.lang.String)value);
			}
			return null;
		}else if(type == java.lang.Long.TYPE){
			return ceylon.language.Integer.instance((java.lang.Long)value);
		}else if(type == java.lang.Double.TYPE){
			return ceylon.language.Float.instance((java.lang.Double)value);
		}else if(type == java.lang.Boolean.TYPE){
			return ceylon.language.Boolean.instance((java.lang.Boolean)value);
		}else if(type == java.lang.Integer.TYPE){
			return ceylon.language.Character.instance((java.lang.Integer)value);
		}
		// FIXME: handle void methods
		return value;
	}

	public static MutableList<Parameter> initParameters(
			java.lang.Class<?>[] parameterTypes,
			java.lang.annotation.Annotation[][] parameterAnnotations) {
		LinkedList<Parameter> parameters = new ceylon.collection.LinkedList<Parameter>();
		for(int i=0;i<parameterTypes.length;i++){
			parameters.add(new Parameter(parameterTypes[i], parameterAnnotations[i]));
		}
		return parameters;
	}
}
