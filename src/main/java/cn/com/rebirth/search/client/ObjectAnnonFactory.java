/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client ObjectAnnonFactory.java 2012-4-1 15:43:01 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import cn.com.rebirth.commons.search.annotation.AbstractSearchProperty;
import cn.com.rebirth.commons.search.annotation.AnnotationInfo;
import cn.com.rebirth.commons.search.annotation.AnnotationManager;
import cn.com.rebirth.commons.search.annotation.FieldAnalyzer;
import cn.com.rebirth.commons.search.annotation.FieldBoost;
import cn.com.rebirth.commons.search.annotation.Index;
import cn.com.rebirth.commons.search.annotation.PKey;
import cn.com.rebirth.commons.utils.ConvertUtils;
import cn.com.rebirth.commons.utils.ExceptionUtils;
import cn.com.rebirth.search.client.data.DataProcessingFactory;
import cn.com.rebirth.search.commons.inject.Injector;
import cn.com.rebirth.search.commons.inject.ModulesBuilder;
import cn.com.rebirth.search.commons.settings.ImmutableSettings;
import cn.com.rebirth.search.commons.settings.SettingsModule;
import cn.com.rebirth.search.commons.xcontent.XContentBuilder;
import cn.com.rebirth.search.commons.xcontent.XContentFactory;
import cn.com.rebirth.search.core.env.Environment;
import cn.com.rebirth.search.core.env.EnvironmentModule;
import cn.com.rebirth.search.core.index.IndexNameModule;
import cn.com.rebirth.search.core.index.analysis.AnalysisModule;
import cn.com.rebirth.search.core.index.analysis.AnalysisService;
import cn.com.rebirth.search.core.index.settings.IndexSettingsModule;
import cn.com.rebirth.search.core.indices.analysis.IndicesAnalysisModule;
import cn.com.rebirth.search.core.indices.analysis.IndicesAnalysisService;

/**
 * A factory for creating ObjectAnnon objects.
 */
public abstract class ObjectAnnonFactory {

	/** The annotation manager. */
	private static AnnotationManager annotationManager = AnnotationManager.getInstance();

	/**
	 * The Class Source.
	 *
	 * @author l.xue.nong
	 */
	public static class Source {

		/** The id. */
		public String id;

		/** The builder. */
		public XContentBuilder builder;

		/** The mapper builder. */
		public XContentBuilder mapperBuilder;

		/** The index. */
		public Index index;
	}

	/**
	 * Source.
	 *
	 * @param object the object
	 * @return the source
	 */
	public static Source source(Object object) {
		Source source = new Source();
		AnnotationInfo annotationInfo = annotationManager.getAnnotationInfo(object);
		Object id = annotationInfo.getIdMethod().getProperty(object);
		if (id == null)
			throw new IllegalArgumentException("not find object class " + object.getClass() + ",to Summall Index "
					+ PKey.class);
		source.id = id.toString();
		source.mapperBuilder = createProMapper(annotationInfo);
		source.builder = createPro(object, annotationInfo);
		source.index = getIndex(annotationInfo);
		return source;
	}

	/**
	 * Annotation info.
	 *
	 * @param entityClass the entity class
	 * @return the annotation info
	 */
	public static AnnotationInfo annotationInfo(Class<?> entityClass) {
		return annotationManager.getAnnotationInfo(entityClass);
	}

	/**
	 * Creates a new ObjectAnnon object.
	 *
	 * @param annotationInfo the annotation info
	 * @return the x content builder
	 */
	public static XContentBuilder createProMapper(AnnotationInfo annotationInfo) {
		try {
			Index index = getIndex(annotationInfo);
			final XContentBuilder jsonBuilder = XContentFactory.jsonBuilder();
			jsonBuilder.startObject();
			jsonBuilder.startObject(index.indexName());
			jsonBuilder.field("dynamic", "strict");
			jsonBuilder.field("_id").startObject().field("path", "key").endObject();
			Map<String, AbstractSearchProperty> properties = annotationInfo.getProperties();
			if (!properties.isEmpty())
				jsonBuilder.startObject("properties");
			for (Map.Entry<String, AbstractSearchProperty> entry : properties.entrySet()) {
				AbstractSearchProperty property = entry.getValue();
				if (property.isFieldIndex() || property.isFieldStore()) {
					String name = property.getFieldIndex().name();
					name = StringUtils.isBlank(name) ? property.getFieldName() : name;
					String store = property.getFieldStore().value();
					String fieldIndex = property.getFieldIndex().value();
					FieldBoost fieldBoost = property.getFieldBoost();
					Float boost = fieldBoost == null ? null : fieldBoost.boost();
					String type = property.getRawClass().getSimpleName();
					jsonBuilder.startObject(name).field("type", type.toLowerCase()).field("index", toAnaly(fieldIndex))
							.field("store", store.toLowerCase());
					if (boost != null) {
						jsonBuilder.field("boost", boost);
					}
					FieldAnalyzer fieldAnalyzer = property.getFieldAnalyzer();
					if (fieldAnalyzer != null) {
						jsonBuilder.field("analyzer", fieldAnalyzer.toAnalyzer().getName());
					}
					jsonBuilder.endObject();
				} else if (property.isId()) {
					String name = property.getFieldName();
					String type = property.getRawClass().getSimpleName();
					jsonBuilder.startObject(name).field("type", type.toLowerCase()).field("index", "not_analyzed")
							.field("store", "yes").endObject();
				}
			}
			jsonBuilder.endObject();
			jsonBuilder.endObject();
			jsonBuilder.endObject();
			return jsonBuilder;
		} catch (Exception e) {
			throw ExceptionUtils.unchecked(e);
		}
	}

