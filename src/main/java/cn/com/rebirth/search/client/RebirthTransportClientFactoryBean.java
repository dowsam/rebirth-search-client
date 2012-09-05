/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client SumMallTransportClientFactoryBean.java 2012-7-6 16:10:41 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import cn.com.rebirth.commons.settings.ImmutableSettings;
import cn.com.rebirth.commons.settings.Settings;
import cn.com.rebirth.commons.utils.ExceptionUtils;
import cn.com.rebirth.search.commons.transport.InetSocketTransportAddress;
import cn.com.rebirth.search.commons.transport.TransportAddress;
import cn.com.rebirth.search.core.client.Client;
import cn.com.rebirth.search.core.client.transport.TransportClient;

/**
 * The Class SumMallTransportClientFactoryBean.
 *
 * @author l.xue.nong
 */
public class RebirthTransportClientFactoryBean implements FactoryBean<TransportClient>, InitializingBean,
		DisposableBean {

	/** The logger. */
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/** The transport client. */
	private TransportClient transportClient;

	/** The transport addresses. */
	private Map<String, Integer> transportAddresses;

	/** The addresses. */
	private List<TransportAddress> addresses;

	/**
	 * Gets the single instance of SumMallTransportClientFactoryBean.
	 *
	 * @return single instance of SumMallTransportClientFactoryBean
	 */
	public static RebirthTransportClientFactoryBean getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * The Class SingletonHolder.
	 *
	 * @author l.xue.nong
	 */
	private static class SingletonHolder {

		/** The instance. */
		static RebirthTransportClientFactoryBean instance = new RebirthTransportClientFactoryBean();
	}

	/**
	 * Instantiates a new sum mall transport client factory bean.
	 */
	private RebirthTransportClientFactoryBean() {
		super();
	}

	/**
	 * Sets the addresses.
	 *
	 * @param addresses the new addresses
	 */
	public void setAddresses(List<TransportAddress> addresses) {
		this.addresses = addresses;
	}

	/**
	 * Sets the transport addresses.
	 *
	 * @param transportAddresses the transport addresses
	 */
	public void setTransportAddresses(final Map<String, Integer> transportAddresses) {
		this.transportAddresses = transportAddresses;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		transportClient.close();
	}

	/**
	 * Internal create transport client.
	 */
	private void internalCreateTransportClient() {
		Settings settings = ImmutableSettings.settingsBuilder().loadFromClasspath("rebirth-search-client.properties")
				.build();
		String nodeName = settings.get("name");
		settings = ImmutableSettings.settingsBuilder().put(settings).put("name", nodeName).build();
		this.transportClient = new TransportClient(settings);
		if (null != transportAddresses) {
			for (final Entry<String, Integer> address : transportAddresses.entrySet()) {
				if (logger.isInfoEnabled()) {
					logger.info("Adding transport address: " + address.getKey() + " port: " + address.getValue());
				}
				transportClient
						.addTransportAddress(new InetSocketTransportAddress(address.getKey(), address.getValue()));
			}
		}
		if (null != addresses) {
			for (TransportAddress address : addresses) {
				transportClient.addTransportAddress(address);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		internalCreateTransportClient();
		SingletonHolder.instance = this;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public TransportClient getObject() throws Exception {
		return transportClient;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return TransportClient.class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Gets the client.
	 *
	 * @return the client
	 */
	public Client getClient() {
		try {
			return getObject();
		} catch (Exception e) {
			throw ExceptionUtils.unchecked(e);
		}
	}

}
