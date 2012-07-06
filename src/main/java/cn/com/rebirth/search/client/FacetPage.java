/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client FacetPage.java 2012-4-1 17:25:42 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.List;
import java.util.Map;

import cn.com.rebirth.commons.search.SearchPage;
import cn.com.rebirth.commons.search.SearchPageRequest;
import cn.com.rebirth.search.client.group.LuceneGroup;

import com.google.common.collect.Maps;

/**
 * The Class FacetPage.
 *
 * @param <T> the generic type
 * @author l.xue.nong
 */
public class FacetPage<T> extends SearchPage<T> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9146369484193898879L;

	/** The groups. */
	protected Map<String, List<LuceneGroup>> groups = Maps.newHashMap();

	/**
	 * Instantiates a new facet page.
	 */
	public FacetPage() {
		super();
	}

	/**
	 * Instantiates a new facet page.
	 *
	 * @param request the request
	 */
	public FacetPage(SearchPageRequest request) {
		super(request);
	}

	/**
	 * Gets the groups.
	 *
	 * @return the groups
	 */
	public Map<String, List<LuceneGroup>> getGroups() {
		return groups;
	}

	/**
	 * Sets the groups.
	 *
	 * @param groups the groups
	 */
	public void setGroups(Map<String, List<LuceneGroup>> groups) {
		this.groups = groups;
	}

}
