/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
 * Takes care of selecting all source record Ids from the configured cache.
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
		try {
    	    if (this.recordIdIterator == null) {
    			this.recordIdIterator = this.cache.getCachedRecordIds().iterator();
    		}
    		if (this.recordIdIterator.hasNext()) {
    			return true;
    		}
		} catch (Exception e) {
		    log.error("Error obtaining record from cache:" + cache, e);
		}
        this.recordIdIterator = null;
        return false;
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
