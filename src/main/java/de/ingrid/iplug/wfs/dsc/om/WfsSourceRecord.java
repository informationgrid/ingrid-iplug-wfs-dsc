/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.iplug.wfs.dsc.om;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSRecord;

/**
 * Represents a record from a WFS source.
 *
 * @author joachim@wemove.com
 */
public class WfsSourceRecord extends SourceRecord {

	private static final long serialVersionUID = 5660303708840795055L;

	public static final String WFS_RECORD = "wfsRecord";

	/**
	 * Creates a WfsSourceRecord. It holds the source record id and
	 * the original wfs record for further usage.
	 * @param record
	 */
	public WfsSourceRecord(WFSRecord record) {
		super(record.getId());
		this.put(WFS_RECORD, record);
	}
}
