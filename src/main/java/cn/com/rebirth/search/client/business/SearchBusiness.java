/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client SearchBusiness.java 2012-7-30 9:40:26 l.xue.nong$$
 */
package cn.com.rebirth.search.client.business;

import cn.com.rebirth.commons.Page;
import cn.com.rebirth.commons.search.SearchPageRequest;
import cn.com.rebirth.search.client.FacetPage;
import cn.com.rebirth.search.client.FacetPageRequest;

/**
 * The Interface SearchBusiness.
 *
 * @param <T> the generic type
 * @author l.xue.nong
 */
public interface SearchBusiness<T> {

	/**
	 * Count.
	 *
	 * @return the long
	 */
	Long count();

	/**
	 * Count.
	 *
	 * @param queryString the query string
	 * @return the long
	 */
	Long count(String queryString);

	/**
	 * Search.
	 *
	 * @param queryString the query string
	 * @param pageRequest the page request
	 * @return the page
	 */
	public Page<T> search(final String queryString, final SearchPageRequest pageRequest);

	/**
	 * Search.
	 *
	 * @param queryString the query string
	 * @param pageRequest the page request
	 * @return the facet page
	 */
	public FacetPage<T> search(final String queryString, final FacetPageRequest pageRequest);

}
