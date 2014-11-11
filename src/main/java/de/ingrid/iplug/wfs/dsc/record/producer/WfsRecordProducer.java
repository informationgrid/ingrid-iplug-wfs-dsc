/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.iplug.wfs.dsc.record.producer;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import de.ingrid.iplug.wfs.dsc.cache.Cache;
import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.utils.PlugDescription;

/**
 * This class retrieves a record from a database data source. It retrieves an
 * database id from a lucene document ({@link getRecord}) and creates a
 * {@link DatabaseSourceRecord} containing the database ID that identifies the
 * database record and the open connection to the database.
 * 
 * The database connection is configured via the {@link PlugDescription}.
 * 
 * 
 * @author joachim@wemove.com
 * 
 */
public class WfsRecordProducer implements IRecordProducer {

	final private static Log log = LogFactory.getLog(WfsRecordProducer.class);

	Cache cache;

	WFSFactory factory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ingrid.iplug.dsc.record.IRecordProducer#getRecord(org.apache.lucene
	 * .document.Document)
	 */
	@Override
	public SourceRecord getRecord(Document doc) {
		// TODO make the field configurable
		Field field = doc.getField("t01_object.obj_id");
		try {
			return new WfsCacheSourceRecord(this.cache.getRecord(field.stringValue()));
		} catch (IOException e) {
			log.error("Error reading record '" + field.stringValue() + "' from cache '"
					+ this.cache.toString() + "'.");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.iplug.dsc.record.IRecordProducer#closeDatasource()
	 */
	@Override
	public void closeDatasource() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.iplug.dsc.record.IRecordProducer#openDatasource()
	 */
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
