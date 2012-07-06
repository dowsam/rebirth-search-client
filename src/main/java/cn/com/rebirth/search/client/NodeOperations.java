/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client NodeOperations.java 2012-3-30 18:18:43 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import cn.com.rebirth.search.core.action.ActionResponse;

/**
 * The Interface NodeOperations.
 *
 * @author l.xue.nong
 */
public interface NodeOperations {

	/**
	 * Creates the index.
	 */
	void createIndex(Class<?> entityClass);

	/**
	 * Index exists.
	 *
	 * @return true, if successful
	 */
	boolean indexExists(Class<?> entityClass);

	/**
	 * Delete index.
	 */
	void deleteIndex(Class<?> entityClass);

	/**
	 * Refresh index.
	 */
	void refreshIndex(Class<?> entityClass);

	/**
	 * Close index.
	 */
	void closeIndex(Class<?> entityClass);

	/**
	 * Flush index.
	 */
	void flushIndex(Class<?> entityClass);

	/**
	 * Snapshot index.
	 */
	void snapshotIndex(Class<?> entityClass);

	/**
	 * Optimize index.
	 */
	void optimizeIndex(Class<?> entityClass);

	/**
	 * Execute get.
	 *
	 * @param <T> the generic type
	 * @param callback the callback
	 * @return the t
	 */
	<T extends ActionResponse> T executeGet(final NodeCallback<T> callback);

	/**
	 * Execute get.
	 *
	 * @param <T> the generic type
	 * @param callback the callback
	 * @return the t
	 */
	<T extends ActionResponse> T executeGet(final ClusterCallback<T> callback);

	/**
	 * Execute get.
	 *
	 * @param <T> the generic type
	 * @param callback the callback
	 * @return the t
	 */
	<T extends ActionResponse> T executeGet(final ClientCallback<T> callback);

	/**
	 * Gets the index name.
	 *
	 * @return the index name
	 */
	String getIndexName(Class<?> entityClass);
}
