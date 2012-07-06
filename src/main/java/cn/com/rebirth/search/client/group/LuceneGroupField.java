/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-commons LuceneGroupField.java 2012-5-3 16:03:59 l.xue.nong$$
 */
package cn.com.rebirth.search.client.group;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * The Class LuceneGroupField.
 *
 * @author l.xue.nong
 */
public class LuceneGroupField implements Serializable {

	/** The DEFAUL t_ produc t_ grou p_ field. */
	public static List<LuceneGroupField> DEFAULT_PRODUCT_GROUP_FIELD = Lists.newArrayList();

	/** The DEFAUL t_ produc t_ at t_ grou p_ field. */
	public static List<LuceneGroupField> DEFAULT_PRODUCT_ATT_GROUP_FIELD = Lists.newArrayList();
	static {
		LuceneGroupField luceneGroupField = new LuceneGroupField();
		luceneGroupField.setGroupField("category");
		luceneGroupField.setGroupFieldTop(100);
		luceneGroupField.setTopOrder(20);
		DEFAULT_PRODUCT_GROUP_FIELD.add(luceneGroupField);
		luceneGroupField = new LuceneGroupField();
		luceneGroupField.setGroupField("merchant");
		luceneGroupField.setGroupFieldTop(100);
		luceneGroupField.setTopOrder(20);
		DEFAULT_PRODUCT_GROUP_FIELD.add(luceneGroupField);

		luceneGroupField = new LuceneGroupField();
		luceneGroupField.setGroupField("attributeName");
		luceneGroupField.setGroupFieldTop(50);
		luceneGroupField.setTopOrder(20);
		luceneGroupField.setFunCalculate(true);

		List<LuceneGroupField> children = Lists.newArrayList();
		LuceneGroupField field = new LuceneGroupField();
		field.setGroupField("attributeValue");
		field.setGroupFieldTop(50);
		field.setTopOrder(20);
		children.add(field);
		luceneGroupField.setChildren(children);
		DEFAULT_PRODUCT_ATT_GROUP_FIELD.add(luceneGroupField);
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6739635289400218790L;

	/** The group field. */
	private String groupField;

	/** The group field top. */
	private Integer groupFieldTop;

	/** The top order. */
	private Integer topOrder = 20;

	/** The parent. */
	private LuceneGroupField parent;

	/** The children. */
	private List<LuceneGroupField> children;

	/** The fun calculate. */
	private boolean funCalculate = false;

	/**
	 * Gets the group field.
	 *
	 * @return the group field
	 */
	public String getGroupField() {
		return groupField;
	}

	/**
	 * Sets the group field.
	 *
	 * @param groupField the new group field
	 */
	public void setGroupField(String groupField) {
		this.groupField = groupField;
	}

	/**
	 * Gets the group field top.
	 *
	 * @return the group field top
	 */
	public Integer getGroupFieldTop() {
		return groupFieldTop;
	}

	/**
	 * Sets the group field top.
	 *
	 * @param groupFieldTop the new group field top
	 */
	public void setGroupFieldTop(Integer groupFieldTop) {
		this.groupFieldTop = groupFieldTop;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public LuceneGroupField getParent() {
		return parent;
	}

	/**
	 * Sets the parent.
	 *
	 * @param parent the new parent
	 */
	public void setParent(LuceneGroupField parent) {
		this.parent = parent;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public List<LuceneGroupField> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(List<LuceneGroupField> children) {
		this.children = children;
	}

	/**
	 * Gets the top order.
	 *
	 * @return the top order
	 */
	public Integer getTopOrder() {
		return topOrder;
	}

	/**
	 * Sets the top order.
	 *
	 * @param topOrder the new top order
	 */
	public void setTopOrder(Integer topOrder) {
		this.topOrder = topOrder;
	}

	/**
	 * Checks if is fun calculate.
	 *
	 * @return true, if is fun calculate
	 */
	public boolean isFunCalculate() {
		return funCalculate;
	}

	/**
	 * Sets the fun calculate.
	 *
	 * @param funCalculate the new fun calculate
	 */
	public void setFunCalculate(boolean funCalculate) {
		this.funCalculate = funCalculate;
	}

}
