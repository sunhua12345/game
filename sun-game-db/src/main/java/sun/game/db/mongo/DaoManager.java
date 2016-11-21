package sun.game.db.mongo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sun.game.common.find.DynamicFind;



public class DaoManager extends DynamicFind {
	private DaoManager() {
	}

	private static DaoManager daoManager = new DaoManager();
	private final Map<String, MongoDao<?>> mongoDaos = new ConcurrentHashMap<String, MongoDao<?>>();

	public static DaoManager getManager() {
		return daoManager;
	}

	@Override
	public void findClass(Class<?> clz) throws Exception {
		MongoDao<?> mongoDao = (MongoDao<?>) SpringManager.getInstance(
				clz);
		mongoDaos.put(clz.getSimpleName(), mongoDao);
	}

	public boolean verification(Class<?> clazz) {
		return superClassOn(clazz, MongoDao.class);
	}

	public static <T extends MongoDao<?>> T getDao(Class<? extends MongoDao<?>> clz) {
		return daoManager.getMongoDao(clz);
	}

	@SuppressWarnings("unchecked")
	private <T extends MongoDao<?>> T getMongoDao(Class<? extends MongoDao<?>> clz) {
		return (T) this.mongoDaos.get(clz.getSimpleName());
	}
}
