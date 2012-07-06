/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client ClientNodeTemplate.java 2012-4-1 9:48:48 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cn.com.rebirth.search.core.client.Client;

/**
 * The Class ClientNodeTemplate.
 *
 * @author l.xue.nong
 */
@Component
public class ClientNodeTemplate extends AbstractNodeTemplate implements NodeOperations, InitializingBean {

	/**
	 * Gets the single instance of ClientNodeTemplate.
	 *
	 * @return single instance of ClientNodeTemplate
	 */
	public static ClientNodeTemplate getInstance() {
		return SingletonHolder.clientNodeTemplate;
	}

	/**
	 * The Class SingletonHolder.
	 *
	 * @author l.xue.nong
	 */
	private static class SingletonHolder {

		/** The client node template. */
		static ClientNodeTemplate clientNodeTemplate;
	}

	/**
	 * Instantiates a new client node template.
	 *
	 * @param client the client
	 */
	public ClientNodeTemplate(@Qualifier Client client) {
		super(client);
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		SingletonHolder.clientNodeTemplate = this;
	}

}
