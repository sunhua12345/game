package sun.game.def.xe;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ClassManager {

	/**
	 * <类名，类全路径>
	 */
	private final Map<String, Class<?>> clazMap = new HashMap<String, Class<?>>();

	private static ClassManager manager;

	private ClassManager() {
		load();
	}

	public static ClassManager instance() {
		if (manager == null) {
			manager = new ClassManager();
		}
		return manager;
	}

	/**
	 * 加载配置类
	 */
	private void load() {
		Set<Class<?>> classSet = DefClassManager.getManager().getClasses();
		for (Class<?> claz : classSet) {
			String simpleName = claz.getSimpleName();
			clazMap.put(simpleName, claz);
		}
	}

	/**
	 * 获得配置类型
	 * 
	 * @param simpleName
	 *            类简单类名
	 * @return
	 */
	public Class<?> get(String simpleName) {
		return clazMap.get(simpleName);
	}
}
