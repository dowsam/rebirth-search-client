/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client IndexToolboxFactory.java 2012-7-30 9:27:02 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.Collection;

import org.apache.commons.lang3.Validate;

import cn.com.rebirth.commons.search.SearchPage;
import cn.com.rebirth.commons.search.SearchPageRequest;
import cn.com.rebirth.search.client.ObjectAnnonFactory.Source;
import cn.com.rebirth.search.core.action.ActionResponse;

/**
 * A factory for creating IndexToolbox objects.
 */
public class IndexToolboxFactory implements BaseNodeOperations, IndexSearchEngine {

	/**
	 * Gets the single instance of IndexToolboxFactory.
	 *
	 * @return single instance of IndexToolboxFactory
	 */
	public static IndexToolboxFactory getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * The Class SingletonHolder.
	 *
	 * @author l.xue.nong
	 */
	private static class SingletonHolder {

		/** The instance. */
		static IndexToolboxFactory instance = new IndexToolboxFactory();
	}

	/**
	 * Instantiates a new index toolbox factory.
	 */
	private IndexToolboxFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.BaseNodeOperations#createIndex(java.lang.Object)
	 */
	@Override
	public void createOrUpdateIndex(Object object) {
		Validate.notNull(object);
		Source source = ObjectAnnonFactory.source(object);
		RegistrationRebirthMapper.getInstance().template().createOrUpdateIndex(source);
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.BaseNodeOperations#deleteIndex(java.lang.Object)
	 */
	@Override
	public void deleteIndex(final Object object) {
		Validate.notNull(object);
		RegistrationRebirthMapper.getInstance().template().deleteIndex(ObjectAnnonFactory.source(object));
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.BaseNodeOperations#getIndex(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> T getIndex(final String id, final Class<T> entityClass) {
		Validate.notNull(id);
		Validate.notNull(entityClass);
		return RegistrationRebirthMapper.getInstance().template().getIndex(id, entityClass);
	}

	/**
	 * Refresh.
	 *
	 * @param entityClass the entity class
	 */
	public void refresh(final Class<?> entityClass) {
		Validate.notNull(entityClass);
		RegistrationRebirthMapper.getInstance().template().refreshIndex(entityClass);
	}

	/**
	 * Flush.
	 *
	 * @param entityClass the entity class
	 */
	public void flush(final Class<?> entityClass) {
		Validate.notNull(entityClass);
		RegistrationRebirthMapper.getInstance().template().flushIndex(entityClass);
	}

	/**
	 * Optimize.
	 *
	 * @param entityClass the entity class
	 */
	public void optimize(final Class<?> entityClass) {
		Validate.notNull(entityClass);
		RegistrationRebirthMapper.getInstance().template().optimizeIndex(entityClass);
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.BaseNodeOperations#batchDeleteIndex(java.util.Collection)
	 */
	@Override
	public void batchDeleteIndex(final Collection<?> collections) {
		Validate.notNull(collections);
		RegistrationRebirthMapper.getInstance().template().batchDeleteIndex(collections);
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.BaseNodeOperations#batchCreateOrUpdateIndex(java.util.Collection)
	 */
	@Override
	public void batchCreateOrUpdateIndex(final Collection<?> collections) {
		Validate.notNull(collections);
		RegistrationRebirthMapper.getInstance().template().batchCreateOrUpdateIndex(collections);
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.IndexSearchEngine#search(java.lang.String, cn.com.summall.commons.PageRequest, java.lang.Class)
	 */
	@Override
	public <T> SearchPage<T> search(String queryString, SearchPageRequest pageRequest, Class<T> entityClass) {
		Validate.notNull(queryString);
		Validate.notNull(pageRequest);
		Validate.notNull(entityClass);
		return RegistrationRebirthMapper.getInstance().template().search(queryString, pageRequest, entityClass);
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.IndexSearchEngine#search(java.lang.String, cn.com.summall.search.client.FacetPageRequest, java.lang.Class)
	 */
	@Override
	public <T> FacetPage<T> search(String queryString, FacetPageRequest pageRequest, Class<T> entityClass) {
		Validate.notNull(queryString);
		Validate.notNull(pageRequest);
		Validate.notNull(entityClass);
		return RegistrationRebirthMapper.getInstance().template().search(queryString, pageRequest, entityClass);
	}

	/**
	 * Execute.
	 *
	 * @param <T> the generic type
	 * @param callback the callback
	 * @return the t
	 */
	public <T extends ActionResponse> T execute(ClientCallback<T> callback) {
		Validate.notNull(callback);
		return RegistrationRebirthMapper.getInstance().template().executeGet(callback);
	}

	/* (non-Javadoc)
	 * @see cn.com.rebirth.search.client.IndexSearchEngine#count(java.lang.Class)
	 */
	@Override
	public <T> Long count(Class<T> entityClass) {
		return count(entityClass, null);
	}

	/* (non-Javadoc)
	 * @see cn.com.rebirth.search.client.IndexSearchEngine#count(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> Long count(Class<T> entityClass, String queryString) {
		Validate.notNull(entityClass);
		return RegistrationRebirthMapper.getInstance().template().count(entityClass, queryString);
	}
}
