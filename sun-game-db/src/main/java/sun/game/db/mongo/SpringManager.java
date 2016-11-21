package sun.game.db.mongo;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringManager implements ApplicationContextAware{
	private static ApplicationContext springContext;

	public static <T> T getInstance(String bean, Class<T> clazz) {
		return springContext.getBean(bean, clazz);
	}

	public static <T> T getInstance(Class<T> clazz) {
		return springContext.getBean(clazz.getSimpleName(), clazz);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		springContext = applicationContext;
	}
}
