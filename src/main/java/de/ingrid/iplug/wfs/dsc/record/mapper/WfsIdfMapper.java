/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.record.mapper;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;
import de.ingrid.iplug.wfs.dsc.tools.ScriptEngine;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.WfsNamespaceContext;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * Creates a base InGrid Detail data Format (IDF) skeleton.
 * 
 * @author joachim@wemove.com
 * 
 */
public class WfsIdfMapper implements IIdfMapper {

	private File[] mappingScripts;
	private boolean compile = false;

	protected static final Logger log = Logger.getLogger(WfsIdfMapper.class);
	protected static final XPathUtils xPathUtils = new XPathUtils(new IDFNamespaceContext());

	@Override
	public void map(SourceRecord record, Document doc) throws Exception {
		if (this.mappingScripts == null) {
			log.error("Mapping scripts are not set!");
			throw new IllegalArgumentException("Mapping scripts are not set!");
		}
		if (!(record instanceof WfsCacheSourceRecord)) {
			log.error("Source Record is not a WfsCacheSourceRecord!");
			throw new IllegalArgumentException("Source Record is not a WfsCacheSourceRecord!");
		}

		WFSFeature wfsRecord = (WFSFeature)record.get(WfsCacheSourceRecord.WFS_RECORD);
		WfsNamespaceContext nsc = wfsRecord.getNamespaceContext();
		nsc.addNamespace("idf", IDFNamespaceContext.NAMESPACE_URI_IDF);
		XPathUtils xPathUtils = new XPathUtils(nsc);

		try {
			Map<String, Object> parameters = new Hashtable<String, Object>();
			parameters.put("wfsRecord", wfsRecord);
			parameters.put("document", doc);
			parameters.put("xPathUtils", xPathUtils);
			parameters.put("log", log);
			ScriptEngine.execute(this.mappingScripts, parameters, this.compile);
		} catch (Exception e) {
			log.error("Error mapping source record to idf document.", e);
			throw e;
		}
	}

	public void setMappingScripts(File[] mappingScripts) {
		this.mappingScripts = mappingScripts;
	}

	public void setCompile(boolean compile) {
		this.compile = compile;
	}
}
