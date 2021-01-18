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
/*
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.cache.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.cache.ExecutionContext;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSCapabilities;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSClient;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;

/**
 * This UpdateStrategy fetches each feature type via a paging mechanism and NOT in one call !
 * This avoids memory problems !
 */
public class PagingUpdateStrategy extends AbstractUpdateStrategy {

	final protected static Log log = LogFactory.getLog(PagingUpdateStrategy.class);

	/**	The maximum number of features to fetch in one call. */
	int maxFeatures = 1000;

	protected ExecutionContext context = null;

	@Override
	public List<String> execute(ExecutionContext context) throws Exception {

		this.context = context;
		WFSFactory factory = context.getFactory();

		// prepare the filter set
		Set<Document> filterSet = new HashSet<Document>();
		for (String filterStr : context.getFilterStrSet()) {
			Document filterDoc = this.createFilterDocument(filterStr);
			filterSet.add(filterDoc);
		}

		// set up client
		WFSClient client = factory.createClient();

		// get all feature types from the capabilities document
		WFSCapabilities capabilities = client.getCapabilities();
		String[] typeNames = capabilities.getFeatureTypeNames();

		List<String> allRecordIds = new ArrayList<String>();

		updateState("FETCHED_FEATURES", "Start fetching " + typeNames.length + " featuretype(s).", false);

//		int numFeatureTypesFetched=0;
		for (String typeName : typeNames) {
			if (log.isInfoEnabled()) {
				log.info("Fetching features of type "+typeName+"...");
			}

			// fetch total number of features of the current type
			int totalNumRecords = 0;
			try {
				totalNumRecords = this.fetchTotalNumRecords(client, typeName);
				if (log.isInfoEnabled()) {
					log.info("Start fetching " + totalNumRecords + " features of type '" + typeName + "'");
				}
				updateState("FETCH_FEATURE_INTRO_" + typeName, "Start fetching " + totalNumRecords + " features of type '" + typeName + "'", false);

			} catch (Exception ex) {
				log.error("Problems fetching total number of features of type '" + typeName + "', we skip this feature type !", ex);
				updateState("FETCH_FEATURE_INTRO_" + typeName, "Problems fetching total number of features of type '" + typeName + "', we skip this feature type !", false);
				continue;
			}

			// fetch all features PAGED !

			int numRequests = (totalNumRecords / maxFeatures);
			if (totalNumRecords % maxFeatures > 0) {
				numRequests++;
			}

			boolean errorInFeaturetype = false;
			for (int i=0; i<numRequests; i++) {
				boolean errorInChunk = false;
				int startIndex = i * maxFeatures;
				if (log.isDebugEnabled()) {
					log.debug("Fetching features of type "+typeName+", maxFeatures=" +
						maxFeatures + ", startIndex=" + startIndex + " ...");
				}
				updateState("FETCH_FEATURE_" + typeName, "Fetching features of type '" + typeName + "' ... " + startIndex + "-" + (startIndex + maxFeatures), false);
				try {
					List<String> fetchedIds = fetchRecordsPaged(client, typeName, filterSet, true, maxFeatures, startIndex);
					allRecordIds.addAll(fetchedIds);

				} catch (Exception ex) {
					String msg = "Problems fetching features of type '" + typeName +"', maxFeatures=" +
							maxFeatures + ", startIndex=" + startIndex + ", we skip these ones !";
					// only log as error with full exception with first exception to avoid lots of repeating errors / exceptions in log file with every chunk !
					if (!errorInChunk) {
						errorInChunk = true;
						errorInFeaturetype = true;
						log.error(msg, ex);
						log.error("NOTICE: We do NOT log further full exceptions of this feature type to avoid huge log output ! Switch to DEBUG to see full exceptions !");
					} else {
						log.error(msg + " -> " + ex.getMessage());
						// log as debug to avoid huge chunk of messages !
						log.debug("Caused Exception:", ex);
					}
					updateState("FETCH_FEATURE_" + typeName, "PROBLEMS fetching features of type '" + typeName + "', we skip these ones: " + startIndex + "-" + (startIndex + maxFeatures), false);
				}
			}

			if (errorInFeaturetype) {
				updateState("FETCH_FEATURE_" + typeName, "PROBLEMS fetching features of type '" + typeName + "' !", false);
			} else {
				updateState("FETCH_FEATURE_" + typeName, "OK, fetched all features of type '" + typeName + "' !", false);
			}

			// activate this for local testing of restricted number of feature types !
/*
			numFeatureTypesFetched++;
			if (numFeatureTypesFetched > 5) {
				break;
			}
*/
		}
		return allRecordIds;
	}

	/**
	 * Set the maximum number of features to fetch in one call to the WFS server.
	 */
	public void setMaxFeatures(int maxFeatures) {
		this.maxFeatures = maxFeatures;
	}

	@Override
	public ExecutionContext getExecutionContext() {
		return this.context;
	}

	@Override
	public Log getLog() {
		return log;
	}
}
