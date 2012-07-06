/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client RegistrationSumMallService.java 2012-3-30 17:29:21 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import cn.com.rebirth.commons.RebirthContainer;
import cn.com.rebirth.commons.search.SearchConstants;
import cn.com.rebirth.commons.search.config.support.ZooKeeperExpand;
import cn.com.rebirth.search.commons.transport.TransportAddress;
import cn.com.rebirth.search.core.client.transport.TransportClient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * The Class RegistrationSumMallService.
 *
 * @author l.xue.nong
 */
@Component
public class RegistrationRebirthService implements BeanFactoryPostProcessor {

	/** The logger. */
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/** The node bean name. */
	protected String nodeBeanName = StringUtils.uncapitalize(RebirthNodeFactoryBean.class.getSimpleName());

	/** The client bean name. */
	protected String clientBeanName = StringUtils.uncapitalize(RebirthTransportClientFactoryBean.class.getSimpleName());

	/** The atomic integer. */
	protected java.util.concurrent.atomic.AtomicInteger atomicInteger = new AtomicInteger(0);

	/** The expand. */
	protected ZooKeeperExpand expand;

	/**
	 * Instantiates a new registration sum mall service.
	 */
	public RegistrationRebirthService() {
		super();
		RebirthContainer.getInstance().start();
		this.expand = ZooKeeperExpand.getInstance();
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 */
	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
			RebirthTransportClientFactoryBean transportClientFactoryBean = null;
			try {
				transportClientFactoryBean = beanFactory.getBean(RebirthTransportClientFactoryBean.class);
			} catch (NoSuchBeanDefinitionException e) {
			}
			if (transportClientFactoryBean == null) {
				resgistration(beanDefinitionRegistry, clientBeanName, new BeanDefinitonCallbak() {
					@Override
					public BeanDefinition execute(String beanName) {
						RegistrationRebirthService.this.clientBeanName = beanName;
						return BeanDefinitionBuilder.rootBeanDefinition(RebirthTransportClientFactoryBean.class)
								.addPropertyValue("addresses", getTransportAddresses()).getBeanDefinition();
					}

					private List<TransportAddress> getTransportAddresses() {
						List<TransportAddress> addresses = Lists.newArrayList();
						List<String> childNode = RegistrationRebirthService.this.expand.list(SearchConstants
								.getRebirthSearchBulidZKConfig());
						if (childNode != null) {
							regirtListener(childNode, addresses);
						}
						RegistrationRebirthService.this.expand.getZkClient().subscribeChildChanges(
								SearchConstants.getRebirthSearchBulidZKConfig(), new IZkChildListener() {

									@Override
									public void handleChildChange(String parentPath, List<String> currentChilds)
											throws Exception {
										List<TransportAddress> addresses = Lists.newArrayList();
										regirtListener(currentChilds, addresses);
										if (!addresses.isEmpty()) {
											TransportClient transportClient = beanFactory
													.getBean(TransportClient.class);
											ImmutableList<TransportAddress> immutableList = transportClient
													.transportAddresses();
											for (TransportAddress transportAddress : addresses) {
												if (!immutableList.contains(transportAddress))
													transportClient.addTransportAddress(transportAddress);
											}
										}
									}
								});
						return addresses;
					}

					private void regirtListener(List<String> childNode, List<TransportAddress> addresses) {
						for (String node : childNode) {
							Object object = RegistrationRebirthService.this.expand.get(SearchConstants
									.getRebirthSearchBulidZKConfig() + "/" + node);
							if (object instanceof TransportAddress) {
								final TransportAddress address = (TransportAddress) object;
								if (addresses != null)
									addresses.add(address);
								RegistrationRebirthService.this.expand.getZkClient().subscribeDataChanges(
										SearchConstants.getRebirthSearchBulidZKConfig() + "/" + node,
										new NodeDataListener(beanFactory, address));
							}
						}
					}
				});
			}
			//node config
			RebirthNodeFactoryBean rebirthNodeFactoryBean = null;
			try {
				rebirthNodeFactoryBean = beanFactory.getBean(RebirthNodeFactoryBean.class);
			} catch (NoSuchBeanDefinitionException e) {
			}
			if (rebirthNodeFactoryBean == null) {
				resgistration(beanDefinitionRegistry, nodeBeanName, new BeanDefinitonCallbak() {

					@Override
					public BeanDefinition execute(String beanName) {
						RegistrationRebirthService.this.nodeBeanName = beanName;
						Resource resource = new ClassPathResource("/rebirth-search-client.properties");
						return BeanDefinitionBuilder.rootBeanDefinition(RebirthNodeFactoryBean.class)
								.addPropertyValue("configLocation", resource).getBeanDefinition();
					}
				});
			}
		} else {
			throw new IllegalArgumentException("Error [" + beanFactory + "] not be Instanceof BeanDefinitionRegistry");
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

	/**
	 * Resgistration.
	 *
	 * @param beanDefinitionRegistry the bean definition registry
	 * @param beanName the bean name
	 * @param definitonCallbak the definiton callbak
	 */
	private void resgistration(BeanDefinitionRegistry beanDefinitionRegistry, String beanName,
			BeanDefinitonCallbak definitonCallbak) {
		boolean b = beanDefinitionRegistry.isBeanNameInUse(beanName);
		if (b) {
			beanName = beanName + "_" + atomicInteger.decrementAndGet();
		}
		beanDefinitionRegistry.registerBeanDefinition(beanName, definitonCallbak.execute(beanName));
		logger.info("RegisterBeanDefinition Bean Nmae[{}]", beanName);
	}

	/**
	 * The Interface BeanDefinitonCallbak.
	 *
	 * @author l.xue.nong
	 */
	private interface BeanDefinitonCallbak {

		/**
		 * Execute.
		 *
		 * @param beanName the bean name
		 * @return the bean definition
		 */
		BeanDefinition execute(String beanName);
	}
}
