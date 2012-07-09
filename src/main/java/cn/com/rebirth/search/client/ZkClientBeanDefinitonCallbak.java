/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client ZkClientBeanDefinitonCallbak.java 2012-7-9 11:14:05 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import cn.com.rebirth.commons.search.SearchConstants;
import cn.com.rebirth.commons.search.config.support.ZooKeeperExpand;
import cn.com.rebirth.search.client.RegistrationRebirthService.BeanDefinitonCallbak;
import cn.com.rebirth.search.commons.transport.TransportAddress;
import cn.com.rebirth.search.core.client.transport.TransportClient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * The Class ZkClientBeanDefinitonCallbak.
 *
 * @author l.xue.nong
 */
public class ZkClientBeanDefinitonCallbak implements BeanDefinitonCallbak {

	/** The registration rebirth service. */
	private final RegistrationRebirthService registrationRebirthService;

	/** The bean factory. */
	private final ConfigurableListableBeanFactory beanFactory;

	/**
	 * Instantiates a new zk client bean definiton callbak.
	 *
	 * @param registrationRebirthService the registration rebirth service
	 * @param beanFactory the bean factory
	 */
	public ZkClientBeanDefinitonCallbak(RegistrationRebirthService registrationRebirthService,
			ConfigurableListableBeanFactory beanFactory) {
		super();
		this.registrationRebirthService = registrationRebirthService;
		this.beanFactory = beanFactory;
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
	private List<TransportAddress> getTransportAddresses() {
		List<TransportAddress> addresses = Lists.newArrayList();
		if (!registrationRebirthService.isZkJarLib()) {
			return addresses;
		}
		List<String> childNode = ZooKeeperExpand.getInstance().list(SearchConstants.getRebirthSearchBulidZKConfig());
		if (childNode != null) {
			regirtListener(childNode, addresses);
		}
		ZooKeeperExpand.getInstance().getZkClient()
				.subscribeChildChanges(SearchConstants.getRebirthSearchBulidZKConfig(), new IZkChildListener() {

					@Override
					public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
						List<TransportAddress> addresses = Lists.newArrayList();
						regirtListener(currentChilds, addresses);
						if (!addresses.isEmpty()) {
							TransportClient transportClient = beanFactory.getBean(TransportClient.class);
							ImmutableList<TransportAddress> immutableList = transportClient.transportAddresses();
							for (TransportAddress transportAddress : addresses) {
								if (!immutableList.contains(transportAddress))
									transportClient.addTransportAddress(transportAddress);
							}
						}
					}
				});
		return addresses;
	}

	/**
	 * Regirt listener.
	 *
	 * @param childNode the child node
	 * @param addresses the addresses
	 */
	private void regirtListener(List<String> childNode, List<TransportAddress> addresses) {
		for (String node : childNode) {
			Object object = ZooKeeperExpand.getInstance().get(
					SearchConstants.getRebirthSearchBulidZKConfig() + "/" + node);
			if (object instanceof TransportAddress) {
				final TransportAddress address = (TransportAddress) object;
				if (addresses != null)
					addresses.add(address);
				ZooKeeperExpand
						.getInstance()
						.getZkClient()
						.subscribeDataChanges(SearchConstants.getRebirthSearchBulidZKConfig() + "/" + node,
								new NodeDataListener(beanFactory, address));
			}
		}
	}

	/**
	 * The listener interface for receiving nodeData events.
	 * The class that is interested in processing a nodeData
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addNodeDataListener<code> method. When
	 * the nodeData event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see NodeDataEvent
	 */
	class NodeDataListener implements IZkDataListener {

		/** The transport client. */
		private TransportClient transportClient;

		/** The current transport address. */
		private TransportAddress currentTransportAddress;

		/** The bean factory. */
		private final ConfigurableListableBeanFactory beanFactory;

		/**
		 * Gets the transport client.
		 *
		 * @return the transport client
		 */
		public TransportClient getTransportClient() {
			if (transportClient != null)
				return transportClient;
			try {
				transportClient = beanFactory.getBean(TransportClient.class);
			} catch (NoSuchBeanDefinitionException e) {
			}
			if (transportClient == null)
				transportClient = new TransportClient();
			return transportClient;
		}

		/**
		 * Instantiates a new node data listener.
		 *
		 * @param beanFactory the bean factory
		 * @param address the address
		 */
		public NodeDataListener(final ConfigurableListableBeanFactory beanFactory, TransportAddress address) {
			super();
			this.beanFactory = beanFactory;
			this.currentTransportAddress = address;
		}

		/* (non-Javadoc)
		 * @see org.I0Itec.zkclient.IZkDataListener#handleDataChange(java.lang.String, java.lang.Object)
		 */
		@Override
		public void handleDataChange(String dataPath, Object data) throws Exception {
			getTransportClient().removeTransportAddress(currentTransportAddress);
			if (data instanceof TransportAddress) {
				currentTransportAddress = (TransportAddress) data;
				getTransportClient().addTransportAddress(currentTransportAddress);
			}
		}

		/* (non-Javadoc)
		 * @see org.I0Itec.zkclient.IZkDataListener#handleDataDeleted(java.lang.String)
		 */
		@Override
		public void handleDataDeleted(String dataPath) throws Exception {
			getTransportClient().removeTransportAddress(currentTransportAddress);
		}

	}

}
