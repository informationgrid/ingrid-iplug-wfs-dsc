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

package de.ingrid.iplug.wfs.dsc.wfsclient.impl;

import java.io.Serializable;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSConstants;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.OutputFormat;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.ResultType;
import de.ingrid.utils.xml.XMLUtils;

public class GenericQuery implements Serializable, WFSQuery {

	private static final long serialVersionUID = GenericQuery.class.getName().hashCode();

	protected OutputFormat outputFormat = null;
	protected String version = "";
	protected String typeName = "";
	protected Document filter = null;

	// Paging
	protected Integer maxFeatures = null;
	protected Integer startIndex = null;

	/**
	 * GetFeature specific
	 */
	protected ResultType resultType = null;

	/**
	 * Constructor
	 */
	public GenericQuery() {
		// set defaults according to
		// OpenGIS Web Feature Service Implementation Specification 1.1.0 - 14.4
		this.outputFormat = OutputFormat.TEXT_XML_GML;
		this.version = WFSConstants.VERSION_1_1_0;
		this.resultType = ResultType.RESULTS;
	}

	@Override
	public void setOutputFormat(OutputFormat format) {
		this.outputFormat = format;
	}

	@Override
	public OutputFormat getOutputFormat() {
		return this.outputFormat;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String getTypeName() {
		return this.typeName;
	}

	@Override
	public void setResultType(ResultType resultType) {
		this.resultType  = resultType;
	}

	@Override
	public ResultType getResultType() {
		return this.resultType;
	}

	@Override
	public void setFilter(Document filter) {
		this.filter = filter;
	}

	@Override
	public Document getFilter() {
		return this.filter;
	}

	@Override
	public String getFilterAsString() {
		if (this.filter != null) {
			try {
				return XMLUtils.toString(this.filter);
			} catch (TransformerException e) {}
		}
		return "";
	}

	@Override
	public void setMaxFeatures(Integer maxFeatures) {
		this.maxFeatures  = maxFeatures;
	}

	@Override
	public Integer getMaxFeatures() {
		return this.maxFeatures;
	}

	@Override
	public void setStartIndex(Integer startIndex) {
		this.startIndex  = startIndex;
	}

	@Override
	public Integer getStartIndex() {
		return this.startIndex;
	}
}
