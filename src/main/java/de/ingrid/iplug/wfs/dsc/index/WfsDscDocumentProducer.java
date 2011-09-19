/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.index;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;

import de.ingrid.admin.object.IDocumentProducer;
import de.ingrid.iplug.wfs.dsc.cache.Cache;
import de.ingrid.iplug.wfs.dsc.cache.UpdateJob;
import de.ingrid.iplug.wfs.dsc.index.mapper.IRecordMapper;
import de.ingrid.iplug.wfs.dsc.index.producer.IWfsCacheRecordSetProducer;
import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.utils.PlugDescription;

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
		try {
			if (this.tmpCache == null) {
				try {
					// start transaction
					this.tmpCache = this.cache.startTransaction();
					this.tmpCache.removeAllRecords();

					// run the update job: fetch all wfs data from wfs source
					this.job.setCache(this.tmpCache);
					this.job.init();
					this.job.execute();

					this.recordSetProducer.setCache(this.tmpCache);

				} catch (Exception e) {
					log.error("Error harvesting WFS datasource.", e);
					if (this.tmpCache != null) {
						this.tmpCache.rollbackTransaction();
					}
				}
			}
			if (this.recordSetProducer.hasNext()) {
				return true;
			} else {
				this.tmpCache.commitTransaction();
				this.tmpCache = null;
				return false;
			}
		} catch (Exception e) {
			log.error("Error obtaining information about a next record. Skip all records.", e);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.admin.object.IDocumentProducer#next()
	 */
	@Override
	public Document next() {
		Document doc = new Document();
		try {
			SourceRecord record = this.recordSetProducer.next();
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
			log.error("Error obtaining next record.", e);
			if (this.tmpCache != null) {
				this.tmpCache.rollbackTransaction();
				this.tmpCache = null;
			}
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

}
