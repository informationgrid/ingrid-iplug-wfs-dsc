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
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient.constants;

import javax.xml.namespace.QName;

public enum Namespace {
	
	WFS			{ public QName getQName() { return nsWFS; } },
	OGC			{ public QName getQName() { return nsOGC; } },
	OWS			{ public QName getQName() { return nsOWS; } },
	GML			{ public QName getQName() { return nsGML; } };
	
	/**
	 * Get the QName of a namespace constant
	 * @return
	 */
	public abstract QName getQName();

	/**
	 * namespaces
	 */
	private static final QName nsWFS = new QName("http://www.opengis.net/wfs", "", "wfs");
	private static final QName nsOGC = new QName("http://www.opengis.net/ogc", "", "ogc");
	private static final QName nsOWS = new QName("http://www.opengis.net/ows", "", "ows");
	private static final QName nsGML = new QName("http://www.opengis.net/gml", "", "gml");
}
