/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
package de.ingrid.iplug.wfs.dsc.index;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.elasticsearch.IndexInfo;
import de.ingrid.admin.object.IDocumentProducer;
import de.ingrid.iplug.wfs.dsc.cache.Cache;
import de.ingrid.iplug.wfs.dsc.cache.UpdateJob;
import de.ingrid.iplug.wfs.dsc.index.mapper.IRecordMapper;
import de.ingrid.iplug.wfs.dsc.index.producer.IWfsCacheRecordSetProducer;
import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.PlugDescription;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author joachim
 * 
 */
public class WfsDscDocumentProducer implements IDocumentProducer {

	private IWfsCacheRecordSetProducer recordSetProducer = null;

	private List<IRecordMapper> recordMapperList = null;

	Cache cache;

	Cache tmpCache = null;

	WFSFactory factory;

	UpdateJob job;

	private IndexInfo indexInfo;
	
	final private static Log log = LogFactory.getLog(WfsDscDocumentProducer.class);

	public WfsDscDocumentProducer() {
		log.info("WfsDscDocumentProducer started.");
	}

	public void init() {
		this.cache.configure(this.factory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.admin.object.IDocumentProducer#hasNext()
	 */
	@Override
	public boolean hasNext() {
        boolean result = false;
		try {
			if (this.tmpCache == null) {
				try {
					// start transaction
					this.tmpCache = this.cache.startTransaction();
                    this.recordSetProducer.setCache(this.tmpCache);
					this.tmpCache.removeAllRecords();

					// run the update job: fetch all wfs data from wfs source
					this.job.setCache(this.tmpCache);
					this.job.init();
					this.job.execute();

				} catch (Exception e) {
					log.error("Error harvesting WFS datasource.", e);
					if (this.tmpCache != null) {
						this.tmpCache.rollbackTransaction();
					}
					throw e;
				}
			}
			if (this.recordSetProducer.hasNext()) {
                result = true;
			} else {
			    // prevent runtime exception if the cache was not in transaction
			    // this can happen if the harvest process throws an exception and the
			    // transaction was rolled back (see above)
			    if (tmpCache.isInTransaction()) {
			        tmpCache.commitTransaction();
			    }				
				this.tmpCache = null;
                result = false;
			}
		} catch (Exception e) {
			log.error("Error obtaining information about a next record. Skip all records.", e);
            // make sure the tmp cache is released after exception occurs
            // otherwise the indexer will never "heal" from this exception
            tmpCache = null;
            throw new RuntimeException("Error harvesting WFS datasource");
        } finally {
            if (!result) {
                tmpCache = null;
            }
        }
        return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.admin.object.IDocumentProducer#next()
	 */
	@Override
	public ElasticDocument next() {
		ElasticDocument doc = new ElasticDocument();
		SourceRecord record = null;
		try {
			record = this.recordSetProducer.next();
			for (IRecordMapper mapper : this.recordMapperList) {
				long start = 0;
				if (log.isDebugEnabled()) {
					start = System.currentTimeMillis();
				}
				mapper.map(record, doc);
				if (log.isDebugEnabled()) {
					log.debug("Mapping of source record with " + mapper + " took: " + (System.currentTimeMillis() - start) + " ms.");
				}
			}
			return doc;
		} catch (Exception e) {
            if (record == null) {
                log.error("Error obtaining next record.", e);
            } else {
                log.error("Error mapping record.", e);
            }

            // DO NOT EMPTY CACHE !!! We want to continue indexing the fetched records !!!
            // if tmpCache is set to null the fetching process is started from scratch (see this.hasNext() method) !

            return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ingrid.utils.IConfigurable#configure(de.ingrid.utils.PlugDescription)
	 */
	@Override
	public void configure(PlugDescription arg0) {
		log.info("WfsDscDocumentProducer: configure called.");
	}

	public IWfsCacheRecordSetProducer getRecordSetProducer() {
		return this.recordSetProducer;
	}

	public void setRecordSetProducer(IWfsCacheRecordSetProducer recordProducer) {
		this.recordSetProducer = recordProducer;
	}

	public List<IRecordMapper> getRecordMapperList() {
		return this.recordMapperList;
	}

	public void setRecordMapperList(List<IRecordMapper> recordMapperList) {
		this.recordMapperList = recordMapperList;
	}

	public Cache getCache() {
		return this.cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public WFSFactory getFactory() {
		return this.factory;
	}

	public void setFactory(WFSFactory factory) {
		this.factory = factory;
	}

	public UpdateJob getJob() {
		return this.job;
	}

	public void setJob(UpdateJob job) {
		this.job = job;
	}

    @Override
    public IndexInfo getIndexInfo() {
        return indexInfo;
    }

    @Override
    public Integer getDocumentCount() {
        return null;
    }

	public void setIndexInfo(IndexInfo indexInfo) {
		this.indexInfo = indexInfo;
	}
}
