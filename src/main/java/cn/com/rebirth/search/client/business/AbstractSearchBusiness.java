/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client AbstractSearchBusiness.java 2012-7-30 9:43:09 l.xue.nong$$
 */
package cn.com.rebirth.search.client.business;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.rebirth.commons.Page;
import cn.com.rebirth.commons.PageRequest;
import cn.com.rebirth.commons.StopWatch;
import cn.com.rebirth.commons.search.SearchPage;
import cn.com.rebirth.commons.search.SearchPageRequest;
import cn.com.rebirth.commons.utils.ReflectionUtils;
import cn.com.rebirth.search.client.FacetPage;
import cn.com.rebirth.search.client.FacetPageRequest;
import cn.com.rebirth.search.client.IndexToolboxFactory;
import cn.com.rebirth.search.client.RebirthSearchClientVersion;

import com.google.common.collect.Lists;

/**
 * The Class AbstractSearchBusiness.
 *
 * @param <E> the element type
 * @param <F> the generic type
 * @author l.xue.nong
 */
public abstract class AbstractSearchBusiness<E, F> implements BaseBusiness<E>, SearchBusiness<E> {

	/** The logger. */
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/** The index toolbox factory. */
	protected IndexToolboxFactory indexToolboxFactory;

	/** The entity class. */
	protected Class<F> entityClass;

	/** The host name. */
	protected String hostName;

