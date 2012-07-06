/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client DataProcessingFactory.java 2012-7-6 15:52:25 l.xue.nong$$
 */
package cn.com.rebirth.search.client.data;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import cn.com.rebirth.commons.entity.BaseEntity;
import cn.com.rebirth.commons.search.annotation.AnalyzerType;
import cn.com.rebirth.commons.search.rule.DocumentBoost;
import cn.com.rebirth.commons.search.rule.LuceneBoost;
import cn.com.rebirth.commons.search.rule.MultiDocBoostMerger;
import cn.com.rebirth.commons.utils.ClassResolverUtils;
import cn.com.rebirth.commons.utils.ExceptionUtils;
import cn.com.rebirth.commons.utils.ReflectionUtils;

import com.google.common.collect.Lists;

/**
 * A factory for creating DataProcessing objects.
 */
@Component
@Lazy(false)
public class DataProcessingFactory {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(DataProcessingFactory.class);

	/** The lock. */
	private static Object lock = new Object();

	/** The lucene boosts. */
	private static List<LuceneBoost> luceneBoosts = ClassResolverUtils.findImpl(LuceneBoost.class);

	/**
	 * To document boost.
	 *
	 * @param object the object
	 * @return the float
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Float toDocumentBoost(Object object) {
		if (object == null)
			throw new RuntimeException("Object not be null");
		if (luceneBoosts == null || luceneBoosts.isEmpty())
			return null;
		List<Float> floats = Lists.newArrayList();
		List<MultiDocBoostMerger> boostMergers = Lists.newArrayList();
		synchronized (lock) {
			for (LuceneBoost luceneBoost : luceneBoosts) {
				if (luceneBoost instanceof DocumentBoost) {
					Class<?> class1 = ReflectionUtils.getSuperClassGenricType(luceneBoost.getClass());
					Class<?> class2 = object.getClass();
					if (class1.isAssignableFrom(class2)) {
						DocumentBoost boost = (DocumentBoost) luceneBoost;
						findDefaultAnalyzerMethod(boost);
						try {
							float f = boost.calculateBoost((BaseEntity) object);
							floats.add(f);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
							continue;
						}
					}
				} else if (luceneBoost instanceof MultiDocBoostMerger) {
					boostMergers.add((MultiDocBoostMerger) luceneBoost);
				}
			}
			if (floats.isEmpty())
				return null;
			if (boostMergers.isEmpty())
				return floats.iterator().next();
			if (boostMergers.size() > 1)
				logger.info("Find MultiDocBoostMerger Impl multi size:" + boostMergers.size());
			return boostMergers.iterator().next().merger(floats.toArray(new Float[floats.size()]));
		}
	}

	/**
	 * Find default analyzer method.
	 *
	 * @param boost the boost
	 */
	@SuppressWarnings("rawtypes")
	protected static void findDefaultAnalyzerMethod(DocumentBoost boost) {
		try {
			ReflectionUtils.invokeSetterMethod(boost, "defaultAnalyzer", loadDefaultAnalyzer(), Analyzer.class);
		} catch (Throwable e) {
			try {
				ReflectionUtils.setFieldValue(boost, "defaultAnalyzer", loadDefaultAnalyzer());
			} catch (Throwable e2) {
			}
		}
	}

	/**
	 * Load default analyzer.
	 *
	 * @return the object
	 */
	protected static Object loadDefaultAnalyzer() {
		try {
			return AnalyzerType.DEFAULT.newAnalyzer();
		} catch (Exception e) {
			throw ExceptionUtils.unchecked(e);
		}
	}
}
