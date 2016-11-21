package sun.game.def.xe;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import sun.game.def.xe.mark.Type;
import sun.game.def.xe.mark.XmlLabel;

public class DataSettingTool {

	private static Logger logger = LoggerFactory.getLogger(DataSettingTool.class);

	/**
	 * 根据输入流返回配置对像
	 * 
	 * @param inputStream
	 *            输入流
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> parseByInputStream(InputStream inputStream) throws Exception {
		if (inputStream == null)
			return null;

		List<T> settingDatas = new ArrayList<T>();
		try {
			// 创建SAXBuilder对象
			SAXBuilder saxBuilder = new SAXBuilder();
			// 创建记录
			Document document = saxBuilder.build(inputStream);
			// 获取根元素
			Element root = document.getRootElement();
			// 类属性
			Attribute attribute = root.getAttribute(XmlLabel.CLASS_ATTR);
			// 判断class属性是否存在
			if (attribute == null) {
				logger.error("element 'def' has no 'class' attribute");
				throw new NullPointerException("element def has no 'class' attributition");
			}

			// 获取根元素下面的def子元素,一个def表示一个配置
			List<Element> defList = root.getChildren(XmlLabel.DEF_LABEL);
			for (Element element : defList) {
				iteration(element, settingDatas, attribute);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return settingDatas;
	}

	/**
	 * 遍历def元素,找到对应的类
	 * 
	 * @param element
	 *            def元素
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static <T> void iteration(Element element, List<T> settingDatas, Attribute attributeClass) throws Exception {
		try {
			// 获得defs的class属性值
			String attrClassValue = attributeClass.getValue();
			// 判断def的class属性值是否为空
			if (StringUtils.isEmpty(attrClassValue)) {
				logger.error("{}'s value is empty, please check it", attributeClass.getName());
				throw new NullPointerException("the def element's class attribution is null");
			}

			// 获得class属性对应的配置类型
			Class<?> baseDefClass = ClassManager.instance().get(attrClassValue);
			if (baseDefClass == null) {
				throw new NullPointerException("the class simple name=" + attrClassValue + " isn't  exsit def class");
			}
			// 创建对应class对象
			T def = (T) baseDefClass.newInstance();
			// 将对应field信息注入到def对象中
			newBaseDef(element, def, baseDefClass);
			// 将BaseDef对象插入到列表中
			settingDatas.add(def);
		} catch (Exception e) {
			throw new RuntimeException("attributeClass", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void collection(Class<?> fieldType, Method method, Element e, Object object) throws Exception {
		Type type = getType(fieldType);
		List<Element> elements = e.getChildren(XmlLabel.E);
		int i = 0;
		for (Element element : elements) {
			if (type == Type.list) {
				List<Object> obj = (List<Object>) object;
				// list泛型的参数
				Class<?> param0 = GenericsUtils.getMethodParamGenericType(method, 0);
				Type paramType = getType(param0);
				if (paramType == Type.basic) {
					// 值属性
					Attribute valueAttr = element.getAttribute(XmlLabel.VALUE);
					if (valueAttr == null) {
						throw new RuntimeException("varible=" + e.getName() + " <e> has no value attribute");
					}
					String value = valueAttr.getValue();
					// 判断值是否存在
					if (StringUtils.isBlank(value))
						throw new RuntimeException("varible=" + e.getName() + " <e> value attribute value is empty");
					obj.add(toType(value, param0));
				} else if (paramType == Type.complex) {// 复合类型
					// 类属性
					Attribute classAttr = element.getAttribute(XmlLabel.CLASS_ATTR);
					if (classAttr == null) {
						throw new RuntimeException("varible=" + e.getName() + " <e> has no class attribute");
					}
					String clazz = classAttr.getValue();
					// 判断class是否存在
					if (StringUtils.isBlank(clazz))
						throw new RuntimeException("varible=" + e.getName() + " <e> class attribute value is empty");
					// 指定加载中的内存中的Class
					Class<?> claz = ClassManager.instance().get(clazz);
					if (claz == null) {
						throw new RuntimeException("varible=" + e.getName() + ", class value=" + clazz
								+ " has not def class");
					}
					// 创建对应class对象
					T def = (T) claz.newInstance();
					newBaseDef(element, def, claz);
					obj.add(def);
				}
			} else if (type == Type.map) {
				Map<Object, Object> map = (Map<Object, Object>) object;
				// map泛型的参数
				Class<?> param0 = GenericsUtils.getMethodParamGenericType(method, 0);
				Class<?> param1 = GenericsUtils.getMethodParamGenericType(method, 1);
				if (!isSimple(param0.getName()))
					throw new RuntimeException("varible=" + e.getName() + " <e> key class must be basic data type");
				// 键属性
				Attribute keyeAttr = element.getAttribute(XmlLabel.KEY);
				if (keyeAttr == null) {
					throw new RuntimeException("varible=" + e.getName() + " <e> has no key attribute");
				}
				// 键
				String keyValue = keyeAttr.getValue();
				// 判断键是否存在
				if (StringUtils.isBlank(keyValue))
					throw new RuntimeException("varible=" + e.getName() + " <e> key attribute value is empty");
				// key
				Object keyObject = toType(keyValue, param0);
				Type paramType = getType(param1);
				if (paramType == Type.basic) {
					// 值属性
					Attribute valueAttr = element.getAttribute(XmlLabel.VALUE);
					if (valueAttr == null) {
						throw new RuntimeException("varible=" + e.getName() + " <e> has no value attribute");
					}
					String value = valueAttr.getValue();
					// 判断值是否存在
					if (StringUtils.isBlank(value))
						throw new RuntimeException("varible=" + e.getName() + " <e> value attribute value is empty");
					// 放入参数
					map.put(keyObject, toType(value, param1));
				} else if (paramType == Type.complex) {
					// 类属性
					Attribute classAttr = element.getAttribute(XmlLabel.CLASS_ATTR);
					if (classAttr == null) {
						throw new RuntimeException("varible=" + e.getName() + " <e> has no class attribute");
					}
					String clazz = classAttr.getValue();
					// 判断class是否存在
					if (StringUtils.isBlank(clazz))
						throw new RuntimeException("varible=" + e.getName() + " <e> class attribute value is empty");
					// 指定加载中的内存中的Class
					Class<?> claz = ClassManager.instance().get(clazz);
					if (claz == null) {
						throw new RuntimeException("varible=" + e.getName() + ", class value=" + clazz
								+ " has not def class");
					}
					// 创建对应class对象
					T def = (T) claz.newInstance();
					newBaseDef(element, def, claz);
					map.put(keyObject, def);
				}
			} else if (type == Type.arr) {
				// 值属性
				Attribute valueAttr = element.getAttribute(XmlLabel.VALUE);
				// 类属性
				Attribute classAttr = element.getAttribute(XmlLabel.CLASS_ATTR);
				if (valueAttr == null && classAttr == null) {
					throw new RuntimeException("varible=" + e.getName() + ", please correct your type");
				}
				if (classAttr == null) {
					String value = valueAttr.getValue();
					// 判断值是否存在
					if (StringUtils.isBlank(value))
						throw new RuntimeException("varible=" + e.getName() + " <e> value attribute value is empty");
					Class<?> claz = ClassTool.getArrayType(object.getClass());
					Object obj = toType(value, claz);
					Array.set(object, i, obj);
				} else if (valueAttr == null) {
					String clazz = classAttr.getValue();
					if (StringUtils.isBlank(clazz))
						throw new RuntimeException("varible=" + e.getName() + " <e> class attribute value is empty");
					// 指定加载中的内存中的Class
					Class<?> claz = ClassManager.instance().get(clazz);
					if (claz == null) {
						throw new RuntimeException("varible=" + e.getName() + ", class value=" + clazz
								+ " has not def class");
					}
					// 创建对应class对象
					T def = (T) claz.newInstance();
					newBaseDef(element, def, claz);
					Array.set(object, i, def);
				}
				i++;
			}
		}
	}

	/**
	 * 根据迭代将数据注入到对应字段
	 * 
	 * @param element
	 *            字段<field>信息
	 * @param def
	 *            转换的类对象
	 * @param clazz
	 *            类型
	 * @throws Exception
	 */
	private static void newBaseDef(Element element, Object def, Class<?> clazz) throws Exception {
		// 找到def下的field的子元素
		List<Element> elements = element.getChildren();
		for (Element ele : elements) {
			// 变量名
			String varName = ele.getName();
			Field field = ClassTool.getFieldAboutSuper(def.getClass(), varName);
			// 判断clazz是否有该字段
			if (field == null) {
				logger.error("class {} has no {} field", def.getClass().getName(), varName);
				throw new NullPointerException("class=" + def.getClass().getName() + " ,field=" + varName
						+ " is not exsit!!");
			}
			// 获得方法名(setter)
			String filedName = ClassTool.getSetterMethod(field.getName(), field.getType());
			// 方法
			Method method = ClassTool.getMethodByClassAndName(clazz, filedName);
			// 判断方法是否存在
			if (method == null) {
				throw new RuntimeException("not found method by " + filedName);
			}
			// 获得第一个参数
			Class<?> fieldType = method.getParameterTypes()[0];
			// 获得参数类型
			Type type = getType(fieldType);
			Object object = null;
			// 基础类型
			if (type == Type.basic) {
				// value属性
				Attribute valueAttr = ele.getAttribute(XmlLabel.VALUE);
				if (valueAttr == null) {
					throw new RuntimeException("parent element name=" + ele.getParentElement().getName()
							+ ", element name=" + ele.getName() + ", attribute=" + XmlLabel.VALUE + " is null");
				}
				String value = valueAttr.getValue();
				// 属性对象
				object = toType(value, fieldType);
				// 复杂类型
			} else if (type == Type.complex) {
				// class属性
				Attribute classAttr = ele.getAttribute(XmlLabel.CLASS_ATTR);
				if (classAttr == null) {
					throw new RuntimeException("class=" + clazz.getName() + ", varible=" + varName
							+ " xml has not class attribute");
				}
				String clazVal = classAttr.getValue();// 复杂类型的class
				Class<?> claz = ClassManager.instance().get(clazVal);
				if (claz == null) {
					throw new RuntimeException("class name=" + clazVal + " is not exsit");
				}
				object = claz.newInstance();
				newBaseDef(ele, object, claz);
			} else if (type == Type.arr) {
				Class<?> claz = ClassTool.getArrayType(fieldType);
				Integer[] integers = getComp(ele.getChildren(XmlLabel.E));
				object = Array.newInstance(claz, ArrayUtils.toPrimitive(integers));
				// array元素
				collection(fieldType, method, ele, object);
			} else if (type == Type.map) {
				Map<Object, Object> map = new HashMap<Object, Object>();
				// map元素
				collection(fieldType, method, ele, map);
				object = map;
			} else if (type == Type.list) {
				// 初始化为ArrayList
				ArrayList<Object> list = new ArrayList<Object>();
				// list元素
				collection(fieldType, method, ele, list);
				// 调整为列表
				list.trimToSize();
				// 将获得list放入
				object = list;
			}
			method.invoke(def, object);
		}
	}

