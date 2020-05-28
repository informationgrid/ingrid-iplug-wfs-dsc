/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.cache.impl;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.iplug.wfs.dsc.cache.Cache;
import de.ingrid.iplug.wfs.dsc.cache.ExecutionContext;
import de.ingrid.iplug.wfs.dsc.cache.UpdateStrategy;
import de.ingrid.iplug.wfs.dsc.tools.StringUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSClient;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQueryResult;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.ResultType;
import de.ingrid.utils.statusprovider.StatusProvider;
import de.ingrid.utils.statusprovider.StatusProvider.Classification;
import de.ingrid.utils.statusprovider.StatusProviderService;

public abstract class AbstractUpdateStrategy implements UpdateStrategy {

	@Autowired
	private StatusProviderService statusProviderService;
	private StatusProvider statusProvider;

	DocumentBuilder docBuilder = null;

	// The time in msec the strategy pauses between requests to the WFS server.
	int requestPause = 1000;

	/**
	 * Set the time in msec the strategy pauses between requests to the WFS server.
	 *
	 * @param requestPause the requestPause to set
	 */
	public void setRequestPause(int requestPause) {
		this.requestPause = requestPause;
	}

	/**
	 * Create a filter Document from a filter string. Replace any filter
	 * variables. TODO: if there should be more variables, this could be done
	 * more generic
	 *
	 * @param filterStr
	 * @return Document
	 * @throws Exception
	 */
	protected Document createFilterDocument(String filterStr) throws Exception {

		ExecutionContext context = this.getExecutionContext();

		if (this.docBuilder == null) {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			this.docBuilder = docBuilderFactory.newDocumentBuilder();
		}

		// replace last update date variable
		Pattern lastUpdateDatePattern = Pattern.compile("\\{LAST_UPDATE_DATE\\}", Pattern.MULTILINE);
		Matcher matcher = lastUpdateDatePattern.matcher(filterStr);
		if (matcher.find()) {
			Date lastUpdateDate = context.getLastExecutionDate();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			filterStr = matcher.replaceAll(df.format(lastUpdateDate));
		}

		return this.docBuilder.parse(new InputSource(new StringReader(filterStr)));
	}

	/**
	 * Fetch all records that satisfy the given filter using the GetFeature method and
	 * return the ids and put them into the cache
	 * @note This method guarantees to query the server without a constraint, if the
	 * provided filter set is empty
	 *
	 * @param client The WSFClient to use
	 * @param typeName The feature type of the records
	 * @param filterSet The filter set used to select the records
	 * @param doCache Determines whether to cache the records or not
	 * @return A list of ids of the fetched records
	 * @throws Exception
	 */
	protected List<String> fetchRecords(WFSClient client, String typeName, Set<Document> filterSet,
			boolean doCache) throws Exception {

		return fetchRecordsPaged(client, typeName, filterSet, doCache, null, null);
	}

