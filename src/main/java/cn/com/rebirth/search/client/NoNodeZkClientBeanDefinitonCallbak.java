/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client NoNodeZkClientBeanDefinitonCallbak.java 2012-7-9 11:30:03 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.List;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import cn.com.rebirth.search.client.RegistrationRebirthService.BeanDefinitonCallbak;
import cn.com.rebirth.search.commons.transport.TransportAddress;

import com.google.common.collect.Lists;

/**
 * The Class NoNodeZkClientBeanDefinitonCallbak.
 *
 * @author l.xue.nong
 */
public class NoNodeZkClientBeanDefinitonCallbak extends ZkClientBeanDefinitonCallbak implements BeanDefinitonCallbak {

	/**
	 * Instantiates a new no node zk client bean definiton callbak.
	 *
	 * @param registrationRebirthService the registration rebirth service
	 * @param beanFactory the bean factory
	 */
	public NoNodeZkClientBeanDefinitonCallbak(RegistrationRebirthService registrationRebirthService,
			ConfigurableListableBeanFactory beanFactory) {
		super(registrationRebirthService, beanFactory);
	}

	/* (non-Javadoc)
	 * @see cn.com.rebirth.search.client.ZkClientBeanDefinitonCallbak#getTransportAddresses()
	 */
	@Override
	protected List<TransportAddress> getTransportAddresses() {
		return Lists.newArrayList();
	}

}
