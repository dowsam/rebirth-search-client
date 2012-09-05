/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client AbstractNodeTemplate.java 2012-7-30 9:25:19 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.lucene.search.Similarity;

import cn.com.rebirth.commons.PageRequest.Direction;
import cn.com.rebirth.commons.PageRequest.Order;
import cn.com.rebirth.commons.PageRequest.Sort;
import cn.com.rebirth.commons.search.SearchPage;
import cn.com.rebirth.commons.search.SearchPageRequest;
import cn.com.rebirth.commons.search.SearchPageRequest.SearchOrder;
import cn.com.rebirth.commons.search.SortType;
import cn.com.rebirth.commons.search.annotation.AnnotationInfo;
import cn.com.rebirth.commons.search.annotation.AnnotationManager;
import cn.com.rebirth.commons.search.annotation.Index;
import cn.com.rebirth.commons.utils.CglibProxyUtils;
import cn.com.rebirth.commons.xcontent.XContentBuilder;
import cn.com.rebirth.search.client.ObjectAnnonFactory.Source;
import cn.com.rebirth.search.client.group.LuceneGroup;
import cn.com.rebirth.search.client.group.LuceneGroupField;
import cn.com.rebirth.search.core.action.ActionFuture;
import cn.com.rebirth.search.core.action.ActionResponse;
import cn.com.rebirth.search.core.action.admin.cluster.health.ClusterHealthResponse;
import cn.com.rebirth.search.core.action.admin.cluster.health.ClusterHealthStatus;
import cn.com.rebirth.search.core.action.admin.cluster.state.ClusterStateResponse;
import cn.com.rebirth.search.core.action.admin.indices.close.CloseIndexResponse;
import cn.com.rebirth.search.core.action.admin.indices.create.CreateIndexResponse;
import cn.com.rebirth.search.core.action.admin.indices.delete.DeleteIndexResponse;
import cn.com.rebirth.search.core.action.admin.indices.flush.FlushResponse;
import cn.com.rebirth.search.core.action.admin.indices.gateway.snapshot.GatewaySnapshotResponse;
import cn.com.rebirth.search.core.action.admin.indices.mapping.delete.DeleteMappingResponse;
import cn.com.rebirth.search.core.action.admin.indices.mapping.put.PutMappingResponse;
import cn.com.rebirth.search.core.action.admin.indices.optimize.OptimizeResponse;
import cn.com.rebirth.search.core.action.admin.indices.refresh.RefreshResponse;
import cn.com.rebirth.search.core.action.admin.indices.status.IndicesStatusResponse;
import cn.com.rebirth.search.core.action.bulk.BulkRequest;
import cn.com.rebirth.search.core.action.bulk.BulkResponse;
import cn.com.rebirth.search.core.action.count.CountRequest;
import cn.com.rebirth.search.core.action.count.CountResponse;
import cn.com.rebirth.search.core.action.delete.DeleteRequest;
import cn.com.rebirth.search.core.action.delete.DeleteResponse;
import cn.com.rebirth.search.core.action.get.GetRequest;
import cn.com.rebirth.search.core.action.get.GetResponse;
import cn.com.rebirth.search.core.action.index.IndexRequest;
import cn.com.rebirth.search.core.action.index.IndexResponse;
import cn.com.rebirth.search.core.action.search.SearchRequest;
import cn.com.rebirth.search.core.action.search.SearchResponse;
import cn.com.rebirth.search.core.action.search.SearchType;
import cn.com.rebirth.search.core.client.Client;
import cn.com.rebirth.search.core.client.ClusterAdminClient;
import cn.com.rebirth.search.core.client.IndicesAdminClient;
import cn.com.rebirth.search.core.client.Requests;
import cn.com.rebirth.search.core.cluster.ClusterState;
import cn.com.rebirth.search.core.cluster.metadata.IndexMetaData;
import cn.com.rebirth.search.core.cluster.metadata.MappingMetaData;
import cn.com.rebirth.search.core.index.mapper.MapperException;
import cn.com.rebirth.search.core.index.query.BoolQueryBuilder;
import cn.com.rebirth.search.core.index.query.QueryBuilder;
import cn.com.rebirth.search.core.index.query.QueryBuilders;
import cn.com.rebirth.search.core.node.Node;
import cn.com.rebirth.search.core.search.SearchHit;
import cn.com.rebirth.search.core.search.SearchHits;
import cn.com.rebirth.search.core.search.builder.SearchSourceBuilder;
import cn.com.rebirth.search.core.search.facet.FacetBuilders;
import cn.com.rebirth.search.core.search.facet.Facets;
import cn.com.rebirth.search.core.search.facet.terms.TermsFacet;
import cn.com.rebirth.search.core.search.facet.terms.TermsFacet.Entry;
import cn.com.rebirth.search.core.search.highlight.HighlightBuilder;
import cn.com.rebirth.search.core.search.highlight.HighlightField;
import cn.com.rebirth.search.core.search.sort.SortBuilders;
import cn.com.rebirth.search.core.search.sort.SortOrder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The Class AbstractNodeTemplate.
 *
 * @author l.xue.nong
 */
