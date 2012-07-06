/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client RegistrationSumMallMapper.java 2012-5-7 9:48:09 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import cn.com.rebirth.commons.search.SearchConstants;
import cn.com.rebirth.commons.search.annotation.AnnotationInfo;
import cn.com.rebirth.commons.search.annotation.AnnotationManager;
import cn.com.rebirth.commons.search.annotation.Index;
import cn.com.rebirth.commons.utils.ResolverUtils;
import cn.com.rebirth.commons.utils.TemplateMatcher;
import cn.com.rebirth.search.commons.xcontent.XContentBuilder;
import cn.com.rebirth.search.core.client.Client;
import cn.com.rebirth.search.core.node.Node;

/**
 * The Class RegistrationSumMallMapper.
 *
 * @author l.xue.nong
 */
@Component
@Lazy(false)
public class RegistrationRebirthMapper implements InitializingBean {

	/** The client. */
	@Autowired
	private Client client;

	/** The node. */
	@Autowired
	private Node node;

	/** The force. */
	private Boolean force;

	/** The impl achieve. */
	private String implAchieve = "${client.impl}";

	/** The default impl achieve. */
	private String defaultImplAchieve = "node";

	/** The template. */
	private AbstractNodeTemplate template;

	/**
	 * Instantiates a new registration sum mall mapper.
	 */
	private RegistrationRebirthMapper() {
		super();
	}

	/**
	 * Gets the single instance of RegistrationSumMallMapper.
	 *
	 * @return single instance of RegistrationSumMallMapper
	 */
	public static RegistrationRebirthMapper getInstance() {
		return SingletonHolder.registrationRebirthMapper;
	}

	/**
	 * The Class SingletonHolder.
	 *
	 * @author l.xue.nong
	 */
	private static class SingletonHolder {

		/** The registration sum mall mapper. */
		static RegistrationRebirthMapper registrationRebirthMapper = new RegistrationRebirthMapper();
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		org.apache.commons.lang3.Validate.notNull(client);
		org.apache.commons.lang3.Validate.notNull(node);
		ClientNodeTemplate clientNodeTemplate = ClientNodeTemplate.getInstance();
		if (clientNodeTemplate == null) {
			clientNodeTemplate = new ClientNodeTemplate(client);
			clientNodeTemplate.afterPropertiesSet();
		}
		NodeTemplate nodeTemplate = NodeTemplate.getInstance();
		if (nodeTemplate == null) {
			nodeTemplate = new NodeTemplate(node);
			nodeTemplate.afterPropertiesSet();
		}
		AnnotationManager annotationManager = AnnotationManager.getInstance();
		ResolverUtils<Index> resolverUtils = new ResolverUtils<Index>();
		resolverUtils.findAnnotated(Index.class, SearchConstants.PACKAGE_NAME_FIX);
		Set<Class<? extends Index>> classes = resolverUtils.getClasses();
		for (Class<? extends Index> class1 : classes) {
			AnnotationInfo annotationInfo = annotationManager.getAnnotationInfo(class1);
			XContentBuilder builder = ObjectAnnonFactory.createProMapper(annotationInfo);
			Index index = ObjectAnnonFactory.getIndex(annotationInfo);
			AbstractNodeTemplate abstractNodeTemplate = bulidTemplate();
			abstractNodeTemplate.createIndex(class1);
			abstractNodeTemplate.pushMapping(index.indexName(), index.indexType(), isForce(), builder);
		}
		SingletonHolder.registrationRebirthMapper = this;
	}

	/**
	 * Bulid template.
	 *
	 * @return the abstract node template
	 */
	protected AbstractNodeTemplate bulidTemplate() {
		if (this.template != null)
			return this.template;
		if ("node".equals(getImplAchieve())) {
			this.template = NodeTemplate.getInstance();
		} else {
			this.template = ClientNodeTemplate.getInstance();
		}
		return this.template;
	}

	/**
	 * Checks if is force.
	 *
	 * @return true, if is force
	 */
	public boolean isForce() {
		if (this.force == null) {
			this.force = Boolean.parseBoolean(System.getProperty("summall.index.mapper.force", "false"));
		}
		return force;
	}

	/**
	 * Sets the force.
	 *
	 * @param force the new force
	 */
	public void setForce(boolean force) {
		this.force = force;
	}

	/**
	 * Gets the impl achieve.
	 *
	 * @return the impl achieve
	 */
	public String getImplAchieve() {
		if (this.implAchieve == null || "${client.impl}".equals(implAchieve)) {
			this.implAchieve = new TemplateMatcher("${", "}").replace(this.implAchieve,
					new TemplateMatcher.VariableResolver() {

						@Override
						public String resolve(String variable) {
							return System.getProperty(variable, defaultImplAchieve);
						}
					});
		}
		return implAchieve;
	}

	/**
	 * Sets the impl achieve.
	 *
	 * @param implAchieve the new impl achieve
	 */
	public void setImplAchieve(String implAchieve) {
		this.implAchieve = implAchieve;
	}

	/**
	 * Template.
	 *
	 * @return the abstract node template
	 */
	public AbstractNodeTemplate template() {
		return this.template;
	}

	/**
	 * Gets the template.
	 *
	 * @return the template
	 */
	public AbstractNodeTemplate getTemplate() {
		return bulidTemplate();
	}

}
