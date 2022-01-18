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
/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
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
 * The update job.
 * @author ingo herwig <ingo@wemove.com>
 */
public class DefaultUpdateStrategy extends AbstractUpdateStrategy {

	final protected static Log log = LogFactory.getLog(DefaultUpdateStrategy.class);

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
		WFSCapabilities capabilities = null;
		try {
			capabilities = client.getCapabilities();
		} catch (Exception ex) {
			updateState("ERROR_NEXT", "Could not fetch service URL. Index will be empty!", true);
			throw ex;
		}
		String[] typeNames = capabilities.getFeatureTypeNames();

		updateState("FETCHED_FEATURES", "Fetching " + typeNames.length + " featuretypes.", false);

		List<String> allRecordIds = new ArrayList<String>();
//		int numFeatureTypesFetched=0;
		for (String typeName : typeNames) {
			// fetch all features for the current type
			if (log.isInfoEnabled()) {
				log.info("Fetching features of type "+typeName+"...");
			}

			updateState("FETCH_FEATURE_" + typeName, "Fetching featuretype '" + typeName + "' ...", false);

			try {
				List<String> l = this.fetchRecords(client, typeName, filterSet, true);
				allRecordIds.addAll(l);
				updateState("FETCH_FEATURE_" + typeName, "Fetched " + l.size() + " features of type '" + typeName + "'.", false);
			} catch (Exception ex) {
				String msg = "Problems fetching features of type '" + typeName + "', we skip these ones !";
				log.error(msg, ex);
				updateState("ERROR_FEATURE_" + typeName, msg, true);
			}

			// activate this for local testing !
/*
			numFeatureTypesFetched++;
			if (numFeatureTypesFetched > 5) {
				break;
			}
*/
		}
		return allRecordIds;
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
