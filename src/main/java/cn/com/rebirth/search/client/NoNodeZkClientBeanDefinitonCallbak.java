/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client NoNodeZkClientBeanDefinitonCallbak.java 2012-7-9 11:48:06 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import cn.com.rebirth.search.client.RegistrationRebirthService.BeanDefinitonCallbak;

/**
 * The Class NoNodeZkClientBeanDefinitonCallbak.
 *
 * @author l.xue.nong
 */
public class NoNodeZkClientBeanDefinitonCallbak implements BeanDefinitonCallbak {

	/** The registration rebirth service. */
	private final RegistrationRebirthService registrationRebirthService;

	/**
	 * Instantiates a new no node zk client bean definiton callbak.
	 *
	 * @param registrationRebirthService the registration rebirth service
	 */
	public NoNodeZkClientBeanDefinitonCallbak(RegistrationRebirthService registrationRebirthService) {
		super();
		this.registrationRebirthService = registrationRebirthService;
	}

	/* (non-Javadoc)
	 * @see cn.com.rebirth.search.client.RegistrationRebirthService.BeanDefinitonCallbak#execute(java.lang.String)
	 */
	@Override
	public BeanDefinition execute(String beanName) {
		registrationRebirthService.clientBeanName = beanName;
		return BeanDefinitionBuilder.rootBeanDefinition(RebirthTransportClientFactoryBean.class)
				.addPropertyValue("addresses", getTransportAddresses()).getBeanDefinition();
	}

	/**
	 * Gets the transport addresses.
	 *
	 * @return the transport addresses
	 */
	protected Object getTransportAddresses() {
		return null;
	}

}
