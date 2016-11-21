package sun.game.common.thread;

import java.io.Serializable;

public abstract interface Work extends Serializable {

	public void init(Object... objs) throws Exception;

}
