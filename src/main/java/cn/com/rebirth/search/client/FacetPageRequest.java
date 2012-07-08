/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client FacetPageRequest.java 2012-5-4 9:33:50 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.ArrayList;
import java.util.List;

import cn.com.rebirth.search.client.group.LuceneGroupField;

/**
 * The Class FacetPageRequest.
 *
 * @author l.xue.nong
 */
public class FacetPageRequest extends HighlightSearchPageRequest {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7875451968438179611L;

	/** The lucene group fields. */
	private List<LuceneGroupField> luceneGroupFields;

	/**
	 * Instantiates a new facet page request.
	 */
	public FacetPageRequest() {
		this(new ArrayList<LuceneGroupField>());
	}

	/**
	 * Instantiates a new facet page request.
	 *
	 * @param luceneGroupFields the lucene group fields
	 */
	public FacetPageRequest(List<LuceneGroupField> luceneGroupFields) {
		super();
		this.luceneGroupFields = luceneGroupFields;
	}

	/**
	 * Instantiates a new facet page request.
	 *
	 * @param pageNo the page no
	 * @param pageSize the page size
	 * @param luceneGroupFields the lucene group fields
	 */
	public FacetPageRequest(int pageNo, int pageSize, List<LuceneGroupField> luceneGroupFields) {
		super(pageNo, pageSize);
		this.luceneGroupFields = luceneGroupFields;
	}

	/**
	 * Gets the lucene group fields.
	 *
	 * @return the lucene group fields
	 */
	public List<LuceneGroupField> getLuceneGroupFields() {
		return luceneGroupFields;
	}

	public void setLuceneGroupFields(List<LuceneGroupField> luceneGroupFields) {
		this.luceneGroupFields = luceneGroupFields;
	}

}
