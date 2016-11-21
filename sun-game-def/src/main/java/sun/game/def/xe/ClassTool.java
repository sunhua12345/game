package sun.game.def.xe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;


public class ClassTool {
	public static Class<?> getArrayType(Class<?> typeClass) throws Exception {
		Class<?> claz = typeClass.getComponentType();
		if (!claz.isArray())
			return claz;
		else {
			return getArrayType(claz);
		}
	}

	public static Field[] getFieldsContainSuper(Class<?> claz) {
		Field[] fields = claz.getDeclaredFields();
		Class<?> superClass = claz.getSuperclass();

		Field[] fieldsAll = null;
		if (superClass != null) {
			Field[] superField = getFieldsContainSuper(superClass);
			fieldsAll = ArrayUtils.addAll(superField, fields);
		}

		return fieldsAll;
	}

	public static Field getFieldAboutSuper(Class<?> claz, String name) {
		Field field = null;
		try {
			field = claz.getDeclaredField(name);
		} catch (Exception  e) {

		}

		if (field == null) {
			Class<?> superClass = claz.getSuperclass();
			if (superClass != null) {
				field = getFieldAboutSuper(superClass, name);
			}
		}

		return field;
	}

	public static int getArrayNum(Class<?> typeClass) {
		int num = 1;
		Class<?> claz = typeClass.getComponentType();
		if (claz == null)
			return 0;

		if (claz.isPrimitive())
			return num;

		return num + getArrayNum(claz);
	}

	public static int getMaxArrLen(String[] arr) {
		int max = 0;
		for (String len : arr) {
			int length = len.split(",").length;
			if (max < length) {
				max = length;
			}
		}

		return max;
	}

	public static Object parseString2Array(String context, int arrNum) {
		if (arrNum == 1) {
			if (StringUtils.isEmpty(context)) {
				return new String[0];
			}
			String strs[] = context.trim().split(",");
			return strs;
		}

		if (arrNum == 2) {
			if (StringUtils.isEmpty(context)) {
				return new String[0][0];
			}
			String strs[] = context.trim().split(";");
			String array[][] = new String[strs.length][getMaxArrLen(strs)];
			for (int i = 0; i < strs.length; i++) {
				String ione[] = strs[i].split(",");
				for (int j = 0; j < ione.length; j++) {
					array[i][j] = ione[j];
				}
			}
			return array;
		}

		return null;
	}

	public static String getSetterMethod(String fieldName, Class<?> clazz) {
		if ((clazz.getName().equals("boolean") || clazz.getName().equals("java.lang.Boolean"))
				&& fieldName.startsWith("is")) {
			fieldName = fieldName.replaceFirst("is", "");
		}
		return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
	}

	public static Method getMethodByClassAndName(Class<?> baseDefClass, String name) {
		Method methods[] = getAllMethodContainSuper(baseDefClass);
		Method m = null;
		for (Method method : methods) {
			String methodName = method.getName();
			if (name.trim().equals(methodName)) {
				m = method;
				break;
			}
		}
		return m;
	}

	public static Method[] getAllMethodContainSuper(Class<?> clazz) {
		Method methods[] = clazz.getMethods();

		Method methodsAll[] = null;
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			Method[] superMethos = getAllMethodContainSuper(superClass);
			methodsAll = ArrayUtils.addAll(superMethos, methods);
		}
		return methodsAll;
	}

}
