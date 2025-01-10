/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.cache;

import java.util.Date;
import java.util.Set;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;

public class ExecutionContext {

	private WFSFactory factory;
	private Cache cache;
	private Set<String> filterStrSet;
	private Date lastExecutionDate;
	private int requestPause;

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
