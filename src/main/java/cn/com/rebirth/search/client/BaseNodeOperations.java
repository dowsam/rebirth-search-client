/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client BaseNodeOperations.java 2012-3-31 18:09:24 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.Collection;

/**
 * The Interface BaseNodeOperations.
 *
 * @author l.xue.nong
 */
public interface BaseNodeOperations {

	/**
	 * Gets the index.
	 *
	 * @param <T> the generic type
	 * @param id the id
	 * @param entityClass the entity class
	 * @return the index
	 */
	<T> T getIndex(String id, Class<T> entityClass);

	/**
	 * Creates the or update index.
	 *
	 * @param object the object
	 */
	void createOrUpdateIndex(Object object);

	/**
	 * Delete index.
	 *
	 * @param object the object
	 */
	void deleteIndex(Object object);

	/**
	 * Batch delete index.
	 *
	 * @param collections the collections
	 */
	void batchDeleteIndex(Collection<?> collections);

	/**
	 * Batch create or update index.
	 *
	 * @param collections the collections
	 */
	void batchCreateOrUpdateIndex(Collection<?> collections);
}
