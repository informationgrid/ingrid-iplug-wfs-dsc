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
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc;

/**
 * Names used in the plug description
 */
public class ConfigurationKeys {

	public final static String WFS_FACTORY 						= "wfsFactory";
	public final static String WFS_QUERY_TEMPLATE 				= "wfsQueryTemplate";
	public final static String WFS_CACHE 						= "wfsCache";
	public final static String WFS_HARVEST_FILTER 				= "wfsHarvestFilter";
	public final static String WFS_UPDATE_STRATEGIES			= "wfsUpdateStrategies";
	public final static String WFS_UPDATE_JOB					= "wfsUpdateJob";
	public final static String WFS_DOCUMENT_PRODUCER			= "wfsDscDocumentProducer";
	public final static String WFS_RECORD_PRODUCER				= "dscRecordProducer";

	public final static String PLUGDESCRIPTION					= "plugDescription";

	public final static String PLUGDESCRIPTION_KEY_DIRECT_DATA	= "directData";
	public static final String REQUEST_KEY_WFS_DIRECT_RESPONSE 	= "wfsDirectResponse";
	public static final String RESPONSE_KEY_WFS_DATA 	= "wfsData";
	public static final String RESPONSE_KEY_IDF_RECORD 	= "idfRecord";

}
