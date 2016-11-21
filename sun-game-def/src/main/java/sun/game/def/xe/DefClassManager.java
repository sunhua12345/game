package sun.game.def.xe;

import java.util.HashSet;
import java.util.Set;

import sun.game.common.find.DynamicFind;
import sun.game.def.entity.BaseDef;



public class DefClassManager extends DynamicFind{
	private DefClassManager(){
		
	}
	private static DefClassManager defClassManager = new DefClassManager();
	private final Set<Class<?>> classes = new HashSet<Class<?>>();
	public static DefClassManager getManager(){
		return defClassManager;
	}
	@Override
	public void findClass(Class<?> clz) throws Exception {
		this.classes.add(clz);
	}

	public boolean verification(Class<?> clazz) {
		return haveSuperInterfaceOn(clazz,BaseDef.class);
	}
	public Set<Class<?>> getClasses() {
		return classes;
	}
	
	

}
