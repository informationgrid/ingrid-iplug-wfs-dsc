/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.index.producer;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.wfs.dsc.cache.Cache;
import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;

/**
 * Takes care of selecting all source record Ids from a database. The SQL
 * statement is configurable via Spring.
 * 
 * The database connection is configured via the PlugDescription.
 * 
 * 
 * @author joachim@wemove.com
 * 
 */
public class WfsRecordSetProducer implements IWfsCacheRecordSetProducer {

	Cache cache;

	Iterator<String> recordIdIterator = null;

	final private static Log log = LogFactory
			.getLog(WfsRecordSetProducer.class);

	public WfsRecordSetProducer() {
		log.info("WfsRecordSetProducer started.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.iplug.dsc.index.IRecordProducer#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (this.recordIdIterator == null) {
			this.recordIdIterator = this.cache.getCachedRecordIds().iterator();
		}
		if (this.recordIdIterator.hasNext()) {
			return true;
		} else {
			this.recordIdIterator = null;
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.iplug.dsc.index.IRecordProducer#next()
	 */
	@Override
	public SourceRecord next() {
		String recordId = null;
		try {
			recordId = this.recordIdIterator.next();
			return new WfsCacheSourceRecord(this.cache.getRecord(recordId));
		} catch (Exception e) {
			log.error("Error reading record '" + recordId + "' from cache '"
					+ this.cache + "'.");
		}
		return null;
	}

	@Override
	public void setCache(Cache cache) {
		this.cache = cache;
	}

}
