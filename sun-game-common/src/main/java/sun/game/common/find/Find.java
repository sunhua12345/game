package sun.game.common.find;

public interface Find {
	public void find() throws Exception;

	public boolean verification(Class<?> clazz);

	public void start();
}
