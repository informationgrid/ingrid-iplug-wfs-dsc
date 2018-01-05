/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSCapabilities;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSClient;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFactory;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeatureType;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQuery;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSQueryResult;
import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;

public class GenericClient implements WFSClient {

	final protected static Log log = LogFactory.getLog(GenericClient.class);

	protected WFSFactory factory;
	protected WFSCapabilities capabilities;

	@Override
	public void configure(WFSFactory factory) {
		this.factory = factory;
	}

	@Override
	public WFSFactory getFactory() {
		return this.factory;
	}

	@Override
	public WFSCapabilities getCapabilities() throws Exception {
		if (this.capabilities == null) {
			if (this.factory != null) {
				this.capabilities = this.factory.createCapabilities();

				String serviceUrl = this.factory.getServiceUrl();
				Document capDoc = this.factory.createRequest(Operation.GET_CAPABILITIES).doGetCapabilities(serviceUrl);
				this.capabilities.initialize(capDoc);
			}
			else
				throw new RuntimeException("WFSClient is not configured properly. Make sure to call WFSClient.configure.");
		}
		return this.capabilities;
	}

	@Override
	public WFSFeatureType describeFeatureType() throws Exception {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public WFSQueryResult getFeature(WFSQuery query) throws Exception {
		if (this.factory != null) {
			WFSCapabilities cap = this.getCapabilities();
			String opUrl = cap.getOperationUrl(Operation.GET_FEATURE);
			if (opUrl == null) {
				opUrl = this.factory.getServiceUrl();
			}
			Document responseDoc = this.factory.createRequest(Operation.GET_FEATURE).doGetFeature(opUrl, query);

			WFSQueryResult result = this.factory.createQueryResult();
			result.initialize(responseDoc, query);
			return result;
		}
		else
			throw new RuntimeException("WFSClient is not configured properly. Make sure to call WFSClient.configure.");
	}
}
