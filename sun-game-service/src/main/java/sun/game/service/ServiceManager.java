package sun.game.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sun.game.common.annotation.IService;
import sun.game.common.find.DynamicFind;
import sun.game.common.hot.HotNotify;




public class ServiceManager extends DynamicFind implements HotNotify {
	private ServiceManager() {

	}

	private static ServiceManager serviceManager = new ServiceManager();

	public static ServiceManager getManager() {
		return serviceManager;
	}

	private final Map<String, Object> services = new ConcurrentHashMap<String, Object>();

	public boolean verification(Class<?> clazz) {
		return annotationOn(clazz, IService.class)
				&& clazz.getInterfaces().length > 0;
	}

	@Override
	public void findClass(Class<?> clz) throws Exception {
		Class<?> clazz = clz.getInterfaces()[0];
		services.put(clazz.getSimpleName(), clz.newInstance());
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<?> clz) {
		return (T) services.get(clz.getSimpleName());
	}

	public static <T> T getService(Class<?> clz) {
		return serviceManager.getInstance(clz);
	}

	public void update(Class<?> newClz) throws Exception {
		if (newClz.isAnonymousClass()) {
			return;
		}
		if (newClz.isInterface()) {
			return;
		}
		Class<?> clazz = newClz.getInterfaces()[0];
		services.put(clazz.getSimpleName(), newClz.newInstance());
	}

	public Object getMangerObject() {
		return this;
	}
}
