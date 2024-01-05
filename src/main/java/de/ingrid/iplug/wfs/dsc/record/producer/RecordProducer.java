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
package de.ingrid.iplug.wfs.dsc.record.producer;

import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.utils.ElasticDocument;

/**
 * Defines all aspects a record producer must implement. The record producer is
 * used to retrieve ONE record from the data source for further processing.
 *
 * @author joachim@wemove.com
 */
public interface RecordProducer {

	/**
	 * Open the data source. The functionality depends on the type of data
	 * source.
	 *
	 * The parameters in {@link SourceRecord} returned by {@link getRecord} may
	 * contain a reference to the data source, so that the following mapping
	 * step can access the data source as well.
	 *
	 */
	void openDatasource();

	/**
	 * Close the data source.
	 */
	void closeDatasource();

	/**
	 * Get a record from the data source. How the record must be derived from
	 * the fields of the lucene document.
	 *
	 * @param document
	 * @return
	 */
	SourceRecord getRecord(ElasticDocument document);
}