	/**
	 * Invoke.
	 *
	 * @param <T> the generic type
	 * @param id the id
	 * @param source the source
	 * @param annotationInfo the annotation info
	 * @return the t
	 */
	public static <T> T invoke(String id, Map<String, Object> source, AnnotationInfo annotationInfo) {
		T t = newInstance(annotationInfo.getMainClass());
		Class<?> idClass = annotationInfo.getIdMethod().getRawClass();
		if (id != null) {
			Object idValue = ConvertUtils.convertStringToObject(id, idClass);
			annotationInfo.getIdMethod().setProperty(t, idValue);
		}
		Map<String, AbstractSearchProperty> pMap = annotationInfo.getProperties();
		for (Map.Entry<String, AbstractSearchProperty> entry : pMap.entrySet()) {
			AbstractSearchProperty property = entry.getValue();
			if (property.isFieldIndex() || property.isFieldStore()) {
				String name = property.getFieldIndex().name();
				name = StringUtils.isBlank(name) ? property.getFieldName() : name;
				Object value = source.get(name);
				if (value == null)
					continue;
				Class<?> type = property.getRawClass();
				value = ConvertUtils.convertObjectToObject(value, type);
				property.setProperty(t, value);
			}
		}
		return t;
	}

	/**
	 * New instance.
	 *
	 * @param <T> the generic type
	 * @param mainClass the main class
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	private static <T> T newInstance(Class<?> mainClass) {
		try {
			return (T) mainClass.newInstance();
		} catch (Exception e) {
			throw ExceptionUtils.unchecked(e);
		}
	}

	/**
	 * To analy.
	 *
	 * @param fieldIndex the field index
	 * @return the string
	 */
	private static String toAnaly(String fieldIndex) {
		if (StringUtils.isBlank(fieldIndex))
			return "analyzed";
		if ("NO_ANALYZED".equalsIgnoreCase(fieldIndex) || "NOT_ANALYZED".equalsIgnoreCase(fieldIndex)) {
			return "not_analyzed";
		}
		return fieldIndex.toLowerCase();
	}

	/**
	 * Gets the index.
	 *
	 * @param object the object
	 * @return the index
	 */
	public static Index getIndex(Object object) {
		Validate.notNull(object);
		return getIndex(object.getClass());
	}

	/**
	 * Gets the index.
	 *
	 * @param entityClass the entity class
	 * @return the index
	 */
	public static Index getIndex(Class<?> entityClass) {
		AnnotationInfo annotationInfo = annotationManager.getAnnotationInfo(entityClass);
		return getIndex(annotationInfo);
	}

