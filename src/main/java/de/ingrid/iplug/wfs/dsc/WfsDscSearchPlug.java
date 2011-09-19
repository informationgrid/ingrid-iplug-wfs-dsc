/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.admin.search.IngridIndexSearcher;
import de.ingrid.iplug.HeartBeatPlug;
import de.ingrid.iplug.IPlugdescriptionFieldFilter;
import de.ingrid.iplug.PlugDescriptionFieldFilters;
import de.ingrid.iplug.wfs.dsc.record.IdfRecordCreator;
import de.ingrid.utils.IRecordLoader;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.metadata.IMetadataInjector;
import de.ingrid.utils.processor.IPostProcessor;
import de.ingrid.utils.processor.IPreProcessor;
import de.ingrid.utils.query.IngridQuery;

/**
 * This iPlug connects to the iBus delivers search results based on a index.
 * 
 * @author joachim@wemove.com
 * 
 */
@Service
public class WfsDscSearchPlug extends HeartBeatPlug implements IRecordLoader {

	/**
	 * The logging object
	 */
	private static Log log = LogFactory.getLog(WfsDscSearchPlug.class);

	private IdfRecordCreator dscRecordProducer = null;

	private final IngridIndexSearcher _indexSearcher;

	@Autowired
	public WfsDscSearchPlug(final IngridIndexSearcher indexSearcher, IPlugdescriptionFieldFilter[] fieldFilters,
			IMetadataInjector[] injector, IPreProcessor[] preProcessors, IPostProcessor[] postProcessors)
					throws IOException {
		super(60000, new PlugDescriptionFieldFilters(fieldFilters), injector, preProcessors, postProcessors);
		this._indexSearcher = indexSearcher;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.utils.ISearcher#search(de.ingrid.utils.query.IngridQuery,
	 * int, int)
	 */
	@Override
	public final IngridHits search(final IngridQuery query, final int start, final int length) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("Incoming query: " + query.toString() + ", start=" + start + ", length=" + length);
		}
		this.preProcess(query);
		return this._indexSearcher.search(query, start, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.utils.IRecordLoader#getRecord(de.ingrid.utils.IngridHit)
	 */
	@Override
	public Record getRecord(IngridHit hit) throws Exception {
		Document document = this._indexSearcher.doc(hit.getDocumentId());
		return this.dscRecordProducer.getRecord(document);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.iplug.HeartBeatPlug#close()
	 */
	@Override
	public void close() throws Exception {
		this._indexSearcher.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.iplug.HeartBeatPlug#close()
	 */
	@Override
	public IngridHitDetail getDetail(IngridHit hit, IngridQuery query, String[] fields) throws Exception {
		final IngridHitDetail detail = this._indexSearcher.getDetail(hit, query, fields);

		// add original idf data (including the original response), if requested
		if (log.isDebugEnabled()) {
			log.debug("Request for direct WFS Data found. (" + ConfigurationKeys.REQUEST_KEY_WFS_DIRECT_RESPONSE + ")");
		}
		this.setDirectData(detail);

		return detail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.iplug.HeartBeatPlug#close()
	 */
	@Override
	public IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query, String[] fields) throws Exception {
		IngridHitDetail[] details = new IngridHitDetail[hits.length];
		for (int i = 0; i < hits.length; i++) {
			IngridHit ingridHit = hits[i];
			IngridHitDetail detail = this.getDetail(ingridHit, query, fields);
			details[i] = detail;
		}
		return details;
	}

	public IdfRecordCreator getDscRecordProducer() {
		return this.dscRecordProducer;
	}

	public void setDscRecordProducer(IdfRecordCreator dscRecordProducer) {
		this.dscRecordProducer = dscRecordProducer;
	}

	/**
	 * Set the original idf data in an IngridHitDetail
	 * 
	 * @param document
	 * @throws Exception
	 */
	protected void setDirectData(IngridHitDetail document) throws Exception {
		Document luceneDoc = this._indexSearcher.doc(document.getDocumentId());
		long startTime = 0;
		if (log.isDebugEnabled()) {
			startTime = System.currentTimeMillis();
		}
		Record r = this.dscRecordProducer.getRecord(luceneDoc);
		if (log.isDebugEnabled()) {
			log.debug("Get IDF record in " + (System.currentTimeMillis() - startTime) + " ms");
		}
		document.put(ConfigurationKeys.RESPONSE_KEY_IDF_RECORD, r);
	}

}
