package sun.game.def.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.game.common.utils.StringHelper;
import sun.game.def.xe.DefDataTool;

public class DefFactory {
	private final Logger logger = LoggerFactory.getLogger(DefFactory.class);

	private DefFactory() {

	}

	private static DefFactory defFactory = new DefFactory();

	public static DefFactory getFactory() {
		return defFactory;
	}

	private final Map<DefType, Map<Object, BaseDef<? extends Serializable>>> keyDefMap = new HashMap<DefType, Map<Object, BaseDef<? extends Serializable>>>();// 主键id对应map

	@SuppressWarnings("unchecked")
	public <T extends BaseDef<PK>, PK extends Serializable> T getDef(
			DefType type, PK defId) {
		Map<Object, BaseDef<? extends Serializable>> defMap = keyDefMap
				.get(type);
		if (defMap == null) {
			return null;
		}
		return (T) defMap.get(defId);
	}

	public Collection<BaseDef<? extends Serializable>> getDefList(DefType type) {
		Map<Object, BaseDef<? extends Serializable>> map = keyDefMap.get(type);
		if (map != null) {
			return map.values();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseDef<? extends Serializable>> T getDef(DefType type,
			Object... keyPropertys) {
		Map<Object, BaseDef<? extends Serializable>> defMap = keyDefMap
				.get(type);
		if (defMap == null) {
			return null;
		}
		return (T) defMap.get(StringHelper.buildKey(keyPropertys));
	}

	@SuppressWarnings("unchecked")
	public void loadDef() throws Exception {
		Map<String, List<Object>> dataMap = DefDataTool.instance()
				.getBaseObjectMap();
		for (Entry<String, List<Object>> entry : dataMap.entrySet()) {
			List<Object> objs = entry.getValue();
			for (Object obj : objs) {
				BaseDef<? extends Serializable> def = (BaseDef<? extends Serializable>) obj;
				put(def);
			}
			logger.debug("-----------------{} xml to object is load over!",
					entry.getKey());
		}
	}

	public void reloadDef() throws Exception {
		synchronized (this.keyDefMap) {
			this.keyDefMap.clear();
			DefDataTool.instance().getBaseObjectMap().clear();
			loadDef();
		}
	}

	private void put(BaseDef<? extends Serializable> def) {
		if (!keyDefMap.containsKey(def.getDefType())) {
			// 还没有加载过,直接放进cache
			Map<Object, BaseDef<? extends Serializable>> tempMap = new HashMap<Object, BaseDef<? extends Serializable>>();
			tempMap.put(def.getId(), def);
			keyDefMap.put(def.getDefType(), tempMap);
			return;
		}
		// 已经加载过
		Map<Object, BaseDef<? extends Serializable>> map = keyDefMap.get(def
				.getDefType());
		if (map.containsKey(def.getId())) {
			throw new RuntimeException("the same id is load!");
		}
		map.put(def.getId(), def);
	}

	public void start() throws Exception {
		loadDef();
	}
}
