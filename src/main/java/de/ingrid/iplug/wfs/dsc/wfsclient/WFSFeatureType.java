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

import java.util.List;

/**
 * Representation of a feature type returned by a WFS server.
 *
 * @author ingo herwig <ingo@wemove.com>
 */
public interface WFSFeatureType extends WFSRecord {

	/**
	 * Get the name of this type
	 * @return String
	 */
	public String getName();

	/**
	 * Get the number of features of this type
	 * @return int
	 */
	public int getNumberOfFeatures();

	/**
	 * Get the of features of this type
	 * @note This list might only be populated, if the number of features does not 
	 * exceed the value set in the configuration value 'featurePreviewLimit'
	 * @return List<WFSFeature>
	 */
	public List<WFSFeature> getFeatures();
}
