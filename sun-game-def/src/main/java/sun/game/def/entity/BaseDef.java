package sun.game.def.entity;

import java.io.Serializable;

public interface BaseDef<PK extends Serializable> extends Serializable {

	public abstract PK getId();

	public DefType getDefType();

}
