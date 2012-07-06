/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-commons LuceneGroup.java 2012-2-24 10:36:50 l.xue.nong$$
 */
package cn.com.rebirth.search.client.group;

import java.io.Serializable;
import java.util.Collection;

/**
 * The Class LuceneGroup.
 *
 * @author l.xue.nong
 */
public class LuceneGroup implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7224887660305278267L;

	/** The name. */
	private String name;//属性名称

	/** The occur. */
	private int occur;//属性名称在分组中的匹配树

	/** The occur rate. */
	private Double occurRate;//出现概率

	/** The children. */
	private Collection<LuceneGroup> children;//所有子属性

	/**
	 * To name.
	 *
	 * @return the string
	 */
	public String toName() {
		return name;
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
	 * Gets the occur.
	 *
	 * @return the occur
	 */
	public int getOccur() {
		return occur;
	}

	/**
	 * Sets the occur.
	 *
	 * @param occur the new occur
	 */
	public void setOccur(int occur) {
		this.occur = occur;
	}

	/**
	 * Gets the occur rate.
	 *
	 * @return the occur rate
	 */
	public Double getOccurRate() {
		return occurRate;
	}

	/**
	 * Sets the occur rate.
	 *
	 * @param occurRate the new occur rate
	 */
	public void setOccurRate(Double occurRate) {
		this.occurRate = occurRate;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public Collection<LuceneGroup> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(Collection<LuceneGroup> children) {
		this.children = children;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toName() + "(" + name + ")" + "(" + getOccur() + "),(" + (getOccurRate() == null ? 0 : getOccurRate())
				+ ")";
	}

}
