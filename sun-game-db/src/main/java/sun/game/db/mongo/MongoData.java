package sun.game.db.mongo;

import java.io.Serializable;

public interface MongoData<PK extends Serializable> extends Serializable {
	public String idName();

	public PK idValue();
}
