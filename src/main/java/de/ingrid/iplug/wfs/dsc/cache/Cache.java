/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.cache;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;

/**
 * Interface for a cache that stores WFS feature records.
 *
 * Transaction support. A transaction allows to do all cache operations in a temporary
 * store. The cache guarantees that changes do not affect the content of the cache from
 * which the transaction started until the transaction is committed. A rollback ends the
 * transaction without changing the original content.
 *
 * @author ingo herwig <ingo@wemove.com>
 */
public interface Cache {

	/**
	 * Configure the cache.
	 * @param factory
	 */
	public void configure(WFSFactory factory);

	/**
	 * Get the ids all the records, that are cached.
	 * @return List
	 */
	public Set<String> getCachedRecordIds();

	/**
	 * Check if a record is cached.
	 * @param id
	 * @return boolean
	 */
	public boolean isCached(String id) throws IOException;

	/**
	 * Get a record.
	 * @param id
	 * @param elementSetName
	 * @return WFSFeature
	 */
	public WFSFeature getRecord(String id) throws IOException;

	/**
	 * Store a record. Overrides the old record with the same id.
	 * @param record
	 */
	public void putRecord(WFSFeature record) throws IOException;

	/**
	 * Remove a record.
	 * @param id
	 */
	public void removeRecord(String id);

	/**
	 * Remove all records.
	 */
	public void removeAllRecords();

	/**
	 * Check whether the cache is in transaction mode.
	 * @param boolean
	 */
	public boolean isInTransaction();

	/**
	 * Start the transaction. The content of the returned cache is the same as the content
	 * of this cache initially.
	 * @param Returns a new cache instance in transaction mode.
	 */
	public Cache startTransaction() throws IOException;

	/**
	 * Commit the transaction. Transfer all changes, that are done since the transaction was opened,
	 * to the original content.
	 */
	public void commitTransaction() throws IOException;

	/**
	 * Rollback the transaction. Discard all changes, that are done since the transaction was opened.
	 */
	public void rollbackTransaction();

	/**
	 * Get the cache from that a transaction was started. If the cache is not in transaction,
	 * the result is the same instance on which the method is called.
	 * @param Returns the initial cache instance.
	 */
	public Cache getInitialCache();

	/**
	 * Get the date of the last commit, returns null, if it could not be determined
	 */
	public Date getLastCommitDate();
}
