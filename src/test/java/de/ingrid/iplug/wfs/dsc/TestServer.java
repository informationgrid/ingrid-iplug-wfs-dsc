/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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

package de.ingrid.iplug.wfs.dsc;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.OutputFormat;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.ResultType;

public enum TestServer {

	PEGELONLINE ("http://www.pegelonline.wsv.de:80/webservices/gis/aktuell/wfs", OutputFormat.TEXT_XML_GML, "1.1.0", ResultType.RESULTS);

	private String capUrl;
	private OutputFormat outputFormat;
	private String version;
	private ResultType resultType;

	TestServer(String capUrl, OutputFormat outputFormat, String version, ResultType resultType) {
		this.capUrl = capUrl;
		this.outputFormat = outputFormat;
		this.version = version;
		this.resultType = resultType;
	}

	public WFSQuery getQuery(WFSQuery query) {
		query.setOutputFormat(this.outputFormat);
		query.setVersion(this.version);
		query.setResultType(this.resultType);
		return query;
	}

	public String getCapUrl() {
		return this.capUrl;
	}
}
