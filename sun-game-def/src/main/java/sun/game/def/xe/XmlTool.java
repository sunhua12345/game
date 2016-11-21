package sun.game.def.xe;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.game.def.entity.Describle;
import sun.game.def.xe.mark.Suffix;
import sun.game.def.xe.mark.XmlLabel;


public class XmlTool {

	private static Logger logger = LoggerFactory.getLogger(XmlTool.class);

	public static String downpath = System.getProperty("user.dir") + "/def/xml/" == null ? "F:/workspace/maveneclipse/cs_resource/src/main/webapp/xml/"
			: System.getProperty("user.dir") + "/def/xml/";

	public static String xmlpath_server = "F:/workspace/maveneclipse/cs_resource/src/main/webapp/xml_avaliable/server/";

	public static String xmlpath_client = "F:/workspace/maveneclipse/cs_resource/src/main/webapp/xml_avaliable/client/";

	/**
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 *             根据类型查询配置信息
	 * 
	 * @param claz
	 *            类型
	 * @return List<MyField> 返回配置信息
	 * @throws
	 */
	public static List<MyField> getFieldDesc(Class<?> claz) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		List<MyField> fileds = new ArrayList<MyField>();
		Field[] fields = ClassTool.getFieldsContainSuper(claz);

		for (Field field : fields) {
			String name_desc[] = findOne(field);

			if (name_desc == null) {
				logger.error("{} has no describle", field.getName());
				continue;
			}
			// 获得设置方法名
			String setter = ClassTool.getSetterMethod(field.getName(), field.getType());
			Method method = ClassTool.getMethodByClassAndName(claz, setter);
			if (method == null) {
				throw new RuntimeException("not found method with " + setter);
			}
			// 获取方法的形参
			Class<?> param = method.getParameterTypes()[0];
			MyField MyField = new MyField();
			MyField.setName(name_desc[0]);
			MyField.setDescrible(name_desc[1]);
			// 参数类型
			MyField.setClazz(param);
			fileds.add(MyField);
		}

