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
