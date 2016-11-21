package sun.game.db.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;

import sun.game.common.utils.StringHelper;


public abstract class MongoDao<E extends MongoData<?>> {
	@Autowired
	protected MongoTemplate mongoTemplate;

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public abstract Class<? extends MongoData<?>> getEntityClass();

	@SuppressWarnings("unchecked")
	public E findOne(Object id) {
		E e = (E) this.mongoTemplate.findById(id, getEntityClass(),
				getEntityClass().getSimpleName().toLowerCase());
		return e;
	}

	@SuppressWarnings("static-access")
	public void save(E e) {
		BasicDBObject dbDoc = new BasicDBObject();
		this.mongoTemplate.getConverter().write(e, dbDoc);
		Query query = new Query();
		query.addCriteria(Criteria.where(e.idName()).is(e.idValue()));
		Update update = new Update().fromDBObject(dbDoc);
		this.mongoTemplate.upsert(query, update, getEntityClass(),
				getEntityClass().getSimpleName().toLowerCase());
	}

	@SuppressWarnings("unchecked")
	public List<E> findAll() {
		return (List<E>) mongoTemplate.findAll(getEntityClass(),
				getEntityClass().getSimpleName().toLowerCase());
	}

	@SuppressWarnings("unchecked")
	public List<E> findAll(String collcetionName) {
		if (StringHelper.isNullOrEmpty(collcetionName)) {
			throw new RuntimeException("字段名不能为空");
		}
		return (List<E>) mongoTemplate
				.findAll(getEntityClass(), collcetionName);
	}

}
