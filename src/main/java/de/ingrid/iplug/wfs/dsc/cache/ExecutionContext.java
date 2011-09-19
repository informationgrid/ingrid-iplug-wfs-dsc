/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.cache;

import java.util.Date;
import java.util.Set;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;

public class ExecutionContext {

	WFSFactory factory;
	Cache cache;
	Set<String> filterStrSet;
	Date lastExecutionDate;
	int requestPause;

	/**
	 * @return the factory
	 */
	public WFSFactory getFactory() {
		return this.factory;
	}
	/**
	 * @param factory the factory to set
	 */
	public void setFactory(WFSFactory factory) {
		this.factory = factory;
	}
	/**
	 * @return the cache
	 */
	public Cache getCache() {
		return this.cache;
	}
	/**
	 * @param cache the cache to set
	 */
	public void setCache(Cache cache) {
		this.cache = cache;
	}
	/**
	 * @return the filterStrSet
	 */
	public Set<String> getFilterStrSet() {
		return this.filterStrSet;
	}
	/**
	 * @param filterStrSet the filterStrSet to set
	 */
	public void setFilterStrSet(Set<String> filterStrSet) {
		this.filterStrSet = filterStrSet;
	}
	/**
	 * @return the lastExecutionDate
	 */
	public Date getLastExecutionDate() {
		return this.lastExecutionDate;
	}
	/**
	 * @param lastExecutionDate the lastExecutionDate to set
	 */
	public void setLastExecutionDate(Date lastExecutionDate) {
		this.lastExecutionDate = lastExecutionDate;
	}
	/**
	 * @return the requestPause
	 */
	public int getRequestPause() {
		return this.requestPause;
	}
	/**
	 * @param requestPause the requestPause to set
	 */
	public void setRequestPause(int requestPause) {
		this.requestPause = requestPause;
	}
}
