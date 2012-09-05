/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client RebirthSearchClientVersion.java 2012-7-6 16:50:30 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import cn.com.rebirth.commons.AbstractVersion;
import cn.com.rebirth.commons.Version;

/**
 * The Class RebirthSearchClientVersion.
 *
 * @author l.xue.nong
 */
public class RebirthSearchClientVersion extends AbstractVersion implements Version {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8043854155386241988L;

	/* (non-Javadoc)
	 * @see cn.com.summall.commons.Version#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "rebirth-search-client";
	}

}
