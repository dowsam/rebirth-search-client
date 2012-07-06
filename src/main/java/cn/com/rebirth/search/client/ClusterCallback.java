/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client ClusterCallback.java 2012-3-30 10:17:23 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import cn.com.rebirth.search.core.action.ActionFuture;
import cn.com.rebirth.search.core.action.ActionResponse;
import cn.com.rebirth.search.core.client.ClusterAdminClient;

/**
 * The Interface ClusterCallback.
 *
 * @param <T> the generic type
 * @author l.xue.nong
 */
public interface ClusterCallback<T extends ActionResponse> {

	/**
	 * Execute.
	 *
	 * @param admin the admin
	 * @return the action future
	 */
	ActionFuture<T> execute(ClusterAdminClient admin);

}
