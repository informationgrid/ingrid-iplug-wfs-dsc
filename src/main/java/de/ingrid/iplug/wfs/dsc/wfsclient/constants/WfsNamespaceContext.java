/**
 * Copyright (c) 2011 wemove digital solutions. All rights reserved.
 */
package de.ingrid.iplug.wfs.dsc.wfsclient.constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

/**
 * This NamespaceContext implementation contains the default WFS namespaces
 * and allows to add additional namespaces for concrete WFS features.
 * @author ingo@wemove.com
 */
public class WfsNamespaceContext implements NamespaceContext {

	/**
	 * Additional namespaces
	 */
	private Map<String, String> nsMap = new HashMap<String, String>();

	@Override
	public String getNamespaceURI(String prefix) {
		// search in default namespaces
		for (Namespace ns : Namespace.values()) {
			QName qName = ns.getQName();
			if (qName.getPrefix().equals(prefix)) {
				return qName.getNamespaceURI();
			}
		}
		// search additional namespaces
		for (String curPrefix : this.nsMap.keySet()) {
			if (curPrefix.equals(prefix)) {
				return this.nsMap.get(curPrefix);
			}
		}
		return XMLConstants.NULL_NS_URI;
	}

	@Override
	public String getPrefix(String namespaceURI) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Add an additional namespace
	 * @param prefix
	 * @param namespaceURI
	 */
	public void addNamespace(String prefix, String namespaceURI) {
		if (!this.nsMap.containsKey(prefix)) {
			this.nsMap.put(prefix, namespaceURI);
		}
	}
}
