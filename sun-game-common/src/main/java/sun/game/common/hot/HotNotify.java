package sun.game.common.hot;

public interface HotNotify {
	public void update(Class<?> newClz) throws Exception;

	public Object getMangerObject();
}