	/**
	 * Gets the index.
	 *
	 * @param annotationInfo the annotation info
	 * @return the index
	 */
	public static Index getIndex(AnnotationInfo annotationInfo) {
		if (annotationInfo == null)
			throw new IllegalArgumentException("Could not Object info");
		Annotation[] annotations = annotationInfo.getClassAnnotations();
		if (annotations == null || annotations.length <= 0)
			throw new IllegalArgumentException("Could not Object Class Annotation info");
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().isAssignableFrom(Index.class)) {
				return (Index) annotation;
			}
		}
		throw new IllegalArgumentException("Find Object Class Annotation not " + Index.class.getName());
	}

	/**
	 * Creates a new ObjectAnnon object.
	 *
	 * @param object the object
	 * @param annotationInfo the annotation info
	 * @return the x content builder
	 */
	private static XContentBuilder createPro(Object object, AnnotationInfo annotationInfo) {
		try {
			final XContentBuilder jsonBuilder = XContentFactory.jsonBuilder();
			jsonBuilder.startObject();
			Map<String, AbstractSearchProperty> properties = annotationInfo.getProperties();
			for (Map.Entry<String, AbstractSearchProperty> entry : properties.entrySet()) {
				AbstractSearchProperty property = entry.getValue();
				Object value = property.getProperty(object);
				if (value != null && StringUtils.isNotBlank(value.toString())) {
					if (property.isFieldIndex() || property.isFieldStore()) {
						String name = property.getFieldIndex().name();
						name = StringUtils.isBlank(name) ? property.getFieldName() : name;
						if (property.isSkipHTMLEscape()) {
							value = regularizeXmlString(value.toString());
						} else {
							value = value.toString();
						}
						jsonBuilder.field(name, value);
					}
				}
			}
			Float b = DataProcessingFactory.toDocumentBoost(object);
			//Float b = 2.0f;
			if (b != null)
				jsonBuilder.field("_boost", b);
			jsonBuilder.endObject();
			return jsonBuilder;
		} catch (Exception e) {
			throw ExceptionUtils.unchecked(e);
		}
	}

	/**
	 * Regularize xml string.
	 *
	 * @param strInput the str input
	 * @return the string
	 */
	public static String regularizeXmlString(String strInput) {
		String emptyString = "";
		if (strInput == null || strInput.length() == 0) {
			return emptyString;
		}
		String result = strInput.replaceAll("[\\x00-\\x08|\\x0b-\\x0c|\\x0e-\\x1f]", emptyString);
		//CDATA要先于HTML过滤
		result = escapeCDATA(result);
		//HTML过滤
		result = escapeHTMLTag(result);
		//&nbsp;类型过滤
		result = escapeNBSP(result);
		return result;
	}

	/**
	 * Escape cdata.
	 *
	 * @param strInput the str input
	 * @return the string
	 */
	public static String escapeCDATA(String strInput) {
		String emptyString = "";
		if (strInput == null || strInput.length() == 0) {
			return emptyString;
		}
		String result = Pattern.compile("<!\\[CDATA\\[.*?\\]\\]>", Pattern.DOTALL).matcher(strInput)
				.replaceAll(emptyString);
		return result;
	}

	/**
	 * Escape html tag.
	 *
	 * @param strInput the str input
	 * @return the string
	 */
	public static String escapeHTMLTag(String strInput) {
		String emptyString = "";
		if (strInput == null || strInput.length() == 0) {
			return emptyString;
		}
		String result = strInput.replaceAll("<[^>]*>", emptyString);
		return result;
	}

	/**
	 * Escape nbsp.
	 *
	 * @param strInput the str input
	 * @return the string
	 */
	public static String escapeNBSP(String strInput) {
		String emptyString = "";
		if (strInput == null || strInput.length() == 0) {
			return emptyString;
		}
		String result = strInput.replaceAll("&[a-z0-9#]+;", emptyString);
		return result;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		//		SumMallDocIndex sumMallDocIndex = new SumMallDocIndex();
		//		sumMallDocIndex.setKey("fadsfdas");
		//		sumMallDocIndex.setAttributeCollection("fasdfadsfdsa");
		//		sumMallDocIndex.setCategory("fdasfads");
		//		sumMallDocIndex.setDescription("fdasfdas");
		//		sumMallDocIndex.setMerchant("fdasfads");
		//		sumMallDocIndex.setPrice(1000);
		//		sumMallDocIndex.setProductName("fdasfad");
		//		sumMallDocIndex.setService("fdafads");
		//
		//		Source source = source(sumMallDocIndex);
		//		System.out.println(source.mapperBuilder.string());
		//		System.out.println(source.builder.string());
		//		DocumentMapperParser documentMapperParser = new DocumentMapperParser(
		//				new cn.com.rebirth.search.core.index.Index(source.index.indexName()), newAnalysisService(source.index));
		//		DocumentMapper documentMapper = documentMapperParser.parse(source.mapperBuilder.string());
		//		System.out.println(documentMapper.toString());
	}

	public static AnalysisService newAnalysisService(Index index) {
		Injector parentInjector = new ModulesBuilder().add(
				new SettingsModule(ImmutableSettings.Builder.EMPTY_SETTINGS),
				new EnvironmentModule(new Environment(ImmutableSettings.Builder.EMPTY_SETTINGS)),
				new IndicesAnalysisModule()).createInjector();
		Injector injector = new ModulesBuilder().add(
				new IndexSettingsModule(new cn.com.rebirth.search.core.index.Index(index.indexName()),
						ImmutableSettings.Builder.EMPTY_SETTINGS),
				new IndexNameModule(new cn.com.rebirth.search.core.index.Index(index.indexName())),
				new AnalysisModule(ImmutableSettings.Builder.EMPTY_SETTINGS, parentInjector
						.getInstance(IndicesAnalysisService.class))).createChildInjector(parentInjector);

		return injector.getInstance(AnalysisService.class);
	}
}
