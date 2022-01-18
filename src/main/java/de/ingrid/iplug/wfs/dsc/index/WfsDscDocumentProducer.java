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
/**
 *
 */
package de.ingrid.iplug.wfs.dsc.index;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.admin.object.IDocumentProducer;
import de.ingrid.elasticsearch.IndexInfo;
import de.ingrid.iplug.wfs.dsc.index.mapper.RecordMapper;
import de.ingrid.iplug.wfs.dsc.index.producer.RecordSetProducer;
import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.PlugDescription;

/**
 * @author joachim
 */
public class WfsDscDocumentProducer implements IDocumentProducer {

	private RecordSetProducer recordSetProducer = null;

	private List<RecordMapper> recordMapperList = null;

	private IndexInfo indexInfo;

	final private static Log log = LogFactory.getLog(WfsDscDocumentProducer.class);

	/**
	 * Constructor
	 */
	public WfsDscDocumentProducer() {
		log.info("WfsDscDocumentProducer started.");
	}

	@Override
	public boolean hasNext() {
		boolean result = false;
		try {
			result = this.recordSetProducer.hasNext();
		}
		catch (Exception e) {
			log.error("Error checking next record.", e);
		}
		return result;
	}

	@Override
	public ElasticDocument next() {
		ElasticDocument doc = new ElasticDocument();
		SourceRecord record = null;
		try {
			record = this.recordSetProducer.next();
			for (RecordMapper mapper : this.recordMapperList) {
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
		}
		catch (Exception e) {
			if (record == null) {
				log.error("Error obtaining next record.", e);
			}
			else {
				log.error("Error mapping record.", e);
			}
			return null;
		}
	}

	@Override
	public void configure(PlugDescription arg0) {
		log.info("WfsDscDocumentProducer: configure called.");
		this.recordSetProducer.reset();
	}

	public RecordSetProducer getRecordSetProducer() {
		return this.recordSetProducer;
	}

	public void setRecordSetProducer(RecordSetProducer recordProducer) {
		this.recordSetProducer = recordProducer;
	}

	public List<RecordMapper> getRecordMapperList() {
		return this.recordMapperList;
	}

	public void setRecordMapperList(List<RecordMapper> recordMapperList) {
		this.recordMapperList = recordMapperList;
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
