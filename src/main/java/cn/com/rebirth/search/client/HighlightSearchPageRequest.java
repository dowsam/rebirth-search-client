/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client HighlightSearchPageRequest.java 2012-7-8 13:41:13 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.List;

import cn.com.rebirth.commons.search.SearchPageRequest;

import com.google.common.collect.Lists;

/**
 * The Class HighlightSearchPageRequest.
 *
 * @author l.xue.nong
 */
public class HighlightSearchPageRequest extends SearchPageRequest {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8941842930368107135L;

	/** The pre tag. */
	private String preTag = "<font color=\"red\">";

	/** The post tag. */
	private String postTag = "</font>";

	/** The highligth search fields. */
	private List<HighligthSearchField> highligthSearchFields = Lists.newArrayList();

	/**
	 * Instantiates a new highlight search page request.
	 */
	public HighlightSearchPageRequest() {
		super();
	}

	/**
	 * Instantiates a new highlight search page request.
	 *
	 * @param pageNo the page no
	 * @param pageSize the page size
	 */
	public HighlightSearchPageRequest(int pageNo, int pageSize) {
		super(pageNo, pageSize);
	}

	/**
	 * Instantiates a new highlight search page request.
	 *
	 * @param highligthSearchFields the highligth search fields
	 */
	public HighlightSearchPageRequest(List<HighligthSearchField> highligthSearchFields) {
		super();
		this.highligthSearchFields = highligthSearchFields;
	}

	/**
	 * Instantiates a new highlight search page request.
	 *
	 * @param preTag the pre tag
	 * @param postTag the post tag
	 * @param highligthSearchFields the highligth search fields
	 */
	public HighlightSearchPageRequest(String preTag, String postTag, List<HighligthSearchField> highligthSearchFields) {
		super();
		this.preTag = preTag;
		this.postTag = postTag;
		this.highligthSearchFields = highligthSearchFields;
	}

	/**
	 * Instantiates a new highlight search page request.
	 *
	 * @param pageNo the page no
	 * @param pageSize the page size
	 * @param highligthSearchFields the highligth search fields
	 */
	public HighlightSearchPageRequest(int pageNo, int pageSize, List<HighligthSearchField> highligthSearchFields) {
		super(pageNo, pageSize);
		this.highligthSearchFields = highligthSearchFields;
	}

	/**
	 * Instantiates a new highlight search page request.
	 *
	 * @param pageNo the page no
	 * @param pageSize the page size
	 * @param preTag the pre tag
	 * @param postTag the post tag
	 * @param highligthSearchFields the highligth search fields
	 */
	public HighlightSearchPageRequest(int pageNo, int pageSize, String preTag, String postTag,
			List<HighligthSearchField> highligthSearchFields) {
		super(pageNo, pageSize);
		this.preTag = preTag;
		this.postTag = postTag;
		this.highligthSearchFields = highligthSearchFields;
	}

	/**
	 * The Class HighligthSearchField.
	 *
	 * @author l.xue.nong
	 */
	class HighligthSearchField {

		/** The name. */
		private String name;

		/** The fragment size. */
		private int fragmentSize = 100;

		/**
		 * Instantiates a new highligth search field.
		 *
		 * @param name the name
		 * @param fragmentSize the fragment size
		 */
		public HighligthSearchField(String name, int fragmentSize) {
			super();
			this.name = name;
			this.fragmentSize = fragmentSize;
		}

		/**
		 * Instantiates a new highligth search field.
		 *
		 * @param name the name
		 */
		public HighligthSearchField(String name) {
			super();
			this.name = name;
		}

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the name.
		 *
		 * @param name the new name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Gets the fragment size.
		 *
		 * @return the fragment size
		 */
		public int getFragmentSize() {
			return fragmentSize;
		}

		/**
		 * Sets the fragment size.
		 *
		 * @param fragmentSize the new fragment size
		 */
		public void setFragmentSize(int fragmentSize) {
			this.fragmentSize = fragmentSize;
		}

	}

	/**
	 * Gets the pre tag.
	 *
	 * @return the pre tag
	 */
	public String getPreTag() {
		return preTag;
	}

	/**
	 * Sets the pre tag.
	 *
	 * @param preTag the new pre tag
	 */
	public void setPreTag(String preTag) {
		this.preTag = preTag;
	}

	/**
	 * Gets the post tag.
	 *
	 * @return the post tag
	 */
	public String getPostTag() {
		return postTag;
	}

	/**
	 * Sets the post tag.
	 *
	 * @param postTag the new post tag
	 */
	public void setPostTag(String postTag) {
		this.postTag = postTag;
	}

	/**
	 * Gets the highligth search fields.
	 *
	 * @return the highligth search fields
	 */
	public List<HighligthSearchField> getHighligthSearchFields() {
		return highligthSearchFields;
	}

	/**
	 * Sets the highligth search fields.
	 *
	 * @param highligthSearchFields the new highligth search fields
	 */
	public void setHighligthSearchFields(List<HighligthSearchField> highligthSearchFields) {
		this.highligthSearchFields = highligthSearchFields;
	}

}
