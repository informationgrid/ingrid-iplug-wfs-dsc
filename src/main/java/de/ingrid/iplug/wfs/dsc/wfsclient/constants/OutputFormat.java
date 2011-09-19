/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient.constants;

public enum OutputFormat {
	TEXT_XML_GML {
		@Override
		public String toString() {
			return "text/xml; subtype=gml/3.1.1";
		}
	}
}
