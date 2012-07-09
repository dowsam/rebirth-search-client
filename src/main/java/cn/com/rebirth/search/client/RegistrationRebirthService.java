/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client RegistrationSumMallService.java 2012-3-30 17:29:21 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.concurrent.atomic.AtomicInteger;

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

	protected boolean zkJarLib = true;

	/**
	 * Instantiates a new registration sum mall service.
	 */
	public RegistrationRebirthService() {
		super();
		RebirthContainer.getInstance().start();
		try {
			Class.forName("org.apache.zookeeper.ZooKeeper", false, Thread.currentThread().getContextClassLoader());
		} catch (Exception e) {
			zkJarLib = false;
		}
	}

	public boolean isZkJarLib() {
		return zkJarLib;
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
				resgistration(beanDefinitionRegistry, clientBeanName, new ZkClientBeanDefinitonCallbak(this,
						beanFactory));
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
	public static interface BeanDefinitonCallbak {

		/**
		 * Execute.
		 *
		 * @param beanName the bean name
		 * @return the bean definition
		 */
		BeanDefinition execute(String beanName);
	}
}