	private static Type getType(Class<?> claz) {
		Type result = null;
		if (isSimple(claz.getName())) {
			result = Type.basic;
		} else if (claz.isArray()) {
			result = Type.arr;
		} else if (GenericsUtils.checkInterface(claz, Map.class)) {
			result = Type.map;
		} else if (GenericsUtils.checkInterface(claz, Collection.class)) {
			result = Type.list;
		} else {
			result = Type.complex;
		}
		return result;

	}

	public static boolean isSimple(String type) {
		// 判断数据类型
		if (type.equals("int") || type.equals("java.lang.Integer")) {
			return true;
		} else if (type.equals("String") || type.equals("java.lang.String")) {
			return true;
		} else if (type.equals("long") || type.equals("java.lang.Long")) {
			return true;
		} else if (type.equals("float") || type.equals("java.lang.Float")) {
			return true;
		} else if (type.equals("short") || type.equals("java.lang.Short")) {
			return true;
		} else if (type.equals("byte") || type.equals("java.lang.Byte")) {
			return true;
		} else if (type.equals("double") || type.equals("java.lang.Double")) {
			return true;
		} else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
			return true;
		} else if (type.equals("char") || type.equals("java.lang.Character")) {
			return true;
		}
		return false;
	}

	/**
	 * 获得复合数组的最大列长度
	 * 
	 * @param arrElements
	 * @return
	 */
	private static void getMaxCompositiveLen(List<Element> arrElements, List<Integer> comp, int num) {
		int max = comp.size() == num ? 0 : comp.get(num);
		for (int i = 0; i < arrElements.size(); i++) {
			int txt = num;
			Element element = arrElements.get(i);
			List<Element> childElements = element.getChildren(XmlLabel.E);
			if (childElements == null || childElements.size() == 0) {
				continue;
			}
			int len = childElements.size();// 长度
			if (max < len) {
				max = len;
				if (comp.size() == num)
					comp.add(num, max);
				else
					comp.set(num, max);
			}
			txt++;
			getMaxCompositiveLen(childElements, comp, txt);
		}
	}

	private static Integer[] getComp(List<Element> arrElements) {
		List<Integer> comp = new ArrayList<Integer>();
		comp.add(0, arrElements.size());
		getMaxCompositiveLen(arrElements, comp, 1);
		return comp.toArray(new Integer[comp.size()]);
	}

	/**
	 * 将数据信息转换成相应类型
	 * 
	 * @param context
	 *            数据信息
	 * @param typeClass
	 *            指定类型
	 * @return
	 */

	public static Object toType(String context, Class<?> typeClass) {
		// 判断内容是否为空，以及判断传入的制定类型是否为空
		if (typeClass == null) {
			logger.debug("{} or {} is empty", context, typeClass);
			return null;
		}

		String type = typeClass.getName();

		// 判断数据类型
		if (type.equals("int") || type.equals("java.lang.Integer")) {
			return StringUtils.isEmpty(context) ? 0 : Integer.valueOf(context);
		} else if (type.equals("String") || type.equals("java.lang.String")) {
			return context;
		} else if (type.equals("long") || type.equals("java.lang.Long")) {
			return StringUtils.isEmpty(context) ? 0l : Long.valueOf(context);
		} else if (type.equals("float") || type.equals("java.lang.Float")) {
			return StringUtils.isEmpty(context) ? 0f : Float.valueOf(context);
		} else if (type.equals("short") || type.equals("java.lang.Short")) {
			return StringUtils.isEmpty(context) ? 0 : Short.valueOf(context);
		} else if (type.equals("byte") || type.equals("java.lang.Byte")) {
			return StringUtils.isEmpty(context) ? 0 : Byte.valueOf(context);
		} else if (type.equals("double") || type.equals("java.lang.Double")) {
			return StringUtils.isEmpty(context) ? 0d : Double.valueOf(context);
		} else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
			return StringUtils.isEmpty(context) ? false : Boolean.valueOf(context);
		} else if (type.equals("char") || type.equals("java.lang.Character")) {
			return StringUtils.isEmpty(context) ? "" : context.charAt(0);
		}

		return null;
	}

	/**
	 * 将字符串转换为基础类型的数组[只处理一维与二维的数组]
	 * 
	 * @param context
	 *            字符串
	 * @param arrNum
	 *            维数
	 * @param typeClass
	 *            类型
	 * @return
	 */
	private static Object getOne(String context, int arrNum, Class<?> typeClass) {
 
		
		try {
			// 获得基础类型的valueOf方法
			Method method = typeClass.getMethod("valueOf", String.class);
			// 判断方法是否存在
			if (method == null) {
				logger.error("{} has no method 'valueOf'", typeClass);
				throw new NullPointerException("'valueOf' method is not exsit");
			}
			if (StringUtils.isEmpty(context)) {// 如果配置为空串，返回0长度数组
				return Array.newInstance((Class<?>) typeClass.getField("TYPE").get("TYPE"), 0);
			}

			// 处理一维数组
			if (arrNum == 1) {
				String strs[] = context.trim().split(",");
				Object array = Array.newInstance((Class<?>) typeClass.getField("TYPE").get("TYPE"), strs.length);
				for (int i = 0; i < strs.length; i++) {
					Array.set(array, i, method.invoke(typeClass, strs[i]));
				}
				return array;
			}
			// 处理二维数组
			if (arrNum == 2) {
				String strs[] = context.trim().split(";");
				Object array = Array.newInstance((Class<?>) typeClass.getField("TYPE").get("TYPE"), strs.length,
						ClassTool.getMaxArrLen(strs));
				for (int i = 0; i < strs.length; i++) {
					String ione[] = strs[i].split(",");
					for (int j = 0; j < ione.length; j++) {
						Object obj = Array.get(array, i);
						Array.set(obj, j, method.invoke(typeClass, ione[j]));
					}
				}
				return array;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return typeClass;
	}

	/**
	 * 字符串转换为数组的处理
	 * 
	 * @param context
	 *            字符串
	 * @param arrNum
	 *            转换的维维数
	 * @param typeClass
	 *            字符串的类类型
	 * @return
	 */
	public static Object getArray(String context, int arrNum, Class<?> typeClass) {
		if (context == null) {
			return null;
		}
		String type = typeClass.getName();
		if (type.equals("int") || type.equals("java.lang.Integer")) {
			return getOne(context, arrNum, Integer.class);
		} else if (type.equals("String") || type.equals("java.lang.String")) {
			return ClassTool.parseString2Array(context, arrNum);
		} else if (type.equals("long") || type.equals("java.lang.Long")) {
			return getOne(context, arrNum, Long.class);
		} else if (type.equals("float") || type.equals("java.lang.Float")) {
			return getOne(context, arrNum, Float.class);
		} else if (type.equals("short") || type.equals("java.lang.Short")) {
			return getOne(context, arrNum, Short.class);
		} else if (type.equals("byte") || type.equals("java.lang.Byte")) {
			return getOne(context, arrNum, Byte.class);
		} else if (type.equals("double") || type.equals("java.lang.Double")) {
			return getOne(context, arrNum, Double.class);
		} else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
			return getOne(context, arrNum, Boolean.class);
		} else if (type.equals("char") || type.equals("java.lang.Character")) {
			return getOne(context, arrNum, Character.class);
		}
		return null;
	}
}
