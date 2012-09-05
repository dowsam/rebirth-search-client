/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client IndexSearchEngine.java 2012-7-30 9:38:49 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import cn.com.rebirth.commons.search.SearchPage;
import cn.com.rebirth.commons.search.SearchPageRequest;

/**
 * The Interface IndexSearchEngine.
 *
 * @author l.xue.nong
 */
public interface IndexSearchEngine {

	/**
	 * Count.
	 *
	 * @param <T> the generic type
	 * @param entityClass the entity class
	 * @return the long
	 */
	<T> Long count(Class<T> entityClass);

	/**
	 * Count.
	 *
	 * @param <T> the generic type
	 * @param entityClass the entity class
	 * @param queryString the query string
	 * @return the long
	 */
	<T> Long count(Class<T> entityClass, String queryString);

	/**
	 * Search.
	 *
	 * @param <T> the generic type
	 * @param queryString the query string
	 * @param searchPageRequest the search page request
	 * @param entityClass the entity class
	 * @return the search page
	 */
	public <T> SearchPage<T> search(final String queryString, final SearchPageRequest searchPageRequest,
			Class<T> entityClass);

	/**
	 * Search.
	 *
	 * @param <T> the generic type
	 * @param queryString the query string
	 * @param facetPageRequest the facet page request
	 * @param entityClass the entity class
	 * @return the facet page
	 */
	public <T> FacetPage<T> search(final String queryString, final FacetPageRequest facetPageRequest,
			Class<T> entityClass);
}
