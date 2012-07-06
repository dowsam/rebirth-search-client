/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-business SearchBusiness.java 2012-4-9 10:09:32 l.xue.nong$$
 */
package cn.com.rebirth.search.client.business;

import java.util.Collection;

/**
 * The Interface SearchBusiness.
 *
 * @param <T> the generic type
 * @author l.xue.nong
 */
public interface BaseBusiness<T> {

	/**
	 * Creates the or update index.
	 *
	 * @param object the object
	 */
	void createOrUpdateIndex(T object);

	/**
	 * Delete index.
	 *
	 * @param object the object
	 */
	void deleteIndex(T object);

	/**
	 * Batch delete index.
	 *
	 * @param collections the collections
	 */
	void batchDeleteIndex(Collection<T> collections);

	/**
	 * Batch create or update index.
	 *
	 * @param collections the collections
	 */
	void batchCreateOrUpdateIndex(Collection<T> collections);
}
