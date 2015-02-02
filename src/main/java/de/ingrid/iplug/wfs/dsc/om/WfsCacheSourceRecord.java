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
/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.om;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;

/**
 * Represents a record from a wfs cache.
 * 
 * @author joachim@wemove.com
 * 
 */
public class WfsCacheSourceRecord extends SourceRecord {

	private static final long serialVersionUID = 5660303708840795055L;

	public static final String WFS_RECORD = "wfsRecord";

	/**
	 * Creates a WfsCacheSourceRecord. It holds the source record id and the
	 * cache for further usage.
	 * 
	 * @param id
	 * @param connection
	 */
	public WfsCacheSourceRecord(WFSFeature record) {
		super(record.getId());
		this.put(WFS_RECORD, record);
	}

}