	/**
	 * Instantiates a new abstract search business.
	 */
	@SuppressWarnings("unchecked")
	public AbstractSearchBusiness() {
		super();
		this.indexToolboxFactory = IndexToolboxFactory.getInstance();
		entityClass = ReflectionUtils.getSuperClassGenricType(getClass(), 1);
		logger.info("Rebirth Search Server Client Model Veserion....."
				+ new RebirthSearchClientVersion().getModuleVersion());
		try {
			this.hostName = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (Exception e) {
			this.hostName = "<NA>";
		}
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.business.BaseBusiness#createOrUpdateIndex(java.lang.Object)
	 */
	@Override
	public void createOrUpdateIndex(E object) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		F o = beforeCreateOrUpdateIndex(object);
		if (o != null) {
			indexToolboxFactory.createOrUpdateIndex(o);
		} else {
			indexToolboxFactory.createOrUpdateIndex(object);
		}
		afterCreateOrUpdateIndex(object, o);
		stopWatch.stop();
		logger.info("HostName:[" + this.hostName + "],TheardName:[" + Thread.currentThread().getName()
				+ "],Action Class Name:[{}],Method Name:[createOrUpdateIndex],Time-consuming:[{}]", getClass(),
				stopWatch.totalTime());
	}

	/**
	 * After create or update index.
	 *
	 * @param object the object
	 * @param o the o
	 */
	protected void afterCreateOrUpdateIndex(E object, F o) {
	}

	/**
	 * Before create or update index.
	 *
	 * @param object the object
	 * @return the f
	 */
	@SuppressWarnings("unchecked")
	protected F beforeCreateOrUpdateIndex(E object) {
		if (entityClass.isAssignableFrom(object.getClass())) {
			return (F) object;
		}
		throw new UnsupportedOperationException("not yet implement");
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.business.BaseBusiness#deleteIndex(java.lang.Object)
	 */
	@Override
	public void deleteIndex(E object) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		F o = beforeDeleteIndex(object);
		if (o != null) {
			indexToolboxFactory.deleteIndex(o);
		} else {
			indexToolboxFactory.deleteIndex(object);
		}
		afterDeleteIndex(object, o);
		stopWatch.stop();
		logger.info("HostName:[" + this.hostName + "],TheardName:[" + Thread.currentThread().getName()
				+ "],Action Class Name:[{}],Method Name:[deleteIndex],Time-consuming:[{}]", getClass(),
				stopWatch.totalTime());
	}

	/**
	 * After delete index.
	 *
	 * @param object the object
	 * @param o the o
	 */
	protected void afterDeleteIndex(E object, F o) {
	}

	/**
	 * Before delete index.
	 *
	 * @param object the object
	 * @return the f
	 */
	@SuppressWarnings("unchecked")
	protected F beforeDeleteIndex(E object) {
		if (entityClass.isAssignableFrom(object.getClass())) {
			return (F) object;
		}
		throw new UnsupportedOperationException("not yet implement");
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.business.BaseBusiness#batchDeleteIndex(java.util.Collection)
	 */
	@Override
	public void batchDeleteIndex(Collection<E> collections) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Collection<F> c = beforeBatchDeleteIndex(collections);
		if (c != null) {
			indexToolboxFactory.batchDeleteIndex(c);
		} else {
			indexToolboxFactory.batchDeleteIndex(collections);
		}
		afterBatchDeleteIndex(collections);
		stopWatch.stop();
		logger.info("HostName:[" + this.hostName + "],TheardName:[" + Thread.currentThread().getName()
				+ "],Action Class Name:[{}],Method Name:[batchDeleteIndex],Time-consuming:[{}]", getClass(),
				stopWatch.totalTime());
	}

	/**
	 * After batch delete index.
	 *
	 * @param collections the collections
	 */
	protected void afterBatchDeleteIndex(Collection<E> collections) {
	}

	/**
	 * Before batch delete index.
	 *
	 * @param collections the collections
	 * @return the collection
	 */
	@SuppressWarnings("unchecked")
	protected Collection<F> beforeBatchDeleteIndex(Collection<E> collections) {
		org.apache.commons.lang3.Validate.notNull(collections);
		Collection<F> object = Lists.newArrayList();
		for (E e : collections) {
			if (entityClass.isAssignableFrom(e.getClass())) {
				object.add((F) e);
			} else {
				logger.error("E EntityClass :[{}],to F EntityClass:[{}]", entityClass, e.getClass());
				object.add(null);
			}
		}
		if (!object.isEmpty())
			return object;
		throw new UnsupportedOperationException("not yet implement");
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.business.BaseBusiness#batchCreateOrUpdateIndex(java.util.Collection)
	 */
	@Override
	public void batchCreateOrUpdateIndex(Collection<E> collections) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Collection<F> c = beforeBatchCreateOrUpdateIndex(collections);
		if (c != null) {
			indexToolboxFactory.batchCreateOrUpdateIndex(c);
		} else {
			indexToolboxFactory.batchCreateOrUpdateIndex(collections);
		}
		afterBatchCreateOrUpdateIndex(collections);
		stopWatch.stop();
		logger.info("HostName:[" + this.hostName + "],TheardName:[" + Thread.currentThread().getName()
				+ "],Action Class Name:[{}],Method Name:[batchCreateOrUpdateIndex],Time-consuming:[{}]", getClass(),
				stopWatch.totalTime());
	}

	/**
	 * After batch create or update index.
	 *
	 * @param collections the collections
	 */
	protected void afterBatchCreateOrUpdateIndex(Collection<E> collections) {
	}

	/**
	 * Before batch create or update index.
	 *
	 * @param collections the collections
	 * @return the collection
	 */
	@SuppressWarnings("unchecked")
	protected Collection<F> beforeBatchCreateOrUpdateIndex(Collection<E> collections) {
		org.apache.commons.lang3.Validate.notNull(collections);
		Collection<F> object = Lists.newArrayList();
		for (E e : collections) {
			if (entityClass.isAssignableFrom(e.getClass())) {
				object.add((F) e);
			} else {
				logger.error("E EntityClass :[{}],to F EntityClass:[{}]", entityClass, e.getClass());
				object.add(null);
			}
		}
		if (!object.isEmpty())
			return object;
		throw new UnsupportedOperationException("not yet implement");
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.business.SearchBusiness#search(java.lang.String, cn.com.summall.commons.PageRequest)
	 */
	@Override
	public Page<E> search(String queryString, SearchPageRequest pageRequest) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		beforeSearch(queryString, pageRequest);
		Page<F> page = indexToolboxFactory.search(queryString, pageRequest, entityClass);
		Page<E> p = afterSearch(page, queryString, pageRequest);
		stopWatch.stop();
		logger.info("HostName:[" + this.hostName + "],TheardName:[" + Thread.currentThread().getName()
				+ "],Action Class Name:[{}],Method Name:[search],Time-consuming:[{}]", getClass(),
				stopWatch.totalTime());
		return p;
	}

	/**
	 * After search.
	 *
	 * @param page the page
	 * @param queryString the query string
	 * @param pageRequest the page request
	 * @return the page
	 */
	@SuppressWarnings("unchecked")
	protected Page<E> afterSearch(Page<F> page, String queryString, PageRequest pageRequest) {
		if (page.getTotalItems() > 0) {
			List<F> fs = page.getResult();
			Page<E> page2 = new Page<E>(pageRequest);
			if (page instanceof FacetPage && pageRequest instanceof FacetPageRequest) {
				page2 = new FacetPage<E>((FacetPageRequest) pageRequest);
				((FacetPage<E>) page2).setGroups(((FacetPage<F>) page).getGroups());
			} else if (page instanceof SearchPage && pageRequest instanceof SearchPageRequest) {
				page2 = new SearchPage<E>((SearchPageRequest) pageRequest);
				((SearchPage<E>) page2).setDebugMsg(((SearchPage<F>) page).getDebugMsg());
			}
			List<E> es = Lists.newArrayList();
			for (F f : fs) {
				if (entityClass.isAssignableFrom(f.getClass())) {
					es.add((E) f);
				} else {
					logger.error("E EntityClass :[{}],to F EntityClass:[{}]", entityClass, f.getClass());
					es.add(null);
				}
			}
			page2.setResult(es);
			page2.setTotalItems(page.getTotalItems());
			return page2;
		} else {
			return new Page<E>(pageRequest);
		}
	}

	/**
	 * Before search.
	 *
	 * @param queryString the query string
	 * @param pageRequest the page request
	 */
	protected void beforeSearch(String queryString, PageRequest pageRequest) {
		logger.info("Search Query String:[{}]", queryString);
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.business.SearchBusiness#search(java.lang.String, cn.com.summall.search.client.FacetPageRequest)
	 */
	@Override
	public FacetPage<E> search(String queryString, FacetPageRequest pageRequest) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		beforeSearch(queryString, pageRequest);
		FacetPage<F> facetPage = indexToolboxFactory.search(queryString, pageRequest, entityClass);
		FacetPage<E> p = (FacetPage<E>) afterSearch(facetPage, queryString, pageRequest);
		stopWatch.stop();
		logger.info("HostName:[" + this.hostName + "],TheardName:[" + Thread.currentThread().getName()
				+ "],Action Class Name:[{}],Method Name:[search-Facte],Time-consuming:[{}]", getClass(),
				stopWatch.totalTime());
		return p;
	}

	/**
	 * Optimize.
	 */
	public void optimize() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		beforeOptimize();
		indexToolboxFactory.optimize(entityClass);
		afterOptimize();
		stopWatch.stop();
		logger.info("HostName:[" + this.hostName + "],TheardName:[" + Thread.currentThread().getName()
				+ "],Action Class Name:[{}],Method Name:[optimize],Time-consuming:[{}]", getClass(),
				stopWatch.totalTime());
	}

