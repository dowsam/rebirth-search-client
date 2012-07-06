/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-search-client ClientCallback.java 2012-3-30 10:16:57 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import cn.com.rebirth.search.core.action.ActionFuture;
import cn.com.rebirth.search.core.action.ActionResponse;
import cn.com.rebirth.search.core.client.Client;

/**
 * The Interface ClientCallback.
 *
 * @param <T> the generic type
 * @author l.xue.nong
 */
public interface ClientCallback<T extends ActionResponse> {

	/**
	 * Execute.
	 *
	 * @param client the client
	 * @return the action future
	 */
	ActionFuture<T> execute(final Client client);
}