	/**
	 * Fetch all records PAGED that satisfy the given filter using the GetFeature method and
	 * return the ids and put them into the cache
	 * @note This method guarantees to query the server without a constraint, if the
	 * provided filter set is empty
	 *
	 * @param client The WSFClient to use
	 * @param typeName The feature type of the records
	 * @param filterSet The filter set used to select the records
	 * @param doCache Determines whether to cache the records or not
	 * @param maxNumFeatures maximum number of features to fetch. PASS NULL IF ALL FEATURES !
	 * @param startIndex where to start fetching ? PASS NULL IF ALL FEATURES !
	 * @return A list of ids of the fetched records
	 * @throws Exception
	 */
	protected List<String> fetchRecordsPaged(WFSClient client, String typeName, Set<Document> filterSet,
			boolean doCache, Integer maxNumFeatures, Integer startIndex) throws Exception {

		WFSFactory factory = client.getFactory();
		Log log = this.getLog();

		// if the filter set is empty, we add a null at least
		// this causes execution of the iteration below, but
		// but will not add a filter definition to the request
		if (filterSet == null) {
			filterSet = new HashSet<Document>();
		}
		if (filterSet.size() == 0) {
			filterSet.add(null);
		}
		// variables for complete fetch process
		List<String> fetchedRecordIds = new CopyOnWriteArrayList<String>();

		// iterate over all filters
		int filterIndex = 1;
		for (Document filter : filterSet) {
			String filterString = "''";
			if (filter != null) {
				filterString = StringUtils.nodeToString(filter).replace("\n", "");
			}
			if (log.isDebugEnabled()) {
				log.debug("Processing filter "+filterIndex+": "+filterString+".");
			}
			// variables for current fetch process (current filter)
			int numCurrentTotal = 0;
			List<String> currentFetchedRecordIds = new ArrayList<String>();

			// create the query
			WFSQuery query = factory.createQuery();
			query.setTypeName(typeName);
			query.setFilter(filter);
			query.setResultType(ResultType.RESULTS);
			query.setMaxFeatures(maxNumFeatures);
			query.setStartIndex(startIndex);

			// do request
			WFSQueryResult result = client.getFeature(query);
			numCurrentTotal = result.getNumberOfFeatures();
			if (log.isDebugEnabled()) {
				log.debug("Fetched "+numCurrentTotal+" record(s) from chunk with filter "+filterIndex+": "+filterString+".");
			}
			if (numCurrentTotal > 0) {
				// process
				currentFetchedRecordIds.addAll(this.processResult(result, doCache));
			} else {
				String msg = "Fetched 0 features of type '" + typeName +"' from chunk " + startIndex + "-" + (startIndex + maxNumFeatures) + " with filter "+filterIndex+": "+filterString+".";
				log.error(msg);
				throw new RuntimeException(msg);
			}

			// collect record ids
			fetchedRecordIds.addAll(currentFetchedRecordIds);
			filterIndex++;
		}
		return fetchedRecordIds;
	}

	/**
	 * Fetch total number of records of given feature type.
	 *
	 * @param client The WSFClient to use
	 * @param typeName The feature type of the records
	 * @return the total number of records
	 * @throws Exception
	 */
	protected int fetchTotalNumRecords(WFSClient client, String typeName) throws Exception {
		WFSFactory factory = client.getFactory();

		// create the query
		WFSQuery query = factory.createQuery();
		query.setTypeName(typeName);
		// number of HITS, not RESULTS
		query.setResultType(ResultType.HITS);

		// do request
		WFSQueryResult result = client.getFeature(query);

		return result.getNumberOfFeaturesTotal();
	}

	/**
	 * Process a fetched search result (collect ids and cache records)
	 *
	 * @param result The search result
	 * @param doCache Determines whether to cache the record or not
	 * @return The list of ids of the fetched records
	 * @throws Exception
	 */
	protected List<String> processResult(WFSQueryResult result, boolean doCache)
			throws Exception {

		Cache cache = this.getExecutionContext().getCache();
		Log log = this.getLog();

		List<String> fetchedRecordIds = new ArrayList<String>();
		for (WFSFeature record : result.getFeatureList()) {
			String id = record.getId();

			if (log.isDebugEnabled()) {
				log.debug("Fetched record: "+id);
			}
			if (fetchedRecordIds.contains(id)) {
				log.warn("Duplicated id: "+id+". Overriding previous entry.");
			}
			fetchedRecordIds.add(id);

			// cache only if requested
			if (doCache) {
				cache.putRecord(record);
			}
		}
		String msg = "Fetched "+fetchedRecordIds.size()+" of "+result.getNumberOfFeatures();
		if (fetchedRecordIds.size() != result.getNumberOfFeatures()) {
			log.warn("ERROR?: Could not fetch all records of chunk -> " + msg);
		} else {
			if (log.isDebugEnabled()) {
				log.debug(msg);
			}
		}
		return fetchedRecordIds;
	}

	/**
	 * Add state to the status provider
	 * @param key
	 * @param value
	 * @param isError
	 */
	protected void updateState(final String key, final String value, final boolean isError) {
		if (statusProvider == null && statusProviderService != null) {
			statusProvider = statusProviderService.getDefaultStatusProvider();
		}
		if (statusProvider != null) {
			statusProvider.addState(key, value, isError ? Classification.ERROR : Classification.INFO);
		}
	}
}
