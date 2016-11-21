package sun.game.net.tcp.process;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sun.game.common.annotation.IProcess;
import sun.game.common.find.DynamicFind;
import sun.game.common.hot.HotNotify;



public class ProcessManager extends DynamicFind implements HotNotify {
	private static final Map<Integer, Class<?>> processClazzMap = new ConcurrentHashMap<Integer, Class<?>>();

	private ProcessManager() {
	}

	private static ProcessManager processManager = new ProcessManager();

	public static ProcessManager getManager() {
		return processManager;
	}

	public boolean verification(Class<?> clazz) {
		return annotationOn(clazz, IProcess.class);
	}

	@Override
	public void findClass(Class<?> clz) throws Exception {
		IProcess iProcess = clz.getAnnotation(IProcess.class);
		processClazzMap.put(iProcess.code(), clz);
	}

	public Class<?> getProcess(int code) {
		return processClazzMap.get(code);
	}

	public Object getMangerObject() {
		return this;
	}

	public void update(Class<?> newClz) throws Exception {
		if (newClz.isAnonymousClass()) {
			return;
		}
		if (newClz.isInterface()) {
			return;
		}
		IProcess iProcess = newClz.getAnnotation(IProcess.class);
		processClazzMap.put(iProcess.code(), newClz);
		System.out.println("class be reload:"+newClz.getSimpleName());
	}
}
