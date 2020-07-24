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
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.utils.statusprovider.StatusProvider.Classification;
import de.ingrid.utils.statusprovider.StatusProviderService;

/**
 * The update job.
 * @author ingo herwig <ingo@wemove.com>
 */
public class UpdateJob {

	final protected static Log log = LogFactory.getLog(UpdateJob.class);
	final private static String DATE_FILENAME = "updatejob.dat";
	final private static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private WFSFactory factory;

	private Cache cache;

	private Set<String> filterStrSet;

	private UpdateStrategy updateStrategy;

	@Autowired
	private StatusProviderService statusProviderService;

	/**
	 * Constructor
	 */
	public UpdateJob() {}

	public void init() {
		this.cache.configure(this.factory);
	}

	/**
	 * Execute the update job.
	 * @throws Exception
	 */
	public void execute() throws Exception {
		Date start = new Date();

		// get cached record ids (for later removal of records that do not exist anymore)
		Set<String> cachedRecordIds = this.cache.getCachedRecordIds();

		Date lastExecutionDate = this.getLastExecutionDate();
		log.info("Starting update job using strategy '"+this.updateStrategy.getClass().getSimpleName()+"'");
		log.info("Last execution was on "+DATEFORMAT.format(lastExecutionDate));

		// create the execution context
		ExecutionContext ctx = new ExecutionContext();
		ctx.setFactory(this.factory);
		ctx.setCache(this.cache);
		ctx.setFilterStrSet(this.filterStrSet);
		ctx.setLastExecutionDate(lastExecutionDate);

		// delegate execution to the strategy
		List<String> allRecordIds = this.updateStrategy.execute(ctx);

		// remove deprecated records
		for (String cachedRecordId : cachedRecordIds) {
			if (!allRecordIds.contains(cachedRecordId))
				this.cache.removeRecord(cachedRecordId);
		}

		// write the execution date as last operation
		// this is the start date, to make sure that the next execution will fetch
		// all modified records from the job execution start on
		this.writeLastExecutionDate(start);

		// duplicates are filtered out automatically by the cache, so there is no need for action here
		int duplicates = allRecordIds.size() - new HashSet<String>(allRecordIds).size();

		// summary
		Date end = new Date();
		long diff = end.getTime()-start.getTime();
		String msg = "Fetched " + allRecordIds.size() + " records from " + this.factory.getServiceUrl() + ". Duplicates: " + duplicates;
		updateState("FETCH", msg, false);
		log.info(msg);
		log.info("Job executed within "+diff+" ms.");
	}

	/**
	 * Get the last exectution date of this job. Returns 1970-01-01 00:00:00 if an error occurs.
	 * @return Date
	 */
	public Date getLastExecutionDate() {
		File dateFile = new File(DATE_FILENAME);

		try {
			BufferedReader input = new BufferedReader(new FileReader(dateFile));
			// we expect only one line with the date string
			String line = input.readLine();
			if (line != null) {
				Date date = DATEFORMAT.parse(line.trim());
				input.close();
				return date;
			}
			input.close();
		}
		catch (Exception e) {
			log.warn("Could not read from "+DATE_FILENAME+". " +
					"The update job fetches all records.");
		}
		// return the minimum date if no date could be found
		return new Date(0);
	}

	/**
	 * Write the last exectution date of this job.
	 * @param Date
	 */
	public void writeLastExecutionDate(Date date) {
		File dateFile = new File(DATE_FILENAME);

		try {
			Writer output = new BufferedWriter(new FileWriter(dateFile));
			output.write(DATEFORMAT.format(date));
			output.close();
		}
		catch (Exception e) {
			// delete the date file to make sure we do not use a corrupted version
			if (dateFile.exists())
				dateFile.delete();
			log.warn("Could not write to "+DATE_FILENAME+". " +
					"The update job fetches all records next time.");
		}
	}

	public UpdateStrategy getUpdateStrategy() {
		return this.updateStrategy;
	}

	public void setUpdateStrategy(UpdateStrategy updateStrategy) {
		this.updateStrategy = updateStrategy;
	}

	public WFSFactory getFactory() {
		return this.factory;
	}

	public void setFactory(WFSFactory factory) {
		this.factory = factory;
	}

	public Cache getCache() {
		return this.cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public Set<String> getFilterStrSet() {
		return this.filterStrSet;
	}

	public void setFilterStrSet(Set<String> filterStrSet) {
		this.filterStrSet = filterStrSet;
	}

	/**
	 * Add state to the status provider
	 * @param key
	 * @param value
	 * @param isError
	 */
	protected void updateState(final String key, final String value, final boolean isError) {
		if (statusProviderService != null) {
			statusProviderService.getDefaultStatusProvider().addState(key, value, isError ? Classification.ERROR : Classification.INFO);
		}
	}
}
