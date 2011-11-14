package de.ingrid.iplug.wfs.dsc.cache.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.cache.ExecutionContext;
import de.ingrid.iplug.wfs.dsc.tools.StringUtils;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSCapabilities;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQueryResult;

/**
 * This UpdateStrategy processes local files. Filter queries are not supported.
 * Each feature type is fetched in one call.
 * 
 * @author ingo@wemove.com
 */
public class LocalUpdateStrategy extends AbstractUpdateStrategy {

	final protected static Log log = LogFactory.getLog(DefaultUpdateStrategy.class);
	protected ExecutionContext context = null;

	File capabilitiesFile = null;
	File contentDirectory = null;

	/**
	 * Configuration parameter.
	 * @param capabilitiesFile The capabilitiesFile to set
	 */
	public void setCapabilitiesFile(File capabilitiesFile) {
		this.capabilitiesFile = capabilitiesFile;
	}

	/**
	 * Configuration parameter.
	 * @param contentDirectory The contentDirectory to set
	 */
	public void setContentDirectory(File contentDirectory) {
		this.contentDirectory = contentDirectory;
	}

	@Override
	public List<String> execute(ExecutionContext context) throws Exception {

		this.context = context;
		WFSFactory factory = context.getFactory();

		// get the capabilities
		Document capDoc = this.readXML(this.capabilitiesFile);
		WFSCapabilities capabilities = factory.createCapabilities();
		capabilities.initialize(capDoc);

		// get all feature types from the capabilities document
		String[] typeNames = capabilities.getFeatureTypeNames();

		List<String> allRecordIds = new ArrayList<String>();
		for (String typeName : typeNames) {
			// fetch all features for the current type
			if (log.isInfoEnabled()) {
				log.info("Fetching features of type "+typeName+"...");
			}
			allRecordIds.addAll(this.fetchRecords(factory, typeName));
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

	/**
	 * Fetch the records for the given feature type from the content directory
	 * @param factory
	 * @param typeName
	 * @return List<String> of record ids
	 * @throws Exception
	 */
	protected List<String> fetchRecords(WFSFactory factory, String typeName) throws Exception {

		List<String> fetchedRecordIds = new CopyOnWriteArrayList<String>();

		File queryResultFile = new File(this.contentDirectory.getAbsolutePath().concat(File.separator).concat(typeName).concat(".xml"));
		Document resultDoc = this.readXML(queryResultFile);
		WFSQueryResult result = factory.createQueryResult();
		result.initialize(resultDoc, null);

		int numTotal = result.getNumberOfFeatures();
		if (log.isInfoEnabled()) {
			log.info(numTotal+" record(s)");
		}
		if (numTotal > 0) {
			// process
			fetchedRecordIds.addAll(this.processResult(result, true));
		}
		return fetchedRecordIds;
	}

	/**
	 * Create a Document instance from the content of the given file
	 * @param file
	 * @return Document
	 * @throws Exception
	 */
	protected Document readXML(File file) throws Exception {
		StringBuilder content = new StringBuilder();
		BufferedReader input = new BufferedReader(new FileReader(file));
		try {
			String line = null;
			while((line = input.readLine()) != null) {
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}
			input.close();
			input = null;

			Document document = StringUtils.stringToDocument(content.toString());
			return document;
		}
		finally {
			if (input != null) {
				input.close();
			}
		}
	}
}
