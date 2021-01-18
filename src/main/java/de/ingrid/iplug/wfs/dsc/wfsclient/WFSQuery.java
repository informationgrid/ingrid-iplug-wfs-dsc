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
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient;

import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.wfsclient.constants.OutputFormat;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.ResultType;

/**
 * Representation of a WFS query. Encapsulates the varying parts of a query.
 * Instances may be used for the DescribeFeatureType or the GetFeature requests.
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
	 * Paging: Set the maximum number of features to fetch for this query.
	 * @param maxFeatures PASS NULL IF ALL FEATURES !
	 */
	public void setMaxFeatures(Integer maxFeatures);

	/**
	 * Paging: Get the maximum number of features to fetch for this query.
	 * @return Integer NULL IF ALL FEATURES !
	 */
	public Integer getMaxFeatures();

	/**
	 * Paging: Set the start index of features to fetch for this query
	 * @param startIndex PASS NULL IF ALL FEATURES !
	 */
	public void setStartIndex(Integer startIndex);

	/**
	 * Paging: Get the start index of features to fetch for this query
	 * @return Integer NULL IF ALL FEATURES !
	 */
	public Integer getStartIndex();

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