		logger.info("{} excel load successful", claz.getName());
		return fileds;
	}

	/**
	 * 将配置信息转换为xml文件
	 * 
	 * @param treeMap
	 *            传入所有配置信息
	 * @throws
	 */
	public static void convertToXml(TreeMap<String, List<MyField>> treeMap, boolean hasType)
			throws FileNotFoundException, IOException {
		for (Entry<String, List<MyField>> entry : treeMap.entrySet()) {
			// 创建一个xml文档的 根节点
			Document document = new Document();
			List<MyField> fileds = entry.getValue();
			// 创建一个xml文档的 根元素节点
			Element root = new Element(XmlLabel.DEFS_LABEL);
			root.setAttribute(XmlLabel.CLASS_ATTR, entry.getKey());
			// 把这个root根元素加到这个document文档下
			document.addContent(root);
			if (fileds != null && fileds.size() != 0) {
				Element def = new Element(XmlLabel.DEF_LABEL);
				root.addContent(def);
				for (MyField MyField : fileds) {
					result(MyField, def, hasType);
				}
			}

			// 把创建的xml文档写到流中
			XMLOutputter out = new XMLOutputter();
			// 设置生成xml文档的格式
			Format format = Format.getPrettyFormat();
			// 自定义xml文档的缩进(敲了四个空格，代表四个缩进)
			format.setIndent("    ");
			out.setFormat(format);
			out.output(document, new FileOutputStream(downpath + getDefName(entry.getKey()) + Suffix.XML));
		}
	}

	public static String getDefName(String pathName) {
		return pathName.substring(pathName.lastIndexOf('.') + 1, pathName.length());
	}

	/**
	 * 将excel中传入的数据转换为xml
	 * 
	 * @param messages
	 *            描述数据
	 * @param treemap
	 *            传入的数据(具体详细数据)
	 * @return void 返回类型
	 * @throws
	 */
	public static void convertExcel2XML(List<List<FieldExcel>> xmlDef, String version, String sheetName, String right) {
		String clazzName = sheetName;
		String xmlName = getDefName(clazzName) + RegixTool.Mark.Line.toString() + version + Suffix.XML;

		// 创建一个xml文档的 根节点
		Document document = new Document();
		// 创建一个xml文档的 根元素节点
		Element root = new Element(XmlLabel.DEFS_LABEL);
		// 把这个root根元素加到这个document文档下
		document.addContent(root);

		for (int i = 0; i < xmlDef.size(); i++) {
			List<FieldExcel> listContent = xmlDef.get(i);
			// def元素添加
			Element def = new Element(XmlLabel.DEF_LABEL);
			def.setAttribute(XmlLabel.CLASS_ATTR, clazzName);
			root.addContent(def);
			for (int j = 0; j < listContent.size(); j++) {
				FieldExcel fieldExcel = listContent.get(j);
				Element elementField = new Element(XmlLabel.FIELD_LABEL);
				elementField.setAttribute(XmlLabel.NAME_ATTR, fieldExcel.getName());
				elementField.setText(fieldExcel.getValue());
				def.addContent(elementField);
			}
		}

		// 把创建的xml文档写到流中
		XMLOutputter out = new XMLOutputter();
		// 设置生成xml文档的格式
		Format format = Format.getPrettyFormat();
		// 自定义xml文档的缩进(敲了四个空格，代表四个缩进)
		format.setIndent("    ");
		out.setFormat(format);
		try {
			if (right.equals("s")) {
				out.output(document, new FileOutputStream(xmlpath_server + xmlName));
			} else if (right.equals("l")) {
				out.output(document, new FileOutputStream(xmlpath_client + xmlName));
			}
			logger.debug("convert excel to xml success");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static FieldExcel get(List<String> list, int num) {
		String right = list.get(0);
		String type = list.get(1);
		String desc = list.get(2);
		String name = list.get(3);
		FieldExcel excel = new FieldExcel();
		excel.setDesc(desc);
		excel.setName(name);
		excel.setRight(right);
		excel.setType(type);
		excel.setValue(list.get(num));

		return excel;
	}

	/**
	 * 迭代转换xml
	 * 
	 * @param MyField
	 *            字段信息
	 * @param def
	 *            xml元素
	 * @throws
	 */
	public static void result(MyField MyField, Element def, boolean hasType) {
		int type = MyField.getType();
		Class<?> clz = MyField.getClazz();
		Element field = new Element(XmlLabel.FIELD_LABEL);
		field.setAttribute(XmlLabel.NAME_ATTR, MyField.getName());
		if (type == 0) {
			field.setText(MyField.getDescrible());
			if (hasType) {
				String types = MyField.getClazz().getName();
				if (types.equals("String") || types.equals("java.lang.String"))
					types = "string";
				field.setAttribute(XmlLabel.TYPE_ATTR, types);
			}
			def.addContent(field);
		} else if (type == 1) {
			field.setAttribute(XmlLabel.CLASS_ATTR, clz.getName());
			List<MyField> MyFields = MyField.getFiled();
			for (MyField myfield : MyFields) {
				result(myfield, field, hasType);
			}
			def.addContent(field);
		} else if (type == 2) {
			field.setText(MyField.getDescrible());
			def.addContent(field);
		} else if (type == 3) {
			if (hasType) {
				field.setAttribute(XmlLabel.CLASS_ATTR, clz.getName());
				field.setAttribute(XmlLabel.TYPE_ATTR, XmlLabel.SYNTHETIC_VALUE);
				// field.setAttribute(XmlLabel.DIMENSION_ATTR,
				// String.valueOf(arrNum));
			}

			Element arrays = new Element(XmlLabel.ARRAYS_LABEL);
			field.addContent(arrays);
			def.addContent(field);
			Element array = new Element(XmlLabel.ARRAY_LABEL);
			arrays.addContent(array);
			List<MyField> MyFields = MyField.getFiled();
			for (MyField filed : MyFields) {
				result(filed, array, hasType);
			}
		}
	}

	/**
	 * 判断是否为基础类型
	 * 
	 * @param @param typeClass 类型
	 * @return boolean 返回是否为基础类型
	 */
	public static boolean toType(Class<?> typeClass) {
		String type = typeClass.getName();

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
	 * 查找对应类的字段信息
	 * 
	 * @param claz
	 *            类型
	 * @return List<MyField> 返回配置对应的字段信息
	 * @throws
	 */
	public static List<MyField> fieldOne(Class<?> claz) throws Exception {
		Field fields[] = ClassTool.getFieldsContainSuper(claz);

		List<MyField> fileds = new ArrayList<MyField>();
		for (Field field : fields) {
			// 返回字段名与描述文字
			String value[] = findOne(field);
			// 获得设置方法名
			String setter = ClassTool.getSetterMethod(field.getName(), field.getType());
			Method method = ClassTool.getMethodByClassAndName(claz, setter);
			if (method == null) {
				logger.error("{} has no method {}", claz.getName(), setter);
				continue;
			}

			// 获取方法的形参
			Class<?> param = method.getParameterTypes()[0];
			// 判断是否为基础类型
			boolean isPrimitive = toType(param);

			MyField MyField = new MyField();
			// 设置字段描述
			MyField.setDescrible(value[1]);
			// 设置字段名
			MyField.setName(value[0]);

			if (isPrimitive) {
				// 如果是基础类型则设置type=0,并传入参数类型
				MyField.setType(0);
				MyField.setClazz(param);
			} else if (param.isArray()) {
				// 如果是数组类型，找到数组类型的基础类型
				Class<?> cls = ClassTool.getArrayType(param);
				// 找到数组的维数
				int arrNum = ClassTool.getArrayNum(param);
				// 判断该基础类型是否为基本类型数据
				boolean clsIsPrimitive = toType(cls);
				if (clsIsPrimitive) {
					// 如果是基本类型数据，则设置type=2,并设置维数
					MyField.setType(2);
					MyField.setArrNum(arrNum);
				} else {
					// 如果是复合类型，则设置type=3和设置维数,并且进入继续查找对应复合类型的字段
					MyField.setType(3);
					MyField.setArrNum(arrNum);
					if (field.getName().contains("feasibles")) {
						System.out.println(1);
					}
					insertMyField(cls, MyField);
				}
				// 设置类型
				MyField.setClazz(cls);
			} else {
				// 如果为复合类型,则设置type=1,设置参数类型，并且进入继续查找对应复合类型的字段
				MyField.setType(1);
				MyField.setClazz(param);
				insertMyField(param, MyField);
			}

			fileds.add(MyField);
		}

		logger.info("{} xml loadding successful", claz.getName());
		return fileds;
	}

	/**
	 * 根据传入的父类字段，找到父类字段对应类的字段信息
	 * 
	 * @param claz
	 *            类型
	 * @param @param MyFieldParent 字段
	 * @throws
	 */
	public static void insertMyField(Class<?> claz, MyField MyFieldParent) throws Exception {
		Field fields[] = claz.getDeclaredFields();

		for (Field field : fields) {
			// 返回字段名与描述文字
			String value[] = findOne(field);
			// 获得设置方法名
			String setter = ClassTool.getSetterMethod(field.getName(), field.getClass());
			Method method = ClassTool.getMethodByClassAndName(claz, setter);
			if (setter.equals("setSerialVersionUID")) {
				logger.error("{} dosen't has the method {}", claz.getName(), setter);
				continue;
			}
			// 获取方法的形参
			Class<?> param = method.getParameterTypes()[0];

			// 判断是否为基础类型
			boolean isPrimitive = toType(param);
			MyField MyField = new MyField();
			// 插入字段描述
			MyField.setDescrible(value[1]);
			// 设置字段名
			MyField.setName(value[0]);
			if (isPrimitive) {
				// 如果是基础类型将type设置为0
				MyField.setType(0);
			} else if (param.isArray()) {
				// 判断字段是否为数组

				// 找到字段数组的基础类型
				Class<?> cls = ClassTool.getArrayType(param);
				// 判断该字段数组维数
				int arrNum = ClassTool.getArrayNum(param);
				// 判断该数组为基本类型数组,还是复合类型数组
				boolean clsIsPrimitive = toType(cls);
				if (clsIsPrimitive) {
					// 如果为基础类型数组,设置字段type=2,然后设置维数
					MyField.setType(2);
					MyField.setArrNum(arrNum);
				} else {
					// 如果为复合类型数组，设置字段type=3,然后设置维数,并且迭代复合类型
					MyField.setType(3);
					MyField.setArrNum(arrNum);
					insertMyField(cls, MyField);
				}
			} else {
				// 如果是复合类型，则设置type=1,然后迭代复合类型
				MyField.setType(1);
				insertMyField(param, MyField);
			}

			if (param.isArray()) {
				param = ClassTool.getArrayType(param);
			}
			// 设置参数类型
			MyField.setClazz(param);
			// 将查找出来的字段插入到父字段中
			MyFieldParent.getFiled().add(MyField);
		}
	}

	/**
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 *             根据传入字段返回字段的字段名和字段描述
	 * 
	 * @param field
	 *            字段
	 * @return String[] 返回字段名和字段描述
	 * @throws
	 */
	public static String[] findOne(Field field) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		boolean hasDescrible = false;
		String value = "";
		if(field.isAnnotationPresent(Describle.class)){
			hasDescrible = true;
			Annotation annotation = field.getAnnotation(Describle.class);
			value = (String) annotation.annotationType().getDeclaredMethod("value").invoke(annotation);
		}

		if (!hasDescrible) {
			logger.error("{} has no @Describle annotation", field.getName());
			return null;
		}

		return new String[] { field.getName(), value };
	}

}
