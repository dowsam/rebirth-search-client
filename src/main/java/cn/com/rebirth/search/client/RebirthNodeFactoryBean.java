/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client SumMallNodeFactoryBean.java 2012-3-30 10:22:57 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import cn.com.rebirth.commons.utils.NonceUtils;
import cn.com.rebirth.search.core.node.Node;
import cn.com.rebirth.search.core.node.NodeBuilder;

/**
 * The Class SumMallNodeFactoryBean.
 *
 * @author l.xue.nong
 */
public class RebirthNodeFactoryBean implements FactoryBean<Node>, InitializingBean, DisposableBean {

	/** The logger. */
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/** The config locations. */
	private List<Resource> configLocations;

	/** The config location. */
	private Resource configLocation;

	/** The settings. */
	private Map<String, String> settings;

	/** The node. */
	private Node node;

	/**
	 * Sets the config location.
	 *
	 * @param configLocation the new config location
	 */
	public void setConfigLocation(final Resource configLocation) {
		this.configLocation = configLocation;
	}

	/**
	 * Sets the config locations.
	 *
	 * @param configLocations the new config locations
	 */
	public void setConfigLocations(final List<Resource> configLocations) {
		this.configLocations = configLocations;
	}

	/**
	 * Sets the settings.
	 *
	 * @param settings the settings
	 */
	public void setSettings(final Map<String, String> settings) {
		this.settings = settings;
	}

	/**
	 * Internal create node.
	 */
	private void internalCreateNode() {
		final NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder();

		if (null != configLocation) {
			internalLoadSettings(nodeBuilder, configLocation);
		}

		if (null != configLocations) {
			for (final Resource location : configLocations) {
				internalLoadSettings(nodeBuilder, location);
			}
		}

		if (null != settings) {
			nodeBuilder.getSettings().put(settings);
		}
		String nodeName = nodeBuilder.getSettings().get("name");
		nodeBuilder.getSettings().put("name", nodeName + "-" + NonceUtils.randomInt());
		node = nodeBuilder.node();
	}

	/**
	 * Internal load settings.
	 *
	 * @param nodeBuilder the node builder
	 * @param configLocation the config location
	 */
	private void internalLoadSettings(final NodeBuilder nodeBuilder, final Resource configLocation) {
		try {
			final String filename = configLocation.getFilename();
			if (logger.isInfoEnabled()) {
				logger.info("Loading configuration file from: " + filename);
			}
			nodeBuilder.getSettings().loadFromStream(filename, configLocation.getInputStream());
		} catch (final Exception e) {
			throw new IllegalArgumentException("Could not load settings from configLocation: "
					+ configLocation.getDescription(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		node.close();
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		internalCreateNode();
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public Node getObject() throws Exception {
		return node;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return Node.class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

}
