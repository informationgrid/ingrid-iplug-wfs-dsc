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
package de.ingrid.iplug.wfs.dsc.record.producer.impl;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.wfs.dsc.cache.Cache;
import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.om.WfsSourceRecord;
import de.ingrid.iplug.wfs.dsc.record.producer.RecordProducer;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.PlugDescription;

/**
 * This class retrieves a record from a database data source. It retrieves an
 * database id from a lucene document ({@link getRecord}) and creates a
 * {@link DatabaseSourceRecord} containing the database ID that identifies the
 * database record and the open connection to the database.
 *
 * The database connection is configured via the {@link PlugDescription}.
 *
 * @author joachim@wemove.com
 */
public class WfsRecordProducer implements RecordProducer {

	final private static Log log = LogFactory.getLog(WfsRecordProducer.class);

	private Cache cache;

	private WFSFactory factory;

	@Override
	public SourceRecord getRecord(ElasticDocument doc) {
		// TODO make the field configurable
		String field = (String) doc.get("t01_object.obj_id");
		try {
			return new WfsSourceRecord(this.cache.getRecord(field));
		} catch (IOException e) {
			log.error("Error reading record '" + field + "' from cache '"
					+ this.cache.toString() + "'.");
		}
		return null;
	}

	@Override
	public void closeDatasource() {
	}

	@Override
	public void openDatasource() {
		this.cache.configure(this.factory);
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
}
