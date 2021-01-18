/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
package de.ingrid.iplug.wfs.dsc.index.producer.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.wfs.dsc.index.producer.RecordSetProducer;
import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.om.WfsSourceRecord;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSCapabilities;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSClient;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeatureType;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;

/**
 * FeatureTypeRecordSetProducer provides WFS feature types directly fetched
 * from an external source.
 *
 * @author ingo@wemove.com
 */
public class FeatureTypeRecordSetProducer implements RecordSetProducer {

	private WFSFactory factory;
	private WFSClient client;

	private Iterator<String> typeNameIterator;

	final private static Log log = LogFactory.getLog(FeatureTypeRecordSetProducer.class);

	/**
	 * Constructor
	 */
	public FeatureTypeRecordSetProducer() {
		log.info("FeatureTypeRecordSetProducer started.");
	}

	/**
	 * Set the factory for making WFS requests
	 * @param factory
	 */
	public void setFactory(WFSFactory factory) {
		this.factory = factory;
	}

	@Override
	public void reset() {
		try {
			// initialize iterator for WFS feature type names
			log.info("Initializing feature type iterator...");

			// set up client
			client = factory.createClient();

			// get all feature types from the capabilities document
			WFSCapabilities capabilities = client.getCapabilities();
			List<String> typeNames = Arrays.asList(capabilities.getFeatureTypeNames());
			typeNameIterator = typeNames.iterator();
			log.info("Found "+typeNames.size()+" feature type(s).");
		}
		catch (Exception e) {
			log.error("Error obtaining information about a next record. Skip all records.", e);
			throw new RuntimeException("Error harvesting WFS datasource");
		}
	}

	@Override
	public boolean hasNext() {
		if (typeNameIterator == null) {
			this.reset();
		}

		return typeNameIterator.hasNext();
	}

	@Override
	public SourceRecord next() {
		if (typeNameIterator == null) {
			this.reset();
		}

		SourceRecord result = null;
		if (typeNameIterator.hasNext()) {
			String currentTypeName = typeNameIterator.next();
			try {
				// get the current feature type record
				WFSQuery query = factory.createQuery();
				query.setTypeName(currentTypeName);

				WFSFeatureType featureType = client.describeFeatureType(query);
				result = new WfsSourceRecord(featureType);
			}
			catch (Exception e) {
				log.error("Error reading record '" + currentTypeName + "'.", e);
			}
		}
		else {
			throw new NoSuchElementException();
		}
		return result;
	}
}
