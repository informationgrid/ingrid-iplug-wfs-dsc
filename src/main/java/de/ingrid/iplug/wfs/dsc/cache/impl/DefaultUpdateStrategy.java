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
		client.configure(factory);

		// get all feature types from the capabilities document
		WFSCapabilities capabilities = client.getCapabilities();
		String[] typeNames = capabilities.getFeatureTypeNames();

		List<String> allRecordIds = new ArrayList<String>();
		for (String typeName : typeNames) {
			// fetch all features for the current type
			if (log.isInfoEnabled()) {
				log.info("Fetching features of type "+typeName+"...");
			}
			allRecordIds.addAll(this.fetchRecords(client, typeName, filterSet, true));
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
