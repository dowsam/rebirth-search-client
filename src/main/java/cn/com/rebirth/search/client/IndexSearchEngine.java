/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client IndexSearchEngine.java 2012-4-1 10:18:31 l.xue.nong$$
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
	 * Search.
	 *
	 * @param <T> the generic type
	 * @param queryString the query string
	 * @param pageRequest the page request
	 * @param entityClass the entity class
	 * @return the page
	 */
	public <T> SearchPage<T> search(final String queryString, final SearchPageRequest searchPageRequest,
			Class<T> entityClass);

	/**
	 * Search.
	 *
	 * @param <T> the generic type
	 * @param queryString the query string
	 * @param pageRequest the page request
	 * @param entityClass the entity class
	 * @return the facet page
	 */
	public <T> FacetPage<T> search(final String queryString, final FacetPageRequest facetPageRequest,
			Class<T> entityClass);
}
