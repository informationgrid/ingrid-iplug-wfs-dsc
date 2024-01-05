/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
package de.ingrid.iplug.wfs.dsc.index.producer.impl;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.wfs.dsc.cache.Cache;
import de.ingrid.iplug.wfs.dsc.cache.UpdateJob;
import de.ingrid.iplug.wfs.dsc.index.producer.RecordSetProducer;
import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.om.WfsSourceRecord;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;

/**
 * CachedFeatureRecordSetProducer provides WFS feature records from a pre-build cache.
 *
 * The implementation fetches all WFS feature records into a temporary cache first and
 * commits them into the application cache after the iteration of all records completed.
 *
 * @author joachim@wemove.com
 */
public class CachedFeatureRecordSetProducer implements RecordSetProducer {

	private WFSFactory factory;

	private Cache cache;
	private Cache tmpCache = null;

	private UpdateJob job;

	private Iterator<String> recordIdIterator = null;

	final private static Log log = LogFactory.getLog(CachedFeatureRecordSetProducer.class);

	/**
	 * Constructor
	 */
	public CachedFeatureRecordSetProducer() {
		log.info("CachedFeatureRecordSetProducer started.");
	}

	/**
	 * Set the factory for making WFS requests
	 * @param factory
	 */
	public void setFactory(WFSFactory factory) {
		this.factory = factory;
	}

	/**
	 * Set the cache
	 * @param cache
	 */
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	/**
	 * Set the update job the is used to fill the cache
	 * @param job
	 */
	public void setJob(UpdateJob job) {
		this.job = job;
	}

	/**
	 * Check if the cache provides more records
	 * @return boolean
	 */
	private boolean hasNextInCache() {
		boolean result = false;
		try {
			if (this.recordIdIterator == null) {
				this.recordIdIterator = this.tmpCache.getCachedRecordIds().iterator();
			}
			if (this.recordIdIterator.hasNext()) {
				result = true;
			}
		}
		catch (Exception e) {
			log.error("Error obtaining record from cache:" + this.tmpCache, e);
		}
		return result;
	}

	@Override
	public void reset() {
		this.tmpCache = null;
		this.recordIdIterator = null;
	}

	@Override
	public boolean hasNext() {
		boolean result = false;
		try {
			this.cache.configure(this.factory);

			// run update job when called the first time
			if (this.tmpCache == null) {
				try {
					// start transaction
					this.tmpCache = this.cache.startTransaction();
					this.tmpCache.removeAllRecords();

					// fetch all wfs data from wfs source
					this.job.setCache(this.tmpCache);
					this.job.init();
					this.job.execute();
				}
				catch (Exception e) {
					log.error("Error harvesting WFS datasource.", e);
					if (this.tmpCache != null) {
						this.tmpCache.rollbackTransaction();
					}
					throw e;
				}
			}
			if (this.hasNextInCache()) {
				result = true;
			}
			else {
				// update cache after iteration has completed

				// prevent runtime exception if the cache was not in transaction
				// this can happen if the harvest process throws an exception and the
				// transaction was rolled back (see above)
				if (this.tmpCache.isInTransaction()) {
					this.tmpCache.commitTransaction();
				}
				this.tmpCache = null;
				result = false;
			}
		}
		catch (Exception e) {
			log.error("Error obtaining information about a next record. Skip all records.", e);
			// make sure the tmp cache is released after exception occurs
			// otherwise the indexer will never "heal" from this exception
			this.tmpCache = null;
			throw new RuntimeException("Error harvesting WFS datasource");
		}
		finally {
			if (!result) {
				this.tmpCache = null;
			}
		}
		return result;
	}

	@Override
	public SourceRecord next() {
		SourceRecord result = null;
		String recordId = null;
		try {
			recordId = this.recordIdIterator.next();
			result = new WfsSourceRecord(this.tmpCache.getRecord(recordId));
		}
		catch (Exception e) {
			log.error("Error reading record '" + recordId + "' from cache '" + this.tmpCache + "'.");
		}
		return result;
	}
}
