/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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

import java.io.Serializable;
import java.util.Map;

import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Operation;
import de.ingrid.iplug.wfs.dsc.wfsclient.impl.GenericClient;
import de.ingrid.utils.IConfigurable;
import de.ingrid.utils.PlugDescription;

/**
 * This class is used to create all concrete WFS related classes
 * which may vary between the different WFS servers.
 * The specific implementation is configured in spring.xml.
 * @author ingo herwig <ingo@wemove.com>
 */
public class WFSFactory implements IConfigurable, Serializable {

	private static final long serialVersionUID = WFSFactory.class.getName().hashCode();
	private static final String serviceUrlKey = "serviceUrl";

	private PlugDescription plugDescription;

	private String clientImpl;
	private Map<String, String> requestImpl;
	private String capabilitiesImpl;
	private String featureTypeImpl;
	private String queryImpl;
	private String queryResultImpl;
	private String featureImpl;
	private WFSQuery queryTemplate;
	private WFSFeature featureTemplate;

	/**
	 * Get the service url.
	 * @return The service url
	 * @throws RuntimeException
	 */
	public String getServiceUrl() throws Exception {
		if (this.plugDescription != null) {
			if (this.plugDescription.get(serviceUrlKey) != null) {
				return (String) this.plugDescription.get(serviceUrlKey);
			} else {
				throw new RuntimeException("WFSFactory is not configured properly. Parameter 'serviceUrl' is missing in PlugDescription.");
			}
		} else {
			throw new RuntimeException("WFSFactory is not configured properly. Parameter 'plugDescription' is missing.");
		}
	}


	/**
	 * Set the WFSClient implementation
	 * @param clientImpl
	 */
	public void setClientImpl(String clientImpl) {
		this.clientImpl = clientImpl;
	}

	/**
	 * Set the WFSRequest implementation
	 * @param requestImpl
	 */
	public void setRequestImpl(Map<String, String> requestImpl) {
		this.requestImpl = requestImpl;
	}

	/**
	 * Set the WFSCapabilities implementation
	 * @param capabilitiesImpl
	 */
	public void setCapabilitiesImpl(String capabilitiesImpl) {
		this.capabilitiesImpl = capabilitiesImpl;
	}

	/**
	 * Set the WFSFeatureType implementation
	 * @param featureTypeImpl
	 */
	public void setFeatureTypeImpl(String featureTypeImpl) {
		this.featureTypeImpl = featureTypeImpl;
	}

	/**
	 * Set the WFSQuery implementation
	 * @param queryImpl
	 */
	public void setQueryImpl(String queryImpl) {
		this.queryImpl = queryImpl;
	}

	/**
	 * Set the WFSQueryResult implementation
	 * @param queryResultImpl
	 */
	public void setQueryResultImpl(String queryResultImpl) {
		this.queryResultImpl = queryResultImpl;
	}

	/**
	 * Set the WFSFeature implementation
	 * @param featureImpl
	 */
	public void setFeatureImpl(String featureImpl) {
		this.featureImpl = featureImpl;
	}

	/**
	 * Set the query template, which will be used when creating queries
	 * @param queryTemplate
	 */
	public void setQueryTemplate(WFSQuery queryTemplate) {
		this.queryTemplate = queryTemplate;
	}

	/**
	 * Get the query template, which will be used when creating queries
	 * @return WFSQuery
	 */
	public WFSQuery getQueryTemplate() {
		return this.queryTemplate;
	}

	/**
	 * Set the feature template, which will be used when creating features
	 * @param featureTemplate
	 */
	public void setFeatureTemplate(WFSFeature featureTemplate) {
		this.featureTemplate = featureTemplate;
	}

	/**
	 * Get the feature template, which will be used when creating features
	 * @return WFSFeature
	 */
	public WFSFeature getFeatureTemplate() {
		return this.featureTemplate;
	}

	/**
	 * Factory methods
	 */

	/**
	 * Create a WFSClient.
	 * @return A concrete WFSClient instance
	 */
	public WFSClient createClient() throws RuntimeException {
		GenericClient client;
		try {
			client = (GenericClient)Class.forName(this.clientImpl).newInstance();
			client.configure(this);
		} catch (Exception e) {
			throw new RuntimeException("WFSFactory is not configured properly. Parameter 'clientImpl' is missing or wrong.");
		}
		return client;
	}

	/**
	 * Create a WFSRequest.
	 * @return A concrete WFSRequest instance
	 */
	public WFSRequest createRequest(Operation op) throws RuntimeException {
		WFSRequest request;
		try {
			request = (WFSRequest)Class.forName(this.requestImpl.get(op.toString()).toString()).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("WFSFactory is not configured properly. Parameter 'requestImpl' is missing or wrong. No value found for operation "+op+".");
		}
		return request;
	}

	/**
	 * Create a WFSCapabilities.
	 * @return A concrete WFSCapabilities instance
	 */
	public WFSCapabilities createCapabilities() throws RuntimeException {
		WFSCapabilities capabilities;
		try {
			capabilities = (WFSCapabilities)Class.forName(this.capabilitiesImpl).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("WFSFactory is not configured properly. Parameter 'capabilitiesImpl' is missing or wrong.");
		}
		return capabilities;
	}

	/**
	 * Create a WFSFeatureType.
	 * @return A concrete WFSFeatureType instance
	 */
	public WFSFeatureType createFeatureType() throws RuntimeException {
		WFSFeatureType featureType;
		try {
			featureType = (WFSFeatureType)Class.forName(this.featureTypeImpl).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("WFSFactory is not configured properly. Parameter 'featureTypeImpl' is missing or wrong.");
		}
		return featureType;
	}

	/**
	 * Create a WFSQuery.
	 * @return A concrete WFSQuery instance
	 */
	public WFSQuery createQuery() throws RuntimeException {
		WFSQuery query;
		try {
			query = (WFSQuery)Class.forName(this.queryImpl).newInstance();

			// set default config values from the template query
			if (this.queryTemplate != null) {
				query.setOutputFormat(this.queryTemplate.getOutputFormat());
				query.setVersion(this.queryTemplate.getVersion());
				query.setResultType(this.queryTemplate.getResultType());
			}
		} catch (Exception e) {
			throw new RuntimeException("WFSFactory is not configured properly. Parameter 'queryImpl' is missing or wrong.");
		}
		return query;
	}

	/**
	 * Create a WFSQueryResult.
	 * @return A concrete WFSQueryResult instance
	 */
	public WFSQueryResult createQueryResult() throws RuntimeException {
		WFSQueryResult result;
		try {
			result = (WFSQueryResult)Class.forName(this.queryResultImpl).newInstance();
			result.configure(this);
		} catch (Exception e) {
			throw new RuntimeException("WFSFactory is not configured properly. Parameter 'queryResultImpl' is missing or wrong.");
		}
		return result;
	}

	/**
	 * Create a WFSFeature.
	 * @return A concrete WFSFeature instance
	 */
	public WFSFeature createFeature() throws RuntimeException {
		WFSFeature feature;
		try {
			feature = (WFSFeature)Class.forName(this.featureImpl).newInstance();
			feature.configure(this);

			// set default config values from the template feature
			if (this.featureTemplate != null) {
				feature.setIdMappingScript(this.featureTemplate.getIdMappingScript());
			}
		} catch (Exception e) {
			throw new RuntimeException("WFSFactory is not configured properly. Parameter 'featureImpl' is missing or wrong.");
		}
		return feature;
	}

	@Override
	public void configure(PlugDescription arg0) {
		this.plugDescription = arg0;
	}
}
