package sun.game.def.xe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericsUtils {

	/**
	 * 获得指定类的父类的泛型参数的实际类型
	 * 
	 * @param clazz
	 *            Class
	 * @param index
	 *            泛型参数所在索引,从0开始
	 * @return Class
	 */
	public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) {
		if (clazz == null) {
			return null;
		}

		Type genericType = clazz.getGenericSuperclass();
		while ((genericType != null) && (!(genericType instanceof ParameterizedType))) {
			clazz = clazz.getSuperclass();
			if (clazz == null) {
				break;
			}
			genericType = clazz.getGenericSuperclass();
		}

		if (!(genericType instanceof ParameterizedType)) {
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genericType).getActualTypeArguments();
		if ((params != null) && (index >= 0) && (index < params.length) && ((params[index] instanceof Class<?>))) {
			return (Class<?>) params[index];
		}

		return Object.class;
	}

	/**
	 * 只适用用一个参数的方法
	 * 
	 * @param method
	 * @param index
	 * @return
	 */
	public static Class<?> getMethodParamGenericType(Method method, int index) {
		if (method == null) {
			return null;
		}
		Type[] types = method.getGenericParameterTypes();
		if (types.length != 1)
			throw new RuntimeException("method parameter length must eq. 1");
		Type type = types[0];
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type params[] = parameterizedType.getActualTypeArguments();
			if ((params != null) && (index >= 0) && (index < params.length) && ((params[index] instanceof Class<?>))) {
				return (Class<?>) params[index];
			}
		}
		return Object.class;
	}

	/**
	 * 判断childClass是否为superclass的子类
	 * 
	 * @param childClass
	 * @param superclass
	 * @return
	 */
	public static boolean checkSuperclass(Class<?> childClass, Class<?> superclass) {
		if ((childClass == null) || (superclass == null)) {
			return false;
		}
		if (childClass.equals(superclass)) {
			return true;
		}
		Class<?> clazz = childClass.getSuperclass();
		while (clazz != null) {
			if (clazz.equals(superclass)) {
				return true;
			}
			clazz = clazz.getSuperclass();
		}
		return false;
	}

	/**
	 * 检测制定类是否实现指定接口
	 * 
	 * @param checkClass
	 * @param interClass
	 * @return
	 */
	public static boolean checkInterface(Class<?> checkClass, Class<?> interClass) {
		if ((checkClass == null) || (interClass == null)) {
			return false;
		}
		if (checkClass.equals(interClass)) {
			return true;
		}
		Class<?>[] interClzs = checkClass.getInterfaces();
		for (Class<?> class1 : interClzs) {
			if (checkInterface(class1, interClass)) {
				return true;
			}
		}
		Class<?> superClz = checkClass.getSuperclass();
		return checkInterface(superClz, interClass);
	}

	/**
	 * 转移属性
	 * 
	 * @param oldService
	 * @param newService
	 */
	public static void distractField(Object srcObj, Object decObj) throws Exception {
		if (!srcObj.getClass().getName().equals(decObj.getClass().getName())) {
			throw new RuntimeException(srcObj.getClass() + ", " + decObj.getClass() + "not same class");
		}
		Class<?> clazz = srcObj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			field.setAccessible(true);
			field.set(decObj, field.get(srcObj));
		}
	}

}