public abstract class AbstractNodeTemplate implements NodeOperations, BaseNodeOperations, IndexSearchEngine {

	/** The client. */
	private final Client client;

	/**
	 * Instantiates a new abstract node template.
	 *
	 * @param client the client
	 */
	public AbstractNodeTemplate(Client client) {
		super();
		this.client = client;
	}

	/**
	 * Instantiates a new abstract node template.
	 *
	 * @param node the node
	 */
	public AbstractNodeTemplate(Node node) {
		this(node.client());
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#indexExists()
	 */
	@Override
	public boolean indexExists(Class<?> entityClass) {
		executeGet(new ClusterCallback<ClusterHealthResponse>() {

			@Override
			public ActionFuture<ClusterHealthResponse> execute(final ClusterAdminClient admin) {
				return admin.health(Requests.clusterHealthRequest().waitForStatus(ClusterHealthStatus.YELLOW));
			}
		});
		final IndicesStatusResponse response = executeGet(new NodeCallback<IndicesStatusResponse>() {

			@Override
			public ActionFuture<IndicesStatusResponse> execute(final IndicesAdminClient admin) {
				return admin.status(Requests.indicesStatusRequest());
			}
		});
		return response.getIndices().get(toIndex(entityClass).indexName()) != null;
	}

	/**
	 * To index.
	 *
	 * @param entityClass the entity class
	 * @return the index
	 */
	private Index toIndex(Class<?> entityClass) {
		return ObjectAnnonFactory.getIndex(entityClass);
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#deleteIndex()
	 */
	@Override
	public void deleteIndex(final Class<?> entityClass) {
		executeGet(new NodeCallback<DeleteIndexResponse>() {
			@Override
			public ActionFuture<DeleteIndexResponse> execute(final IndicesAdminClient admin) {
				return admin.delete(Requests.deleteIndexRequest(toIndex(entityClass).indexName()));
			}
		});
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#refreshIndex()
	 */
	@Override
	public void refreshIndex(final Class<?> entityClass) {
		executeGet(new NodeCallback<RefreshResponse>() {

			@Override
			public ActionFuture<RefreshResponse> execute(final IndicesAdminClient admin) {
				return admin.refresh(Requests.refreshRequest(toIndex(entityClass).indexName()).waitForOperations(true));
			}
		});
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#closeIndex()
	 */
	@Override
	public void closeIndex(final Class<?> entityClass) {
		executeGet(new NodeCallback<CloseIndexResponse>() {
			@Override
			public ActionFuture<CloseIndexResponse> execute(final IndicesAdminClient admin) {
				return admin.close(Requests.closeIndexRequest(toIndex(entityClass).indexName()));
			}
		});
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#flushIndex()
	 */
	@Override
	public void flushIndex(final Class<?> entityClass) {
		executeGet(new NodeCallback<FlushResponse>() {
			@Override
			public ActionFuture<FlushResponse> execute(final IndicesAdminClient admin) {
				return admin.flush(Requests.flushRequest(toIndex(entityClass).indexName()));
			}
		});
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#snapshotIndex()
	 */
	@Override
	public void snapshotIndex(final Class<?> entityClass) {
		executeGet(new NodeCallback<GatewaySnapshotResponse>() {
			@Override
			public ActionFuture<GatewaySnapshotResponse> execute(final IndicesAdminClient admin) {
				return admin.gatewaySnapshot(Requests.gatewaySnapshotRequest(toIndex(entityClass).indexName()));
			}
		});
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#executeGet(cn.com.summall.search.client.NodeCallback)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ActionResponse> T executeGet(NodeCallback<T> callback) {
		final IndicesAdminClient indicesAdmin = client.admin().indices();
		final ActionFuture<?> action = callback.execute(indicesAdmin);
		final T response = (T) action.actionGet();
		return response;
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#executeGet(cn.com.summall.search.client.ClusterCallback)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ActionResponse> T executeGet(ClusterCallback<T> callback) {
		final ClusterAdminClient clusterAdmin = client.admin().cluster();
		final ActionFuture<?> action = callback.execute(clusterAdmin);
		final T response = (T) action.actionGet();
		return response;
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#executeGet(cn.com.summall.search.client.ClientCallback)
	 */
	@Override
	public <T extends ActionResponse> T executeGet(ClientCallback<T> callback) {
		final ActionFuture<T> action = callback.execute(client);
		final T response = action.actionGet();
		return response;
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#getIndexName()
	 */
	@Override
	public String getIndexName(Class<?> entityClass) {
		return toIndex(entityClass).indexName();
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#createIndex()
	 */
	@Override
	public void createIndex(final Class<?> entityClass) {
		if (!indexExists(entityClass)) {
			executeGet(new NodeCallback<CreateIndexResponse>() {

				@Override
				public ActionFuture<CreateIndexResponse> execute(final IndicesAdminClient admin) {
					return admin.create(Requests.createIndexRequest(getIndexName(entityClass)));
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.BaseNodeOperations#createIndex(java.lang.Object)
	 */
	@Override
	public void createOrUpdateIndex(Object object) {
		Validate.notNull(object);
		final Source source = toSource(object);
		executeGet(new ClientCallback<IndexResponse>() {

			@Override
			public ActionFuture<IndexResponse> execute(Client client) {
				IndexRequest request = Requests.indexRequest(source.index.indexName()).id(source.id)
						.source(source.builder).type(source.index.indexType());
				return client.index(request);
			}
		});
	}

	/**
	 * To source.
	 *
	 * @param object the object
	 * @return the source
	 */
	protected Source toSource(Object object) {
		Validate.notNull(object);
		if (object instanceof Source) {
			return (Source) object;
		}
		return ObjectAnnonFactory.source(object);
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.BaseNodeOperations#deleteIndex(java.lang.Object)
	 */
	@Override
	public void deleteIndex(Object object) {
		Validate.notNull(object);
		final Source source = toSource(object);
		executeGet(new ClientCallback<DeleteResponse>() {

			@Override
			public ActionFuture<DeleteResponse> execute(Client client) {
				DeleteRequest request = Requests.deleteRequest(source.index.indexName()).id(source.id)
						.type(source.index.indexType());
				return client.delete(request);
			}
		});
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.NodeOperations#optimizeIndex()
	 */
	@Override
	public void optimizeIndex(final Class<?> entityClass) {
		executeGet(new NodeCallback<OptimizeResponse>() {

			@Override
			public ActionFuture<OptimizeResponse> execute(IndicesAdminClient client) {
				return client.optimize(Requests.optimizeRequest(getIndexName(entityClass)));
			}
		});
	}

	/**
	 * Checks if is mapping exist.
	 *
	 * @param index the index
	 * @param type the type
	 * @return true, if is mapping exist
	 */
	protected boolean isMappingExist(final String index, final String type) {
		ClusterStateResponse clusterStateResponse = executeGet(new ClusterCallback<ClusterStateResponse>() {
			@Override
			public ActionFuture<ClusterStateResponse> execute(ClusterAdminClient admin) {
				return admin.prepareState().setFilterIndices(index).execute();
			}
		});
		ClusterState cs = clusterStateResponse.getState();
		IndexMetaData imd = cs.getMetaData().index(index);
		if (imd == null)
			return false;
		MappingMetaData mdd = imd.mapping(type);
		if (mdd != null)
			return true;
		return false;
	}

	/**
	 * Push mapping.
	 *
	 * @param index the index
	 * @param type the type
	 * @param force the force
	 * @param source the source
	 */
	protected void pushMapping(final String index, final String type, final boolean force, final XContentBuilder source) {
		if (force && isMappingExist(index, type)) {
			executeGet(new NodeCallback<DeleteMappingResponse>() {
				@Override
				public ActionFuture<DeleteMappingResponse> execute(IndicesAdminClient client) {
					return client.prepareDeleteMapping(index).setType(type).execute();
				}
			});
		}
		if (!isMappingExist(index, type)) {
			PutMappingResponse response = executeGet(new NodeCallback<PutMappingResponse>() {

				@Override
				public ActionFuture<PutMappingResponse> execute(IndicesAdminClient client) {
					return client.preparePutMapping(index).setType(type).setSource(source).execute();
				}
			});
			if (!response.acknowledged())
				throw new MapperException("Could not define mapping for type [" + index + "]/[" + type + "].");
		}
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.BaseNodeOperations#getIndex(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> T getIndex(final String id, final Class<T> entityClass) {
		AnnotationInfo annotationInfo = AnnotationManager.getInstance().getAnnotationInfo(entityClass);
		final Index index = ObjectAnnonFactory.getIndex(annotationInfo);
		GetResponse getResponse = executeGet(new ClientCallback<GetResponse>() {

			@Override
			public ActionFuture<GetResponse> execute(Client client) {
				GetRequest request = Requests.getRequest(index.indexName()).id(id).type(index.indexType());
				return client.get(request);
			}
		});
		if (!getResponse.exists())
			return null;
		return ObjectAnnonFactory.invoke(getResponse.getId(), getResponse.getSource(), annotationInfo);
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.BaseNodeOperations#batchDeleteIndex(java.util.Collection)
	 */
	@Override
	public void batchDeleteIndex(Collection<?> collections) {
		Validate.notNull(collections);
		final BulkRequest request = Requests.bulkRequest();
		for (Object object : collections) {
			Source source = toSource(object);
			request.add(Requests.deleteRequest(source.index.indexName()).id(source.id).type(source.index.indexType()));
		}
		BulkResponse bulkResponse = executeGet(new ClientCallback<BulkResponse>() {
			@Override
			public ActionFuture<BulkResponse> execute(Client client) {
				return client.bulk(request);
			}
		});
		if (bulkResponse.hasFailures()) {
			throw new RuntimeException(bulkResponse.buildFailureMessage());
		}
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.BaseNodeOperations#batchCreateOrUpdateIndex(java.util.Collection)
	 */
	@Override
	public void batchCreateOrUpdateIndex(Collection<?> collections) {
		Validate.notNull(collections);
		final BulkRequest request = Requests.bulkRequest();
		for (Object object : collections) {
			Source source = toSource(object);
			request.add(Requests.indexRequest(source.index.indexName()).id(source.id).type(source.index.indexType())
					.source(source.builder));
		}
		BulkResponse bulkResponse = executeGet(new ClientCallback<BulkResponse>() {
			@Override
			public ActionFuture<BulkResponse> execute(Client client) {
				return client.bulk(request);
			}
		});
		if (bulkResponse.hasFailures()) {
			throw new RuntimeException(bulkResponse.buildFailureMessage());
		}
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.IndexSearchEngine#search(java.lang.String, cn.com.summall.commons.PageRequest, java.lang.Class)
	 */
	@Override
	public <T> SearchPage<T> search(final String queryString, final SearchPageRequest pageRequest,
			final Class<T> entityClass) {
		QueryBuilder queryBuilder = QueryParser.parse(queryString);
		return search(queryBuilder, pageRequest, entityClass);
	}

	/**
	 * To sort order.
	 *
	 * @param direction the direction
	 * @return the sort order
	 */
	public static SortOrder toSortOrder(Direction direction) {
		if (Direction.ASC.equals(direction)) {
			return SortOrder.ASC;
		}
		return SortOrder.DESC;
	}

	/**
	 * Execute.
	 *
	 * @param <T> the generic type
	 * @param callback the callback
	 * @return the t
	 */
	protected <T extends ActionResponse> T execute(ClientCallback<T> callback) {
		return executeGet(callback);
	}

	/**
	 * Search.
	 *
	 * @param <T> the generic type
	 * @param queryBuilder the query builder
	 * @param pageRequest the page request
	 * @param entityClass the entity class
	 * @return the search page
	 */
	protected <T> SearchPage<T> search(final QueryBuilder queryBuilder, final SearchPageRequest pageRequest,
			final Class<T> entityClass) {
		SearchPage<T> page = new SearchPage<T>();
		if (pageRequest instanceof FacetPageRequest) {
			page = new FacetPage<T>();
		}
		SearchResponse searchResponse = executeGet(new ClientCallback<SearchResponse>() {
			@Override
			public ActionFuture<SearchResponse> execute(Client client) {
				Similarity similarity = pageRequest.getSimilarity();
				final SearchRequest request = Requests.searchRequest(toIndex(entityClass).indexName())
						.types(toIndex(entityClass).indexType()).searchType(SearchType.DFS_QUERY_THEN_FETCH);
				final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
				sourceBuilder.query(queryBuilder);
				sourceBuilder.from((pageRequest.getPageNo() - 1) * pageRequest.getPageSize());
				sourceBuilder.size(pageRequest.getPageSize());
				Sort sort = pageRequest.getSort();
				if (sort != null && !sort.getOrders().isEmpty()) {
					for (Order order : sort) {
						if (order instanceof SearchOrder) {
							SearchOrder searchOrder = (SearchOrder) order;
							SortType sortType = searchOrder.getSortType();
							if (SortType.SCORE.equals(sortType) || StringUtils.isBlank(order.getProperty())) {
								sourceBuilder.sort(SortBuilders.scoreSort());
							} else {
								sourceBuilder.sort(order.getProperty(), toSortOrder(order.getDirection()));
							}
						}
					}
				} else {
					sourceBuilder.sort(SortBuilders.scoreSort());
				}
				if (similarity != null) {
					sourceBuilder.similarity((similarity));
				}
				if (pageRequest instanceof FacetPageRequest) {
					List<LuceneGroupField> groupFields = ((FacetPageRequest) pageRequest).getLuceneGroupFields();
					if (groupFields != null && !groupFields.isEmpty()) {
						for (LuceneGroupField groupField : groupFields) {
							sourceBuilder.facet(FacetBuilders.termsFacet(groupField.getGroupField())
									.field(groupField.getGroupField()).size(groupField.getGroupFieldTop()));
						}
					}
				}
				if (pageRequest.isSearchDebug()) {
					sourceBuilder.explain(pageRequest.isSearchDebug());
				}
				//higth
				if (pageRequest instanceof HighlightSearchPageRequest) {
					HighlightSearchPageRequest highlightSearchPageRequest = (HighlightSearchPageRequest) pageRequest;
					HighlightBuilder highlightBuilder = new HighlightBuilder();
					highlightBuilder.postTags(highlightSearchPageRequest.getPostTag());
					highlightBuilder.preTags(highlightSearchPageRequest.getPreTag());
					List<HighlightSearchPageRequest.HighligthSearchField> fields = highlightSearchPageRequest
							.getHighligthSearchFields();
					if (fields != null && !fields.isEmpty()) {
						for (HighlightSearchPageRequest.HighligthSearchField field : fields) {
							highlightBuilder.field(field.getName(), field.getFragmentSize());
						}
					}
					sourceBuilder.highlight(highlightBuilder);
				}
				request.source(sourceBuilder);
				return client.search(request);
			}

		});
		List<T> rest = Lists.newArrayList();
		SearchHits searchHits = searchResponse.getHits();
		for (SearchHit searchHit : searchHits) {
			Map<String, HighlightField> hiMap = searchHit.highlightFields();
			Map<String, Object> map = searchHit.getSource();
			T t = ObjectAnnonFactory.invoke(searchHit.id(), higthMap(map, hiMap),
					ObjectAnnonFactory.annotationInfo(entityClass));
			rest.add(t);
		}
		if (pageRequest.isCountTotal()) {
			page.setTotalItems(searchResponse.hits().totalHits());
		}
		page.setResult(rest);
		if (pageRequest.isSearchDebug()) {
			page.setDebugMsg(searchResponse.toString());
		}
		Facets facets = searchResponse.getFacets();
		if (facets != null && (pageRequest instanceof FacetPageRequest)) {
			Map<String, List<LuceneGroup>> map = Maps.newHashMap();
			FacetPageRequest facetPageRequest = ((FacetPageRequest) pageRequest);
			List<LuceneGroupField> groupFields = facetPageRequest.getLuceneGroupFields();
			for (LuceneGroupField groupField : groupFields) {
				TermsFacet facet = facets.facet(groupField.getGroupField());
				List<? extends TermsFacet.Entry> entries = (facet).entries();
				List<LuceneGroup> groups = map.get(groupField.getGroupField());
				if (groups == null)
					groups = Lists.newArrayList();
				for (Entry entry : entries) {
					LuceneGroup pg = new LuceneGroup();
					pg.setName(entry.term());
					pg.setOccur(entry.count());
					groups.add(pg);
				}
				map.put(groupField.getGroupField(), groups);
				List<LuceneGroupField> children = groupField.getChildren();
				if (children != null && !children.isEmpty()) {
					facetChildren(map, groupField, children, queryBuilder, entityClass, facetPageRequest);
				}
			}
			if (page instanceof FacetPage)
				((FacetPage<T>) page).setGroups(map);
		}
		return page;
	}

	/**
	 * Higth map.
	 *
	 * @param map the map
	 * @param hiMap the hi map
	 * @return the map
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> higthMap(final Map<String, Object> map, final Map<String, HighlightField> hiMap) {
		if (hiMap == null || hiMap.isEmpty())
			return map;
		return CglibProxyUtils.getProxyInstance(map.getClass(), new MethodInterceptor() {

			@Override
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				String methodName = method.getName();
				if ("get".equals(methodName)) {
					String key = (String) args[0];
					HighlightField highlightField = hiMap.get(key);
					if (highlightField != null && highlightField.getFragments() != null
							&& highlightField.getFragments().length > 0) {
						return highlightField.getFragments()[0];
					}
				}
				return method.invoke(map, args);
			}
		});
	}

	/* (non-Javadoc)
	 * @see cn.com.summall.search.client.IndexSearchEngine#search(java.lang.String, cn.com.summall.search.client.FacetPageRequest, java.lang.Class)
	 */
	@Override
	public <T> FacetPage<T> search(final String queryString, final FacetPageRequest pageRequest,
			final Class<T> entityClass) {
		final QueryBuilder queryBuilder = QueryParser.parse(queryString);
		return (FacetPage<T>) search(queryBuilder, pageRequest, entityClass);
	}

	/**
	 * Facet children.
	 *
	 * @param map the map
	 * @param groupField the group field
	 * @param children the children
	 * @param queryBuilder the query builder
	 * @param entityClass the entity class
	 * @param pageRequest the page request
	 */
	private void facetChildren(Map<String, List<LuceneGroup>> map, LuceneGroupField groupField,
			List<LuceneGroupField> children, QueryBuilder queryBuilder, Class<?> entityClass,
			FacetPageRequest pageRequest) {
		List<LuceneGroup> groups = map.get(groupField.getGroupField());
		for (LuceneGroupField groupField2 : children) {
			for (LuceneGroup luceneGroup : groups) {
				BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
				boolQueryBuilder.must(queryBuilder);
				boolQueryBuilder.must(QueryBuilders.termQuery(groupField.getGroupField(), luceneGroup.getName()));
				List<LuceneGroup> groups2 = childGroup(boolQueryBuilder, entityClass, pageRequest, groupField2);
				luceneGroup.setChildren(groups2);
				map.put(groupField2.getGroupField(), groups2);
			}
			List<LuceneGroupField> luceneGroupFields = groupField2.getChildren();
			if (luceneGroupFields != null && !luceneGroupFields.isEmpty()) {
				facetChildren(map, groupField2, luceneGroupFields, queryBuilder, entityClass, pageRequest);
			}
		}
	}

	/**
	 * Child group.
	 *
	 * @param boolQueryBuilder the bool query builder
	 * @param entityClass the entity class
	 * @param pageRequest the page request
	 * @param groupField2 the group field2
	 * @return the list
	 */
	private List<LuceneGroup> childGroup(final BoolQueryBuilder boolQueryBuilder, final Class<?> entityClass,
			final FacetPageRequest pageRequest, final LuceneGroupField groupField2) {
		SearchResponse searchResponse = executeGet(new ClientCallback<SearchResponse>() {

			@Override
			public ActionFuture<SearchResponse> execute(Client client) {
				final SearchRequest request = Requests.searchRequest(toIndex(entityClass).indexName())
						.types(toIndex(entityClass).indexType()).searchType(SearchType.DFS_QUERY_THEN_FETCH);
				final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
				sourceBuilder.query(boolQueryBuilder);
				Sort sort = pageRequest.getSort();
				if (sort != null && !sort.getOrders().isEmpty()) {
					for (Order order : sort) {
						sourceBuilder.sort(order.getProperty(), toSortOrder(order.getDirection()));
					}
				} else {
					sourceBuilder.sort(SortBuilders.scoreSort());
				}
				sourceBuilder.facet(FacetBuilders.termsFacet(groupField2.getGroupField())
						.field(groupField2.getGroupField()).size(groupField2.getGroupFieldTop()));
				request.source(sourceBuilder);
				return client.search(request);
			}
		});
		Facets facets = searchResponse.getFacets();
		TermsFacet termsFacet = facets.facet(groupField2.getGroupField());
		List<LuceneGroup> groups = Lists.newArrayList();
		for (Entry entry : termsFacet) {
			LuceneGroup pg = new LuceneGroup();
			pg.setName(entry.term());
			pg.setOccur(entry.count());
			groups.add(pg);
		}
		return groups;
	}

	/* (non-Javadoc)
	 * @see cn.com.rebirth.search.client.IndexSearchEngine#count(java.lang.Class)
	 */
	@Override
	public <T> Long count(Class<T> entityClass) {
		return count(entityClass, null);
	}

	/* (non-Javadoc)
	 * @see cn.com.rebirth.search.client.IndexSearchEngine#count(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> Long count(final Class<T> entityClass, final String queryString) {
		CountResponse countResponse = executeGet(new ClientCallback<CountResponse>() {

			@Override
			public ActionFuture<CountResponse> execute(Client client) {
				CountRequest countRequest = Requests.countRequest(toIndex(entityClass).indexName()).types(
						toIndex(entityClass).indexType());
				if (queryString != null) {
					QueryBuilder queryBuilder = QueryParser.parse(queryString);
					countRequest.query(queryBuilder);
				}
				return client.count(countRequest);
			}
		});
		return countResponse.count();
	}
}
