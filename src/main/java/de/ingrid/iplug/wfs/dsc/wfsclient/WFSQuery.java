/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient;

import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.wfsclient.constants.OutputFormat;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.ResultType;

/**
 * Representation of a WFS query. Encapsulates the varying parts of a query.
 * Instances may be used for the GetFeatureType or the GetFeature requests.
 * Properties that are not needed for the specific request are ignored.
 * @see OpenGIS Web Feature Service Implementation Specification 1.1.0
 * @author ingo herwig <ingo@wemove.com>
 */
public interface WFSQuery {

	/**
	 * Set the output format
	 * @param schema
	 */
	public void setOutputFormat(OutputFormat format);

	/**
	 * Get the output format
	 * @return OutputFormat
	 */
	public OutputFormat getOutputFormat();

	/**
	 * Set the version
	 * @param version
	 */
	public void setVersion(String version);

	/**
	 * Get the version
	 * @return String
	 */
	public String getVersion();

	/**
	 * Set the typeName for this query
	 * @param typeName
	 */
	public void setTypeName(String typeName);

	/**
	 * Get the typeName for this query
	 * @return String
	 */
	public String getTypeName();

	/**
	 * Set the result type for this query
	 * @param resultType
	 */
	public void setResultType(ResultType resultType);

	/**
	 * Get the result type for this query
	 * @return ResultType
	 */
	public ResultType getResultType();

	/**
	 * Set the OGC filter for this query
	 * @param filter
	 */
	public void setFilter(Document filter);

	/**
	 * Get the OGC filter for this query
	 * @return Document
	 */
	public Document getFilter();

	/**
	 * Get a string representation of the OGC filter for this query
	 * @return String
	 */
	public String getFilterAsString();
}