	/**
	 * After optimize.
	 */
	protected void afterOptimize() {

	}

	/**
	 * Before optimize.
	 */
	protected void beforeOptimize() {

	}

	/**
	 * Refresh.
	 */
	public void refresh() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		beforeRefresh();
		indexToolboxFactory.refresh(entityClass);
		afterRefresh();
		stopWatch.stop();
		logger.info("HostName:[" + this.hostName + "],TheardName:[" + Thread.currentThread().getName()
				+ "],Action Class Name:[{}],Method Name:[refresh],Time-consuming:[{}]", getClass(),
				stopWatch.totalTime());
	}

	/**
	 * After refresh.
	 */
	protected void afterRefresh() {

	}

	/**
	 * Before refresh.
	 */
	protected void beforeRefresh() {

	}

	/**
	 * Flush.
	 */
	public void flush() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		beforeFlush();
		indexToolboxFactory.flush(entityClass);
		afterFlush();
		stopWatch.stop();
		logger.info("HostName:[" + this.hostName + "],TheardName:[" + Thread.currentThread().getName()
				+ "],Action Class Name:[{}],Method Name:[flush],Time-consuming:[{}]", getClass(), stopWatch.totalTime());
	}

	/**
	 * After flush.
	 */
	protected void afterFlush() {

	}

	/**
	 * Before flush.
	 */
	protected void beforeFlush() {

	}

	/* (non-Javadoc)
	 * @see cn.com.rebirth.search.client.business.SearchBusiness#count()
	 */
	@Override
	public Long count() {
		return count(null);
	}

	/* (non-Javadoc)
	 * @see cn.com.rebirth.search.client.business.SearchBusiness#count(java.lang.String)
	 */
	@Override
	public Long count(String queryString) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		queryString = beforeCount(queryString);
		Long count = indexToolboxFactory.count(entityClass, queryString);
		count = afterCount(queryString, count);
		stopWatch.stop();
		logger.info("HostName:[" + this.hostName + "],TheardName:[" + Thread.currentThread().getName()
				+ "],Action Class Name:[{}],Method Name:[flush],Time-consuming:[{}]", getClass(), stopWatch.totalTime());
		return count;
	}

	/**
	 * After count.
	 *
	 * @param queryString the query string
	 * @param count the count
	 * @return the long
	 */
	protected Long afterCount(String queryString, Long count) {
		return count;
	}

	/**
	 * Before count.
	 *
	 * @param queryString the query string
	 * @return the string
	 */
	protected String beforeCount(String queryString) {
		return queryString;
	}

}
