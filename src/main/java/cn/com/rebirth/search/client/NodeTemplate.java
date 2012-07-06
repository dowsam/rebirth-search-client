/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client NodeTemplate.java 2012-5-7 9:29:33 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import cn.com.rebirth.search.core.node.Node;

/**
 * The Class NodeTemplate.
 *
 * @author l.xue.nong
 */
@Component
public class NodeTemplate extends AbstractNodeTemplate implements NodeOperations, InitializingBean {

	/** The node. */
	private final Node node;

	/**
	 * Gets the single instance of NodeTemplate.
	 *
	 * @return single instance of NodeTemplate
	 */
	public static NodeTemplate getInstance() {
		return SingletonHolder.nodeTemplate;
	}

	/**
	 * The Class SingletonHolder.
	 *
	 * @author l.xue.nong
	 */
	private static class SingletonHolder {

		/** The node template. */
		static NodeTemplate nodeTemplate;
	}

	/**
	 * Instantiates a new node template.
	 *
	 * @param node the node
	 */
	public NodeTemplate(Node node) {
		super(node);
		this.node = node;
	}

	/**
	 * Gets the node.
	 *
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		SingletonHolder.nodeTemplate = this;
	}

}
